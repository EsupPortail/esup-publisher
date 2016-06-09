package org.esupportail.publisher.service.factories.impl;

import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.service.factories.CategoryProfileFactory;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.rest.vo.CategoryProfile;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Component
public class CategoryProfileFactoryImpl implements CategoryProfileFactory {

    @Inject
    private VisibilityFactory visibilityFactory;

    @Override
    public CategoryProfile from(final Publisher publisher, final List<Subscriber> subscribers, final String urlActualites, final String urlCategory) {
        Assert.isTrue(urlActualites != null || urlCategory != null);
        if (publisher != null) {
            CategoryProfile cp = new CategoryProfile();
            cp.setName(publisher.getContext().getOrganization().getDisplayName());
            cp.setId(publisher.getId());
            cp.setAccess(AccessType.PUBLIC);
            cp.setTimeout(5000);
            cp.setTtl(86400);
            cp.setTrustCategory(true);
            cp.setVisibility(visibilityFactory.from(subscribers));
            cp.setUrlActualites(urlActualites);
            cp.setUrlCategory(urlCategory);
            return cp;
        }
        return null;
    }
}
