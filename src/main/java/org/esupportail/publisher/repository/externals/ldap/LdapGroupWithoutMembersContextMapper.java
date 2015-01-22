package org.esupportail.publisher.repository.externals.ldap;

    import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
    import org.esupportail.publisher.domain.externals.IExternalGroup;
    import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.Assert;

import javax.naming.NamingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GIP RECIA - Julien Gribonvald 3 juin. 2015
 */
@Slf4j
public class LdapGroupWithoutMembersContextMapper implements ContextMapper<IExternalGroup> {

    private ExternalGroupHelper externalGroupHelper;

    private IExternalGroupDisplayNameFormatter groupDisplayNameFormatter;

    /**
     * @param externalGroupHelper
     */
    public LdapGroupWithoutMembersContextMapper(ExternalGroupHelper externalGroupHelper, IExternalGroupDisplayNameFormatter groupDisplayNameFormatter) {
        super();
        this.externalGroupHelper = externalGroupHelper;
        this.groupDisplayNameFormatter = groupDisplayNameFormatter;
    }

    @Override
    public ExternalGroup mapFromContext(Object ctx) throws NamingException {
        Assert.notNull(externalGroupHelper);
        DirContextAdapter context = (DirContextAdapter) ctx;
        Map<String, List<String>> attrs = new HashMap<String, List<String>>();
        ExternalGroup group = new ExternalGroup();

        group.setId(context.getStringAttribute(externalGroupHelper.getGroupIdAttribute()));
        attrs.put(externalGroupHelper.getGroupIdAttribute(), Lists.newArrayList(group.getId()));

        group.setDisplayName(context.getStringAttribute(externalGroupHelper.getGroupDisplayNameAttribute()));
        attrs.put(externalGroupHelper.getGroupDisplayNameAttribute(),
            Lists.newArrayList(context.getStringAttribute(externalGroupHelper.getGroupDisplayNameAttribute())));

        if (externalGroupHelper.getOtherGroupDisplayedAttributes() != null
            && !externalGroupHelper.getOtherGroupDisplayedAttributes().isEmpty()) {
            for (String attr : externalGroupHelper.getOtherGroupDisplayedAttributes()) {
                if (context.attributeExists(attr)) {
                    attrs.put(attr.toLowerCase(), Arrays.asList(context.getStringAttributes(attr)));
                }
            }
        }
        group.setAttributes(attrs);

        group = groupDisplayNameFormatter.format(group);

        return group;
    }

    public ExternalGroupHelper getLdapGroupHelper() {
        return externalGroupHelper;
    }

    public void setLdapGroupHelper(ExternalGroupHelper externalGroupHelper) {
        this.externalGroupHelper = externalGroupHelper;
    }
}
