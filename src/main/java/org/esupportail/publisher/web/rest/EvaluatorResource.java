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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.repository.EvaluatorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api")
public class EvaluatorResource {

	private final Logger log = LoggerFactory.getLogger(EvaluatorResource.class);

	@Inject
	private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;

	/**
	 * POST /evaluators -> Create a new evaluator.
	 */
	@RequestMapping(value = "/evaluators", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody AbstractEvaluator evaluator) throws URISyntaxException {
        log.debug("REST request to save Evaluator : {}", evaluator);
        if (evaluator.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new Evaluator cannot already have an ID").build();
        }
        evaluatorRepository.save(evaluator);
        return ResponseEntity.created(new URI("/api/evaluators/" + evaluator.getId())).build();
    }

    /**
     * PUT  /operatorEvaluators -> Updates an existing operatorEvaluator.
     */
    @RequestMapping(value = "/evaluators", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestBody AbstractEvaluator evaluator) throws URISyntaxException {
        log.debug("REST request to update Evaluator : {}", evaluator);
        if (evaluator.getId() == null) {
            return create(evaluator);
        }
        evaluatorRepository.save(evaluator);
        return ResponseEntity.ok().build();
    }

	/**
	 * GET /evaluators -> get all the evaluators.
	 */
	@RequestMapping(value = "/evaluators", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AbstractEvaluator> getAll() {
		log.debug("REST request to get all Evaluators");
		return evaluatorRepository.findAll();
	}

	/**
	 * GET /evaluators/:id -> get the "id" evaluator.
	 */
	@RequestMapping(value = "/evaluators/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AbstractEvaluator> get(@PathVariable Long id,
			HttpServletResponse response) {
		log.debug("REST request to get Evaluator : {}", id);
		Optional<AbstractEvaluator> optionalAbstractEvaluator =  evaluatorRepository.findById(id);
		AbstractEvaluator evaluator = optionalAbstractEvaluator.orElse(null);
		if (evaluator == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(evaluator, HttpStatus.OK);
	}

	/**
	 * DELETE /evaluators/:id -> delete the "id" evaluator.
	 */
	@RequestMapping(value = "/evaluators/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Evaluator : {}", id);
		evaluatorRepository.deleteById(id);
	}
}