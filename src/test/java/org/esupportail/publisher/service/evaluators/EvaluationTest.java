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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.domain.evaluators.OperatorEvaluator;
import org.esupportail.publisher.domain.evaluators.UserAttributesEvaluator;
import org.esupportail.publisher.domain.evaluators.UserGroupEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class EvaluationTest {

	@Inject
	private IEvaluationFactory evalFactory;

	private static UserDTO userInfos;

	@BeforeAll
	public static void setUp() throws Exception {
		Map<String, List<String>> attrs = new HashMap<String, List<String>>();
		attrs.put("uid", Lists.newArrayList("F08001ut"));
		attrs.put("cn", Lists.newArrayList("DUPONT Jean"));
		attrs.put("isMemberOf", Lists.newArrayList("esco:admin:central",
				"esco:Applications:Publisher:Fictif_0450822X"));
		userInfos = new UserDTO("F08001ut", "dn", true, false, "test@recia.fr",
				attrs);
	}

	@Test
	public void testUserAttributesEvaluator() {
		UserAttributesEvaluator uae1 = new UserAttributesEvaluator("uid",
				"F08001ut", StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserAttributesEvaluator("cn", "PONT",
				StringEvaluationMode.CONTAINS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserAttributesEvaluator("cn", "ean",
				StringEvaluationMode.ENDS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserAttributesEvaluator("cn", "",
				StringEvaluationMode.EXISTS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserAttributesEvaluator("cn", "DUPONT",
				StringEvaluationMode.STARTS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserAttributesEvaluator("cn", ".*PONT .*",
				StringEvaluationMode.MATCH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));

		uae1 = new UserAttributesEvaluator("uid", "A08001ut",
				StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserAttributesEvaluator("cn", "pont",
				StringEvaluationMode.CONTAINS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserAttributesEvaluator("cn", "EAN",
				StringEvaluationMode.ENDS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserAttributesEvaluator("sn", "",
				StringEvaluationMode.EXISTS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserAttributesEvaluator("cn", "dupont",
				StringEvaluationMode.STARTS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserAttributesEvaluator("cn", ".*pont .*",
				StringEvaluationMode.MATCH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
	}

	@Test
	public void testUserAttributesBadEvaluator() {
		UserAttributesEvaluator uae1 = new UserAttributesEvaluator(
				"isMemberOf", "F08001ut", StringEvaluationMode.EQUALS);
		Assertions.assertThrows(java.lang.IllegalArgumentException.class, () -> {
			assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		});
	}

	@Test
	public void testUserMultivaluedAttributesEvaluator() {
		UserMultivaluedAttributesEvaluator uae1 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin:central", StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"esco:admin", StringEvaluationMode.CONTAINS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"admin:central", StringEvaluationMode.ENDS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf", "",
				StringEvaluationMode.EXISTS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"esco:admin", StringEvaluationMode.STARTS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"((esco)|(clg37)):admin:.*", StringEvaluationMode.MATCH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));

		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"esco:admin", StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"cfa:admin", StringEvaluationMode.CONTAINS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"centralise", StringEvaluationMode.ENDS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserMultivaluedAttributesEvaluator("sn", "",
				StringEvaluationMode.EXISTS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"cfa:admin", StringEvaluationMode.STARTS_WITH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"((cfa)|(clg37)):admin:.*", StringEvaluationMode.MATCH);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
	}

	@Test
	public void testUserGroupEvaluator() {
		UserGroupEvaluator uae1 = new UserGroupEvaluator("esco:admin");
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));

		uae1 = new UserGroupEvaluator("esco:admin:local");
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(false));
	}

	@Test
	public void testOperatorEvaluator() {
		UserGroupEvaluator uge1 = new UserGroupEvaluator("esco:admin");
		assertThat(evalFactory.from(uge1).isApplicable(userInfos), is(true));
		UserAttributesEvaluator uae1 = new UserAttributesEvaluator("uid",
				"F08001ut", StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(uae1).isApplicable(userInfos), is(true));
		UserMultivaluedAttributesEvaluator umae1 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin:central", StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(umae1).isApplicable(userInfos), is(true));

		OperatorEvaluator oe1 = new OperatorEvaluator(OperatorType.AND,
				Sets.newHashSet(uge1, uae1, umae1));
		assertThat(evalFactory.from(oe1).isApplicable(userInfos), is(true));

		UserGroupEvaluator uge2 = new UserGroupEvaluator("esco:admin:local");
		assertThat(evalFactory.from(uge2).isApplicable(userInfos), is(false));
		UserAttributesEvaluator uae2 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin", StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(uae2).isApplicable(userInfos), is(false));
		UserMultivaluedAttributesEvaluator umae2 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin", StringEvaluationMode.EQUALS);
		assertThat(evalFactory.from(umae2).isApplicable(userInfos), is(false));

		OperatorEvaluator oe2 = new OperatorEvaluator(OperatorType.AND,
				Sets.newHashSet(uge2, uae2, umae2));
		assertThat(evalFactory.from(oe2).isApplicable(userInfos), is(false));

		oe1 = new OperatorEvaluator(OperatorType.NOT,
				Sets.newHashSet((AbstractEvaluator) uge2));
		assertThat(evalFactory.from(oe1).isApplicable(userInfos), is(true));

		oe1 = new OperatorEvaluator(OperatorType.OR, Sets.newHashSet(uge1,
				uae2, umae2));
	}

}