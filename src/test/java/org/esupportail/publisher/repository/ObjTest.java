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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.domain.InternalFeed;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.PermissionOnClassificationWithSubjectList;
import org.esupportail.publisher.domain.PermissionOnContext;
import org.esupportail.publisher.domain.PermissionOnSubjects;
import org.esupportail.publisher.domain.PermissionOnSubjectsWithClassificationList;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.SubjectPermKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.ClassificationDecorType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.enums.WritingFormat;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.domain.evaluators.OperatorEvaluator;
import org.esupportail.publisher.domain.evaluators.UserAttributesEvaluator;
import org.esupportail.publisher.domain.evaluators.UserGroupEvaluator;
import org.esupportail.publisher.domain.evaluators.UserMultivaluedAttributesEvaluator;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberFormDTO;

import com.google.common.collect.Sets;

public final class ObjTest {

	public final static String subject1 = "user";
	public final static String subject2 = "system";
	public final static String group1 = "cfa:Etablissements:ECOLE HUBERT CURIEN_0180865T:Apprenti INGE 1ère année:Eleves_ERE IGR 1ère année";
	public final static String group2 = "esco:admin:central";
	public final static String subject3 = "admin";
	public final static String subjectAttrUAI = "0450822X";
	public final static String subjectAttrKeyUAI = "ESCOUAICourant";

	// Must be a Person
	public final static SubjectKey subjectKey1 = new SubjectKey(subject1, SubjectType.PERSON);
	// Must be a Person
	public final static SubjectKey subjectKey2 = new SubjectKey(subject2, SubjectType.PERSON);
	// Must be a Person
	public final static SubjectKey subjectKey3 = new SubjectKey(subject3, SubjectType.PERSON);
	// A Person attribut
	public final static SubjectKeyExtended subjectKeyFromUserAttr = new SubjectKeyExtended(subjectAttrUAI,
			subjectAttrKeyUAI, SubjectType.PERSON_ATTR);
	//Must be a Group
	public final static SubjectKey subjectKey4 = new SubjectKey(group1, SubjectType.GROUP);
	//Must be a Group
	public final static SubjectKey subjectKey5 = new SubjectKey(group2, SubjectType.GROUP);

	public final static SubjectPermKey subjectPerm1 = new SubjectPermKey(subjectKey1, false);
	public final static SubjectPermKey subjectPerm1WithValidation = new SubjectPermKey(subjectKey1, true);
	public final static SubjectPermKey subjectPerm2 = new SubjectPermKey(subjectKey2, false);
	public final static SubjectPermKey subjectPerm3 = new SubjectPermKey(subjectKey3, false);
	public final static SubjectPermKey subjectPerm4 = new SubjectPermKey(subjectKey4, false);

	public final static SubjectPermKey[] subkeys1 = { subjectPerm1, subjectPerm2, subjectPerm3 };
	public final static SubjectPermKey[] subkeys2 = { subjectPerm2, subjectPerm3 };
	public final static SubjectPermKey[] subkeys3 = { subjectPerm1, subjectPerm2 };
	public final static SubjectPermKey[] subkeys4 = { subjectPerm3 };
	public final static SubjectPermKey[] subkeysEmpty = {};

	public final static Random rnd = new Random();

	public static Instant d1 = null;
	public static Instant d2 = null;
	public static Instant d3 = null;
	public static Instant d4 = null;
	public static Instant d5 = null;
	public static Instant d6 = null;
	public static Instant d7 = null;
	public static Instant d8 = null;
	public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

	static {

		// df.setLenient(false);
		// df.setTimeZone(TimeZone.getTimeZone("CET"));
		d1 = Instant.from(dateTimeFormatter.parse("2015-01-15 07:23:30"));
		d2 = Instant.from(dateTimeFormatter.parse("2015-02-15 07:23:30"));
		d3 = Instant.from(dateTimeFormatter.parse("2015-03-15 07:23:30"));
		d4 = Instant.from(dateTimeFormatter.parse("2016-01-15 07:23:30"));
		d5 = Instant.from(dateTimeFormatter.parse("2016-02-15 07:23:30"));
		d6 = Instant.from(dateTimeFormatter.parse("2016-03-15 07:23:30"));
		d7 = Instant.from(dateTimeFormatter.parse("2019-12-01 07:23:30"));
		d8 = Instant.from(dateTimeFormatter.parse("2020-01-15 07:23:30"));
	}

	public static Organization newOrganization(String indice) {
		Organization e = new Organization();
		e.setDescription("A Desc" + indice);
		e.setDisplayOrder(200);
		e.setName("A TESTER" + indice);
		e.setDisplayName("À tester");
		return e;
	}

	public static PermissionOnContext newPermissionOnCtx(String indice, PermissionType role, Organization org) {
		PermissionOnContext e = new PermissionOnContext();
		e.setContext(org.getContextKey());
		e.setRole(role);
		e.setEvaluator(newGlobalEvaluator());
		return e;
	}

	public static PermissionOnClassificationWithSubjectList newPermissionOnClassificationWSL(String indice,
			PermissionType role, Organization org, Set<SubjectKey> subjects) {
		PermissionOnClassificationWithSubjectList e = new PermissionOnClassificationWithSubjectList();
		e.setContext(org.getContextKey());
		e.setRole(role);
		e.setEvaluator(newGlobalEvaluator());
		e.setAuthorizedSubjects(subjects);
		return e;
	}

	public static PermissionOnSubjects newPermissionOnSubjects(String indice, Organization org,
			Set<SubjectPermKey> subjects) {
		PermissionOnSubjects e = new PermissionOnSubjects();
		e.setContext(org.getContextKey());
		e.setEvaluator(newGlobalEvaluator());
		e.setRolesOnSubjects(subjects);
		return e;
	}

	public static PermissionOnSubjectsWithClassificationList newPermissionOnSubjectsWCL(String indice,
			Organization org, Set<SubjectPermKey> subjects, Set<ContextKey> contexts) {
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
		evaluators.add(newUserAttributeEvaluatorForAttr("uid", subject3, StringEvaluationMode.EXISTS));
		return newOperatorEvaluator(type, evaluators);
	}

	public static OperatorEvaluator newGlobalEvaluatorOnUser(OperatorType type, String user) {
		Set<AbstractEvaluator> evaluators = new HashSet<AbstractEvaluator>();
		evaluators.add(newUserAttributeEvaluatorForAttr("uid", user, StringEvaluationMode.EXISTS));
		return newOperatorEvaluator(type, evaluators);
	}

	public static OperatorEvaluator newGlobalEvaluatorOnGroup(OperatorType type, String group) {
		Set<AbstractEvaluator> evaluators = new HashSet<AbstractEvaluator>();
		evaluators.add(newMVUEvaluatorForGroup(group));
		return newOperatorEvaluator(type, evaluators);
	}

	public static OperatorEvaluator newGlobalEvaluator() {
		return newGlobalEvaluator(OperatorType.OR);
	}

	public static UserMultivaluedAttributesEvaluator newMVUEvaluatorForGroup(final String group) {
		return newMultiValuedUserAttributeEvaluatorForAttr("isMemberOf", group, StringEvaluationMode.EQUALS);
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
	public static UserAttributesEvaluator newUserAttributeEvaluatorForAttr(final String attribute, final String value,
			final StringEvaluationMode mode) {
		UserAttributesEvaluator umv = new UserAttributesEvaluator(attribute, value, mode);
		return umv;
	}

	public static UserMultivaluedAttributesEvaluator newMultiValuedUserAttributeEvaluatorForAttr(
			final String attribute, final String value, final StringEvaluationMode mode) {
		UserMultivaluedAttributesEvaluator umv = new UserMultivaluedAttributesEvaluator(attribute, value, mode);
		return umv;
	}

	public static OperatorEvaluator newOperatorEvaluator(final OperatorType operator, Set<AbstractEvaluator> evaluators) {
		return new OperatorEvaluator(operator, evaluators);
	}

	public static Filter newFilterLDAP(final String pattern, final Organization organization) {
		return new Filter(pattern, FilterType.LDAP, organization);
	}

	public static Filter newFilterGroup(final String pattern, final Organization organization) {
		return new Filter(pattern, FilterType.GROUP, organization);
	}

	public static Reader newReader(final String indice) {
		return new Reader("lecture_" + indice, "lecture des trucs " + indice, "DESC des " + indice, Sets.newHashSet(
				ItemType.NEWS, ItemType.MEDIA, ItemType.RESOURCE), Sets.newHashSet(ClassificationDecorType.COLOR,
				ClassificationDecorType.ENCLOSURE));
	}

	public static Redactor newRedactor(final String indice) {
		return new Redactor("lecture_" + indice, "lecture des trucs " + indice, "DESC des " + indice,
				WritingFormat.HTML, WritingMode.TARGETS_ON_ITEM, 1, false, 90);
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

		return new Category(true, "CAT " + indice, "ICON_URL" + indice, "fr_fr", 3600, 200, getRandomAccessType(),
				"A DESC" + indice, getRandomDisplayOrderType(), "#F44336", pub);
	}

	public static Category newCategory(final String indice) {
		return new Category(true, "CAT " + indice, "ICON_URL" + indice, "fr_fr", 3600, 200, getRandomAccessType(),
				"A DESC" + indice, getRandomDisplayOrderType(), "#F44336", newPublisher(indice));
	}

	public static Publisher newPublisher(final String indice) {
		return new Publisher(newOrganization(indice), newReader(indice), newRedactor(indice), "PUB " + indice,
				PermissionClass.CONTEXT, rnd.nextBoolean(), true, true);
	}

	public static InternalFeed newInternalFeed(final String indice) {
		return new InternalFeed(true, "CAT " + indice, "ICON_URL" + indice, "fr_fr", 3600, 200, getRandomAccessType(),
				"A DESC" + indice, getRandomDisplayOrderType(), "#F44336", newPublisher(indice), newCategory(indice));
	}

	public static InternalFeed newInternalFeed(final String indice, final Publisher publisher) {
		return new InternalFeed(true, "CAT " + indice, "ICON_URL" + indice, "fr_fr", 3600, 200, getRandomAccessType(),
				"A DESC" + indice, getRandomDisplayOrderType(), "#F44336", publisher, newCategory(indice));
	}

	public static InternalFeed newInternalFeed(final String indice, final Publisher publisher, final Category category) {
		return new InternalFeed(true, "CAT " + indice, "ICON_URL" + indice, "fr_fr", 3600, 200, getRandomAccessType(),
				"A DESC" + indice, getRandomDisplayOrderType(), "#F44336", publisher, category);
	}

	public static News newNews(final String indice) {
		return new News("Titre " + indice, "enclosure" + indice, "body" + indice, instantToLocalDate(d1), instantToLocalDate(d3),
				d2, null, getRandomItemStatus(), "summary" + indice, true, true, newOrganization(indice),
				newRedactor(indice));
	}

	public static News newNews(final String indice, final Organization organization) {
		return new News("Titre " + indice, "enclosure" + indice, "body" + indice, instantToLocalDate(d1), instantToLocalDate(d3),
				d2, null, getRandomItemStatus(), "summary" + indice, true, true, organization, newRedactor(indice));
	}

	public static News newNews(final String indice, final Organization organization, final Redactor redactor) {
		return new News("Titre " + indice, "enclosure" + indice, "body" + indice, instantToLocalDate(d1), instantToLocalDate(d3),
				d2, null, getRandomItemStatus(), "summary" + indice, true, true, organization, redactor);
	}

	public static News newNewsPublished(final String indice, final Organization organization, final Redactor redactor) {
		return new News("Titre " + indice, "enclosure" + indice, "body" + indice, instantToLocalDate(d7), instantToLocalDate(d8),
				d2, null, ItemStatus.PUBLISHED, "summary" + indice, true, true, organization, redactor);
	}

	public static Flash newFlash(final String indice, final Organization organization, final Redactor redactor) {
		return new Flash("Titre " + indice, "enclosure" + indice, "body" + indice, instantToLocalDate(d1), instantToLocalDate(d3),
				d2, null, getRandomItemStatus(), "summary" + indice, true, true, organization, redactor);
	}

	public static Attachment newAttachment(final String indice, final Organization organization, final Redactor redactor) {
		return new Attachment("Titre " + indice, "enclosure" + indice, instantToLocalDate(d1), instantToLocalDate(d3), d2, null,
				getRandomItemStatus(), "summary" + indice, true, true, organization, redactor);
	}

	public static Subscriber newSubscriber(final ContextKey context) {
		return new Subscriber(new SubjectKeyExtended(subjectKey3), context, getRandomSubscribeType());
	}

	public static Subscriber newSubscriberPerson(final ContextKey context) {
		return new Subscriber(new SubjectKeyExtended(subjectKey1), context, getRandomSubscribeType());
	}

	public static Subscriber newSubscriberPersonFromAttr(final ContextKey context) {
		return new Subscriber(subjectKeyFromUserAttr, context, getRandomSubscribeType());
	}

	public static Subscriber newSubscriberGroup(final ContextKey context) {
		return new Subscriber(new SubjectKeyExtended(subjectKey5), context, getRandomSubscribeType());
	}

	public static SubscriberFormDTO newSubscriberDTO() {
		return new SubscriberFormDTO(new SubjectDTO(subjectKey3.getKeyId(), subjectKey3.getKeyType()),
				getRandomSubscribeType());
	}

	public static SubscriberFormDTO newSubscriberDTOPerson() {
		return new SubscriberFormDTO(new SubjectDTO(subjectKey1.getKeyId(), subjectKey1.getKeyType()),
				getRandomSubscribeType());
	}

	public static SubscriberFormDTO newSubscriberDTOGroup() {
		return new SubscriberFormDTO(new SubjectDTO(subjectKey5.getKeyId(), subjectKey5.getKeyType()),
				getRandomSubscribeType());
	}

	public static AccessType getRandomAccessType() {
		return AccessType.valueOf(rnd.nextInt(AccessType.values().length - 1) + 1);
	}

	public static DisplayOrderType getRandomDisplayOrderType() {
		return DisplayOrderType.valueOf(rnd.nextInt(DisplayOrderType.values().length - 1));
	}

	public static ItemStatus getRandomItemStatus() {
		return ItemStatus.valueOf(rnd.nextInt(ItemStatus.values().length - 1));
	}

	public static SubscribeType getRandomSubscribeType() {
		return SubscribeType.valueOf(rnd.nextInt(SubscribeType.values().length - 1));
	}

	public static LocalDate instantToLocalDate(final Instant instant) {
	    return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
