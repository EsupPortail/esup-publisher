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

import org.esupportail.publisher.Application;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class UserResourceTest {

	@Inject
	private UserRepository userRepository;

	private MockMvc restUserMockMvc;

	@Inject
	private UserDTOFactory userFactory;

	@Before
	public void setup() {
		UserResource userResource = new UserResource();
		ReflectionTestUtils.setField(userResource, "userRepository",userRepository);
		ReflectionTestUtils.setField(userResource, "userFactory", userFactory);
		this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
	}

	@Test
	public void testGetExistingUser() throws Exception {
		restUserMockMvc.perform(
			get("/api/users/extended/admin").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.login").value("admin"));
	}

	@Test
	public void testGetUnknownUser() throws Exception {
		restUserMockMvc.perform(
			get("/api/users/unknown").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

    @Test
    public void testGetExtendedUnknownUser() throws Exception {
        restUserMockMvc.perform(
            get("/api/users/extended/unknown").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
    }
}