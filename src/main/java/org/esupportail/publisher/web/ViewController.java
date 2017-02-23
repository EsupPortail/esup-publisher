package org.esupportail.publisher.web;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.predicates.ItemPredicates;
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

    @RequestMapping(value = "/item/{item_id}", produces = MediaType.TEXT_HTML_VALUE )
    public String itemView(Map<String, Object> model, @PathVariable("item_id") Long itemId){
        if (itemId == null) return "itemError";
        final AbstractItem item = itemRepository.findOne(ItemPredicates.ItemWithStatus(itemId, ItemStatus.PUBLISHED));
        log.debug("item found {}", item);
        if (item == null) return "itemNotFound";
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
}
