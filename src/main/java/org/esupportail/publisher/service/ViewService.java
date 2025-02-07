package org.esupportail.publisher.service;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.publisher.config.SecurityConfiguration;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.ReadingIndincatorRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.security.UserDetailsService;
import org.esupportail.publisher.service.exceptions.CustomAccessDeniedException;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@Transactional
public class ViewService {

    public static final String ITEM_VIEW = "/view/item/";
    public static final String FILE_VIEW = "/view/file/";
    private static final String REDIRECT_PARAM = "local-back-to";
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
    @Inject
    private ReadingIndincatorRepository readingIndincatorRepository;

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
            ((News) item).setBody(replaceBodyUrl(((News) item).getBody(), baseUrl));

            return item;
        } else if (item instanceof Flash) {
            ((Flash) item).setBody(replaceBodyUrl(((Flash) item).getBody(), baseUrl));
        } else if (item instanceof Resource) {
            ((Resource) item).setRessourceUrl(replaceRelativeUrl(((Resource) item).getRessourceUrl(), baseUrl));
        } else if (!(item instanceof Media) && !(item instanceof Attachment)) {
            log.error("Warning a new type of Item wasn't managed at this place, the item is :", item);
            throw new IllegalStateException("Warning missing type management of :" + item);
        }

        return item;
    }

    public Set<LinkedFileItem> getItemAttachements(Long itemId, HttpServletRequest request) {

        log.debug("Request to render in viewer, item with id {}", itemId);
        if (itemId == null) throw new IllegalArgumentException("No item identifier was provided to the request!");
        Optional<AbstractItem> optionnalItem = itemRepository.findOne(
            ItemPredicates.ItemWithStatus(itemId, ItemStatus.PUBLISHED, null));

        AbstractItem item = optionnalItem.orElse(null);
        if (item == null) {
            throw new CustomAccessDeniedException("403");
        }
        log.debug("Item found {}", item);

        Set<LinkedFileItem> attachments = Sets.newHashSet(
            linkedFileItemRepository.findByAbstractItemIdAndInBody(item.getId(), false));

        return attachments;
    }

    public void downloadFile(final HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        final String query = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        log.debug("Entering getting file with query {}", query);
        String filePath = query;
        if (filePath == null || filePath.isEmpty()) throw new FileNotFoundException(filePath);
        if (filePath.startsWith("/")) filePath = filePath.substring(1);
        log.debug("Looking for file in database {}", filePath);
        List<LinkedFileItem> itemsFiles = Lists.newArrayList(linkedFileItemRepository.findByUri(filePath));
        if (itemsFiles.isEmpty()) {
            log.warn("Try to download a file that doesn't exist into the fileSystem", filePath);
            throw new FileNotFoundException(filePath);
        }
        boolean canView = false;
        String filename = null;
        for (LinkedFileItem lfiles : itemsFiles) {
            Optional<AbstractItem> optionalItem = itemRepository.findOne(
                ItemPredicates.ItemWithStatus(lfiles.getItemId(), null, null));
            AbstractItem item = optionalItem.orElse(null);
            // a user can see only when published expect if he can Edit the content
            if (item != null && item.getStatus().equals(ItemStatus.PUBLISHED)) {
                try {
                    if (canView(item)) {
                        canView = true;
                        filename = lfiles.getFilename();
                        break;
                    }
                } catch (AccessDeniedException ade) {
                    log.trace("Redirect to establish authentication !");
                    response.sendRedirect(
                        request.getContextPath() + SecurityConfiguration.PROTECTED_PATH + "?" + REDIRECT_PARAM + "=" + query);
                    return;
                }
            } else if (item != null) {
                if (SecurityUtils.getCurrentUserDetails() == null) {
                    log.trace("Redirect to establish authentication !");
                    response.sendRedirect(
                        request.getContextPath() + SecurityConfiguration.PROTECTED_PATH + "?" + REDIRECT_PARAM + "=" + query);
                    return;
                }

                if (permissionService.canEditCtx(SecurityContextHolder.getContext().getAuthentication(),
                    item.getContextKey())) {
                    canView = true;
                    filename = lfiles.getFilename();
                    break;
                }
            }
        }

        if (!canView) throw new AccessDeniedException("Impossible to get file '" + filePath + "'");

        if (query.startsWith(FILE_VIEW)) {
            filePath = query.substring(FILE_VIEW.length());
        }

        Path file = Paths.get(fileService.getProtectedFileUploadHelper().getUploadDirectoryPath(), filePath);
        if (filename == null || filename.isEmpty()) filename = file.getFileName().toString();
        log.debug("Retrieving file {} in path {}, contentType {}", filename, file, Files.probeContentType(file));
        if (Files.exists(file) && !Files.isDirectory(file)) {
            response.setContentType(Files.probeContentType(file));
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.addHeader("Content-Disposition", "attachement; filename=\"" + filename + "\"");
            response.setContentLengthLong(Files.size(file));

            // copy it to response's OutputStream
            try {
                Files.copy(file, response.getOutputStream());
                response.flushBuffer();
            } catch (FileNotFoundException fnfe) {
                log.warn("Try to download a file that doesn't exist into the fileSystem", fnfe);
                throw new FileNotFoundException(filePath);
            } catch (IOException ex) {
                log.info("Error writing file to output stream. Filename was '{}'", filePath, ex);
                throw ex;
            }
        } else throw new FileNotFoundException(filePath);
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

    private String replaceBodyUrl(final String body, final String baseUrl) {
        System.out.println();
        System.out.println(body);
        System.out.println(baseUrl);
        System.out.println();
        if (body != null && !body.trim().isEmpty()) {
            String fileview = FILE_VIEW;
            if (fileview.startsWith("/")) fileview = fileview.substring(1);
            return body.replaceAll("src=\"files/", "src=\"" + baseUrl + "files/").replaceAll("view/file/", "files/");
        }
        return body;
    }
}
