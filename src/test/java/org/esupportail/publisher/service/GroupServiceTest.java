package org.esupportail.publisher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.ExternalUser;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.TreeJS;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.types.Predicate;

@RunWith(PowerMockRunner.class)
@SpringBootTest(classes = Application.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@WebAppConfiguration
public class GroupServiceTest {

	@Mock
	private IExternalGroupDao externalGroupDao;

	@Mock
	private UserDTOFactory userDTOFactory;

	@Mock
	private TreeJSDTOFactory treeJSDTOFactory;

	@Mock
	private IPermissionService permissionService;

	@Mock
	private ContextService contextService;

	@Mock
	private FilterRepository filterRepository;

	@InjectMocks
	private GroupService groupService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getUserMembers_shouldBeReturn2User() {
		// GIVEN
		String idCalled = "23";
		List<UserDTO> result = new ArrayList<>();
		ExternalUser externalUser = new ExternalUser();
		externalUser.setEmail("hjeyoud geu");
		UserDTO user1 = new UserDTO("36", "", false, false);
		UserDTO user2 = new UserDTO("25", "", true, true);
		List<IExternalUser> externalUserList = new ArrayList<>();
		externalUserList.add((IExternalUser)externalUser);
		result.add(user1);
		result.add(user2);

		// GIVEN SERVICES
		when(externalGroupDao.getDirectUserMembers(idCalled)).thenReturn(externalUserList);
		when(userDTOFactory.asDTOList(externalUserList, false)).thenReturn(result);

		// WHEN
		final List<UserDTO> userList = groupService.getUserMembers(idCalled);

		// THEN
		verify(externalGroupDao).getDirectUserMembers(idCalled);
		verify(userDTOFactory).asDTOList(externalUserList, false);
		assertThat(userList.size()).isEqualTo(2);
	}

	@Test
	public void getUserMembers_shouldBeEmpty_becauseUsersIsEmpty() {
		// GIVEN
		String idCalled = "23";
		List<UserDTO> result = new ArrayList<>();
		UserDTO user1 = new UserDTO("36", "", false, false);
		UserDTO user2 = new UserDTO("25", "", true, true);
		result.add(user1);
		result.add(user2);

		// GIVEN SERVICES
		when(externalGroupDao.getDirectUserMembers(idCalled)).thenReturn(Mockito.anyList());

		// WHEN
		final List<UserDTO> userList = groupService.getUserMembers(idCalled);

		// THEN
		verify(externalGroupDao).getDirectUserMembers(idCalled);
		assertThat(userList.size()).isEqualTo(0);
	}

	@Test
	public void getUserMembers_shouldBeEmpty_becauseUsersIsNull() {
		// GIVEN
		String idCalled = "23";
		List<UserDTO> result = new ArrayList<>();
		UserDTO user1 = new UserDTO("36", "", false, false);
		UserDTO user2 = new UserDTO("25", "", true, true);
		result.add(user1);
		result.add(user2);

		// GIVEN SERVICES
		when(externalGroupDao.getDirectUserMembers(idCalled)).thenReturn(null);

		// WHEN
		final List<UserDTO> userList = groupService.getUserMembers(idCalled);

		// THEN
		verify(externalGroupDao).getDirectUserMembers(idCalled);
		assertThat(userList.size()).isEqualTo(0);
	}

	@Test
	public void getGroupMembers_shoudlBeEmpty_becauseGroupsIsNull() {
		// GIVEN
		String idCalled = "15";

		//GIVEN SERVICE
		when(externalGroupDao.getDirectGroupMembers(idCalled, true)).thenReturn(null);

		//WHEN
		final List<TreeJS> treeList = groupService.getGroupMembers(idCalled);

		//THEN
		verify(externalGroupDao).getDirectGroupMembers(idCalled, true);
		assertThat(treeList.size()).isEqualTo(0);
	}

	@Test
	public void getGroupMembers_shoudlBeEmpty_becauseGroupsIsEmpty() {
		// GIVEN
		String idCalled = "15";

		//GIVEN SERVICE
		when(externalGroupDao.getDirectGroupMembers(idCalled, true)).thenReturn(new ArrayList<IExternalGroup>());

		//WHEN
		final List<TreeJS> treeList = groupService.getGroupMembers(idCalled);

		//THEN
		verify(externalGroupDao).getDirectGroupMembers(idCalled, true);
		assertThat(treeList.size()).isEqualTo(0);
	}

	@Test
	public void getGroupMembers_shouldBeReturn2Group() {
		// GIVEN
		String idCalled = "15";
		ExternalGroup externalGroup1 = new ExternalGroup();
		externalGroup1.setId("1");
		ExternalGroup externalGroup2 = new ExternalGroup();
		externalGroup2.setId("2");
		List<IExternalGroup> externalGroupList = new ArrayList<>();
		externalGroupList.add((IExternalGroup) externalGroup1);
		externalGroupList.add((IExternalGroup) externalGroup2);

		List<TreeJS> treeList = new ArrayList<>();
		TreeJS tree1 = new TreeJS();
		tree1.setId(externalGroup1.getId());
		TreeJS tree2 = new TreeJS();
		tree2.setId(externalGroup2.getId());
		treeList.add(tree1);
		treeList.add(tree2);

		//GIVEN SERVICE
		when(externalGroupDao.getDirectGroupMembers(idCalled, true)).thenReturn(externalGroupList);
		when(treeJSDTOFactory.asDTOList(externalGroupList)).thenReturn(treeList);

		//WHEN
		final List<TreeJS> result = groupService.getGroupMembers(idCalled);

		//THEN
		verify(externalGroupDao).getDirectGroupMembers(idCalled, true);
		verify(treeJSDTOFactory).asDTOList(externalGroupList);
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	public void getGroupMembers_shouldBeEmpty_becauseMembersHasNoMembers() {
		// GIVEN
		String idCalled = "15";
		Set<String> groupMembers = Sets.newHashSet();
		groupMembers.add("group1");
		ExternalGroup externalGroup1 = new ExternalGroup();
		externalGroup1.setId("1");
		externalGroup1.setGroupMembers(groupMembers);
		ExternalGroup externalGroup2 = new ExternalGroup();
		externalGroup2.setId("2");
		externalGroup2.setGroupMembers(groupMembers);
		List<IExternalGroup> externalGroupList = new ArrayList<>();
		externalGroupList.add((IExternalGroup) externalGroup1);
		externalGroupList.add((IExternalGroup) externalGroup2);

		List<TreeJS> treeList = new ArrayList<>();

		//GIVEN SERVICE
		when(externalGroupDao.getDirectGroupMembers(idCalled, true)).thenReturn(externalGroupList);
		when(treeJSDTOFactory.asDTOList(externalGroupList)).thenReturn(treeList);

		//WHEN
		final List<TreeJS> result = groupService.getGroupMembers(idCalled);

		//THEN
		verify(externalGroupDao).getDirectGroupMembers(idCalled, true);
		verify(treeJSDTOFactory).asDTOList(externalGroupList);
		assertThat(result.size()).isEqualTo(0);
	}

	@Test
	public void getRootNodes_shouldBeEmpty_becausePermsIsNull() {
		//GIVEN
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(null);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(null);

		//WHEN
		final List<TreeJS> resultList = groupService.getRootNodes(contextKey, subContextKeys);

		//THEN
//		verify(permissionService, times(2));
		assertThat(resultList.size()).isEqualTo(0);
	}

	@Test
	public void getRootNodes_IfPermissionTypeADMINAndRootCtxIsNull_shouldBeIsEmpty() {
		//GIVEN
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		Set<String> groupsIds = Sets.newHashSet();

		List<IExternalGroup> externalGroupList = new ArrayList<>();
		List<TreeJS> treeJSList = new ArrayList<TreeJS>();

		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);


		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(null);
		when(externalGroupDao.getGroupsById(groupsIds, true)).thenReturn(externalGroupList);
		when(treeJSDTOFactory.asDTOList(externalGroupList)).thenReturn(treeJSList);

		//WHEN
		final List<TreeJS> resultList = groupService.getRootNodes(contextKey, subContextKeys);

		//THEN
		verify(externalGroupDao).getGroupsById(groupsIds, true);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(treeJSDTOFactory).asDTOList(externalGroupList);
		assertThat(resultList.size()).isEqualTo(0);
	}

	@Test
	public void getRootNodes_IfPermissionTypeADMINAndFilterIsNull_shouldBeIsEmpty() {
		//GIVEN
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		Set<String> groupsIds = Sets.newHashSet();

		List<IExternalGroup> externalGroupList = new ArrayList<>();
		List<TreeJS> treeJSList = new ArrayList<TreeJS>();

		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);


		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(contextKey);
		when(filterRepository.findOne(Mockito.any(Predicate.class))).thenReturn(null);
		when(externalGroupDao.getGroupsById(groupsIds, true)).thenReturn(externalGroupList);
		when(treeJSDTOFactory.asDTOList(externalGroupList)).thenReturn(treeJSList);

		//WHEN
		final List<TreeJS> resultList = groupService.getRootNodes(contextKey, subContextKeys);

		//THEN
		verify(filterRepository).findOne(Mockito.any(Predicate.class));
		verify(externalGroupDao).getGroupsById(groupsIds, true);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(treeJSDTOFactory).asDTOList(externalGroupList);
		assertThat(resultList.size()).isEqualTo(0);
	}

	@Test
	public void getRootNodes_IfPermissionTypeADMIN_RootCtxAndfilterAreNotNull_GroupsIsNull_shouldBeIsEmpty() {
		//GIVEN
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		Set<String> groupsIds = Sets.newHashSet();

		List<IExternalGroup> externalGroupList = new ArrayList<>();
		List<TreeJS> treeJSList = new ArrayList<TreeJS>();
		Optional<Filter> optionalFilter = Optional.of(new Filter());
		String filter = optionalFilter.get().getPattern();

		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);


		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(contextKey);
		when(filterRepository.findOne(Mockito.any(Predicate.class))).thenReturn(optionalFilter);
		when(externalGroupDao.getGroupsById(groupsIds, true)).thenReturn(externalGroupList);
		when(externalGroupDao.getGroupsWithFilter(filter, null, false)).thenReturn(null);
		when(treeJSDTOFactory.asDTOList(externalGroupList)).thenReturn(treeJSList);

		//WHEN
		final List<TreeJS> resultList = groupService.getRootNodes(contextKey, subContextKeys);

		//THEN
		verify(filterRepository).findOne(Mockito.any(Predicate.class));
		verify(externalGroupDao).getGroupsById(groupsIds, true);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(treeJSDTOFactory).asDTOList(externalGroupList);
		verify(externalGroupDao).getGroupsWithFilter(filter, null, false);
		assertThat(resultList.size()).isEqualTo(0);
	}

	@Test
	public void getRootNodes_IfPermissionTypeADMIN_RootCtxAndfilterAndGroupsAreNotNull_shouldBeIsEmpty() {

		//GIVEN
		ContextKey contextKey = Utils.contextKeyValue(1L, ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(2L, ContextType.PUBLISHER);

		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});

		Set<String> groupsIds = Sets.newHashSet();
		groupsIds.add("1");
		groupsIds.add("2");

		List<IExternalGroup> externalGroupList = new ArrayList<>();
		List<TreeJS> treeJSList = new ArrayList<TreeJS>();
		Optional<Filter> optionalFilter = Optional.of(new Filter());
		String filter = optionalFilter.get().getPattern();

		ExternalGroup externalGroup1 = new ExternalGroup();
		externalGroup1.setId("1");
		ExternalGroup externalGroup2 = new ExternalGroup();
		externalGroup2.setId("2");
		List<IExternalGroup> groups = new ArrayList<>();
		groups.add((IExternalGroup) externalGroup1);
		groups.add((IExternalGroup) externalGroup2);

		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);


		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(contextKey);
		when(filterRepository.findOne(Mockito.any(Predicate.class))).thenReturn(optionalFilter);
		when(externalGroupDao.getGroupsById(groupsIds, true)).thenReturn(externalGroupList);
		when(externalGroupDao.getGroupsWithFilter(filter, null, false)).thenReturn(groups);
		when(treeJSDTOFactory.asDTOList(externalGroupList)).thenReturn(treeJSList);

		//WHEN
		final List<TreeJS> resultList = groupService.getRootNodes(contextKey, subContextKeys);

		//THEN
		verify(filterRepository).findOne(Mockito.any(Predicate.class));
		verify(externalGroupDao).getGroupsById(groupsIds, true);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(treeJSDTOFactory).asDTOList(externalGroupList);
		verify(externalGroupDao).getGroupsWithFilter(filter, null, false);
		assertThat(resultList.size()).isEqualTo(0);
	}


}
