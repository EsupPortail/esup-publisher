package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;

import java.util.Collection;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
public interface RubriqueVOFactory {

    RubriqueVO from(final AbstractClassification model);

    List<RubriqueVO> asVOList(final Collection<? extends AbstractClassification> models);
}
