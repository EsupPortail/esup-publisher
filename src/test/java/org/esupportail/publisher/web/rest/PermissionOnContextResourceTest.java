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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.PermissionOnContext;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.repository.EvaluatorRepository;
import org.esupportail.publisher.repository.ObjTest;
import org.esupportail.publisher.repository.PermissionOnContextRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the PermissionOnContextResource REST controller.
 *
 * @see PermissionOnContextResource
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Slf4j
public class PermissionOnContextResourceTest {

	private static final Long DEFAULT_CTXID = 0L;
	private static final Long UPDATED_CTXID = 1L;
	private static final ContextType DEFAULT_CTX_TYPE = ContextType.ORGANIZATION;
	private static final ContextType UPDATED_CTX_TYPE = ContextType.CATEGORY;
	private static final ContextKey DEFAULT_CTX = new ContextKey(DEFAULT_CTXID,
			DEFAULT_CTX_TYPE);
	private static final ContextKey UPDATED_CTX = new ContextKey(UPDATED_CTXID,
			UPDATED_CTX_TYPE);
	private static final PermissionType DEFAULT_ROLE = PermissionType.ADMIN;
	private static final PermissionType UPDATED_ROLE = PermissionType.MANAGER;

	@Inject
	private PermissionOnContextRepository permissionOnContextRepository;
	@Inject
    private IPermissionService permissionService;
	@Inject
	private EvaluatorRepository<AbstractEvaluator> evaluatorRepository;
	@Inject
    private UserRepository userRepo;
	@Inject
	private UserDTOFactory userDTOFactory;

	private MockMvc restPermissionOnContextMockMvc;

	private PermissionOnContext permissionOnContext;
	private AbstractEvaluator evaluator;

	@PostConstruct
	public void setup() {
		//closeable = MockitoAnnotations.openMocks(this);
		PermissionOnContextResource permissionOnContextResource = new PermissionOnContextResource();
		EvaluatorResource evaluatorResource = new EvaluatorResource();
		ReflectionTestUtils.setField(permissionOnContextResource,
				"permissionOnContextRepository", permissionOnContextRepository);
        ReflectionTestUtils.setField(permissionOnContextResource,
            "permissionService", permissionService);
		ReflectionTestUtils.setField(evaluatorResource, "evaluatorRepository",
				evaluatorRepository);
		this.restPermissionOnContextMockMvc = MockMvcBuilders.standaloneSetup(
				permissionOnContextResource).build();

	    Optional<User> optionalUser = userRepo.findOne(QUser.user.login.like("system"));
        User userPart = optionalUser == null || !optionalUser.isPresent() ? null : optionalUser.get();
        UserDTO userDTOPart = userDTOFactory.from(userPart);
        CustomUserDetails userDetails = new CustomUserDetails(userDTOPart, userPart, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        Authentication authentication = new TestingAuthenticationToken(userDetails, "password", Lists.newArrayList(userDetails.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@BeforeEach
	public void initTest() {
		// evaluator = evaluatorRepository.saveAndFlush(ObjTest
		// .newGlobalEvaluator());
		evaluator = ObjTest.newGlobalEvaluator();
		permissionOnContext = new PermissionOnContext();
		permissionOnContext.setContext(DEFAULT_CTX);
		permissionOnContext.setRole(DEFAULT_ROLE);
		permissionOnContext.setEvaluator(evaluator);
	}

	@Test
	@Transactional
	public void createPermissionOnContext() throws Exception {
		// Validate the database is empty
		assertThat(permissionOnContextRepository.findAll(), hasSize(0));

		// Create the PermissionOnContext
		restPermissionOnContextMockMvc
				.perform(post("/api/permissionOnContexts")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(TestUtil.convertObjectToJsonBytes(permissionOnContext)))
				.andExpect(status().isCreated());

		// Validate the PermissionOnContext in the database
		List<PermissionOnContext> permissionOnContexts = permissionOnContextRepository
				.findAll();
		assertThat(permissionOnContexts, hasSize(1));
		PermissionOnContext testPermissionOnContext = permissionOnContexts
				.iterator().next();
		assertThat(testPermissionOnContext.getContext(), equalTo(DEFAULT_CTX));
		assertThat(testPermissionOnContext.getRole(), equalTo(DEFAULT_ROLE));
		assertThat(testPermissionOnContext.getEvaluator(), equalTo(evaluator));
	}

	@Test
	@Transactional
	public void getAllPermissionOnContexts() throws Exception {
		// Initialize the database
		permissionOnContextRepository.saveAndFlush(permissionOnContext);

		// Get all the permissionOnContexts
		restPermissionOnContextMockMvc
				.perform(get("/api/permissionOnContexts"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].id").value(permissionOnContext.getId().intValue()))
				.andExpect(jsonPath("$.[0].context.keyId").value(DEFAULT_CTXID.intValue()))
				.andExpect(jsonPath("$.[0].context.keyType").value(DEFAULT_CTX_TYPE.toString()))
				.andExpect(jsonPath("$.[0].role").value(DEFAULT_ROLE.name()))
				.andExpect(jsonPath("$.[0].evaluator.id").value(evaluator.getId().intValue()));
	}

	@Test
	@Transactional
	public void getPermissionOnContext() throws Exception {
		// Initialize the database
		permissionOnContextRepository.saveAndFlush(permissionOnContext);

		// Get the permissionOnContext
		restPermissionOnContextMockMvc
				.perform(get("/api/permissionOnContexts/{id}",permissionOnContext.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(permissionOnContext.getId().intValue()))
				.andExpect(jsonPath("$.context.keyId").value(DEFAULT_CTXID.intValue()))
				.andExpect(jsonPath("$.context.keyType").value(DEFAULT_CTX_TYPE.toString()))
				.andExpect(jsonPath("$.role").value(DEFAULT_ROLE.name()))
				.andExpect(jsonPath("$.evaluator.id").value(evaluator.getId().intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingPermissionOnContext() throws Exception {
		// Get the permissionOnContext
		restPermissionOnContextMockMvc.perform(
				get("/api/permissionOnContexts/{id}", 1L)).andExpect(
				status().isNotFound());
	}

	@Test
	@Transactional
	public void updatePermissionOnContext() throws Exception {
		// Initialize the database
		permissionOnContextRepository.saveAndFlush(permissionOnContext);

		// Update the permissionOnContext
		permissionOnContext.setContext(UPDATED_CTX);
		permissionOnContext.setRole(UPDATED_ROLE);
		// permissionOnContext.setEvaluator(UPDATED_EVALUATOR);
		restPermissionOnContextMockMvc
				.perform(
						put("/api/permissionOnContexts")
								.contentType(TestUtil.APPLICATION_JSON_UTF8)
								.content(TestUtil.convertObjectToJsonBytes(permissionOnContext)))
				.andExpect(status().isOk());

		// Validate the PermissionOnContext in the database
		List<PermissionOnContext> permissionOnContexts = permissionOnContextRepository
				.findAll();
		assertThat(permissionOnContexts, hasSize(1));
		PermissionOnContext testPermissionOnContext = permissionOnContexts
				.iterator().next();
		assertThat(testPermissionOnContext.getContext(), equalTo(UPDATED_CTX));
		assertThat(testPermissionOnContext.getRole(), equalTo(UPDATED_ROLE));
		assertThat(testPermissionOnContext.getEvaluator(), equalTo(evaluator));
		List<AbstractEvaluator> evaluators = evaluatorRepository.findAll();
		assertThat(evaluators, hasSize(4));
	}

	@Test
	@Transactional
	public void deletePermissionOnContext() throws Exception {
		// Initialize the database
		permissionOnContextRepository.saveAndFlush(permissionOnContext);

        log.debug("Try to delete {}", permissionOnContext);
		// Get the permissionOnContext
		restPermissionOnContextMockMvc.perform(
				delete("/api/permissionOnContexts/{id}",
						permissionOnContext.getId()).accept(
						TestUtil.APPLICATION_JSON_UTF8)).andExpect(
				status().isOk());

		// Validate the database is empty
		List<PermissionOnContext> permissionOnContexts = permissionOnContextRepository
				.findAll();
		assertThat(permissionOnContexts, hasSize(0));
		List<AbstractEvaluator> evaluators = evaluatorRepository.findAll();
		assertThat(evaluators, hasSize(0));
	}
}