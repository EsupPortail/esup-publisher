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
package org.esupportail.publisher.service.evaluators;

import com.google.common.collect.Sets;
import org.esupportail.publisher.domain.evaluators.*;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Service
public class EvaluationFactoryImpl implements IEvaluationFactory {

	@Inject
	private ExternalUserHelper ldapUserHelper;

	public EvaluationFactoryImpl() {
		super();
	}

	/**
	 * @see org.esupportail.publisher.service.evaluators.IEvaluationFactory#from(org.esupportail.publisher.domain.evaluators.AbstractEvaluator)
	 */
	@Override
	public IEvaluation from(@NotNull final AbstractEvaluator evaluator) {
		if (evaluator instanceof OperatorEvaluator)
			return new OperatorEvaluation(
					((OperatorEvaluator) evaluator).getType(),
					asSet(((OperatorEvaluator) evaluator).getEvaluators()));
		else if (evaluator instanceof UserMultivaluedAttributesEvaluator)
			return new UserMultivaluedAttributesEvaluation(
					((UserMultivaluedAttributesEvaluator) evaluator)
							.getAttribute(),
					((UserMultivaluedAttributesEvaluator) evaluator).getValue(),
					((UserMultivaluedAttributesEvaluator) evaluator).getMode());
		else if (evaluator instanceof UserAttributesEvaluator)
			return new UserAttributesEvaluation(
					((UserAttributesEvaluator) evaluator).getAttribute(),
					((UserAttributesEvaluator) evaluator).getValue(),
					((UserAttributesEvaluator) evaluator).getMode());
		else if (evaluator instanceof UserGroupEvaluator)
			return new UserGroupFromUserInfosEvaluation(
					((UserGroupEvaluator) evaluator).getGroup(),
					ldapUserHelper.getUserGroupAttribute());
		else if (evaluator instanceof AuthenticatedUserEvaluator)
			return new AuthenticatedUserEvaluation();

		throw new IllegalArgumentException(
				"No Implementation done of Evaluator type : "
						+ evaluator.getClass().getCanonicalName());

	}

	/**
	 * @see org.esupportail.publisher.service.evaluators.IEvaluationFactory#asSet(java.util.Set)
	 */
	@Override
	public Set<IEvaluation> asSet(
			@NotNull final Set<AbstractEvaluator> evaluators) {
		final Set<IEvaluation> evals = Sets.newHashSet();
		for (AbstractEvaluator object : evaluators) {
			evals.add(from(object));
		}
		return evals;
	}

}
