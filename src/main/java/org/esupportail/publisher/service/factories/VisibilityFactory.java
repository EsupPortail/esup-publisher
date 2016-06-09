package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.web.rest.vo.Visibility;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jgribonvald on 06/06/16.
 */
public interface VisibilityFactory {

    Visibility from(@NotNull List<Subscriber> models);
}
