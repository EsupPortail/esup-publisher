package org.esupportail.publisher.repository.externals.ldap;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.externals.*;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
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
			externalUserDao.getUsersWithFilter("(ESCOUAI=0410017W)", "GILL");
		} catch (CommunicationException e) {
			log.warn("WARNING no LDAP connection acquired !");
			org.junit.Assume.assumeTrue(
					"WARNING no LDAP connection acquired !", false);
		}
	}

	@Test
	public void testFindUserWithFilter() {
		org.springframework.util.Assert.notNull(externalUserDao);
		List<IExternalUser> lus = externalUserDao.getUsersWithFilter(
				"(ESCOUAI=0410017W)", "GILL");
		Assert.assertNotNull(lus);
		Assert.assertTrue(!lus.isEmpty());
		Assert.assertNotNull(lus.get(0).getId());
		Assert.assertNotEquals(lus.get(0).getId(), "");

		lus = externalUserDao.getUsersWithFilter("(ESCOUAI=0410017W)", "GILL");

		for (IExternalUser lu : lus) {
			log.debug(lu.toString());
		}
	}

    @Test
    public void testFindGroupWithFilter() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        List<IExternalGroup> lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:FICTIF_0450822X:*)", "Profs Principaux", true);
        Assert.assertNotNull(lgps);
        Assert.assertTrue(!lgps.isEmpty());
        Assert.assertNotNull(lgps.get(0).getId());
        Assert.assertNotEquals(lgps.get(0).getId(), "");

        lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:FICTIF_0450822X:*)", "Profs Principaux", true);

        for (IExternalGroup lgp : lgps) {
            log.debug(lgp.toString());
        }
    }

	@Test
	public void testFindUserUid() {
		org.springframework.util.Assert.notNull(externalUserDao);
		IExternalUser lu = externalUserDao.getUserByUid("F1000ugr");
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
        org.springframework.util.Assert.notNull(externalGroupDao);
        IExternalGroup group = externalGroupDao.getGroupById("esco:Etablissements:FICTIF_0450822X:Administratifs:Tous_Administratifs", true);
        Assert.assertNotNull(group);
        log.debug(group.toString());

        Assert.assertNotNull(group.getId());
        Assert.assertNotEquals(group.getId(), "");
        Assert.assertNotNull(group.getDisplayName());
        Assert.assertNotEquals(group.getDisplayName(), "");
        Assert.assertNotNull(group.getGroupMembers());
        Assert.assertTrue(!group.getGroupMembers().isEmpty());
        Assert.assertNotNull(group.getAttribute(externalGroupHelper.getGroupMembersAttribute()));
        Assert.assertTrue(!group.getAttribute(externalGroupHelper.getGroupMembersAttribute()).isEmpty());
    }


    @Test
    public void testFindGroupIdNotFound() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        IExternalGroup group = externalGroupDao.getGroupById("XXXXXXXX", false);
        Assert.assertNull(group);
    }

	@Test
	public void testFindUserUidNotFound() {
		org.springframework.util.Assert.notNull(externalUserDao);
		IExternalUser lu = externalUserDao.getUserByUid("XXXXXXX");
		Assert.assertNull(lu);

	}

}
