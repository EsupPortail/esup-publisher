/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * Copyright (C) 2014 RECIA http://www.recia.fr
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 				http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.service.evaluators;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 27 oct. 2014
 */
@Service
@Slf4j
public class OperatorEvaluation implements IEvaluation, Serializable {

	/** */
	private static final long serialVersionUID = -1694431861034222417L;

	private OperatorType type;

	private Set<IEvaluation> evaluations;

	/**
	 * Empty constructor.
	 */
	public OperatorEvaluation() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param type
	 *            Type operator to test evaluators. Null if only one evaluator
	 * @param evaluations
	 *            List of Evaluation test.
	 */
	public OperatorEvaluation(@NotNull final OperatorType type,
			@NotEmpty Set<IEvaluation> evaluations) {
		super();
		this.type = type;
		this.evaluations = evaluations;
	}

	public boolean isApplicable(@NotNull final UserDTO userInfos) {
		boolean rslt = false;
		if (log.isDebugEnabled())
			log.debug(" >>>> calling OperatorEvaluation[" + this + ", op="
					+ this.type + "].isApplicable()");

		if (this.type != null) {

			switch (this.type) {
			case OR: {
				rslt = false; // presume false in this case...
				for (IEvaluation v : this.evaluations) {
					if (v.isApplicable(userInfos)) {
						rslt = true;
						break;
					}
				}
			}
				break;

			case AND: {
				rslt = true; // presume true in this case...
				for (IEvaluation v : this.evaluations) {
					if (!v.isApplicable(userInfos)) {
						rslt = false;
						break;
					}
				}
			}
				break;

			case NOT: {
				rslt = false; // presume false in this case... until later...
				for (IEvaluation v : this.evaluations) {
					if (v.isApplicable(userInfos)) {
						rslt = true;
						break;
					}
				}
				rslt = !rslt;
			}
				break;
			}
		}

		if (log.isDebugEnabled())
			log.debug(" ---- OperatorEvaluator[" + this + ", op=" + this.type
					+ "].isApplicable()=" + rslt);

		return rslt;
	}

}
