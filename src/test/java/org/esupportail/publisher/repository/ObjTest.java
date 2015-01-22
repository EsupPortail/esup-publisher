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
package org.esupportail.publisher.repository;

import com.google.common.collect.Sets;
import org.esupportail.publisher.domain.*;
import org.esupportail.publisher.domain.enums.*;
import org.esupportail.publisher.domain.evaluators.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class ObjTest {

	public final static String subject1 = "user";
	public final static String subject2 = "system";
	public final static String group1 = "cfa:Etablissements:ECOLE HUBERT CURIEN_0180865T:Apprenti INGE 1ère année:Eleves_ERE IGR 1ère année";
	public final static String group2 = "esco:admin:central";
	public final static String subject3 = "admin";

	public final static SubjectKey subjectKey1 = new SubjectKey(subject1,
			SubjectType.PERSON);
	public final static SubjectKey subjectKey2 = new SubjectKey(subject2,
			SubjectType.PERSON);
	public final static SubjectKey subjectKey3 = new SubjectKey(subject3,
			SubjectType.PERSON);
	public final static SubjectKey subjectKey4 = new SubjectKey(group1,
			SubjectType.GROUP);
	public final static SubjectKey subjectKey5 = new SubjectKey(group2,
			SubjectType.GROUP);

	public final static SubjectPermKey subjectPerm1 = new SubjectPermKey(subjectKey1,
			false);
	public final static SubjectPermKey subjectPerm1WithValidation = new SubjectPermKey(
			subjectKey1, true);
	public final static SubjectPermKey subjectPerm2 = new SubjectPermKey(subjectKey2,
			false);
	public final static SubjectPermKey subjectPerm3 = new SubjectPermKey(subjectKey3,
			false);
	public final static SubjectPermKey subjectPerm4 = new SubjectPermKey(subjectKey4,
			false);

	public final static SubjectPermKey[] subkeys1 = { subjectPerm1, subjectPerm2,
			subjectPerm3 };
	public final static SubjectPermKey[] subkeys2 = { subjectPerm2, subjectPerm3 };
	public final static SubjectPermKey[] subkeys3 = { subjectPerm1, subjectPerm2 };
	public final static SubjectPermKey[] subkeys4 = { subjectPerm3 };
	public final static SubjectPermKey[] subkeysEmpty = {};

	public final static Random rnd = new Random();

	public static DateTime d1 = null;
	public static DateTime d2 = null;
	public static DateTime d3 = null;
	public static DateTime d4 = null;
	public static DateTime d5 = null;
	public static DateTime d6 = null;
	public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd HH:mm:ss");

	static {

		// df.setLenient(false);
		// df.setTimeZone(TimeZone.getTimeZone("CET"));
		d1 = dateTimeFormatter.parseDateTime("2015-01-15 07:23:30");
		d2 = dateTimeFormatter.parseDateTime("2015-02-15 07:23:30");
		d3 = dateTimeFormatter.parseDateTime("2015-03-15 07:23:30");
		d4 = dateTimeFormatter.parseDateTime("2016-01-15 07:23:30");
		d5 = dateTimeFormatter.parseDateTime("2016-02-15 07:23:30");
		d6 = dateTimeFormatter.parseDateTime("2016-03-15 07:23:30");
	}

	public static Organization newOrganization(String indice) {
		Organization e = new Organization();
		e.setDescription("A Desc" + indice);
		e.setDisplayOrder(200);
		e.setName("A TESTER" + indice);
		e.setDisplayName("À tester");
		return e;
	}

	public static PermissionOnContext newPermissionOnCtx(String indice,
			PermissionType role, Organization org) {
		PermissionOnContext e = new PermissionOnContext();
		e.setContext(org.getContextKey());
		e.setRole(role);
		e.setEvaluator(newGlobalEvaluator());
		return e;
	}

	public static PermissionOnClassificationWithSubjectList newPermissionOnClassificationWSL(
			String indice, PermissionType role, Organization org, Set<SubjectKey> subjects) {
		PermissionOnClassificationWithSubjectList e = new PermissionOnClassificationWithSubjectList();
		e.setContext(org.getContextKey());
		e.setRole(role);
		e.setEvaluator(newGlobalEvaluator());
		e.setAuthorizedSubjects(subjects);
		return e;
	}

	public static PermissionOnSubjects newPermissionOnSubjects(String indice,
			Organization org, Set<SubjectPermKey> subjects) {
		PermissionOnSubjects e = new PermissionOnSubjects();
		e.setContext(org.getContextKey());
		e.setEvaluator(newGlobalEvaluator());
		e.setRolesOnSubjects(subjects);
		return e;
	}

	public static PermissionOnSubjectsWithClassificationList newPermissionOnSubjectsWCL(
			String indice, Organization org,
			Set<SubjectPermKey> subjects, Set<ContextKey> contexts) {
		PermissionOnSubjectsWithClassificationList e = new PermissionOnSubjectsWithClassificationList();
		e.setContext(org.getContextKey());
		e.setEvaluator(newGlobalEvaluator());
		e.setRolesOnSubjects(subjects);
		e.setAuthorizedContexts(contexts);
		return e;
	}

	public static OperatorEvaluator newGlobalEvaluator(OperatorType type) {
		Set<AbstractEvaluator> evaluators = new HashSet<AbstractEvaluator>();
		evaluators.add(newMVUEvaluatorForGroup("esco:admin:central"));
		evaluators.add(newRoleEvaluatorForGroup("esco:admin:central"));
		// evaluators.add(newPrefEvaluatorForAttr("portletCtx", "test"));
		evaluators.add(newUserAttributeEvaluatorForAttr("uid", subject3,
				StringEvaluationMode.EXISTS));
		return newOperatorEvaluator(type, evaluators);
	}

	public static OperatorEvaluator newGlobalEvaluator() {
		return newGlobalEvaluator(OperatorType.OR);
	}

	public static UserMultivaluedAttributesEvaluator newMVUEvaluatorForGroup(
			final String group) {
		return newMultiValuedUserAttributeEvaluatorForAttr("isMemberOf", group,
				StringEvaluationMode.EQUALS);
	}

	public static UserGroupEvaluator newRoleEvaluatorForGroup(final String group) {
		UserGroupEvaluator ure = new UserGroupEvaluator(group);
		return ure;
	}

	// public static PreferenceEvaluator newPrefEvaluatorForAttr(final String
	// attribute, final String value) {
	// PreferenceEvaluator umv = new PreferenceEvaluator(attribute, value);
	// return umv;
	// }
	public static UserAttributesEvaluator newUserAttributeEvaluatorForAttr(
			final String attribute, final String value,
			final StringEvaluationMode mode) {
		UserAttributesEvaluator umv = new UserAttributesEvaluator(attribute,
				value, mode);
		return umv;
	}

	public static UserMultivaluedAttributesEvaluator newMultiValuedUserAttributeEvaluatorForAttr(
			final String attribute, final String value,
			final StringEvaluationMode mode) {
		UserMultivaluedAttributesEvaluator umv = new UserMultivaluedAttributesEvaluator(
				attribute, value, mode);
		return umv;
	}

	public static OperatorEvaluator newOperatorEvaluator(
			final OperatorType operator, Set<AbstractEvaluator> evaluators) {
		return new OperatorEvaluator(operator, evaluators);
	}

	public static Filter newFilterLDAP(final String pattern,
			final Organization organization) {
		return new Filter(pattern, FilterType.LDAP, organization);
	}

	public static Filter newFilterGroup(final String pattern,
			final Organization organization) {
		return new Filter(pattern, FilterType.GROUP, organization);
	}

	public static Reader newReader(final String indice) {
		return new Reader("lecture_" + indice, "lecture des trucs " + indice,
				"DESC des " + indice, Sets.newHashSet(ItemType.NEWS, ItemType.MEDIA, ItemType.RESOURCE));
	}

	public static Redactor newRedactor(final String indice) {
		return new Redactor("lecture_" + indice, "lecture des trucs " + indice, "DESC des " + indice
            , WritingFormat.HTML, WritingMode.TARGETS_ON_ITEM, 1);
	}

	/*
	 * private Topic newTopic(boolean insert) { Topic topic = new Topic();
	 * topic.setCategoryId(newCategory(true).getCatId());
	 * topic.setCreatedBy("F08001ut"); Calendar c = Calendar.getInstance();
	 * c.set(Calendar.MILLISECOND, 0); topic.setCreationDate(c.getTime());
	 * topic.setDescription("Desc For topic"); topic.setLang("fr");
	 * topic.setLastUpdateDate(c.getTime()); topic.setName("Topic Name");
	 * topic.setPublicView(true); topic.setRefreshFrequency(1);
	 * topic.setRefreshPeriod("hour"); topic.setRssAllowed(true); if (insert)
	 * topicDao.insert(topic); return topic; }
	 */

	public static Category newCategory(final String indice, final Publisher pub) {
		// Category cat = new Category();
		// cat.setDescription("desc");
		// cat.setCreatedBy("F08001ut");
		// //cat.setDisplayOrder(0);
		// cat.setName("Cat_test "+indice);
		// cat.setPublisher(new Publisher(entity, reader, redactor,
		// PermissionOnClassificationWithSubjectList.class, true));
		// Calendar c = Calendar.getInstance();
		// c.set(Calendar.MILLISECOND, 0);
		// cat.setCreationDate(c.getTime());
		// return cat;

		return new Category(true, "CAT " + indice, "ICON_URL" + indice,
				"fr_fr", 3600, 200, getRandomAccessType(), "A DESC" + indice,
				getRandomDisplayOrderType(), pub);
	}

	public static Category newCategory(final String indice) {
		return new Category(true, "CAT " + indice, "ICON_URL" + indice,
				"fr_fr", 3600, 200, getRandomAccessType(), "A DESC" + indice,
				getRandomDisplayOrderType(), newPublisher(indice));
	}

	public static Publisher newPublisher(final String indice) {
		return new Publisher(newOrganization(indice), newReader(indice),
				newRedactor(indice), PermissionClass.CONTEXT,
				rnd.nextBoolean());
	}

	public static InternalFeed newInternalFeed(final String indice) {
		return new InternalFeed(true, "CAT " + indice, "ICON_URL" + indice,
				"fr_fr", 3600, 200, getRandomAccessType(), "A DESC" + indice,
				getRandomDisplayOrderType(), newPublisher(indice),
				newCategory(indice));
	}

	public static InternalFeed newInternalFeed(final String indice,
			final Publisher publisher) {
		return new InternalFeed(true, "CAT " + indice, "ICON_URL" + indice,
				"fr_fr", 3600, 200, getRandomAccessType(), "A DESC" + indice,
				getRandomDisplayOrderType(), publisher, newCategory(indice));
	}

	public static InternalFeed newInternalFeed(final String indice,
			final Publisher publisher, final Category category) {
		return new InternalFeed(true, "CAT " + indice, "ICON_URL" + indice,
				"fr_fr", 3600, 200, getRandomAccessType(), "A DESC" + indice,
				getRandomDisplayOrderType(), publisher, category);
	}

	public static News newNews(final String indice) {
		return new News("Titre " + indice, "enclosure" + indice, "body"
				+ indice, d1.toLocalDate(), d3.toLocalDate(), d2,
				null, getRandomItemStatus(), "summary" + indice,
				newOrganization(indice), newRedactor(indice));
	}

	public static News newNews(final String indice,
			final Organization organization) {
		return new News("Titre " + indice, "enclosure" + indice, "body"
				+ indice, d1.toLocalDate(), d3.toLocalDate(), d2,
				null, getRandomItemStatus(), "summary" + indice,
				organization, newRedactor(indice));
	}

	public static News newNews(final String indice,
			final Organization organization, final Redactor redactor) {
		return new News("Titre " + indice, "enclosure" + indice, "body"
				+ indice, d1.toLocalDate(), d3.toLocalDate(), d2,
				null, getRandomItemStatus(), "summary" + indice,
				organization, redactor);
	}

	public static Subscriber newSubscriber(final ContextKey context) {
		return new Subscriber(subjectKey3, context, getRandomSubscribeType());

	}

	public static AccessType getRandomAccessType() {
		return AccessType
				.valueOf(rnd.nextInt(AccessType.values().length - 1) + 1);
	}

	public static DisplayOrderType getRandomDisplayOrderType() {
		return DisplayOrderType
				.valueOf(rnd.nextInt(DisplayOrderType.values().length - 1));
	}

	public static ItemStatus getRandomItemStatus() {
		return ItemStatus.valueOf(rnd.nextInt(ItemStatus.values().length - 1));
	}

	public static SubscribeType getRandomSubscribeType() {
		return SubscribeType
				.valueOf(rnd.nextInt(SubscribeType.values().length - 1));
	}

}
