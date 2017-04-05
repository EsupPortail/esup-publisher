package org.esupportail.publisher.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.config.SecurityConfiguration;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jgribonvald on 22/02/17.
 */
@Controller
@Slf4j
public class ViewController {

    @Inject
    //private ItemClassificationOrderRepository itemClassificationOrderRepository;
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private SubscriberRepository subscriberRepository;

    @Inject
    private ExternalUserHelper externalUserHelper;

    private static final String REDIRECT_PARAM = "local-back-to";

    private static final String ITEM_VIEW = "/view/item/";

    @RequestMapping(value = SecurityConfiguration.PROTECTED_PATH, produces = MediaType.TEXT_HTML_VALUE )
    public String itemView(HttpServletRequest request) {
        try {
            final String redirect = "redirect:" + getSecurePathRedirect(request);
            log.debug("Redirecting to {}", redirect);
            return redirect;
        } catch (AccessDeniedException ade) {
            return "403";
        }
    }

    @RequestMapping(value = ITEM_VIEW + "{item_id}", produces = MediaType.TEXT_HTML_VALUE )
    public String itemView(Map<String, Object> model, @PathVariable("item_id") Long itemId, HttpServletRequest request) {
        log.debug("Request to render in viewer, item with id {}", itemId);
        if (itemId == null) throw new IllegalArgumentException("No item identifier was provided to the request!");

        final AbstractItem item = itemRepository.findOne(ItemPredicates.ItemWithStatus(itemId, ItemStatus.PUBLISHED));
        log.debug("Item found {}", item);
        if (item == null) return "objectNotFound";

        try {
            if (!canView(item.getContextKey())) {
                return "403";
            }
        } catch (AccessDeniedException ade) {
            log.trace("Redirect to establish authentication !");
            return "redirect:" + SecurityConfiguration.PROTECTED_PATH + "?" + REDIRECT_PARAM + "=" + ITEM_VIEW + itemId;
        }

        final String baseUrl = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";

        // all items has an enclosure but optional
        item.setEnclosure(replaceRelativeUrl(item.getEnclosure(), baseUrl));
        // looking to replace img src with path of object with body attribute of for specific property
        if (item instanceof News) {
            ((News) item).setBody(replaceBodyUrl(((News) item).getBody(), baseUrl));
        } else if (item instanceof Flash) {
            ((Flash) item).setBody(replaceBodyUrl(((Flash) item).getBody(), baseUrl));
        } else if (item instanceof Resource) {
            ((Resource)item).setRessourceUrl(replaceRelativeUrl(((Resource)item).getRessourceUrl(), baseUrl));
        } else if (!(item instanceof Media)) {
            log.error("Warning a new type of Item wasn't managed at this place, the item is :", item);
            throw new IllegalStateException("Warning missing type management of :" + item.toString());
        }
        model.put("item", item);

        return "item";
    }

    private boolean canView(final ContextKey ctx) throws AccessDeniedException {
        List<Subscriber> subscribers = Lists.newArrayList(subscriberRepository.findAll(SubscriberPredicates.onCtx(ctx)));
        // TODO we consider that all items have targets directly on
        // for targets defined only on classification a check will be needed
        if (subscribers.isEmpty()) {
            log.trace("Subscribers on item {} are empty -> true", ctx);
            return true;
        }
        final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
        if (user == null) {
            log.trace("user is not authenticated -> throw an error to redirect on authentication");
            throw new AccessDeniedException("Access is denied to anonymous !");
        } else {
            final UserDTO userDTO = user.getUser();
            List<String> groups = null;
            if (userDTO != null && userDTO.getAttributes() != null) {
                groups = userDTO.getAttributes().get(externalUserHelper.getUserGroupAttribute());
            }
            for (Subscriber subscriber: subscribers) {
                log.trace("Check if {} is in {}", userDTO, subscriber);
                final SubjectKey subject = subscriber.getSubjectCtxId().getSubject();
                switch (subject.getKeyType()) {
                    case GROUP:
                        if (groups == null || groups.isEmpty()) {
                            log.trace("The user doesn't have a group -> break loop");
                            break;
                        }
                        // test on startWith as some groups in IsMemberOf has only a part the
                        // real group name.
                        for (String val : groups) {
                            if (val.startsWith(subject.getKeyId())) {
                                log.trace("Check if the user group {} match subscriber group {} -> return true", val, subject.getKeyId());
                                return true;
                            }
                        }
                        break;
                    case PERSON:
                        if (subject.getKeyId().equalsIgnoreCase(userDTO.getLogin())) {
                            log.trace("Cjeck if the user key {} match subscriber key {} -> true", userDTO.getLogin(), subject.getKeyId());
                            return true;
                        }
                        break;
                    default: throw new IllegalStateException("Warning Subject Type '" + subject.getKeyType() + "' is not managed");
                }
            }
        }
        log.trace("End of all checks -> false");
        return false;
    }

    @ModelAttribute("version")
    public String getVersion() throws IOException {
        final Properties properties = new Properties();
        properties.load(Application.class.getResourceAsStream("/version.properties"));
        final String version = properties.getProperty("version");
        return version;
    }

    private String replaceBodyUrl(final String body, final String baseUrl){
        if (body != null && !body.trim().isEmpty()){
            return body.replaceAll("src=\"files/", "src=\"" + baseUrl + "files/");
        }
        return body;
    }

    private String replaceRelativeUrl(final String localUrl, final String baseUrl){
        if (localUrl != null && !localUrl.trim().isEmpty() && !localUrl.matches("^https?://.*$")){
            return baseUrl + localUrl;
        }
        return localUrl;
    }

    private String getSecurePathRedirect(final HttpServletRequest request)  throws AccessDeniedException {
        String path = request.getQueryString();
        final String param = REDIRECT_PARAM + "=";
        if ( path.startsWith(param) ) {
            path = request.getQueryString().substring(param.length());
            if (path.startsWith(ITEM_VIEW)) {
                return path;
            }
        }
        log.warn("The security was tested with a wrong request query '{}'", request.getQueryString());
        throw new AccessDeniedException("The security was tested with a wrong request query '" + request.getQueryString());
    }
}
