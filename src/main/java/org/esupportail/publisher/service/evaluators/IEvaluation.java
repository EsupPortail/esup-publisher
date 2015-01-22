package org.esupportail.publisher.service.evaluators;

import org.esupportail.publisher.web.rest.dto.UserDTO;

public interface IEvaluation {

	boolean isApplicable(UserDTO userInfos);

}
