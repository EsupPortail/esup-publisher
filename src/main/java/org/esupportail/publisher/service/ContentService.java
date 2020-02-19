/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.esupportail.publisher.repository.ClassificationRepository;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.RedactorRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.repository.predicates.FilterPredicates;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.security.UserContextLoaderService;
import org.esupportail.publisher.service.factories.CompositeKeyExtendedDTOFactory;
import org.esupportail.publisher.service.factories.SubjectDTOToExtendedKeyConverterFactory;
import org.esupportail.publisher.web.rest.dto.ContentDTO;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.LinkedFileItemDTO;
import org.esupportail.publisher.web.rest.dto.PermOnClassifWSubjDTO;
import org.esupportail.publisher.web.rest.dto.PermOnCtxDTO;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyExtendedDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberFormDTO;
import org.esupportail.publisher.web.rest.dto.ValueResource;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;

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

	@Inject
	private LinkedFileItemRepository linkedFileItemRepository;

	/*@Inject
	@Named("subjectKeyDTOFactoryImpl")
	private transient CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKeySubjectKeyExtendedDTO> subjectConverter;*/

	@Inject
	private transient CompositeKeyExtendedDTOFactory<SubjectKeyExtendedDTO, SubjectKeyExtended, String, String, SubjectType> subjectKeyExtendedConverter;

	@Inject
	private transient SubjectDTOToExtendedKeyConverterFactory subjectDTOConverter;

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

	@Inject
	private RedactorRepository redactorRepository;

	public ResponseEntity<?> saveContent(final ContentDTO content) throws URISyntaxException {
		Optional<Redactor> optionalRedactor = redactorRepository.findById(content.getItem().getRedactor().getId());
		Redactor redactor = optionalRedactor.orElse(null);
		if (redactor == null) {
			log.error("The redactor id wasn't passed !");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		content.getItem().setRedactor(redactor);
		final boolean isUpdate = content.getItem().getId() != null;
		// status DRAFT if endDate is mandatory and is null or if endDate is before now
		if ((!content.getItem().getRedactor().isOptionalPublishTime() && content.getItem().getEndDate() == null)
				|| (content.getItem().getEndDate() != null && content.getItem().getEndDate().isBefore(LocalDate.now()))) {
			content.getItem().setStatus(ItemStatus.DRAFT);
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Set<ItemClassificationOrder> oldLinkedClassifications = null;
		if (isUpdate) {
			oldLinkedClassifications = Sets.newHashSet(itemClassificationOrderRepository.findAll(ItemPredicates
					.itemsClassOfItem(content.getItem().getId())));
		}
		if (content.getClassifications() == null || content.getClassifications().isEmpty()) {
			if (oldLinkedClassifications != null && !oldLinkedClassifications.isEmpty())
				itemClassificationOrderRepository.deleteAll(oldLinkedClassifications);
			// we can save the item as DRAFT if there is at least a validated item
			if (ItemStatus.DRAFT.equals(content.getItem().getStatus())) {
				AbstractItem item = content.getItem();
				item.setValidatedBy(null);
				item.setValidatedDate(null);
				item = itemRepository.save(item);
				updateLinkedFilesToItem(item, content.getLinkedFiles());
				if (isUpdate)
					return ResponseEntity.ok(new ValueResource(item.getStatus()));
				//else
				userSessionTreeLoader.loadUserTree(authentication);
				return ResponseEntity.created(new URI("/api/contents/" + item.getId())).body(
						new ValueResource(item.getStatus()));
			}
			return ResponseEntity.badRequest()
					.header("Failure", "When a content isn't complete it must be saved as a Draft").build();
		}
		//Set<Publisher> publishers = new HashSet<>();
		PermissionClass contextPermClass = PermissionClass.CONTEXT;
		Set<AbstractClassification> authorizedClassifications = new HashSet<>();
		Pair<PermissionType, PermissionDTO> upperClassifPerm = null;
		Pair<PermissionType, PermissionDTO> lowerClassifPerm = null;
		// filter authorized classifs and check constraints
		for (ContextKey classif : content.getClassifications()) {
			Optional<AbstractClassification> optionalClassif = classificationRepository.findById(classif.getKeyId());
			AbstractClassification classification = optionalClassif.orElse(null);
			log.debug("entering filtering : {}", classification);
			if (classification != null && permissionService.canCreateInCtx(authentication, classif)) {
				log.debug("==> can create = true");
				Pair<PermissionType, PermissionDTO> classifPerms = permissionService.getPermsOfUserInContext(
						authentication, classif);
				log.debug("==> classifPerms = {}", classifPerms);
				if (upperClassifPerm == null || classifPerms != null
						&& upperClassifPerm.getFirst().getMask() < classifPerms.getFirst().getMask()) {
					log.debug("==> set upperClassifPerm");
					upperClassifPerm = classifPerms;
				}
				if (lowerClassifPerm == null || classifPerms != null
						&& lowerClassifPerm.getFirst().getMask() > classifPerms.getFirst().getMask()) {
					log.debug("==> set lowerClassifPerm");
					lowerClassifPerm = classifPerms;
				}
				//publishers.add(classification.getPublisher());
				if (classification.getPublisher() != null && classification.getPublisher().getPermissionType() != null) {
					contextPermClass = classification.getPublisher().getPermissionType();
				}
				if (isValidatedLevelLink(classification)) {
					log.debug("==> isValidatedLevelLink = true");
					authorizedClassifications.add(classification);
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("filtered classifs are : {}", authorizedClassifications);
			if (upperClassifPerm != null) {
				log.debug("==> upperClassifPerm = [{}, {}]", upperClassifPerm.getFirst(), upperClassifPerm.getSecond());
			} else {
				log.debug("==> upperClassifPerm is null !");
			}
		}

		if (authorizedClassifications.isEmpty() || upperClassifPerm == null
				|| upperClassifPerm.getFirst().getMask() <= PermissionType.LOOKOVER.getMask()) {
			log.warn("Access forbidden from {} !", authentication.getPrincipal());
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		// Case of DRAFT state and not complete publishment to save data, or in static mode when subscribers are on classifications
		Set<Subscriber> oldSubscribers = null;
		if (isUpdate) {
			oldSubscribers = Sets.newHashSet(subscriberRepository.findAll(SubscriberPredicates.onCtx(content.getItem()
					.getContextKey())));
		}
		if (content.getTargets() == null || content.getTargets().isEmpty()) {
			if (isUpdate)
				subscriberRepository.deleteAll(oldSubscribers);
			log.debug("no targets are provided, will save as draft or is a Static redactor !");
			// we can save the item as DRAFT if there is at least a validated item or we can save
			if (ItemStatus.DRAFT.equals(content.getItem().getStatus())
					|| WritingMode.STATIC.equals(content.getItem().getRedactor().getWritingMode())) {
				AbstractItem item = content.getItem();
				item.setValidatedBy(null);
				item.setValidatedDate(null);

				// manage static mode without target, published or scheduled state is authorized
				if (WritingMode.STATIC.equals(content.getItem().getRedactor().getWritingMode())
						&& (ItemStatus.PUBLISHED.equals(item.getStatus()) || ItemStatus.SCHEDULED.equals(item
								.getStatus()))) {
					// lower rights in all classifications will applied for validation
					if (lowerClassifPerm.getFirst().getMask() > PermissionType.CONTRIBUTOR.getMask()) {
						if (item.getStartDate().isAfter(LocalDate.now())) {
							item.setStatus(ItemStatus.SCHEDULED);
						}
						item.setValidatedBy(SecurityUtils.getCurrentUser());
						item.setValidatedDate(Instant.now());
					} else {
						item.setStatus(ItemStatus.PENDING);
					}
				} else {
					item.setStatus(ItemStatus.DRAFT);
				}

				item = itemRepository.save(item);
				updateLinkedFilesToItem(item, content.getLinkedFiles());

				// now we save all linked classification and before clean olds not anymore linked
				if (oldLinkedClassifications != null && !oldLinkedClassifications.isEmpty()) {
					Set<ItemClassificationOrder> oldToRemoves = new HashSet<>();
					for (ItemClassificationOrder ico : oldLinkedClassifications) {
						if (authorizedClassifications.contains(ico.getId().getAbstractClassification())) {
							authorizedClassifications.remove(ico.getId().getAbstractClassification());
						} else {
							oldToRemoves.add(ico);
						}
					}
					itemClassificationOrderRepository.deleteAll(oldToRemoves);
				}
				Set<ItemClassificationOrder> classifs = new HashSet<>();
				for (AbstractClassification classif : authorizedClassifications) {
					ItemClassificationOrder ico = new ItemClassificationOrder(item, classif,
							itemClassificationOrderRepository.getNextDisplayOrderInClassification(classif.getId()));
					classifs.add(ico);
				}
				itemClassificationOrderRepository.saveAll(classifs);

				if (isUpdate)
					return ResponseEntity.ok(new ValueResource(item.getStatus()));
				//else
				userSessionTreeLoader.loadUserTree(authentication);
				return ResponseEntity.created(new URI("/api/contents/" + item.getId())).body(
						new ValueResource(item.getStatus()));
			}
			return ResponseEntity.badRequest()
					.header("Failure", "When a new content isn't complete it must be saved as a Draft").build();
		}

		// check if targeted subjects are authorized
		// in case of publishing in other way than in a context with rights
		// TODO filtering of authorized ctx and on filter entity group/ldap permission
		// TODO also check if passed argument was in contained last userSearch
		Set<SubscriberFormDTO> authorizedSubscribers = new HashSet<>();
		log.debug("Type of Contexts of publishment {} and userPerms applied {}", contextPermClass,
				lowerClassifPerm.getSecond());
		if (PermissionClass.CONTEXT_WITH_SUBJECTS.equals(contextPermClass)) {
			// case of defining targets on items (with on level of classification only)
			PermOnCtxDTO permObj = (PermOnCtxDTO) lowerClassifPerm.getSecond();
			// permObj could be null only when user is ADMIN
			if (permObj == null) {
				authorizedSubscribers.addAll(content.getTargets());
			} else if (lowerClassifPerm.getSecond() instanceof PermOnClassifWSubjDTO
					&& ((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects() != null
					&& !((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects().isEmpty()) {
				authorizedSubscribers.addAll(filterSubcribers(Sets.newHashSet(content.getTargets()),
						((PermOnClassifWSubjDTO) permObj).getAuthorizedSubjects()));
			} else if (ContextType.ORGANIZATION.equals(permObj.getContext().getKeyType())) {
				// case of perms are herited from ORGANIZATIONS
				authorizedSubscribers.addAll(filterSubcribersOnDefault(Sets.newHashSet(content.getTargets()),
						permObj.getContext()));
			}

			log.debug("Authorized targets are {}", authorizedSubscribers);
			if (authorizedSubscribers.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

		} else if (PermissionClass.CONTEXT.equals(contextPermClass)) {
			authorizedSubscribers.addAll(content.getTargets());
		} else {
			throw new IllegalStateException(String.format("Permission Type %s is not managed",
					lowerClassifPerm.getSecond()));
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
				item.setValidatedDate(Instant.now());
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
		updateLinkedFilesToItem(item, content.getLinkedFiles());

		// now we save all linked classification
		if (oldLinkedClassifications != null && !oldLinkedClassifications.isEmpty()) {
			Set<ItemClassificationOrder> oldToRemoves = new HashSet<>();
			for (ItemClassificationOrder ico : oldLinkedClassifications) {
				if (authorizedClassifications.contains(ico.getId().getAbstractClassification())) {
					authorizedClassifications.remove(ico.getId().getAbstractClassification());
				} else {
					oldToRemoves.add(ico);
				}
			}
			log.debug("remove old/unautorized classif classifications {}", oldToRemoves);
			itemClassificationOrderRepository.deleteAll(oldToRemoves);
		}
		Set<ItemClassificationOrder> classifs = new HashSet<>();
		for (AbstractClassification classif : authorizedClassifications) {
			ItemClassificationOrder ico = new ItemClassificationOrder(item, classif,
					itemClassificationOrderRepository.getNextDisplayOrderInClassification(classif.getId()));
			classifs.add(ico);
		}
		log.debug("saving associated classifications {}", classifs);
		itemClassificationOrderRepository.saveAll(classifs);

		// now we save all subscribers if there is
		if (!authorizedSubscribers.isEmpty()) {
			Set<Subscriber> persistSubscribers = convert(authorizedSubscribers, item.getContextKey());
			if (oldSubscribers != null && !oldSubscribers.isEmpty()) {
				Set<Subscriber> oldSubsToRemoves = new HashSet<>();
				for (Subscriber sub : oldSubscribers) {
					if (persistSubscribers.contains(sub)) {
						persistSubscribers.remove(sub);
					} else {
						oldSubsToRemoves.add(sub);
					}
				}
				subscriberRepository.deleteAll(oldSubsToRemoves);
			}
			log.debug("Will save associated targets :{}", authorizedSubscribers);
			subscriberRepository.saveAll(persistSubscribers);
		}

		if (isUpdate)
			return ResponseEntity.ok(new ValueResource(item.getStatus()));
		//else
		userSessionTreeLoader.loadUserTree(authentication);
		return ResponseEntity.created(new URI("/api/contents/" + item.getId())).body(
				new ValueResource(item.getStatus()));
	}

	public ResponseEntity<?> setValidationItem(final boolean validation, @NotNull final AbstractItem item) {
		PermissionType permType = permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext()
				.getAuthentication(), item.getContextKey());
		if (permType == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		if (PermissionType.CONTRIBUTOR.getMask() < permType.getMask()) {
			if (validation) {
				item.setValidatedBy(SecurityUtils.getCurrentUser());
				item.setValidatedDate(Instant.now());
				if (item.getStartDate().isAfter(LocalDate.now())) {
					item.setStatus(ItemStatus.SCHEDULED);
				} else {
					item.setStatus(ItemStatus.PUBLISHED);
				}
            } else {
				item.setValidatedBy(null);
				item.setValidatedDate(null);
				item.setStatus(ItemStatus.PENDING);
            }
            if (item.getEndDate() != null && item.getEndDate().isBefore(LocalDate.now())) {
                item.setStatus(ItemStatus.DRAFT);
            }
            itemRepository.save(item);
			return ResponseEntity.ok(new ValueResource(item.getStatus()));
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	private boolean isValidatedLevelLink(final AbstractClassification classification) {
		int nbLevel = classification.getPublisher().getContext().getRedactor().getNbLevelsOfClassification();
		boolean validCategory = nbLevel == 1 && classification instanceof Category;
		boolean validFeed = nbLevel == 2 && classification instanceof AbstractFeed;
		return validCategory || validFeed;
	}

	public ResponseEntity<?> setEnclosureItem(final String enclosure, @NotNull final AbstractItem item) {
		if (permissionService.canEditCtx(SecurityContextHolder.getContext().getAuthentication(), item.getContextKey())) {
			item.setEnclosure(enclosure);
			if (item instanceof Flash && item.getEnclosure() == null) {
				item.setStatus(ItemStatus.DRAFT);
			}
			itemRepository.save(item);
			return ResponseEntity.ok(new ValueResource(item.getStatus()));
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	private Set<Subscriber> convert(@NotNull final Set<SubscriberFormDTO> dtos, @NotNull final ContextKey ctx) {
		Set<Subscriber> models = Sets.newHashSet();
		for (SubscriberFormDTO dto : dtos) {
			if (dto.getSubject() != null) {
				models.add(new Subscriber(subjectDTOConverter.convertToModelKey(dto.getSubject()), ctx, dto
						.getSubscribeType()));
			} else if (dto.getExtendedSubject() != null) {
				models.add(new Subscriber(subjectKeyExtendedConverter.convertToModelKey(dto.getExtendedSubject()), ctx,
						dto.getSubscribeType()));
			}
		}
		return models;
	}

	private Set<SubscriberFormDTO> filterSubcribers(final Set<SubscriberFormDTO> targets,
			final Set<SubjectKeyDTO> authorizedSubjects) {
		Set<SubscriberFormDTO> filteredSubjects = Sets.newHashSet();
		Set<String> authorizedGroups = Sets.newHashSet();
		Set<String> authorizedUsers = Sets.newHashSet();
		for (SubjectKeyDTO subjectKeyDTO : authorizedSubjects) {
			switch (subjectKeyDTO.getKeyType()) {
			case GROUP:
				authorizedGroups.add(subjectKeyDTO.getKeyId());
				break;
			case PERSON:
				authorizedUsers.add(subjectKeyDTO.getKeyId());
				break;
			default:
				throw new IllegalArgumentException("SubjectType '" + subjectKeyDTO.getKeyType() + "' not managed !");
			}
		}
		for (SubscriberFormDTO toTest : targets) {
			if (authorizedSubjects.contains(toTest.getSubject().getModelId())) {
				// subjectkey are equals
				filteredSubjects.add(toTest);
			} else {
				// subjectkey are equals so find with "sub queries"
				switch (toTest.getSubject().getModelId().getKeyType()) {
				case GROUP:
					if (externalGroupDao.isGroupMemberOfAtLeastOneGroup(toTest.getSubject().getModelId().getKeyId(),
							authorizedGroups)) {
						filteredSubjects.add(toTest);
					}
					break;
				case PERSON:
					if (externalGroupDao.isUserMemberOfAtLeastOneGroup(toTest.getSubject().getModelId().getKeyId(),
							authorizedGroups)) {
						filteredSubjects.add(toTest);
					}
					break;
				default:
					throw new IllegalArgumentException("SubjectType '" + toTest.getSubject().getModelId().getKeyType()
							+ "' not managed !");
				}
			}
		}
		log.debug("filterSubcribers returned {}, \n comparing to entry {}", filteredSubjects, targets);

		return filteredSubjects;
	}

	private Set<SubscriberFormDTO> filterSubcribersOnDefault(final Set<SubscriberFormDTO> targets,
			final ContextKeyDTO ctx) {
		Assert.isTrue(ContextType.ORGANIZATION.equals(ctx.getKeyType()), "ContextType should be Organization");
		Set<SubscriberFormDTO> filteredSubjects = Sets.newHashSet();
		Optional<Filter> optionalFilterGroup = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(ctx.getKeyId(),
				FilterType.GROUP));
		Filter filterGroup = optionalFilterGroup.orElse(null);
		Optional<Filter> optionalFilterUser = filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(ctx.getKeyId(),
				FilterType.LDAP));
		Filter filterUser = optionalFilterUser.orElse(null);
		for (SubscriberFormDTO subscriberFormDTO : targets) {
			switch (subscriberFormDTO.getSubject().getModelId().getKeyType()) {
			case GROUP:
				if (filterGroup != null
						&& externalGroupDao.isGroupMemberOfGroupFilter(filterGroup.getPattern(), subscriberFormDTO
								.getSubject().getModelId().getKeyId())) {
					filteredSubjects.add(subscriberFormDTO);
				}
				break;
			case PERSON:
				if (filterUser != null
						&& externalUserDao.isUserFoundWithFilter(filterUser.getPattern(), subscriberFormDTO
								.getSubject().getModelId().getKeyId())) {
					filteredSubjects.add(subscriberFormDTO);
				}
				break;
			default:
				throw new IllegalArgumentException("SubjectType '"
						+ subscriberFormDTO.getSubject().getModelId().getKeyType() + "' not managed !");
			}
		}

		log.debug("filterSubcribersOnDefault returned {}, \n comparing to entry {}", filteredSubjects, targets);

		return filteredSubjects;
	}

	private void updateLinkedFilesToItem(final AbstractItem item, final Set<LinkedFileItemDTO> filesLinked) {
		Assert.notNull(item.getId(), "Item id is null");
		Set<LinkedFileItem> old = Sets.newHashSet(linkedFileItemRepository.findByAbstractItemId(item.getId()));
		Set<LinkedFileItem> oldToRemove = Sets.newHashSet();
		Set<String> filesPath = Sets.newHashSet();
		for (LinkedFileItem oldFile : old) {
			final String fileUri = oldFile.getUri();
			filesPath.add(fileUri);
			boolean found = false;
			for (LinkedFileItemDTO fileLinked : filesLinked) {
				if (fileLinked.getUri().equals(fileUri)) {
					found = true;
					break;
				}
			}
			if (!found)
				oldToRemove.add(oldFile);
		}
		if (filesLinked != null && !filesLinked.isEmpty()) {
			Set<LinkedFileItem> linkedFileItems = Sets.newLinkedHashSet();
			for (LinkedFileItemDTO fileLinked : filesLinked) {
				if (!filesPath.contains(fileLinked.getUri())) {
					linkedFileItems.add(new LinkedFileItem(fileLinked.getUri(), fileLinked.getFilename(), item,
							fileLinked.isInBody(), fileLinked.getContentType()));
				}
			}
			linkedFileItemRepository.saveAll(linkedFileItems);
		}

		if (!oldToRemove.isEmpty()) {
			linkedFileItemRepository.deleteAll(oldToRemove);
		}

	}

	public ResponseEntity<?> removeLinkedFileToItem(final AbstractItem item, final String fileUri) {
		if (item.getId() != null && fileUri != null && !fileUri.isEmpty()) {
			log.debug("Try to remove Linked File {} on Item {}", fileUri, item);
			Set<LinkedFileItem> linkedFileItems = Sets.newHashSet(linkedFileItemRepository.findByAbstractItemId(item
					.getId()));
			LinkedFileItem foundfile = null;
			for (LinkedFileItem oldFile : linkedFileItems) {
				if (oldFile.getUri().equals(fileUri)) {
					foundfile = oldFile;
					linkedFileItemRepository.delete(foundfile);
					break;
				}
			}

			if (foundfile != null) {
				log.debug("Linked File found");
				linkedFileItems.remove(foundfile);
				if (item instanceof Attachment && linkedFileItems.isEmpty()) {
					log.debug("the item has no more Linked File, so we set the item state to DRAFT");
					item.setStatus(ItemStatus.DRAFT);
					itemRepository.save(item);
				}
				return ResponseEntity.ok(new ValueResource(item.getStatus()));
			} else {
				log.warn("The file {} wasn't found or isn't linked to the item specified {}", fileUri, item);
			}
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
		Optional<AbstractItem> optionalAbstractItem = itemRepository.findById(id);
        optionalAbstractItem.ifPresent(item -> fileService.deleteInternalResource(item.getEnclosure()));
        final Iterable<Subscriber> subscribersToDel = subscriberRepository.findAll(SubscriberPredicates
				.onCtx(new ContextKey(id, ContextType.ITEM)));
		subscriberRepository.deleteAll(subscribersToDel);
		final Iterable<ItemClassificationOrder> classificationsLinksToDel = itemClassificationOrderRepository
				.findAll(ItemPredicates.itemsClassOfItem(id));
		itemClassificationOrderRepository.deleteAll(classificationsLinksToDel);
		final Iterable<LinkedFileItem> filesToDelete = linkedFileItemRepository.findByAbstractItemId(id);
		linkedFileItemRepository.deleteAll(filesToDelete);
		for (LinkedFileItem lFile : filesToDelete) {
			fileService.deletePrivateResource(lFile.getUri());
		}
		itemRepository.deleteById(id);
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
		for (AbstractItem item : items) {
			deleteContent(item.getId());
		}
	}
}
