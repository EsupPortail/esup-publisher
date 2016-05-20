package org.esupportail.publisher.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.*;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.repository.predicates.FilterPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.security.UserContextLoaderService;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.web.rest.dto.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class ContentService {
    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private ItemClassificationOrderRepository itemClassificationOrderRepository;

    @Inject
    private IExternalGroupDao externalGroupDao;
    @Inject
    private IExternalUserDao externalUserDao;


    //@Inject
    //private PublisherRepository publisherRepository;

    @Inject
    @Named("subjectKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> subjectConverter;

    @Inject
    private ClassificationRepository<AbstractClassification> classificationRepository;

    @Inject
    private SubscriberRepository subscriberRepository;

    @Inject
    private IPermissionService permissionService;

    @Inject
    public UserContextLoaderService userSessionTreeLoader;

    @Inject
    private FilterRepository filterRepository;

    @Inject
    private FileService fileService;

    public ResponseEntity<Void> createContent(final ContentDTO content)  throws URISyntaxException {
        if (content.getItem().getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new contents cannot already have an ID").build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (content.getClassifications() == null || content.getClassifications().isEmpty()) {
            // we can save the item as DRAFT if there is at least a validated item
            if (ItemStatus.DRAFT.equals(content.getItem().getStatus())) {
                AbstractItem item = content.getItem();
                item.setValidatedBy(null);
                item.setValidatedDate(null);
                item = itemRepository.save(item);
                userSessionTreeLoader.loadUserTree(authentication);
                return ResponseEntity.created(new URI("/api/contents/" + item.getId())).build();
            }
            return ResponseEntity.badRequest().header("Failure", "When a new content isn't complete it must be saved as a Draft").build();
        }
        //Set<Publisher> publishers = new HashSet<>();
        PermissionClass contextPermClass = PermissionClass.CONTEXT;
        Set<AbstractClassification> authorizedClassifications = new HashSet<>();
        Pair<PermissionType,PermissionDTO> upperClassifPerm = null;
        Pair<PermissionType,PermissionDTO> lowerClassifPerm = null;
        // filter authorized classifs and check constraints
        for (ContextKey classif : content.getClassifications()) {
            AbstractClassification classification = classificationRepository.findOne(classif.getKeyId());
            log.debug("entering filtering : {}", classification);
            if (permissionService.canCreateInCtx(authentication, classif)) {
                log.debug("==> can create = true");
                Pair<PermissionType,PermissionDTO> classifPerms = permissionService.getPermsOfUserInContext(authentication, classif);
                log.debug("==> classifPerms = {}", classifPerms);
                if (upperClassifPerm == null || classifPerms != null && upperClassifPerm.getFirst().getMask() < classifPerms.getFirst().getMask()) {
                    log.debug("==> set upperClassifPerm");
                    upperClassifPerm = classifPerms;
                }
                if (lowerClassifPerm == null || classifPerms != null && lowerClassifPerm.getFirst().getMask() > classifPerms.getFirst().getMask()) {
                    log.debug("==> set lowerClassifPerm");
                    lowerClassifPerm = classifPerms;
                }
                //publishers.add(classification.getPublisher());
                if (classification.getPublisher().getPermissionType() != null) {
                    contextPermClass = classification.getPublisher().getPermissionType();
                }
                if (isValidatedLevelLink(classification)) {
                    log.debug("==> isValidatedLevelLink = true");
                    authorizedClassifications.add(classification);
                }
            }
        }
        log.debug("filtered classifs are : {}", authorizedClassifications);

        if (authorizedClassifications.isEmpty() || upperClassifPerm == null || upperClassifPerm.getFirst().getMask() <= PermissionType.LOOKOVER.getMask()) {
            log.warn("Access forbidden from {} !", authentication.getPrincipal());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug("==> upperClassifPerm = [{}, {}]", upperClassifPerm.getFirst(), upperClassifPerm.getSecond());

        // Case of DRAFT state and not complete publishment to save data, or in static mode when subscribers are on classifications
        if (content.getTargets() == null || content.getTargets().isEmpty()) {
            log.debug("no targets are provided, will save as draft or is a Static redactor !");
            // we can save the item as DRAFT if there is at least a validated item or we can save
            if (ItemStatus.DRAFT.equals(content.getItem().getStatus()) || WritingMode.STATIC.equals(content.getItem().getRedactor().getWritingMode())) {
                AbstractItem item = content.getItem();
                item.setValidatedBy(null);
                item.setValidatedDate(null);

                // manage static mode without target, published or scheduled state is authorized
                if (WritingMode.STATIC.equals(content.getItem().getRedactor().getWritingMode())
                    && (ItemStatus.PUBLISHED.equals(item.getStatus()) || ItemStatus.SCHEDULED.equals(item.getStatus()))) {
                    // lower rights in all classifications will applied for validation
                    if (lowerClassifPerm.getFirst().getMask() > PermissionType.CONTRIBUTOR.getMask()) {
                        if (item.getStartDate().isAfter(LocalDate.now())) {
                            item.setStatus(ItemStatus.SCHEDULED);
                        }
                        item.setValidatedBy(SecurityUtils.getCurrentUser());
                        item.setValidatedDate(DateTime.now());
                    } else {
                        item.setStatus(ItemStatus.PENDING);
                    }
                } else {
                    item.setStatus(ItemStatus.DRAFT);
                }

                item = itemRepository.save(item);

                // now we save all linked classification
                Set<ItemClassificationOrder> classifs = new HashSet<>();
                for (AbstractClassification classif : authorizedClassifications) {
                    ItemClassificationOrder ico = new ItemClassificationOrder(item, classif,
                        itemClassificationOrderRepository.getNextDisplayOrderInClassification(classif.getId()));
                    classifs.add(ico);
                }
                itemClassificationOrderRepository.save(classifs);

                userSessionTreeLoader.loadUserTree(authentication);
                return ResponseEntity.created(new URI("/api/contents/" + item.getId())).build();
            }
            return ResponseEntity.badRequest().header("Failure", "When a new content isn't complete it must be saved as a Draft").build();
        }

        // check if targeted subjects are authorized
        // in case of publishing in other way than in a context with rights
        // TODO filtering of authorized ctx and on filter entity group/ldap permission
        // TODO also check if passed argument was in contained last userSearch
        Set<SubscriberFormDTO> authorizedSubscribers = new HashSet<>();
        //log.debug("Contexts of publishment {}", publishers);
        log.debug("Type of Contexts of publishment {} and userPerms applied {}", contextPermClass, lowerClassifPerm.getSecond());
        if (PermissionClass.CONTEXT_WITH_SUBJECTS.equals(contextPermClass)) {
            // case of defining targets on items (with on level of classification only)
            PermOnCtxDTO permObj = (PermOnCtxDTO) lowerClassifPerm.getSecond();
            // permObj could be null only when user is ADMIN
            if (permObj == null) {
                authorizedSubscribers.addAll(content.getTargets());
            } else if (lowerClassifPerm.getSecond() instanceof PermOnClassifWSubjDTO
                && ((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects() != null && !((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects().isEmpty()) {
                authorizedSubscribers.addAll(filterSubcribers(Sets.newHashSet(content.getTargets()), ((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects()));
            } else if (ContextType.ORGANIZATION.equals(permObj.getContext().getKeyType())) {
                // case of perms are herited from ORGANIZATIONS
                 authorizedSubscribers.addAll(filterSubcribersOnDefault(Sets.newHashSet(content.getTargets()), permObj.getContext()));
            }

            log.debug("Authorized targets are {}", authorizedSubscribers);
            if (authorizedSubscribers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

        } else if (PermissionClass.CONTEXT.equals(contextPermClass)) {
            authorizedSubscribers.addAll(content.getTargets());
        } else {
            throw new IllegalStateException(String.format("Permission Type %s is not managed", lowerClassifPerm.getSecond()));
        }

        AbstractItem item = content.getItem();
        // check about validation if can be done by the user
        if (ItemStatus.PUBLISHED.equals(item.getStatus()) || ItemStatus.SCHEDULED.equals(item.getStatus())) {
            // lower rights in all classifications will applied for validation
            if (lowerClassifPerm.getFirst().getMask() > PermissionType.CONTRIBUTOR.getMask()) {
                if (item.getStartDate().isAfter(LocalDate.now())) {
                    item.setStatus(ItemStatus.SCHEDULED);
                }
                item.setValidatedBy(SecurityUtils.getCurrentUser());
                item.setValidatedDate(DateTime.now());
            } else {
                item.setStatus(ItemStatus.PENDING);
                item.setValidatedBy(null);
                item.setValidatedDate(null);
            }
        } else {
            item.setStatus(ItemStatus.DRAFT);
            item.setValidatedBy(null);
            item.setValidatedDate(null);
        }
        log.debug("Will save item {}", item);
        item = itemRepository.save(item);

        // now we save all linked classification
        Set<ItemClassificationOrder> classifs = new HashSet<>();
        for (AbstractClassification classif : authorizedClassifications) {
            ItemClassificationOrder ico = new ItemClassificationOrder(item, classif,
                itemClassificationOrderRepository.getNextDisplayOrderInClassification(classif.getId()));
            classifs.add(ico);
        }
        log.debug("saving associated classifications {}", classifs);
        itemClassificationOrderRepository.save(classifs);
        // now we save all subscribers if there is
        if (!authorizedSubscribers.isEmpty()) {
            Set<Subscriber> persistSubscribers = convert(authorizedSubscribers, item.getContextKey());
            log.debug("Will save associated targets :{}", authorizedSubscribers);
            subscriberRepository.save(persistSubscribers);
        }

        userSessionTreeLoader.loadUserTree(authentication);
        return ResponseEntity.created(new URI("/api/contents/" + item.getId())).build();
    }

    public ResponseEntity<Void> updateContent(final ContentDTO content)  throws URISyntaxException {
        if (content.getItem().getId() == null) {
            return ResponseEntity.badRequest().header("Failure", "Trying to update a new content isn't possible.").build();
        }
        Set<ItemClassificationOrder> oldLinkedClassifications = Sets.newHashSet(itemClassificationOrderRepository.findAll(ItemPredicates.itemsClassOfItem(content.getItem().getId())));
        if (content.getClassifications() == null || content.getClassifications().isEmpty()) {
            itemClassificationOrderRepository.delete(oldLinkedClassifications);
            // we can save the item as DRAFT if there is at least a validated item
            if (content.getItem().getStatus().equals(ItemStatus.DRAFT)) {
                AbstractItem item = content.getItem();
                item.setValidatedBy(null);
                item.setValidatedDate(null);
                item = itemRepository.save(item);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().header("Failure", "When a content isn't complete it must be saved as a Draft").build();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Set<Publisher> publishers = new HashSet<>();
        PermissionClass contextPermClass = PermissionClass.CONTEXT;
        Set<AbstractClassification> authorizedClassifications = new HashSet<>();
        Pair<PermissionType,PermissionDTO> upperClassifPerm = null;
        Pair<PermissionType,PermissionDTO> lowerClassifPerm = null;
        // filter authorized classifs and check constraints
        for (ContextKey classif : content.getClassifications()) {
            AbstractClassification classification = classificationRepository.findOne(classif.getKeyId());
            log.debug("entering filtering : {}", classification);
            if (permissionService.canCreateInCtx(authentication, classif)) {
                log.debug("==> can create = true");
                Pair<PermissionType,PermissionDTO> classifPerms = permissionService.getPermsOfUserInContext(authentication, classif);
                log.debug("==> classifPerms = {}", classifPerms);
                if (upperClassifPerm == null || classifPerms != null && upperClassifPerm.getFirst().getMask() < classifPerms.getFirst().getMask()) {
                    log.debug("==> set upperClassifPerm");
                    upperClassifPerm = classifPerms;
                }
                if (lowerClassifPerm == null || classifPerms != null && lowerClassifPerm.getFirst().getMask() > classifPerms.getFirst().getMask()) {
                    log.debug("==> set lowerClassifPerm");
                    lowerClassifPerm = classifPerms;
                }
                //publishers.add(classification.getPublisher());
                if (classification.getPublisher().getPermissionType() != null) {
                    contextPermClass = classification.getPublisher().getPermissionType();
                }
                if (isValidatedLevelLink(classification)) {
                    log.debug("==> isValidatedLevelLink = true");
                    authorizedClassifications.add(classification);
                }
            }
        }
        log.debug("filtered classifs are : {}", authorizedClassifications);
        log.debug("==> upperClassifPerm = [{}, {}]", upperClassifPerm.getFirst(), upperClassifPerm.getSecond());

        if (authorizedClassifications.isEmpty() || upperClassifPerm == null || upperClassifPerm.getFirst().getMask() <= PermissionType.LOOKOVER.getMask()) {
            log.warn("Access forbidden from {} !", authentication.getPrincipal());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Case of DRAFT state and not complete publishment to save data, or in static mode when subscribers are on classifications
        Set<Subscriber> oldSubscribers = Sets.newHashSet(subscriberRepository.findAll(SubscriberPredicates.onCtx(content.getItem().getContextKey())));
        if (content.getTargets() == null || content.getTargets().isEmpty()) {
            subscriberRepository.delete(oldSubscribers);
            log.debug("no targets are provided, will save as draft !");
            // we can save the item as DRAFT if there is at least a validated item
            if (ItemStatus.DRAFT.equals(content.getItem().getStatus()) || WritingMode.STATIC.equals(content.getItem().getRedactor().getWritingMode())) {
                AbstractItem item = content.getItem();
                item.setValidatedBy(null);
                item.setValidatedDate(null);

                // manage static mode without target, published or scheduled state is authorized
                if (WritingMode.STATIC.equals(content.getItem().getRedactor().getWritingMode())
                    && (ItemStatus.PUBLISHED.equals(item.getStatus()) || ItemStatus.SCHEDULED.equals(item.getStatus()))) {
                    // lower rights in all classifications will applied for validation
                    if (lowerClassifPerm.getFirst().getMask() > PermissionType.CONTRIBUTOR.getMask()) {
                        if (item.getStartDate().isAfter(LocalDate.now())) {
                            item.setStatus(ItemStatus.SCHEDULED);
                        }
                        item.setValidatedBy(SecurityUtils.getCurrentUser());
                        item.setValidatedDate(DateTime.now());
                    } else {
                        item.setStatus(ItemStatus.PENDING);
                    }
                } else {
                    item.setStatus(ItemStatus.DRAFT);
                }

                item = itemRepository.save(item);

                // now we save all linked classification
                Set<ItemClassificationOrder> oldToRemoves = new HashSet<>();
                for (ItemClassificationOrder ico : oldLinkedClassifications) {
                    if (authorizedClassifications.contains(ico.getId().getAbstractClassification())) {
                        authorizedClassifications.remove(ico.getId().getAbstractClassification());
                    } else {
                        oldToRemoves.add(ico);
                    }
                }
                itemClassificationOrderRepository.delete(oldToRemoves);
                Set<ItemClassificationOrder> classifs = new HashSet<>();
                for (AbstractClassification classif : authorizedClassifications) {
                    ItemClassificationOrder ico = new ItemClassificationOrder(item, classif,
                        itemClassificationOrderRepository.getNextDisplayOrderInClassification(classif.getId()));
                    classifs.add(ico);
                }
                itemClassificationOrderRepository.save(classifs);

                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().header("Failure", "When a new content isn't complete it must be saved as a Draft").build();
        }

        // check if targeted subjects are authorized
        // in case of publishing in other way than in a context with rights
        // TODO filtering of authorized ctx and on filter entity group/ldap permission
        // TODO also check if passed argument was in contained last userSearch
        Set<SubscriberFormDTO> authorizedSubscribers = new HashSet<>();
        //log.debug("Contexts of publishment {}", publishers);
        log.debug("Type of Contexts of publishment {}", contextPermClass);
        if (PermissionClass.CONTEXT_WITH_SUBJECTS.equals(contextPermClass)) {
            // case of defining targets on items (with on level of classification only)
            // case of defining targets on items (with on level of classification only)
            PermOnCtxDTO permObj = (PermOnCtxDTO) lowerClassifPerm.getSecond();
            // permObj could be null only when user is ADMIN
            if (permObj == null) {
                authorizedSubscribers.addAll(content.getTargets());
            } else if (lowerClassifPerm.getSecond() instanceof PermOnClassifWSubjDTO
                && ((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects() != null && !((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects().isEmpty()) {
                authorizedSubscribers.addAll(filterSubcribers(Sets.newHashSet(content.getTargets()), ((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects()));
            } else if (ContextType.ORGANIZATION.equals(permObj.getContext().getKeyType())) {
                // case of perms are herited from ORGANIZATIONS
                authorizedSubscribers.addAll(filterSubcribersOnDefault(Sets.newHashSet(content.getTargets()), permObj.getContext()));
            }

            log.debug("Authorized targets are {}", authorizedSubscribers);
            if (authorizedSubscribers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

        } else if (!PermissionClass.CONTEXT.equals(contextPermClass)) {
            throw new IllegalStateException(String.format("Permission Type %s is not managed", lowerClassifPerm.getSecond()));
        }

        AbstractItem item = content.getItem();
        // check about validation if can be done by the user
        if (ItemStatus.PUBLISHED.equals(item.getStatus()) || ItemStatus.SCHEDULED.equals(item.getStatus())) {
            // lower rights in all classifications will applied for validation
            if (lowerClassifPerm.getFirst().getMask() > PermissionType.CONTRIBUTOR.getMask()) {
                if (item.getStartDate().isAfter(LocalDate.now())) {
                    item.setStatus(ItemStatus.SCHEDULED);
                }
                item.setValidatedBy(SecurityUtils.getCurrentUser());
                item.setValidatedDate(DateTime.now());
            } else {
                item.setStatus(ItemStatus.PENDING);
                item.setValidatedBy(null);
                item.setValidatedDate(null);
            }
        } else {
            item.setStatus(ItemStatus.DRAFT);
            item.setValidatedBy(null);
            item.setValidatedDate(null);
        }
        log.debug("Will save item {}", item);
        item = itemRepository.save(item);

        // now we save all linked classification
        Set<ItemClassificationOrder> oldToRemoves = new HashSet<>();
        for (ItemClassificationOrder ico : oldLinkedClassifications) {
            if (authorizedClassifications.contains(ico.getId().getAbstractClassification())) {
                authorizedClassifications.remove(ico.getId().getAbstractClassification());
            } else {
                oldToRemoves.add(ico);
            }
        }
        log.debug("remove old/unautorized classif classifications {}", oldToRemoves);
        itemClassificationOrderRepository.delete(oldToRemoves);
        Set<ItemClassificationOrder> classifs = new HashSet<>();
        for (AbstractClassification classif : authorizedClassifications) {
            ItemClassificationOrder ico = new ItemClassificationOrder(item, classif,
                itemClassificationOrderRepository.getNextDisplayOrderInClassification(classif.getId()));
            classifs.add(ico);
        }
        log.debug("saving new associated classifications {}", classifs);
        itemClassificationOrderRepository.save(classifs);
        // now we save all subscribers if there is
        if (!authorizedSubscribers.isEmpty()) {
            Set<Subscriber> persistSubscribers = convert(authorizedSubscribers, item.getContextKey());
            Set<Subscriber> oldSubsToRemoves = new HashSet<>();
            for (Subscriber sub : oldSubscribers) {
                if (persistSubscribers.contains(sub)) {
                    persistSubscribers.remove(sub);
                } else {
                    oldSubsToRemoves.add(sub);
                }
            }
            subscriberRepository.delete(oldSubsToRemoves);
            log.debug("Will save associated targets :{}", authorizedSubscribers);
            subscriberRepository.save(persistSubscribers);
        }

        return ResponseEntity.ok().build();
    }

    private boolean isValidatedLevelLink(final AbstractClassification classification) {
        int nbLevel = classification.getPublisher().getContext().getRedactor().getNbLevelsOfClassification();
        boolean validCategory = nbLevel == 1 && classification instanceof Category;
        boolean validFeed = nbLevel == 2 && classification instanceof AbstractFeed;
        return validCategory || validFeed;

    }

    private Set<Subscriber> convert(@NotNull final Set<SubscriberFormDTO> dtos, @NotNull final ContextKey ctx) {
        Set<Subscriber> models = Sets.newHashSet();
        for (SubscriberFormDTO dto : dtos) {
            models.add(new Subscriber(subjectConverter.convertToModelKey(dto.getSubject().getModelId()), ctx, dto.getSubscribeType()));
        }
        return models;
    }

    private Set<SubscriberFormDTO> filterSubcribers(final Set<SubscriberFormDTO> targets, final Set<SubjectKeyDTO> authorizedSubjects) {
        Set<SubscriberFormDTO> filteredSubjects = Sets.newHashSet();
        Set<String> authorizedGroups = Sets.newHashSet();
        Set<String> authorizedUsers = Sets.newHashSet();
        for (SubjectKeyDTO subjectKeyDTO : authorizedSubjects) {
            switch (subjectKeyDTO.getKeyType()) {
                case GROUP: authorizedGroups.add(subjectKeyDTO.getKeyId()); break;
                case PERSON: authorizedUsers.add(subjectKeyDTO.getKeyId()); break;
                default: throw new IllegalArgumentException("SubjectType '" + subjectKeyDTO.getKeyType() + "' not managed !");
            }
        }
        for (SubscriberFormDTO toTest: targets) {
            if (authorizedSubjects.contains(toTest.getSubject().getModelId())) {
                // subjectkey are equals
                filteredSubjects.add(toTest);
            } else {
                // subjectkey are equals so find with "sub queries"
                switch (toTest.getSubject().getModelId().getKeyType()) {
                    case GROUP:
                        if (externalGroupDao.isGroupMemberOfAtLeastOneGroup(toTest.getSubject().getModelId().getKeyId(), authorizedGroups)) {
                            filteredSubjects.add(toTest);
                        }
                        break;
                    case PERSON:
                        if (externalGroupDao.isUserMemberOfAtLeastOneGroup(toTest.getSubject().getModelId().getKeyId(), authorizedGroups)) {
                            filteredSubjects.add(toTest);
                        }
                        break;
                    default: throw new IllegalArgumentException("SubjectType '" + toTest.getSubject().getModelId().getKeyType() + "' not managed !");
                }
            }
        }
        log.debug("filterSubcribers returned {}, \n comparing to entry {}", filteredSubjects, targets);

        return filteredSubjects;
    }

    private Set<SubscriberFormDTO> filterSubcribersOnDefault(final Set<SubscriberFormDTO> targets, final ContextKeyDTO ctx) {
        Assert.isTrue(ContextType.ORGANIZATION.equals(ctx.getKeyType()));
        Set<SubscriberFormDTO> filteredSubjects = Sets.newHashSet();
        Filter filterGroup = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(ctx.getKeyId(), FilterType.GROUP));
        Filter filterUser = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(ctx.getKeyId(), FilterType.LDAP));
        for (SubscriberFormDTO subscriberFormDTO : targets) {
            switch (subscriberFormDTO.getSubject().getModelId().getKeyType()) {
                case GROUP:
                    if (filterGroup != null && externalGroupDao.isGroupMemberOfGroupFilter(filterGroup.getPattern(),subscriberFormDTO.getSubject().getModelId().getKeyId())) {
                        filteredSubjects.add(subscriberFormDTO);
                    }
                    break;
                case PERSON:
                    if (filterUser != null && externalUserDao.isUserFoundWithFilter(filterUser.getPattern(), subscriberFormDTO.getSubject().getModelId().getKeyId())) {
                        filteredSubjects.add(subscriberFormDTO);
                    }
                    break;
                default: throw new IllegalArgumentException("SubjectType '" + subscriberFormDTO.getSubject().getModelId().getKeyType() + "' not managed !");
            }
        }


        log.debug("filterSubcribersOnDefault returned {}, \n comparing to entry {}", filteredSubjects, targets);

        return filteredSubjects;
    }

    /*private boolean isCompatiblePublishersContext(final Set<Publisher> publishers) {
        if (publishers == null || publishers.isEmpty()) return false;
        if (publishers.size() == 1) return true;
        Publisher presPub = null;
        PermissionClass permsCompatibility = null;
        for (Publisher publisher: publishers) {
            if (presPub == null) {
                presPub = publisher;
                permsCompatibility = publisher.getPermissionType();
            } else {
                if (!permsCompatibility.equals(publisher.getPermissionType())) return false;
            }
        }
        return true;
    }*/

    public void deleteContent(Long id) {
        final AbstractItem item = itemRepository.findOne(id);
        fileService.deleteInternalResource(item.getEnclosure());
        Iterable<Subscriber> subscribersToDel = subscriberRepository.findAll(SubscriberPredicates.onCtx(new ContextKey(id, ContextType.ITEM)));
        subscriberRepository.delete(subscribersToDel);
        Iterable<ItemClassificationOrder> classificationsLinksToDel = itemClassificationOrderRepository.findAll(ItemPredicates.itemsClassOfItem(id));
        itemClassificationOrderRepository.delete(classificationsLinksToDel);
        itemRepository.delete(id);
    }


    @Scheduled(cron = "1 0 0 * * ?")
    //@Scheduled(cron = "1 0/2 * * * ?")
    public void archivePublishedContents() {
        log.warn("################### Launch scheduled task archivePublishedContents to change state of items !");
        Integer nbUpdates = itemRepository.archiveExpiredPublished();
        log.info("scheduled task changed state to ARCHIVED of {} items !", nbUpdates);
    }

    @Scheduled(cron = "1 0 0 * * ?")
    public void publishScheduledContents() {
        log.warn("################### Launch scheduled task publishScheduledContents to change state of items !");
        Integer nbUpdates = itemRepository.publishScheduled();
        log.info("scheduled task changed state to PUBLISHED of {} items !", nbUpdates);
    }

    /**
     * Once time per week we purge useless items
     */
    @Scheduled(cron = "1 0 0 1 * ?")
    public void removeOldContents() {
        log.warn("################### Launch scheduled task removeOldContents to remove items !");
        List<AbstractItem> items = Lists.newArrayList(itemRepository.findAll(ItemPredicates.ItemsToRemove()));
        for (AbstractItem item: items) {
            deleteContent(item.getId());
        }
    }
}
