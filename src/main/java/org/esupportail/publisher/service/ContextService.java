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

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.*;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.OrganizationPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @author GIP RECIA - Julien Gribonvald
 */
@Service
@Slf4j
@Transactional(readOnly=true)
public class ContextService {

    //@Inject
    //private UserContextTree contextTree;
    @Inject
    private IPermissionService permissionService;

    @Inject
    private TreeJSDTOFactory treeJSDTOFactory;

    @Inject
    private OrganizationRepository organizationRepository;
    @Inject
    private PublisherRepository publisherRepository;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private FeedRepository<AbstractFeed> feedRepository;
    @Inject
    private ItemRepository<AbstractItem> itemRepository;

    public List<TreeJS> getTreeChilds(@NotNull ContextKey ctx, PermissionType minPerm, Boolean filter) {
        List<? extends IContext> list = getContextChilds(ctx,minPerm, filter);
        if (list != null)
            return treeJSDTOFactory.asDTOList(list, minPerm);
        return null;
    }

    public List<TreeJS> getRootTree(PermissionType minPerm) {
        List<? extends IContext> list = getRootContexts(minPerm);
        if (list != null)
            return treeJSDTOFactory.asDTOList(list, minPerm);
        return null;
    }

    public ContextKey getOrganizationCtxOfCtx(@NotNull ContextKey ctxKey){
        if (ctxKey.getKeyType() != null && ctxKey.getKeyId() != null) {
            switch (ctxKey.getKeyType()) {
                case ORGANIZATION :
                    return ctxKey;
                case PUBLISHER :
                    return publisherRepository.findById(ctxKey.getKeyId()).orElseThrow().getContext().getOrganization().getContextKey();
                case CATEGORY :
                    return categoryRepository.findById(ctxKey.getKeyId()).orElseThrow().getPublisher().getContext().getOrganization().getContextKey();
                case FEED :
                    return feedRepository.findById(ctxKey.getKeyId()).orElseThrow().getPublisher().getContext().getOrganization().getContextKey();
                case ITEM :
                    return itemRepository.findById(ctxKey.getKeyId()).orElseThrow().getOrganization().getContextKey();
                default: throw new IllegalArgumentException("The context Type " + ctxKey.getKeyType() + " is not managed") ;
            }
        }
        return null;
    }

    public boolean isLinkedPublisherHasSubPermManagement(@NotNull final ContextKey fromCtx) {
        switch (fromCtx.getKeyType()) {
            case ORGANIZATION: return true;
            case PUBLISHER:
                final Publisher pub = publisherRepository.findById(fromCtx.getKeyId()).orElse(null);
                return pub != null && pub.isHasSubPermsManagement();
            case CATEGORY:
                final Category cat = categoryRepository.findById(fromCtx.getKeyId()).orElse(null);
                return cat != null && cat.getPublisher() != null && cat.getPublisher().isHasSubPermsManagement();
            case FEED:
                final AbstractFeed feed = feedRepository.findById(fromCtx.getKeyId()).orElse(null);
                return feed != null && feed.getPublisher() != null && feed.getPublisher().isHasSubPermsManagement();
            case ITEM : return false; // we can't know if permission management is done as several publisher can be set to an item
            default:
                throw new IllegalArgumentException("The context Type " + fromCtx.getKeyType() + " is not managed") ;
        }
    }

    private List<? extends IContext> getRootContexts(PermissionType minPerm) {
        Predicate filter = permissionService.filterAuthorizedAllOfContextType(SecurityContextHolder.getContext().getAuthentication(),
            ContextType.ORGANIZATION, minPerm, OrganizationPredicates.notNull());
        return Lists.newArrayList(organizationRepository.findAll(filter, OrganizationPredicates.orderBy()));
    }

    private List<? extends IContext> getContextChilds(@NotNull ContextKey ctx, PermissionType minPerm, Boolean filterItem) {
        Predicate filter;
        DisplayOrderType displayOrder;
        BooleanBuilder builder = new BooleanBuilder();
        switch (ctx.getKeyType()) {
            case ORGANIZATION :
                filter = permissionService.filterAuthorizedChildsOfContext(SecurityContextHolder.getContext().getAuthentication(),
                    ctx, minPerm, PublisherPredicates.AllOfOrganization(ctx.getKeyId()));
                builder.and(filter);
                return Lists.newArrayList(publisherRepository.findAll(builder, PublisherPredicates.orderByDisplayOrder()));
            case PUBLISHER :
                displayOrder = publisherRepository.findById(ctx.getKeyId()).orElseThrow().getDefaultDisplayOrder();
                filter = permissionService.filterAuthorizedChildsOfContext(SecurityContextHolder.getContext().getAuthentication(),
                    ctx, minPerm, ClassificationPredicates.CategoryOfPublisher(ctx.getKeyId()));
                builder = new BooleanBuilder(filter);
                // Only Super Admins can manage auto associated classifications
                List<Category> myList = Lists.newArrayList(categoryRepository.findAll(builder, ClassificationPredicates.categoryOrderByDisplayOrderType(displayOrder)));
                if(permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), ctx).getMask() < PermissionType.ADMIN.getMask()) {
                    // A loop is needed due to hibernate bug, NPE on publisher.context.reader.authorizedTypes.contains(ItemType.FLASH)
                    //builder.andNot(QCategory.category.publisher.context.isNull());
                    // other cat properties can't be null
                    myList.removeIf(cat -> !cat.getPublisher().getContext().getReader().getAuthorizedTypes().isEmpty()
                            && cat.getPublisher().getContext().getReader().getAuthorizedTypes().contains(ItemType.FLASH));
                }
                return myList;
            case CATEGORY :
            	Optional<Category> optionalCategory = categoryRepository.findById(ctx.getKeyId());
                Category category = optionalCategory.orElseThrow();
                displayOrder = category.getDefaultDisplayOrder();
                if (category.getPublisher().getContext().getRedactor().getNbLevelsOfClassification() > 1) {
                    filter = permissionService.filterAuthorizedChildsOfContext(SecurityContextHolder.getContext().getAuthentication(),
                        ctx, minPerm, ClassificationPredicates.AbstractFeedsOfCategory(category));
                    builder.and(filter);
                    return Lists.newArrayList(feedRepository.findAll(builder, ClassificationPredicates.feedOrderByDisplayOrderType(displayOrder)));
                } else {
                    /** Décision on ne fournit pas les items
                    filter = permissionService.filterAuthorizedChildsOfContext(SecurityContextHolder.getContext().getAuthentication(),
                        ctx, minPerm, ItemPredicates.itemsClassOfClassification(category));
                    builder.and(filter);
                    if (filterItem != null && filterItem) {
                        builder.and(QItemClassificationOrder.itemClassificationOrder.itemClassificationId.abstractItem.status.in(ItemStatus.PUBLISHED, ItemStatus.PENDING));
                    }
                    List<ItemClassificationOrder> itemsClass =
                        Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(displayOrder)));
                    List<AbstractItem> items = Lists.newArrayList();
                    for (ItemClassificationOrder item : itemsClass) {
                        items.add(item.getItemClassificationId().getAbstractItem());
                    }
                    return items;**/
                    return Lists.newArrayList();

                }
            case FEED :
                /** Décision on ne fournit pas les items
                displayOrder = feedRepository.findOne(ctx.getKeyId()).getDefaultDisplayOrder();
                filter = permissionService.filterAuthorizedChildsOfContext(SecurityContextHolder.getContext().getAuthentication(),
                    ctx, minPerm, ItemPredicates.itemsClassOfClassification(ctx.getKeyId()));
                builder.and(filter);
                List<ItemClassificationOrder> itemsClass = Lists.newArrayList(itemClassificationOrderRepository.findAll(builder, ItemPredicates.orderByClassifDefinition(displayOrder)));
                List<AbstractItem> items = Lists.newArrayList();
                for (ItemClassificationOrder item : itemsClass) {
                    items.add(item.getItemClassificationId().getAbstractItem());
                }
                return items;
                 **/
                return Lists.newArrayList();
            default : throw new IllegalArgumentException("The context Type " + ctx.getKeyType() + " is not managed") ;
        }
    }

}
