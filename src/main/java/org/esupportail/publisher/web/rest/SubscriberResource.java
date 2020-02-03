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
package org.esupportail.publisher.web.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.SubjectContextKey;
import org.esupportail.publisher.domain.SubjectKeyExtended;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.esupportail.publisher.repository.ClassificationRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.repository.predicates.SubscriberPredicates;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.CompositeKeyExtendedDTOFactory;
import org.esupportail.publisher.service.factories.SubscriberResolvedDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyExtendedDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberResolvedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

/**
 * REST controller for managing Subscriber.
 */
@RestController
@RequestMapping("/api")
public class SubscriberResource {

	private final Logger log = LoggerFactory.getLogger(SubscriberResource.class);

	@Inject
	private SubscriberRepository subscriberRepository;

	@Inject
	private ClassificationRepository<AbstractClassification> classificationRepository;

	@Inject
	private ItemRepository<AbstractItem> itemRepository;

	@Inject
	private SubscriberResolvedDTOFactory subscriberResolvedDTOFactory;

    @Inject
    private transient CompositeKeyExtendedDTOFactory<SubjectKeyExtendedDTO, SubjectKeyExtended, String, String, SubjectType> subjectKeyExtendedConverter;
    @Inject
    @Named("contextKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> contextConverter;

	/**
	 * POST /subscribers -> Create a new subscriber.
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
			+ " && @permissionService.canEditCtxTargets(authentication, #subscriber.subjectCtxId.context)")
	@Timed
	public ResponseEntity<Void> create(@Validated @RequestBody Subscriber subscriber) throws URISyntaxException,
			UnsupportedEncodingException {
		log.debug("REST request to save Subscriber : {}", subscriber);
		Optional<Subscriber> optionalSubscriber =  subscriberRepository.findById(subscriber.getId());
		Subscriber subscriberRepo = optionalSubscriber == null || !optionalSubscriber.isPresent()? null : optionalSubscriber.get();

		if (subscriberRepo != null) {
			return ResponseEntity.badRequest().header("Failure", "The subscriber should not already exist").build();
		}
		// TODO: check if the context can support subscribers
		EnumSet<ContextType> ctxTypes = EnumSet.of(ContextType.ORGANIZATION, ContextType.PUBLISHER);
		boolean subscribersOnCtx = false;
		switch (subscriber.getSubjectCtxId().getContext().getKeyType()) {
		case ORGANIZATION:
			subscribersOnCtx = true;
			break;
		case PUBLISHER:
			subscribersOnCtx = true;
			break;
		case CATEGORY:

		case FEED:
			Optional<AbstractClassification> optionalAbstractClassification =  classificationRepository.findById(subscriber.getSubjectCtxId().getContext()
					.getKeyId());
			AbstractClassification classif = optionalAbstractClassification == null || !optionalAbstractClassification.isPresent()? null : optionalAbstractClassification.get();
			if (classif != null
					&& !WritingMode.TARGETS_ON_ITEM.equals(classif.getPublisher().getContext().getRedactor()
							.getWritingMode()) && classif.getPublisher().isHasSubPermsManagement()) {
				subscribersOnCtx = true;
			}
			break;
		case ITEM:
			Optional<AbstractItem> optionalAbstractItem =  itemRepository.findById(subscriber.getSubjectCtxId().getContext().getKeyId());
			AbstractItem item = optionalAbstractItem == null || !optionalAbstractItem.isPresent()? null : optionalAbstractItem.get();
			if (item != null && WritingMode.TARGETS_ON_ITEM.equals(item.getRedactor().getWritingMode())) {
				subscribersOnCtx = true;
			}
			break;
		default:
			// non bloquant
			log.warn("ContextType unknown !");
			break;
		}
		if (!subscribersOnCtx) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		subscriberRepository.save(subscriber);
		String composedIdURL = DatatypeConverter.printBase64Binary(subscriber.getId().getSubject().getKeyValue().getBytes(StandardCharsets.UTF_8)) + "/";
		composedIdURL += subscriber.getId().getSubject().getKeyType().getId() + "/";
		composedIdURL += subscriber.getId().getSubject().getKeyAttribute() + "/";
		composedIdURL += subscriber.getId().getContext().getKeyId() + "/";
		composedIdURL += subscriber.getId().getContext().getKeyType().name();
		log.debug(composedIdURL);
		return ResponseEntity.created(
				new URI("/api/subscribers/" + UriUtils.encodeQuery(composedIdURL, StandardCharsets.UTF_8.name())))
				.build();
	}

	/**
	 * PUT  /subscribers -> Updates an existing subscriber.
	 */
	/*    @RequestMapping(value = "/subscribers",
	        method = RequestMethod.PUT,
	        produces = MediaType.APPLICATION_JSON_VALUE)
	    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
	        + " && @permissionService.canEditCtxTargets(authentication, #subscriber.subjectCtxId.context)")
	    @Timed
	    public ResponseEntity<Void> update(@RequestBody Subscriber subscriber) throws URISyntaxException {
	        log.debug("REST request to update Subscriber : {}", subscriber);
	        if (subscriberRepository.findOne(subscriber.getId()) == null) {
	            return ResponseEntity.badRequest().header("Failure", "The subscriber must exist to update it").build();
	        }
	        subscriberRepository.save(subscriber);
	        return ResponseEntity.ok().build();
	    }
	*/
	/**
	 * GET /subscribers -> get all the subscribers.
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_USER)
	@PostFilter("hasPermission(filterObject.subjectCtxId.context.keyId, filterObject.subjectCtxId.context.keyType, '"
			+ SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public List<Subscriber> getAll() {
		log.debug("REST request to get all Subscribers");
		return subscriberRepository.findAll();
	}

	/**
	 * GET /subscribers -> get all the subscribers.
	 */
	@RequestMapping(value = "/subscribers/{ctx_type}/{ctx_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
			+ " && hasPermission(#ctxId, #ctxType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public List<SubscriberResolvedDTO> getAllOf(@PathVariable("ctx_id") Long ctxId,
			@PathVariable("ctx_type") ContextType ctxType) {
		log.debug("REST request to get all Subscribers of context : {} {}", ctxType, ctxId);
		return subscriberResolvedDTOFactory.asDTOList(Lists.newArrayList(subscriberRepository
				.findAll(SubscriberPredicates.onCtx(new ContextKey(ctxId, ctxType)))));
	}

	/**
	 * GET  /subscribers -> get all the subscribers.
	 */
	//   @RequestMapping(value = "/subscribers",
	//            method = RequestMethod.GET,
	//            produces = MediaType.APPLICATION_JSON_VALUE)
	//    @Timed
	//    public ResponseEntity<List<Subscriber>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
	//                                  @RequestParam(value = "per_page", required = false) Integer limit)
	//        throws URISyntaxException {
	//       Page<Subscriber> page = subscriberRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscribers", offset, limit);
	//        return new ResponseEntity<List<Subscriber>>(page.getContent(), headers, HttpStatus.OK);
	//    }

	/**
	    * GET  /subscribers -> get all the subscribers.
	    */
	//    @RequestMapping(value = "/subscribers",
	//            method = RequestMethod.GET,
	//            produces = MediaType.APPLICATION_JSON_VALUE)
	//    @Timed
	//    public ResponseEntity<List<Subscriber>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
	//                                  @RequestParam(value = "per_page", required = false) Integer limit)
	//        throws URISyntaxException {
	//        Page<Subscriber> page = subscriberRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscribers", offset, limit);
	//        return new ResponseEntity<List<Subscriber>>(page.getContent(), headers, HttpStatus.OK);
	//    }

	/**
	 * GET /subscribers/:id -> get the "id" subscriber.
	 */
	@RequestMapping(value = "/subscribers/{subject_id}/{subject_type}/{subject_attribute}/{ctx_id}/{ctx_type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER + " "
			+ "&& hasPermission(#ctxId, #ctxType, '" + SecurityConstants.PERM_LOOKOVER + "')")
	@Timed
	public ResponseEntity<Subscriber> get(@PathVariable("subject_id") String subjectId,
			@PathVariable("subject_type") int subjectType, @PathVariable("subject_attribute") String subjectAttr,
			@PathVariable("ctx_id") Long ctxId, @PathVariable("ctx_type") ContextType ctxType,
			HttpServletResponse response) {
		final SubjectContextKey id = new SubjectContextKey(new SubjectKeyExtended(new String(DatatypeConverter.parseBase64Binary(subjectId)), subjectAttr,
            SubjectType.valueOf(subjectType)), new ContextKey(ctxId, ctxType));
		log.debug("REST request to get SubjectContextKey : {}", id);
		Optional<Subscriber> optionalSubscriber =  subscriberRepository.findById(id);
		Subscriber subscriber = optionalSubscriber == null || !optionalSubscriber.isPresent()? null : optionalSubscriber.get();
		if (subscriber == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(subscriber, HttpStatus.OK);
	}


	/**
	 * DELETE /subscribers/:id -> delete the "id" subscriber.
     * needed until angularjs version >= 1.6.4
	 */
	@RequestMapping(value = "/subscribers/{subject_id}/{subject_type}/{subject_attribute}/{ctx_id}/{ctx_type}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
			+ " && @permissionService.canEditCtxTargets(authentication, #subscriber.subjectCtxId.context)")
	@Timed
	public void delete(@PathVariable("subject_id") String subjectId, @PathVariable("subject_type") int subjectType,
			@PathVariable("subject_attribute") String subjectAttr, @PathVariable("ctx_id") Long ctxId,
			@PathVariable("ctx_type") ContextType ctxType) {
		final SubjectContextKey id = new SubjectContextKey(new SubjectKeyExtended(new String(DatatypeConverter.parseBase64Binary(subjectId)), subjectAttr,
            SubjectType.valueOf(subjectType)), new ContextKey(ctxId, ctxType));
		log.debug("REST request to delete Subscriber : {}", id);
		subscriberRepository.deleteById(id);
	}

    /**
     * DELETE /subscribers/ -> delete the "provided" subscriber.
     */
    @RequestMapping(value = "/subscribers", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && @permissionService.canEditCtxTargets(authentication, #subjectContextKey.contextKey.keyId, #subjectContextKey.contextKey.keyType)")
    @Timed
    public void delete(@Validated @RequestBody final SubjectContextKeyDTO subjectContextKey) {
        log.debug("REST request to delete Subscriber with subjectContextKey: {}", subjectContextKey);
        final SubjectContextKey modelKey = new SubjectContextKey(
            subjectKeyExtendedConverter.convertToModelKey(subjectContextKey.getSubjectKey()),
            contextConverter.convertToModelKey(subjectContextKey.getContextKey()));
        subscriberRepository.deleteById(modelKey);
    }
}
