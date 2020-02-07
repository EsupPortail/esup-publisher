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
package org.esupportail.publisher.service.factories.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.text.StringEscapeUtils;
import org.esupportail.publisher.config.Constants;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.rest.vo.Visibility;
import org.esupportail.publisher.web.rest.vo.VisibilityAbstract;
import org.esupportail.publisher.web.rest.vo.VisibilityGroup;
import org.esupportail.publisher.web.rest.vo.VisibilityRegex;
import org.esupportail.publisher.web.rest.vo.VisibilityRegular;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Component
public class VisibilityFactoryImpl implements VisibilityFactory {

	@Inject
	private Environment environment;

	@Inject
	private ExternalUserHelper externalUserHelper;

	@Override
	public Visibility from(@NotNull List<Subscriber> models) {
		Visibility visibility = new Visibility();
		for (Subscriber subscriber : models) {
			switch (subscriber.getSubscribeType()) {
			case FORCED:
				visibility.getObliged().add(from(subscriber.getSubjectCtxId().getSubject()));
				break;
			case FREE:
				visibility.getAllowed().add(from(subscriber.getSubjectCtxId().getSubject()));
				break;
			case PRE:
				visibility.getAutoSubscribed().add(from(subscriber.getSubjectCtxId().getSubject()));
				break;
			default:
				throw new IllegalArgumentException("Unknown SubcriberType not managed :"
						+ subscriber.getSubscribeType());
			}
		}
		return visibility;
	}

	private VisibilityAbstract from(@NotNull SubjectKeyExtended model) {
		switch (model.getKeyType()) {
		case GROUP:
			if (Arrays.asList(environment.getActiveProfiles()).contains(Constants.SPRING_PROFILE_WS_GROUP)) {
				return new VisibilityGroup(StringEscapeUtils.escapeXml10(model.getKeyValue()));
			}
			//return new VisibilityRegex(externalUserHelper.getUserGroupAttribute(), Pattern.quote(StringEscapeUtils.escapeXml10(model.getKeyId())));
			return new VisibilityRegular(externalUserHelper.getUserGroupAttribute(),
					StringEscapeUtils.escapeXml10(model.getKeyValue()));
		case PERSON:
			return new VisibilityRegular(externalUserHelper.getUserIdAttribute(), model.getKeyValue());
		case PERSON_ATTR:
			return new VisibilityRegular(model.getKeyAttribute(), model.getKeyValue());
		case PERSON_ATTR_REGEX:
			return new VisibilityRegex(model.getKeyAttribute(), model.getKeyValue());
		default:
			throw new IllegalArgumentException("Unknown SubjectType not managed :" + model.getKeyType());
		}
	}
}
