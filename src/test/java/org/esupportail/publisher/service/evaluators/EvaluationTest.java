package org.esupportail.publisher.service.evaluators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EvaluationTest {

	@Inject
	private IEvaluationFactory evalFactory;

	UserDTO userInfos;

	@Before
	public void setUp() throws Exception {
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
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", "PONT",
				StringEvaluationMode.CONTAINS);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", "ean",
				StringEvaluationMode.ENDS_WITH);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", "",
				StringEvaluationMode.EXISTS);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", "DUPONT",
				StringEvaluationMode.STARTS_WITH);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", ".*PONT .*",
				StringEvaluationMode.MATCH);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));

		uae1 = new UserAttributesEvaluator("uid", "A08001ut",
				StringEvaluationMode.EQUALS);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", "pont",
				StringEvaluationMode.CONTAINS);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", "EAN",
				StringEvaluationMode.ENDS_WITH);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("sn", "",
				StringEvaluationMode.EXISTS);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", "dupont",
				StringEvaluationMode.STARTS_WITH);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserAttributesEvaluator("cn", ".*pont .*",
				StringEvaluationMode.MATCH);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testUserAttributesBadEvaluator() {
		UserAttributesEvaluator uae1 = new UserAttributesEvaluator(
				"isMemberOf", "F08001ut", StringEvaluationMode.EQUALS);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
	}

	@Test
	public void testUserMultivaluedAttributesEvaluator() {
		UserMultivaluedAttributesEvaluator uae1 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin:central", StringEvaluationMode.EQUALS);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"esco:admin", StringEvaluationMode.CONTAINS);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"admin:central", StringEvaluationMode.ENDS_WITH);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf", "",
				StringEvaluationMode.EXISTS);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"esco:admin", StringEvaluationMode.STARTS_WITH);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"((esco)|(clg37)):admin:.*", StringEvaluationMode.MATCH);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));

		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"esco:admin", StringEvaluationMode.EQUALS);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"cfa:admin", StringEvaluationMode.CONTAINS);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"centralise", StringEvaluationMode.ENDS_WITH);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("sn", "",
				StringEvaluationMode.EXISTS);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"cfa:admin", StringEvaluationMode.STARTS_WITH);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
		uae1 = new UserMultivaluedAttributesEvaluator("isMemberOf",
				"((cfa)|(clg37)):admin:.*", StringEvaluationMode.MATCH);
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
	}

	@Test
	public void testUserGroupEvaluator() {
		UserGroupEvaluator uae1 = new UserGroupEvaluator("esco:admin");
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));

		uae1 = new UserGroupEvaluator("esco:admin:local");
		assertFalse(evalFactory.from(uae1).isApplicable(userInfos));
	}

	@Test
	public void testOperatorEvaluator() {
		UserGroupEvaluator uge1 = new UserGroupEvaluator("esco:admin");
		assertTrue(evalFactory.from(uge1).isApplicable(userInfos));
		UserAttributesEvaluator uae1 = new UserAttributesEvaluator("uid",
				"F08001ut", StringEvaluationMode.EQUALS);
		assertTrue(evalFactory.from(uae1).isApplicable(userInfos));
		UserMultivaluedAttributesEvaluator umae1 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin:central", StringEvaluationMode.EQUALS);
		assertTrue(evalFactory.from(umae1).isApplicable(userInfos));

		OperatorEvaluator oe1 = new OperatorEvaluator(OperatorType.AND,
				Sets.newHashSet(uge1, uae1, umae1));
		assertTrue(evalFactory.from(oe1).isApplicable(userInfos));

		UserGroupEvaluator uge2 = new UserGroupEvaluator("esco:admin:local");
		assertFalse(evalFactory.from(uge2).isApplicable(userInfos));
		UserAttributesEvaluator uae2 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin", StringEvaluationMode.EQUALS);
		assertFalse(evalFactory.from(uae2).isApplicable(userInfos));
		UserMultivaluedAttributesEvaluator umae2 = new UserMultivaluedAttributesEvaluator(
				"isMemberOf", "esco:admin", StringEvaluationMode.EQUALS);
		assertFalse(evalFactory.from(umae2).isApplicable(userInfos));

		OperatorEvaluator oe2 = new OperatorEvaluator(OperatorType.AND,
				Sets.newHashSet(uge2, uae2, umae2));
		assertFalse(evalFactory.from(oe2).isApplicable(userInfos));

		oe1 = new OperatorEvaluator(OperatorType.NOT,
				Sets.newHashSet((AbstractEvaluator) uge2));
		assertTrue(evalFactory.from(oe1).isApplicable(userInfos));

		oe1 = new OperatorEvaluator(OperatorType.OR, Sets.newHashSet(uge1,
				uae2, umae2));
	}

}
