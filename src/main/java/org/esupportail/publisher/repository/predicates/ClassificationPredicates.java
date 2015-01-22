/**
 *
 */
package org.esupportail.publisher.repository.predicates;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.DisplayOrderType;

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
			return classif.lastModifiedDate.coalesce(classif.createdDate).asc();
		case ONLY_LAST_CREATED_FIRST:
			return classif.createdDate.asc();
		case NAME:
			return classif.name.asc();
		case START_DATE:
			return classif.createdDate.asc();
		case CUSTOM:
			return classif.displayOrder.asc();
		default:
			return classif.name.asc();
		}
	}

    public static OrderSpecifier<?> categoryOrderByDisplayOrderType(DisplayOrderType displayOrder) {
        final QCategory classif = QCategory.category;
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return classif.lastModifiedDate.coalesce(classif.createdDate).asc();
            case ONLY_LAST_CREATED_FIRST:
                return classif.createdDate.asc();
            case NAME:
                return classif.name.asc();
            case START_DATE:
                return classif.createdDate.asc();
            case CUSTOM:
                return classif.displayOrder.asc();
            default:
                return classif.name.asc();
        }
    }

    public static OrderSpecifier<?> feedOrderByDisplayOrderType(DisplayOrderType displayOrder) {
        final QAbstractFeed classif = QAbstractFeed.abstractFeed;
        switch (displayOrder) {
            case LAST_CREATED_MODIFIED_FIRST:
                return classif.lastModifiedDate.coalesce(classif.createdDate).asc();
            case ONLY_LAST_CREATED_FIRST:
                return classif.createdDate.asc();
            case NAME:
                return classif.name.asc();
            case START_DATE:
                return classif.createdDate.asc();
            case CUSTOM:
                return classif.displayOrder.asc();
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
