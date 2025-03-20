package org.esupportail.publisher.repository.predicates;

import com.querydsl.core.types.Predicate;
import org.esupportail.publisher.domain.QReadingIndicator;
import org.esupportail.publisher.web.rest.dto.UserDTO;

public class ReadingIndicatorPredicates {

    public static Predicate readingIndicationOfUser(final UserDTO userDto) {
        final QReadingIndicator qobj = QReadingIndicator.readingIndicator;
        return qobj.user.eq(userDto.getLogin());
    }

    public static Predicate readingIndicationOfItem(final long id) {
        final QReadingIndicator qobj = QReadingIndicator.readingIndicator;
        return qobj.item.id.eq(id);
    }
}
