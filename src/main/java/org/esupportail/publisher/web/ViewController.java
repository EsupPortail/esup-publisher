package org.esupportail.publisher.web;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jgribonvald on 22/02/17.
 */
@Controller
@RequestMapping("/view")
@Slf4j
public class ViewController {

    @Inject
    //private ItemClassificationOrderRepository itemClassificationOrderRepository;
    private ItemRepository<AbstractItem> itemRepository;

    @Inject
    private ServiceUrlHelper urlHelper;

    @RequestMapping(value = "/item/{item_id}", produces = MediaType.TEXT_HTML_VALUE )
    public String itemView(Map<String, Object> model, @PathVariable("item_id") Long itemId, HttpServletRequest request) {
        log.debug("Request to render in viewer, item with id {}", itemId);
        if (itemId == null) return "itemError";
        final AbstractItem item = itemRepository.findOne(ItemPredicates.ItemWithStatus(itemId, ItemStatus.PUBLISHED));
        log.debug("Item found {}", item);
        if (item == null) return "itemNotFound";

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
}
