package org.esupportail.publisher.service.evaluators;

import java.util.Set;

import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;

public interface IEvaluationFactory {

	IEvaluation from(final AbstractEvaluator evaluator);

	Set<IEvaluation> asSet(final Set<AbstractEvaluator> evaluators);

}