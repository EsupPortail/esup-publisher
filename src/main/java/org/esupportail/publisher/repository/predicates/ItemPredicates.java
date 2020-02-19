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
/**
 *
 */
package org.esupportail.publisher.repository.predicates;

import java.time.LocalDate;
import java.util.EnumSet;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.Media;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.QAbstractItem;
import org.esupportail.publisher.domain.QItemClassificationOrder;
import org.esupportail.publisher.domain.Resource;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import lombok.extern.slf4j.Slf4j;

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

    public static Predicate itemsClassOfPublisher(final Publisher publisher) {
        return itemsClassOfPublisher(publisher.getId());
    }
    public static Predicate itemsClassOfPublisher(final long publisherId) {
        return qItemClass.itemClassificationId.abstractClassification.publisher.id.eq(publisherId);
    }
    public static Predicate itemsClassOfOrganization(final Organization organization) {
        return itemsClassOfOrganization(organization.getId());
    }
    public static Predicate itemsClassOfOrganization(final long organizationId) {
        return qItemClass.itemClassificationId.abstractItem.organization.id.eq(organizationId);
    }

    public static Predicate notNull() {
        return qItemClass.isNotNull();
    }

    public static Predicate itemsClassOfItem(final long itemId) {
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
                    .coalesce(qItemClass.itemClassificationId.abstractItem.createdDate).desc();
            case ONLY_LAST_CREATED_FIRST:
                return qItemClass.itemClassificationId.abstractItem.createdDate.desc();
            case NAME:
                return qItemClass.itemClassificationId.abstractItem.title.asc();
            case CUSTOM:
                return qItemClass.displayOrder.desc();
//            case START_DATE:
//                return qItemClass.itemClassificationId.abstractItem.startDate.desc();
            default:
                return qItemClass.itemClassificationId.abstractItem.startDate.desc();
        }
    }

    public static OrderSpecifier<?> orderByPublisherDefinition(
        final DisplayOrderType displayOrder) {
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return qItemClass.itemClassificationId.abstractClassification.lastModifiedDate
                    .coalesce(qItemClass.itemClassificationId.abstractClassification.createdDate).desc();
            case NAME:
                return qItemClass.itemClassificationId.abstractClassification.name.asc();
            case CUSTOM:
                return qItemClass.displayOrder.desc();
//            case ONLY_LAST_CREATED_FIRST:
//                return qItemClass.itemClassificationId.abstractClassification.createdDate.desc();
            default:
                return qItemClass.itemClassificationId.abstractClassification.createdDate.desc();
        }
    }

    public static OrderSpecifier<?> orderByItemDefinition(
        final DisplayOrderType displayOrder) {
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return qItem.lastModifiedDate.coalesce(qItem.createdDate).desc();
            case ONLY_LAST_CREATED_FIRST:
                return qItem.createdDate.desc();
            case NAME:
                return qItem.title.asc();
            case CUSTOM:
                return qItem.id.desc();
//            case START_DATE:
//                return qItem.startDate.desc();
            default:
                return qItem.startDate.desc();
        }
    }

    public static Predicate ItemWithStatus(final long itemId, final ItemStatus status) {
        Predicate onStatus = qItem.status.isNotNull();
        if (status != null) {
            onStatus = qItem.status.eq(status);
        }
        return qItem.id.eq(itemId).and(onStatus);
    }

    public static Predicate OwnedItemsOfStatus(final Boolean owned, final Integer status) {
        Predicate onStatus = qItem.status.isNotNull();
        if (status != null) {
            ItemStatus itemStatus = ItemStatus.valueOf(status);
            if (itemStatus != null)
                onStatus = qItem.status.eq(itemStatus);
        }
        if (owned != null) {
            final String userId = SecurityUtils.getCurrentLogin();
            if (owned) {
                return qItem.createdBy.login.eq(userId).and(onStatus);
            } else {
                return qItem.createdBy.login.ne(userId).and(onStatus);
            }
        }
        return onStatus;
    }

    public static Predicate OwnedItemsClassOfStatus(final Boolean owned, final ItemStatus status) {
        Predicate onStatus = qItemClass.itemClassificationId.abstractItem.status.isNotNull();
        if (status != null) {
            onStatus = qItemClass.itemClassificationId.abstractItem.status.eq(status);
        }
        if (owned != null && owned) {
            String userId = SecurityUtils.getCurrentLogin();
            if (owned) {
                return qItemClass.itemClassificationId.abstractItem.createdBy.login.eq(userId).and(onStatus);
            } else {
                return qItemClass.itemClassificationId.abstractItem.createdBy.login.ne(userId).and(onStatus);
            }
        }
        return onStatus;
    }

    public static Predicate OwnedItemsClassOfRSSAllowed(final boolean isAllowed) {
        return qItemClass.itemClassificationId.abstractItem.rssAllowed.eq(isAllowed);
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

    public static Predicate FlashItems() {
        return qItem.instanceOf(Flash.class);
    }

    public static Predicate FlashItemsOfOrganization(final Organization org) {
        return qItem.organization.id.eq(org.getId()).and(FlashItems());
    }

    public static Predicate ItemsToRemove() {
        return qItem.status.in(EnumSet.of(ItemStatus.ARCHIVED, ItemStatus.DRAFT, ItemStatus.PENDING)).and(itemsEndDateOlderThanNMonth(15));
        //return itemsEndDateOlderThanNMonth(15);
    }

    private static Predicate itemsEndDateOlderThanNMonth(final int nmonth) {
        return qItem.endDate.isNotNull().and(qItem.endDate.before(LocalDate.now().minusMonths(nmonth)));
    }

    public static Predicate itemsOwnedOfOrganizationWithoutClassif(final UserDTO user, final long orgId) {
        return qItem.organization.id.eq(orgId).and(qItem.createdBy.login.eq(user.getLogin()));
    }
}
