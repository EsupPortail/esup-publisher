package org.esupportail.publisher.service.bean;

import java.util.Map;

import org.esupportail.publisher.service.evaluators.IEvaluation;

public interface IAuthoritiesDefinition {

	Map<String, IEvaluation> getAppRoles();

}