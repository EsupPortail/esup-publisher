package org.esupportail.publisher.domain.evaluators;

import org.esupportail.publisher.service.evaluators.IEvaluationVisitor;

public interface IEvaluator {

	boolean isApplicable(final IEvaluationVisitor evaluation);

}
