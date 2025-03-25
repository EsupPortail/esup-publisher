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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Resource;
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
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.service.exceptions.CustomAccessDeniedException;
import org.esupportail.publisher.service.factories.SubscriberDTOFactory;
import org.esupportail.publisher.web.rest.dto.SubjectKeyExtendedDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

//TODO This class is a copy of ViewController and didn't factorise common code !!!
@Slf4j
@Service
public class ViewService {

    @Inject
    private ItemRepository<AbstractItem> itemRepository;
    @Inject
    private SubscriberRepository subscriberRepository;
    @Inject
    private ExternalUserHelper externalUserHelper;
    @Inject
    private UserRepository userRepository;
    @Inject
    private FileService fileService;
    @Inject
    private LinkedFileItemRepository linkedFileItemRepository;
    @Inject
    private IPermissionService permissionService;
    @Inject
    private SubscriberDTOFactory subscriberDTOFactory;

    public AbstractItem itemView(Long itemId, HttpServletRequest request) {
        log.debug("Request to render in viewer, item with id {}", itemId);
        if (itemId == null) throw new IllegalArgumentException("No item identifier was provided to the request!");
        Optional<AbstractItem> optionnalItem = itemRepository.findOne(
            ItemPredicates.ItemWithStatus(itemId, ItemStatus.PUBLISHED, null));

        AbstractItem item = optionnalItem.orElse(null);
        if (item == null) {
            throw new CustomAccessDeniedException("403");
        }
        log.debug("Item found {}", item);

        // TODO rethink exception management ! There should be a common object that send error, use it ! a customAccessDeniedException isn't need.
        try {
            if (!isSubscriber(item)) {
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

    public boolean isSubscriber(final AbstractItem item) throws AccessDeniedException {
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
        final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
        if (user == null) {
            log.trace("user is not authenticated -> throw an error to redirect on authentication");
            throw new AccessDeniedException("Access is denied to anonymous !");
        } else if (user.getRoles().contains(AuthoritiesConstants.ADMIN) || user.getUser().getLogin().equalsIgnoreCase(
            item.getCreatedBy().getLogin()) || user.getUser().getLogin().equalsIgnoreCase(
            item.getLastModifiedBy().getLogin())) {
            return true;
        }

        final UserDTO userDTO = user.getUser();
        if (userDTO != null && isSubscriber(userDTO, subscribers)) return true;
        log.trace("End of all checks -> false");
        return false;
    }

    public boolean isSubscriber(@NotNull UserDTO userDTO, @NotNull List<Subscriber> subscribers) {
        return this.evalSubscribing(userDTO, subscriberDTOFactory.asDTOList(subscribers));
    }

    public boolean evalSubscribing(@NotNull UserDTO userDTO, @NotNull List<SubscriberDTO> subscribersDto) {
        List<String> groups = null;
        if (userDTO.getAttributes() != null) {
            groups = userDTO.getAttributes().get(externalUserHelper.getUserGroupAttribute());
        }
        for (SubscriberDTO subscriber : subscribersDto) {
            log.trace("Check if {} is in {}", userDTO, subscriber);
            final SubjectKeyExtendedDTO subject = subscriber.getModelId().getSubjectKey();
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
