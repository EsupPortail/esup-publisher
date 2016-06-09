package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.service.factories.RubriqueVOFactory;
import org.esupportail.publisher.web.rest.vo.RubriqueVO;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Component
public class RubriqueVOFactoryImpl implements RubriqueVOFactory {

    public RubriqueVO from(final AbstractClassification model) {
        if (model != null) {
            RubriqueVO vo = new RubriqueVO(model.getId().toString(), model.getDisplayName(), model.getColor(), model.getIconUrl(), model.isHighlighted());
            vo.setUuid(model.getId().toString());
            return vo;
        }
        return null;
    }

    public List<RubriqueVO> asVOList(final Collection<? extends AbstractClassification> models) {
        final List<RubriqueVO> tvo = Lists.newArrayList();
        if (models != null && !models.isEmpty()) {
            for (AbstractClassification model : models) {
                tvo.add(from(model));
            }
        }
        return tvo;
    }
}
