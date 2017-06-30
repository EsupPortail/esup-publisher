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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.QUser;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.service.UserService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserService
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest({SecurityUtils.class})
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AccountResourceTest {

	@Inject
	private UserRepository userRepository;
    @Inject
    private UserDTOFactory userDTOFactory;

//	@Mock
//	private UserService userService;

	private MockMvc restUserMockMvc;

	@Before
	public void setup() {
		//MockitoAnnotations.initMocks(this);
		AccountResource accountResource = new AccountResource();
		//ReflectionTestUtils.setField(accountResource, "userRepository", userRepository);
		//ReflectionTestUtils.setField(accountResource, "userService", userService);
		this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
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
        User userPart = userRepository.findOne(QUser.user.login.like("admin"));
		UserDTO userDTOPart = userDTOFactory.from(userPart);
		CustomUserDetails user = new CustomUserDetails(userDTOPart, userPart, Lists.newArrayList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));

        PowerMockito.mockStatic(SecurityUtils.class);
            PowerMockito.when(SecurityUtils.getCurrentUserDetails()).thenReturn(user);

		restUserMockMvc.perform(get("/api/account").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.user.login").value("admin"))
				.andExpect(jsonPath("$.user.displayName").exists())
				.andExpect(jsonPath("$.roles.[0]").value(AuthoritiesConstants.ADMIN));
	}

	@Test
	public void testGetUnknownAccount() throws Exception {
		PowerMockito.mockStatic(SecurityUtils.class);
        PowerMockito.when(SecurityUtils.getCurrentUserDetails()).thenReturn(null);

		restUserMockMvc.perform(get("/api/account").accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isInternalServerError());
	}
}
