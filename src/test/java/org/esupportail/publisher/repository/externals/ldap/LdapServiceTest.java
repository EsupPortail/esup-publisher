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

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.junit.Assert;
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

import com.google.common.collect.Sets;

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
		Assert.assertNotNull(externalUserDao);
		List<IExternalUser> lus = externalUserDao.getUsersWithFilter(
				"(ESCOUAI=0290009C)", "ALE");
		Assert.assertNotNull(lus);
        Assert.assertFalse(lus.isEmpty());
		Assert.assertNotNull(lus.get(0).getId());
		Assert.assertNotEquals(lus.get(0).getId(), "");

		lus = externalUserDao.getUsersWithFilter("(ESCOUAI=0290009C)", "ALE");

		for (IExternalUser lu : lus) {
			log.debug(lu.toString());
		}
	}

    @Test
    public void testFindGroupWithFilter() {
        Assert.assertNotNull(externalGroupDao);
        List<IExternalGroup> lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:DE L IROISE_0290009C:*)", "Profs Principaux", true);
        Assert.assertNotNull(lgps);
        Assert.assertFalse(lgps.isEmpty());
        Assert.assertNotNull(lgps.get(0).getId());
        Assert.assertNotEquals(lgps.get(0).getId(), "");

        lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:DE L IROISE_0290009C:*)", "Profs Principaux", true);

        for (IExternalGroup lgp : lgps) {
            log.debug(lgp.toString());
        }
    }

	@Test
	public void testFindUserUid() {
		Assert.assertNotNull(externalUserDao);
		IExternalUser lu = externalUserDao.getUserByUid("F1700k3r");
		Assert.assertNotNull(lu);
		log.debug(lu.toString());

		Assert.assertNotNull(lu.getId());
		Assert.assertNotEquals(lu.getId(), "");
		Assert.assertNotNull(lu.getDisplayName());
		Assert.assertNotEquals(lu.getDisplayName(), "");
		Assert.assertNotNull(lu.getEmail());
		Assert.assertNotEquals(lu.getEmail(), "");
        log.debug("ESCOUAI : {}", lu.getAttribute("ESCOUAI"));
		Assert.assertNotNull(lu.getAttribute("ESCOUAI"));
		Assert.assertNotEquals(lu.getAttribute("ESCOUAI"), "");
	}

    @Test
    public void testFindGroupId() {
        Assert.assertNotNull(externalGroupDao);
        IExternalGroup group = externalGroupDao.getGroupById("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs", true);
        Assert.assertNotNull(group);
        log.debug(group.toString());

        Assert.assertNotNull(group.getId());
        Assert.assertNotEquals(group.getId(), "");
        Assert.assertNotNull(group.getDisplayName());
        Assert.assertNotEquals(group.getDisplayName(), "");
        Assert.assertNotNull(group.getGroupMembers());
        Assert.assertFalse(group.getGroupMembers().isEmpty());
        Assert.assertNotNull(group.getAttribute(externalGroupHelper.getGroupMembersAttribute()));
        Assert.assertFalse(group.getAttribute(externalGroupHelper.getGroupMembersAttribute()).isEmpty());
    }


    @Test
    public void testFindGroupIdNotFound() {
        Assert.assertNotNull(externalGroupDao);
        IExternalGroup group = externalGroupDao.getGroupById("XXXXXXXX", false);
        Assert.assertNull(group);
    }

	@Test
	public void testFindUserUidNotFound() {
		Assert.assertNotNull(externalUserDao);
		IExternalUser lu = externalUserDao.getUserByUid("XXXXXXX");
		Assert.assertNull(lu);

	}

    @Test
    public void testIsUserFoundFromFilter() {
        Assert.assertNotNull(externalUserDao);
        final String StringFilter = "(isMemberOf=esco:Etablissements:DE L IROISE_0290009C*)";
        boolean ret = externalUserDao.isUserFoundWithFilter(StringFilter, "F1700k3r");
        Assert.assertTrue(ret);
    }

    @Test
    public void testIsNotUserFoundFromFilter() {
        Assert.assertNotNull(externalUserDao);
        final String StringFilter = "(isMemberOf=esco:Etablissements:DE L IROISE_0290009C*)";
        boolean ret = externalUserDao.isUserFoundWithFilter(StringFilter, "F1700k2y");
        Assert.assertFalse(ret);
    }

    @Test
    public void testGroupIsMemberOfGroup() {
        Assert.assertNotNull(externalGroupDao);
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:admin:local:admin_DE L IROISE_0290009C","esco:admin:Publication_contenus:DE L IROISE_0290009C:MANAGER");
        Assert.assertTrue(ret);

        ret = externalGroupDao.isGroupMemberOfGroup("esco:admin:local:admin_DE L IROISE_0290009C","esco:admin:Publication_contenus");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsMemberOfGroupWithDesigner() {
        Assert.assertNotNull(externalGroupDao);
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:DE L IROISE_0290009C:SECONDE GENERALE et TECHNO YC BT:Profs_2NDE3", "esco:Etablissements:DE L IROISE_0290009C:Profs");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsMemberOfGroupFromFilter() {
        Assert.assertNotNull(externalGroupDao);
        final String StringFilter = "(|(cn=esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE*)(cn=esco:Applications:Publication_contenus:DE L IROISE_0290009C))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0290009C:Administratifs:ORIENTATION");
        Assert.assertTrue(ret);

        ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsMemberOfGroupFromFilterWithDesigner() {
        Assert.assertNotNull(externalGroupDao);
        final String StringFilter = "(|(cn=esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE)(cn=esco:Applications:Publication_contenus:DE L IROISE_0290009C))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0290009C:SECONDE GENERALE et TECHNO YC BT:Profs_2NDE3");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsNotMemberOfGroupFromFilter() {
        Assert.assertNotNull(externalGroupDao);
        final String StringFilter = "(|(cn=esco:Etablissements:DE L IROISE_0290009C:Tous_DE L IROISE*)(cn=esco:Applications:Publication_contenus:DE L IROISE_0290009C))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:DE L IROISE_0291595B:Administratifs:Tous_Administratifs");
        Assert.assertFalse(ret);

    }

    @Test
    public void testGroupIsNotMemberOfGroup() {
        Assert.assertNotNull(externalGroupDao);
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs","esco:admin:central");
        Assert.assertFalse(ret);
    }

    @Test
    public void testGroupIsMemberOfAtLeastOneGroup() {
        Assert.assertNotNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C","esco:Applications:mediacentre:GAR:DE L IROISE_0290009C");
        boolean ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs", groups);
        Assert.assertTrue(ret);

        groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C","esco:Etablissements:DE L IROISE_0290009C");
        ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs",groups);
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsNotMemberOfAtLeastOneGroup() {
        Assert.assertNotNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C", "esco:Etablissements:DE L IROISE_0290009C:Profs");
        boolean ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:DE L IROISE_0290009C:Administratifs:Tous_Administratifs", groups);
        Assert.assertFalse(ret);
    }

    @Test
    public void testUserIsMemberOfGroup() {
        Assert.assertNotNull(externalGroupDao);
        boolean ret = externalGroupDao.isUserMemberOfGroup("F1700k3r", "esco:Applications:Publication_contenus:DE L IROISE_0290009C");
        Assert.assertTrue(ret);
    }
    @Test
    public void testUserIsNotMemberOfGroup() {
        Assert.assertNotNull(externalGroupDao);
        boolean ret = externalGroupDao.isUserMemberOfGroup("F1700k2y", "esco:Applications:Publication_contenus:DE L IROISE_0290009C");
        Assert.assertFalse(ret);
    }

    @Test
    public void testUserIsMemberOfAtLeastOneGroup() {
        Assert.assertNotNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:DE L IROISE_0290009C","esco:Applications:Publication_contenus:DE L IROISE_0290009C");
        boolean ret = externalGroupDao.isUserMemberOfAtLeastOneGroup("F1700k3r", groups);
        Assert.assertTrue(ret);
    }

    @Test
    public void testUserIsNotMemberOfAtLeastOneGroup() {
        Assert.assertNotNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:Applications:mail:DE L IROISE_0290009C","esco:Applications:Publication_contenus:DE L IROISE_0290009C","esco:Applications:GRR:DE L IROISE_0290009C");
        boolean ret = externalGroupDao.isUserMemberOfAtLeastOneGroup("F1700k2y", groups);
        Assert.assertFalse(ret);
    }

}
