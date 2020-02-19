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

import org.esupportail.publisher.domain.AbstractFeed;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ExternalFeed;
import org.esupportail.publisher.domain.InternalFeed;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.QAbstractClassification;
import org.esupportail.publisher.domain.QAbstractFeed;
import org.esupportail.publisher.domain.QCategory;
import org.esupportail.publisher.domain.enums.DisplayOrderType;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

/**
 * @author GIP RECIA - Julien Gribonvald 24 juil. 2014
 */
public final class ClassificationPredicates {

    public static Predicate ClassificationsOfOrganization(Long organizationId) {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.publisher.context.organization.id.eq(organizationId);
    }

    public static Predicate notNull() {
        return QAbstractClassification.abstractClassification.isNotNull();
    }

    public static OrderSpecifier<?> classifOrderByDisplayOrderType(DisplayOrderType displayOrder) {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return classif.lastModifiedDate.coalesce(classif.createdDate).desc();
            case ONLY_LAST_CREATED_FIRST:
            case START_DATE: //no start date is available on classif, so use the created date.
                return classif.createdDate.desc();
            case CUSTOM:
                return classif.displayOrder.desc();
            default:
                return classif.name.asc();
        }
    }

    public static OrderSpecifier<?> categoryOrderByDisplayOrderType(DisplayOrderType displayOrder) {
        final QCategory classif = QCategory.category;
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return classif.lastModifiedDate.coalesce(classif.createdDate).desc();
            case ONLY_LAST_CREATED_FIRST:
            case START_DATE: //no start date is available on classif, so use the created date.
                return classif.createdDate.desc();
            case CUSTOM:
                return classif.displayOrder.desc();
            default:
                return classif.name.asc();
        }
    }

    public static OrderSpecifier<?> feedOrderByDisplayOrderType(DisplayOrderType displayOrder) {
        final QAbstractFeed classif = QAbstractFeed.abstractFeed;
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return classif.lastModifiedDate.coalesce(classif.createdDate).desc();
            case ONLY_LAST_CREATED_FIRST:
            case START_DATE: //no start date is available on classif, so use the created date.
                return classif.createdDate.desc();
            case CUSTOM:
                return classif.displayOrder.desc();
            default:
                return classif.name.asc();
        }
    }

    public static Predicate CategoryClassification() {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.instanceOf(Category.class);
    }

    public static Predicate CategoryClassificationOfPublisher(long pubId) {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.instanceOf(Category.class).and(classif.publisher.id.eq(pubId)) ;
    }

    public static Predicate classificationsOfPublisher(long pubId) {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.publisher.id.eq(pubId);
    }

    public static Predicate CategoryOfPublisher(long pubId) {
        final QCategory category = QCategory.category;
        return category.publisher.id.eq(pubId);
    }

    public static Predicate AbstractFeedsOfCategory(Category cat) {
        final QAbstractFeed classif = QAbstractFeed.abstractFeed;
        return classif.parent.id.eq(cat.getId());
    }
    public static Predicate AbstractFeedsOfCategory(long catId) {
        final QAbstractFeed classif = QAbstractFeed.abstractFeed;
        return classif.parent.id.eq(catId);
    }

    public static Predicate InternalFeedClassification() {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.instanceOf(InternalFeed.class);
    }

    public static Predicate InternalFeedClassificationOfPublisher(Publisher pub) {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.publisher.id.eq(pub.getId()).and(InternalFeedClassification());
    }

    public static Predicate ExternalFeedClassification() {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.instanceOf(ExternalFeed.class);
    }

    public static Predicate ExternalFeedClassificationOfPublisher(Publisher pub) {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.publisher.id.eq(pub.getId()).and(ExternalFeedClassification());
    }

    public static Predicate AbstractFeedClassification() {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.instanceOf(AbstractFeed.class);
    }

    public static Predicate AbstractFeedClassificationOfPublisher(long pubId) {
        final QAbstractClassification classif = QAbstractClassification.abstractClassification;
        return classif.publisher.id.eq(pubId).and(AbstractFeedClassification());
    }

}
