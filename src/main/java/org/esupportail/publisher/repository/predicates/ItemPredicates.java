/**
 *
 */
package org.esupportail.publisher.repository.predicates;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.joda.time.LocalDate;

/**
 * @author GIP RECIA - Julien Gribonvald 24 juil. 2014
 */
@Slf4j
public final class ItemPredicates {

    private final static QAbstractItem qItem = QAbstractItem.abstractItem;
    private final static QItemClassificationOrder qItemClass = QItemClassificationOrder.itemClassificationOrder;

	public static Predicate itemsClassOfClassification(final AbstractClassification classif) {
		return itemsClassOfClassification(classif.getId());
	}
    public static Predicate itemsClassOfClassification(final long classifId) {
        return qItemClass.itemClassificationId.abstractClassification.id.eq(classifId);
    }

    public static Predicate notNull() {
        return qItemClass.isNotNull();
    }

	public static Predicate itemsClassOfItem(final Long itemId) {
		return qItemClass.itemClassificationId.abstractItem.id.eq(itemId);
	}

	public static OrderSpecifier<?> orderByClassifDefinition(
			final AbstractClassification classif) {
		return orderByClassifDefinition(classif.getDefaultDisplayOrder());
	}

	public static OrderSpecifier<?> orderByClassifDefinition(
			final DisplayOrderType displayOrder) {
		switch (displayOrder) {
		case LAST_CREATED_MODIFIED_FIRST:
			return qItemClass.itemClassificationId.abstractItem.lastModifiedDate
					.coalesce(
                        qItemClass.itemClassificationId.abstractItem.createdDate)
					.desc();
		case ONLY_LAST_CREATED_FIRST:
			return qItemClass.itemClassificationId.abstractItem.createdDate
					.desc();
		case NAME:
			return qItemClass.itemClassificationId.abstractItem.title.asc();
		case START_DATE:
			return qItemClass.itemClassificationId.abstractItem.startDate.desc();
		case CUSTOM:
			return qItemClass.displayOrder.desc();
		default:
			return qItemClass.itemClassificationId.abstractItem.startDate.asc();
		}
	}

    public static OrderSpecifier<?> orderByItemDefinition(
        final DisplayOrderType displayOrder) {
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return qItem.lastModifiedDate.coalesce(qItem.createdDate).desc();
            case ONLY_LAST_CREATED_FIRST:
                return qItem.createdDate
                    .desc();
            case NAME:
                return qItem.title.asc();
            case START_DATE:
                return qItem.startDate.desc();
            case CUSTOM:
                return qItem.id.desc();
            default:
                return qItem.startDate.desc();
        }
    }

    public static Predicate OwnedItemsOfStatus(final Boolean owned, final Integer status) {
        //Predicate onOwner = item.createdBy.isNotNull();
        Predicate onStatus = qItem.status.isNotNull();
        if (status != null) {
            ItemStatus itemStatus = ItemStatus.valueOf(status);
            if (itemStatus != null)
                onStatus = qItem.status.eq(itemStatus);
        }
        if (owned != null && owned) {
            String userId = SecurityUtils.getCurrentLogin();
            return qItem.createdBy.login.eq(userId).and(onStatus);
        }
        return onStatus;
    }

	public static Predicate NewsItems() {
		return qItem.instanceOf(News.class);

	}

	public static Predicate NewsItemsOfOrganization(final Organization org) {
		return qItem.organization.id.eq(org.getId()).and(NewsItems());
	}

	public static Predicate MediaItems() {
		return qItem.instanceOf(Media.class);
	}

	public static Predicate MediaItemsOfOrganization(final Organization org) {
		return qItem.organization.id.eq(org.getId()).and(MediaItems());
	}

	public static Predicate ResourceItems() {
		return qItem.instanceOf(Resource.class);
	}

	public static Predicate ResourceItemsOfOrganization(final Organization org) {
		return qItem.organization.id.eq(org.getId()).and(ResourceItems());
	}

    public static Predicate ItemsToRemove() {
        final Predicate archived = qItem.status.eq(ItemStatus.ARCHIVED).and(itemsEndDateOlderThanNMonth(4));
        return qItem.status.eq(ItemStatus.DRAFT).and(itemsEndDateOlderThanNMonth(8)).or(archived);
    }

    private static Predicate itemsEndDateOlderThanNMonth(final int nmonth) {
        return qItem.endDate.isNotNull().and(qItem.endDate.after(new LocalDate().minusMonths(nmonth)));
    }

    public static Predicate itemsOwnedOfOrganizationWithoutClassif(final UserDTO user, final long orgId) {
        return qItem.organization.id.eq(orgId).and(qItem.createdBy.login.eq(user.getLogin()));
    }
}
