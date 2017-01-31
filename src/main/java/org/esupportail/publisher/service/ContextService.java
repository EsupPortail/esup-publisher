package org.esupportail.publisher.service;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.FeedRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.repository.predicates.OrganizationPredicates;
import org.esupportail.publisher.repository.predicates.PublisherPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    return publisherRepository.findOne(ctxKey.getKeyId()).getContext().getOrganization().getContextKey();
                case CATEGORY :
                    return categoryRepository.findOne(ctxKey.getKeyId()).getPublisher().getContext().getOrganization().getContextKey();
                case FEED :
                    return feedRepository.findOne(ctxKey.getKeyId()).getPublisher().getContext().getOrganization().getContextKey();
                case ITEM :
                    return itemRepository.findOne(ctxKey.getKeyId()).getOrganization().getContextKey();
                default: throw new IllegalArgumentException("The context Type " + ctxKey.getKeyType() + " is not managed") ;
            }
        }
        return null;
    }

    public boolean isLinkedPublisherHasSubPermManagement(@NotNull final ContextKey fromCtx) {
        switch (fromCtx.getKeyType()) {
            case ORGANIZATION: return true;
            case PUBLISHER:
                final Publisher pub = publisherRepository.findOne(fromCtx.getKeyId());
                return pub != null && pub.isHasSubPermsManagement();
            case CATEGORY:
                final Category cat = categoryRepository.findOne(fromCtx.getKeyId());
                return cat != null && cat.getPublisher() != null && cat.getPublisher().isHasSubPermsManagement();
            case FEED:
                final AbstractFeed feed = feedRepository.findOne(fromCtx.getKeyId());
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
                displayOrder = publisherRepository.findOne(ctx.getKeyId()).getDefaultDisplayOrder();
                filter = permissionService.filterAuthorizedChildsOfContext(SecurityContextHolder.getContext().getAuthentication(),
                    ctx, minPerm, ClassificationPredicates.CategoryOfPublisher(ctx.getKeyId()));
                builder = new BooleanBuilder(filter);
                // Only Super Admins can manage auto associated classifications
                List<Category> myList = Lists.newArrayList(categoryRepository.findAll(builder, ClassificationPredicates.categoryOrderByDisplayOrderType(displayOrder)));
                if(permissionService.getRoleOfUserInContext(SecurityContextHolder.getContext().getAuthentication(), ctx).getMask() < PermissionType.ADMIN.getMask()) {
                    // A loop is needed due to hibernate bug, NPE on publisher.context.reader.authorizedTypes.contains(ItemType.FLASH)
                    //builder.andNot(QCategory.category.publisher.context.isNull());
                    for(Iterator<Category> iterator = myList.iterator(); iterator.hasNext(); ) {
                        final Category cat = iterator.next();
                        // other cat properties can't be null
                        if(!cat.getPublisher().getContext().getReader().getAuthorizedTypes().isEmpty()
                            && cat.getPublisher().getContext().getReader().getAuthorizedTypes().contains(ItemType.FLASH)) {
                            iterator.remove();
                        }
                    }
                }
                return myList;
            case CATEGORY :
                Category category = categoryRepository.findOne(ctx.getKeyId());
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
