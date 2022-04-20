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
package org.esupportail.publisher.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.repository.predicates.FilterPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;

import com.mysema.commons.lang.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)//@RunWith(PowerMockRunner.class)
@SpringBootTest(classes = Application.class)
//@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@WebAppConfiguration
public class UserServiceTest {

	@Mock
	private IPermissionService permissionService;

	@Mock
	private ContextService contextService;

	@Mock
	private IExternalUserDao externalUserDao;

	@Mock
	private UserDTOFactory userDTOFactory;

	@Mock
	private FilterRepository filterRepository;

	@InjectMocks
	private UserService userService;

	@BeforeAll
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getUserFromSearchInCtx_ShouldBeEmpty_becauseSearchIsNull() {
		//GIVEN
		String search = null;
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);

		//THEN
		assertThat(resultList.size(), equalTo(0));

	}

	@Test
	public void getUserFromSearchInCtx_ShouldBeEmpty_becauseSearchIsEmpty() {
		//GIVEN
		String search = "";
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);

		//THEN
		assertThat(resultList.size(), equalTo(0));
	}

	@Test
	public void getUserFromSearchInCtx_ShouldBeEmpty_becauseLengthSearchSmallerThan3() {
		//GIVEN
		String search = "se";
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);

		//THEN
		assertThat(resultList.size(), equalTo(0));
	}

	@Test
	public void getUserFromSearchInCtx_ifPermsIsNull_ShouldBeEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext()
				.getAuthentication(), contextKey)).thenReturn(null);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext()
				.getAuthentication(), contextKey1)).thenReturn(null);

		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);

		//THEN
		assertThat(resultList.size(), equalTo(0));
	}

	@Test
	public void getUserFromSearchInCtx__IfPermissionTypeADMIN_RootCtxIsNull_shouldBeIsEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);
		Filter filter = new Filter();

		List<IExternalUser> externalUserList = new ArrayList<>();
		List<UserDTO> userList = new ArrayList<>();

		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(null);
		when(externalUserDao.getUsersWithFilter(null, search)).thenReturn(externalUserList);
		when(userDTOFactory.asDTOList(externalUserList, false)).thenReturn(userList);

		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);

		//THEN
		verify(externalUserDao).getUsersWithFilter(filter.getPattern(), search);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(userDTOFactory).asDTOList(externalUserList, false);
		assertThat(resultList.size(), equalTo(0));
	}

	@Test
	public void getUserFromSearchInCtx__IfPermissionTypeADMIN_shouldBeIsEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);
		Optional<Filter> optionalFilter = Optional.empty();
		String filter = optionalFilter.map(Filter::getPattern).orElse(null);

		List<IExternalUser> externalUserList = new ArrayList<>();
		List<UserDTO> userList = new ArrayList<>();

		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(contextKey);
		when(filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(contextKey.getKeyId(),
				FilterType.LDAP))).thenReturn(optionalFilter);
		when(externalUserDao.getUsersWithFilter(filter, search)).thenReturn(externalUserList);
		when(userDTOFactory.asDTOList(externalUserList, false)).thenReturn(userList);

		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);

		//THEN
		verify(externalUserDao).getUsersWithFilter(filter, search);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(userDTOFactory).asDTOList(externalUserList, false);
		assertThat(resultList.size(), equalTo(0));
	}

	@Test
	public void getUserFromSearchInCtx__IfPermissionTypeIsCONTRIBUTORAndPermsSecondIsNull_shouldBeIsEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.CONTRIBUTOR, null);

		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);

		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);

		//THEN
//		verify(permissionService, times(2));
		assertThat(resultList.size(), equalTo(0));
	}

}