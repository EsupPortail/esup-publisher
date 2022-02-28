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
package org.esupportail.publisher.repository.externals.ldap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional(readOnly = true)
public class LdapServiceTest {

	private static final Logger log = LoggerFactory
			.getLogger(LdapServiceTest.class);

	@Inject
	private IExternalUserDao externalUserDao;

    @Inject
    private IExternalGroupDao externalGroupDao;

    @Inject
    private ExternalGroupHelper externalGroupHelper;

	@Autowired
	private LdapTemplate template;

	@Before
	public void beforeMethod() {
		try {
			externalUserDao.getUsersWithFilter("(ESCOUAI=0290009C)", "ALE");
		} catch (CommunicationException e) {
			log.warn("WARNING no LDAP connection acquired !");
			org.junit.Assume.assumeTrue(
					"WARNING no LDAP connection acquired !", false);
		}
	}

	@Test
	public void testFindUserWithFilter() {
		assertThat(externalUserDao, notNullValue());
		List<IExternalUser> lus = externalUserDao.getUsersWithFilter(
				"(ESCOUAI=0290009C)", "ALE");
		assertThat(lus, notNullValue());
        assertThat(lus, not(empty()));
		assertThat(lus.get(0).getId(), not(blankOrNullString()));

		lus = externalUserDao.getUsersWithFilter("(ESCOUAI=0290009C)", "ALE");

		for (IExternalUser lu : lus) {
			log.debug(lu.toString());
		}
	}

    @Test
    public void testFindGroupWithFilter() {
        assertThat(externalGroupDao, notNullValue());
        List<IExternalGroup> lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:DE L IROISE_0290009C:*)", "Profs Principaux", true);
        assertThat(lgps, notNullValue());
        assertThat(lgps, not(empty()));
        assertThat(lgps.get(0).getId(), not(blankOrNullString()));

        lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:DE L IROISE_0290009C:*)", "Profs Principaux", true);

        for (IExternalGroup lgp : lgps) {
            log.debug(lgp.toString());
        }
    }

	@Test
	public void testFindUserUid() {
		assertThat(externalUserDao, notNullValue());
		IExternalUser lu = externalUserDao.getUserByUid("F1700k3r");
		assertThat(lu, notNullValue());
		log.debug(lu.toString());

		assertThat(lu.getId(), not(blankOrNullString()));
		assertThat(lu.getDisplayName(), not(blankOrNullString()));
		assertThat(lu.getEmail(), not(blankOrNullString()));
        log.debug("ESCOUAI : {}", lu.getAttribute("ESCOUAI"));
		assertThat(lu.getAttribute("ESCOUAI"), notNullValue());
		assertThat(lu.getAttribute("ESCOUAI"), not(empty()));
	}

    @Test
    public void testFindGroupId() {
        assertThat(externalGroupDao, notNullValue());
        IExternalGroup group = externalGroupDao.getGroupById("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs", true);
        log.debug(group.toString());
        assertThat(group, notNullValue());

        assertThat(group.getId(), not(blankOrNullString()));
        assertThat(group.getDisplayName(), not(blankOrNullString()));
        assertThat(group.getGroupMembers(), notNullValue());
        assertThat(group.getGroupMembers(), not(empty()));
        assertThat(group.getAttribute(externalGroupHelper.getGroupMembersAttribute()), notNullValue());
        assertThat(group.getAttribute(externalGroupHelper.getGroupMembersAttribute()), not(empty()));
    }


    @Test
    public void testFindGroupIdNotFound() {
        assertThat(externalGroupDao, notNullValue());
        IExternalGroup group = externalGroupDao.getGroupById("XXXXXXXX", false);
        assertThat(group, is(nullValue()));
    }

	@Test
	public void testFindUserUidNotFound() {
		assertThat(externalUserDao, notNullValue());
		IExternalUser lu = externalUserDao.getUserByUid("XXXXXXX");
        assertThat(lu, nullValue());

	}

    @Test
    public void testIsUserFoundFromFilter() {
        assertThat(externalUserDao, notNullValue());
        final String StringFilter = "(isMemberOf=esco:Etablissements:DE L IROISE_0290009C*)";
        boolean ret = externalUserDao.isUserFoundWithFilter(StringFilter, "F1700k3r");
        assertThat(ret, is(true));
    }

    @Test
    public void testIsNotUserFoundFromFilter() {
        assertThat(externalUserDao, notNullValue());
        final String StringFilter = "(isMemberOf=esco:Etablissements:DE L IROISE_0290009C*)";
        boolean ret = externalUserDao.isUserFoundWithFilter(StringFilter, "F1700k2y");
        assertThat(ret, is(false));
    }

    @Test
    public void testGroupIsMemberOfGroup() {
        assertThat(externalGroupDao, notNullValue());
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:admin:local:admin_DE L IROISE_0290009C","esco:admin:Publication_contenus:DE L IROISE_0290009C:MANAGER");
        assertThat(ret, is(true));

        ret = externalGroupDao.isGroupMemberOfGroup("esco:admin:local:admin_DE L IROISE_0290009C","esco:admin:Publication_contenus");
        assertThat(ret, is(true));
    }

    @Test
    public void testGroupIsMemberOfGroupWithDesigner() {
        assertThat(externalGroupDao, notNullValue());
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:DE L IROISE_0290009C:SECONDE GENERALE et TECHNO YC BT:Profs_2NDE3", "esco:Etablissements:DE L IROISE_0290009C:Profs");
        assertThat(ret, is(true));
    }

    @Test
    public void testGroupIsMemberOfGroupFromFilter() {
        assertThat(externalGroupDao, notNullValue());
        final String StringFilter = "(|(cn=esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE*)(cn=esco:Applications:Publication_contenus:DE L IROISE_0290009C))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0290009C:Administratifs:ORIENTATION");
        assertThat(ret, is(true));

        ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE");
        assertThat(ret, is(true));
    }

    @Test
    public void testGroupIsMemberOfGroupFromFilterWithDesigner() {
        assertThat(externalGroupDao, notNullValue());
        final String StringFilter = "(|(cn=esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE)(cn=esco:Applications:Publication_contenus:DE L IROISE_0290009C))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0290009C:SECONDE GENERALE et TECHNO YC BT:Profs_2NDE3");
        assertThat(ret, is(true));
    }

    @Test
    public void testGroupIsNotMemberOfGroupFromFilter() {
        assertThat(externalGroupDao, notNullValue());
        final String StringFilter = "(|(cn=esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE*)(cn=esco:Applications:Publication_contenus:DE L IROISE_0290009C))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0291595B:Administratifs:Tous_Administratifs");
        assertThat(ret, is(false));

    }

    @Test
    public void testGroupIsNotMemberOfGroup() {
        assertThat(externalGroupDao, notNullValue());
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs","esco:admin:central");
        assertThat(ret, is(false));
    }

    @Test
    public void testGroupIsMemberOfAtLeastOneGroup() {
        assertThat(externalGroupDao, notNullValue());
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C","esco:Applications:mediacentre:GAR:DE L IROISE_0290009C");
        boolean ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs", groups);
        assertThat(ret, is(true));

        groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C","esco:Etablissements:DE L IROISE_0290009C");
        ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs",groups);
        assertThat(ret, is(true));
    }

    @Test
    public void testGroupIsNotMemberOfAtLeastOneGroup() {
        assertThat(externalGroupDao, notNullValue());
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C", "esco:Etablissements:DE L IROISE_0290009C:Profs");
        boolean ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs", groups);
        assertThat(ret, is(false));
    }

    @Test
    public void testUserIsMemberOfGroup() {
        assertThat(externalGroupDao, notNullValue());
        boolean ret = externalGroupDao.isUserMemberOfGroup("F1700k3r", "esco:Applications:Publication_contenus:DE L IROISE_0290009C");
        assertThat(ret, is(true));
    }
    @Test
    public void testUserIsNotMemberOfGroup() {
        assertThat(externalGroupDao, notNullValue());
        boolean ret = externalGroupDao.isUserMemberOfGroup("F1700k2y", "esco:Applications:Publication_contenus:DE L IROISE_0290009C");
        assertThat(ret, is(false));
    }

    @Test
    public void testUserIsMemberOfAtLeastOneGroup() {
        assertThat(externalGroupDao, notNullValue());
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C","esco:Applications:Publication_contenus:DE L IROISE_0290009C");
        boolean ret = externalGroupDao.isUserMemberOfAtLeastOneGroup("F1700k3r", groups);
        assertThat(ret, is(true));
    }

    @Test
    public void testUserIsNotMemberOfAtLeastOneGroup() {
        assertThat(externalGroupDao, notNullValue());
        Set<String> groups = Sets.newHashSet("esco:Applications:mail:DE L IROISE_0290009C","esco:Applications:Publication_contenus:DE L IROISE_0290009C","esco:Applications:GRR:DE L IROISE_0290009C");
        boolean ret = externalGroupDao.isUserMemberOfAtLeastOneGroup("F1700k2y", groups);
        assertThat(ret, is(false));
    }

}