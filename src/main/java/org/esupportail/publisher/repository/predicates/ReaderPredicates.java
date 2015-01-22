/**
 *
 */
package org.esupportail.publisher.repository.predicates;

import org.esupportail.publisher.domain.QReader;
import org.esupportail.publisher.domain.Reader;

import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * @author GIP RECIA - Julien Gribonvald 29 sept. 2014
 */
public final class ReaderPredicates {

	public static Predicate sameName(final Reader reader) {
		final QReader qreader = QReader.reader;
		final BooleanExpression readerExistName = qreader.name.eq(reader
				.getName());
		final BooleanExpression readerExistId = qreader.id.eq(reader.getId());
		if (reader.getId() > 0) {
			return readerExistName.and(readerExistId.not());
		}
		return readerExistName;
	}

}
