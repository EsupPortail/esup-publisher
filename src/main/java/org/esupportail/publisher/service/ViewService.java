package org.esupportail.publisher.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.UserDetailsService;
import org.esupportail.publisher.service.exceptions.CustomAccessDeniedException;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@Transactional
public class ViewService {

    @Inject
    private ItemRepository<AbstractItem> itemRepository;
    @Inject
    private SubscriberRepository subscriberRepository;
    @Inject
    private ExternalUserHelper externalUserHelper;
    @Inject
    private UserDetailsService userDetailsService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private FileService fileService;
    @Inject
    private LinkedFileItemRepository linkedFileItemRepository;
    @Inject
    private IPermissionService permissionService;

    public Object itemView(Long itemId, HttpServletRequest request) {

        log.debug("Request to render in viewer, item with id {}", itemId);
        if (itemId == null) throw new IllegalArgumentException("No item identifier was provided to the request!");
        Optional<AbstractItem> optionnalItem = itemRepository.findOne(
            ItemPredicates.ItemWithStatus(itemId, ItemStatus.PUBLISHED, null));

        AbstractItem item = optionnalItem.orElse(null);
        if (item == null) {
            throw new CustomAccessDeniedException("403");
        }
        log.debug("Item found {}", item);


        try {
            if (!canView(item)) {
                throw new CustomAccessDeniedException("403");
            }
        } catch (AccessDeniedException ade) {
            throw new AccessDeniedException(ade.getMessage());
        }

        final String baseUrl = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";

        // looking to replace img src with path of object with body attribute of for specific property
        if (item instanceof News) {
            ((News) item).setBody(replaceBodyUrl(((News) item).getBody()));
            return item;
        } else if (item instanceof Flash) {
            ((Flash) item).setBody(replaceBodyUrl(((Flash) item).getBody()));
        } else if (item instanceof Resource) {
            ((Resource) item).setRessourceUrl(replaceRelativeUrl(((Resource) item).getRessourceUrl(), baseUrl));
        } else if (!(item instanceof Media) && !(item instanceof Attachment)) {
            log.error("Warning a new type of Item wasn't managed at this place, the item is : {}", item);
            throw new IllegalStateException("Warning missing type management of :" + item);
        }
        return item;
    }

    private boolean canView(final AbstractItem item) throws AccessDeniedException {
        // when RssAllowed is set then the content published is public
        if (item.isRssAllowed()) return true;
        List<Subscriber> subscribers = Lists.newArrayList(
            subscriberRepository.findAll(SubscriberPredicates.onCtx(item.getContextKey())));
        // TODO we consider that all items have targets directly on
        // for targets defined only on classification a check will be needed
        if (subscribers.isEmpty()) {
            log.trace("Subscribers on item {} are empty -> true", item.getContextKey());
            return true;
        }
        final CustomUserDetails user = this.userDetailsService.loadUserByUsername(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        if (user == null) {
            log.trace("user is not authenticated -> throw an error to redirect on authentication");
            throw new AccessDeniedException("Access is denied to anonymous !");
        } else if (user.getRoles().contains(AuthoritiesConstants.ADMIN) || user.getUser().getLogin().equalsIgnoreCase(
            item.getCreatedBy().getLogin()) || user.getUser().getLogin().equalsIgnoreCase(
            item.getLastModifiedBy().getLogin())) {
            return true;
        }

        final UserDTO userDTO = user.getUser();
        if (userDTO != null) {
            List<String> groups = null;
            if (userDTO.getAttributes() != null) {
                groups = userDTO.getAttributes().get(externalUserHelper.getUserGroupAttribute());
            }
            for (Subscriber subscriber : subscribers) {
                log.trace("Check if {} is in {}", userDTO, subscriber);
                final SubjectKeyExtended subject = subscriber.getSubjectCtxId().getSubject();
                switch (subject.getKeyType()) {
                    case GROUP:
                        if (groups == null || groups.isEmpty()) {
                            log.trace("The user doesn't have a group -> break loop");
                            break;
                        }
                        // test on startWith as some groups in IsMemberOf has only a part the
                        // real group name.
                        for (String val : groups) {
                            if (val.startsWith(subject.getKeyValue())) {
                                log.trace("Check if the user group {} match subscriber group {} -> return true", val,
                                    subject.getKeyValue());
                                return true;
                            }
                        }
                        break;
                    case PERSON:
                        if (subject.getKeyValue().equalsIgnoreCase(userDTO.getLogin())) {
                            log.trace("Check if the user key {} match subscriber key {} -> true", userDTO.getLogin(),
                                subject.getKeyValue());
                            return true;
                        }
                        break;
                    case PERSON_ATTR:
                        if (subject.getKeyAttribute() != null && userDTO.getAttributes().containsKey(
                            subject.getKeyAttribute()) && userDTO.getAttributes().get(
                            subject.getKeyAttribute()).contains(subject.getKeyValue())) {
                            log.trace("Check if the user attribute {} with values {} contains value {} -> true",
                                subject.getKeyAttribute(), userDTO.getAttributes().get(subject.getKeyAttribute()),
                                subject.getKeyValue());
                            return true;
                        }
                        break;
                    case PERSON_ATTR_REGEX:
                        if (subject.getKeyAttribute() != null && userDTO.getAttributes().containsKey(
                            subject.getKeyAttribute())) {
                            for (final String value : userDTO.getAttributes().get(subject.getKeyAttribute())) {
                                if (value.matches(subject.getKeyValue())) {
                                    log.trace("Check if the user attribute {} with values {} match regex {} -> true",
                                        subject.getKeyAttribute(),
                                        userDTO.getAttributes().get(subject.getKeyAttribute()), subject.getKeyValue());
                                    return true;
                                }
                            }
                        }
                        break;
                    default:
                        throw new IllegalStateException(
                            "Warning Subject Type '" + subject.getKeyType() + "' is not managed");
                }
            }
        }
        log.trace("End of all checks -> false");
        return false;
    }

    private String replaceRelativeUrl(final String localUrl, final String baseUrl) {
        if (localUrl != null && !localUrl.trim().isEmpty() && !localUrl.matches("^https?://.*$")) {
            return baseUrl + localUrl;
        }
        return localUrl;
    }

    private String replaceBodyUrl(final String body) {
        if (body != null && !body.trim().isEmpty()) {
            return body.replaceAll("view/file/", "files/");
        }
        return body;
    }
}
