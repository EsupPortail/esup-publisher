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
