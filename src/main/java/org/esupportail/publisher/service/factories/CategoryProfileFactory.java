package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.web.rest.vo.CategoryProfile;

import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
public interface CategoryProfileFactory {

    CategoryProfile from(final Publisher publisher, final List<Subscriber> subscribers, final String urlActualites, final String urlCategory);
}
