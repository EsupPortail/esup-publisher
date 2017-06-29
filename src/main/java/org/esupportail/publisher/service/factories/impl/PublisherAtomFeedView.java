package org.esupportail.publisher.service.factories.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;
import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Generator;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;
import lombok.Setter;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ItemClassificationOrder;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

/**
 * Created by jgribonvald on 28/06/17.
 */
@Component("publisherAtomFeedView")
public class PublisherAtomFeedView extends AbstractAtomFeedView {

    public final static String ORG_PARAM = "organization";
    public final static String PUB_PARAM = "publisher";
    public final static String CLASSIF_PARAM = "classification";
    public final static String ITEMS_PARAM = "items";

    @Setter
    private String channelGenerator = "Publisher";

    @Autowired
    private ServiceUrlHelper urlHelper;

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed,
                                     HttpServletRequest request) {
        final Object orgObj = model.get(ORG_PARAM);
        final Object pubObj = model.get(PUB_PARAM);
        final Object clasObj = model.get(CLASSIF_PARAM);
        if (orgObj != null && orgObj instanceof Organization) {
            Organization org = (Organization) model.get(ORG_PARAM);
            feed.setTitle("Contenus publiés par l'établissement : " + org.getDisplayName());
            Link link = new Link();
            link.setHref(urlHelper.getRootDomainUrl(request) + request.getRequestURI() + "?" + request.getQueryString());
            link.setRel("self");
            feed.setAlternateLinks(Lists.newArrayList(link));
            feed.setUpdated(new Date());
            Generator generator = new Generator();
            generator.setValue(channelGenerator);
            generator.setUrl(urlHelper.getRootAppUrl(request));
            feed.setGenerator(generator);
            if (clasObj != null && clasObj instanceof AbstractClassification) {
                Content content = new Content();
                content.setValue("Contenus limités à la rubrique " + ((AbstractClassification) clasObj).getDisplayName() +
                    ": " + ((AbstractClassification) clasObj).getDescription());
                feed.setSubtitle(content);
                feed.setLanguage(((AbstractClassification) clasObj).getLang());
                if (((AbstractClassification) clasObj).getIconUrl() != null) {
                    if (((AbstractClassification) clasObj).getIconUrl().matches("^https?://.*$")) {
                        feed.setIcon(((AbstractClassification) clasObj).getIconUrl());
                    } else {
                        feed.setIcon(urlHelper.getRootAppUrl(request) + ((AbstractClassification) clasObj).getIconUrl());
                    }

                }
            } else if (pubObj != null && pubObj instanceof Publisher) {
                Content content = new Content();
                content.setValue("Contenus limités au contexte de publication : " + ((Publisher) pubObj).getDisplayName());
                feed.setSubtitle(content);
            } else {
                Content content = new Content();
                content.setValue("Flux RSS de l'outil de publication de contenus de l'ENT à partir des Actualités, informations " +
                    "et autres types de contenus publiés par l'établissement " + org.getDisplayName());
                feed.setSubtitle(content);
            }
        }
    }

    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Entry> items = new ArrayList<>();

        final Object itemsObj = model.get(ITEMS_PARAM);
        if (itemsObj instanceof List) {
            Map<Long, Pair<AbstractItem, List<AbstractClassification>>> itemsMap = Maps.newLinkedHashMap();
            for(Object itemObj: (List<?>)itemsObj){
                if (itemObj instanceof ItemClassificationOrder) {
                    // get unique items associated to all his classifs
                    final AbstractClassification classif = ((ItemClassificationOrder)itemObj).getItemClassificationId().getAbstractClassification();
                    //categories.add(classif);
                    final Long itemId = ((ItemClassificationOrder)itemObj).getItemClassificationId().getAbstractItem().getId();
                    if (!itemsMap.containsKey(itemId)) {
                        itemsMap.put(itemId, new Pair<AbstractItem, List<AbstractClassification>>(((ItemClassificationOrder)itemObj).getItemClassificationId().getAbstractItem(), Lists.newArrayList(classif)));
                    } else {
                        itemsMap.get(itemId).getSecond().add(classif);
                    }
                }
            }

            for (Map.Entry<Long, Pair<AbstractItem, List<AbstractClassification>>> entry : itemsMap.entrySet()) {
                final AbstractItem publication = entry.getValue().getFirst();
                Entry item = new Entry();
                item.setTitle(publication.getTitle());
                Link link = new Link();
                link.setHref(urlHelper.getRootDomainUrl(request) + urlHelper.getContextPath() + urlHelper.getItemUri() + publication.getId());
                item.setAlternateLinks(Lists.newArrayList(link));
                Content content = new Content();
                content.setValue(publication.getSummary());
                item.setSummary(content);
                if (publication.getCreatedBy() != null) {
                    SyndPerson author = new SyndPersonImpl();
                    author.setName(publication.getCreatedBy().getDisplayName());
                    author.setEmail(publication.getCreatedBy().getEmail());
                    item.setAuthors(Lists.newArrayList(author));
                }
                if (publication.getLastModifiedBy() != null && !publication.getLastModifiedBy().equals(publication.getCreatedBy())) {
                    SyndPerson contributor = new SyndPersonImpl();
                    contributor.setName(publication.getLastModifiedBy().getDisplayName());
                    contributor.setEmail(publication.getLastModifiedBy().getEmail());
                    item.setContributors(Lists.newArrayList(contributor));
                }
                if (publication.getStartDate() != null)
                    item.setPublished(publication.getStartDate().toDate());
                if (publication.getCreatedDate() != null)
                    item.setCreated(publication.getCreatedDate().toDate());
                if (publication.getLastModifiedDate() != null)
                    item.setModified(publication.getLastModifiedDate().toDate());
                if (publication.getValidatedDate() != null)
                    item.setIssued(publication.getValidatedDate().toDate());
                if (publication.getId() != null) {
                    item.setId(publication.getId().toString());
                }
                List<Category> cats = Lists.newArrayList();
                for (AbstractClassification classif: entry.getValue().getSecond()) {
                    Category cat = new Category();
                    cat.setLabel(classif.getDisplayName());
                    cats.add(cat);
                }
                item.setCategories(cats);
                if (publication.getEnclosure() != null) {
                    Link enclosure = new Link();
                    enclosure.setRel("enclosure");
                    if (publication.getEnclosure().matches("^https?://.*$")) {
                        enclosure.setHref(publication.getEnclosure());
                    } else {
                        enclosure.setHref(urlHelper.getRootAppUrl(request) + publication.getEnclosure());
                    }
                    item.setOtherLinks(Lists.newArrayList(enclosure));
                }
                items.add(item);
            }
        }


        return items;
    }
}
