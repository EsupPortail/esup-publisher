package org.esupportail.publisher.service.factories.impl;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.service.factories.FlashInfoVOFactory;
import org.esupportail.publisher.web.rest.vo.FlashInfoVO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Service
public class FlashInfoVOFactoryImpl implements FlashInfoVOFactory {

    @Inject
    private ServiceUrlHelper urlHelper;

    @Override
    public FlashInfoVO from(final Flash model) {
        if (model != null) {
            FlashInfoVO flashInfoVO = new FlashInfoVO();
            flashInfoVO.setTitle(model.getTitle());
            flashInfoVO.setSummary(model.getSummary());
            flashInfoVO.setMediaUrl(urlHelper.getRootAppUrl() + model.getEnclosure());
            flashInfoVO.setLink("/" + urlHelper.getContextPath().replaceFirst("/","") + urlHelper.getItemUri() + model.getId());
            return flashInfoVO;
        }
        return null;
    }

    @Override
    public List<FlashInfoVO> asVOList(final Collection<Flash> models) {
        final List<FlashInfoVO> tvo = Lists.newArrayList();
        if ((models != null) && !models.isEmpty()) {
            for (Flash model : models) {
                tvo.add(from(model));
            }
        }
        return tvo;
    }
}
