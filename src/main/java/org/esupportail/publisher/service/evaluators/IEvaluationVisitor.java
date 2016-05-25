package org.esupportail.publisher.service.evaluators;

import org.esupportail.publisher.domain.evaluators.*;
import org.esupportail.publisher.web.rest.dto.UserDTO;

public interface IEvaluationVisitor {

	boolean isApplicable(final OperatorEvaluator evaluator);

	boolean isApplicable(final UserAttributesEvaluator evaluator);

	boolean isApplicable(final UserMultivaluedAttributesEvaluator evaluator);

	boolean isApplicable(final UserGroupEvaluator evaluator);

	boolean isApplicable(final AuthenticatedUserEvaluator evaluator);

	boolean isApplicable(final OperatorEvaluator evaluator,
			final UserDTO userInfos);

	boolean isApplicable(final UserAttributesEvaluator evaluator,
			final UserDTO userInfos);

	boolean isApplicable(final UserMultivaluedAttributesEvaluator evaluator,
			final UserDTO userInfos);

	boolean isApplicable(final UserGroupEvaluator evaluator,
			final UserDTO userInfos);

	boolean isApplicable(final AuthenticatedUserEvaluator evaluator,
			final UserDTO userInfos);

}
