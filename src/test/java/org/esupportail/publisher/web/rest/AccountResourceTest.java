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

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.service.UserService;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserService
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class AccountResourceTest {
	@Inject
	private UserRepository userRepository;
	private AutoCloseable closeable;
	private static MockMvc restUserMockMvc;

	@PostConstruct
	public void setup() {
		//closeable = MockitoAnnotations.openMocks(this);
		AccountResource accountResource = new AccountResource();
		//ReflectionTestUtils.setField(accountResource, "userRepository", userRepository);
		//ReflectionTestUtils.setField(accountResource, "userService", userService);
		restUserMockMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
	}

	@Test
	public void testNonAuthenticatedUser() throws Exception {
		restUserMockMvc.perform(get("/api/authenticate").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(""));
	}

	@Test
	public void testAuthenticatedUser() throws Exception {
		restUserMockMvc.perform(get("/api/authenticate").with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setRemoteUser("user");
				return request;
			}
		}).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string("user"));
	}

	@Test
	public void testGetExistingAccount() throws Exception {
		Optional<User> optionalUser = userRepository.findOne(QUser.user.login.like("admin"));
        User userPart = optionalUser == null || !optionalUser.isPresent() ? null : optionalUser.get();
		UserDTO userDTOPart = new UserDTO("admin", "admin", true, true);
		CustomUserDetails user = new CustomUserDetails(userDTOPart, userPart, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));

		try (MockedStatic mocked = mockStatic(SecurityUtils.class)) {
			when(SecurityUtils.getCurrentUserDetails()).thenReturn(user);

			restUserMockMvc.perform(get("/api/account").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.user.login").value("admin"))
					.andExpect(jsonPath("$.user.displayName").exists())
					.andExpect(jsonPath("$.roles.[0]").value(AuthoritiesConstants.ADMIN));
		}
	}

	@Test
	public void testGetUnknownAccount() throws Exception {
		try (MockedStatic mocked = mockStatic(SecurityUtils.class)) {
			when(SecurityUtils.getCurrentUserDetails()).thenReturn(null);

			restUserMockMvc.perform(get("/api/account").accept(MediaType.APPLICATION_JSON)).andExpect(
					status().isInternalServerError());
		}
	}
}