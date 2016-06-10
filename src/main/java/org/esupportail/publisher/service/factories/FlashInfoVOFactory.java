package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.web.rest.vo.FlashInfoVO;

import java.util.List;
import java.util.Map;

/**
 * Created by jgribonvald on 03/06/16.
 */
public interface FlashInfoVOFactory {

    FlashInfoVO from(final Flash model, final List<AbstractClassification> tags);

    List<FlashInfoVO> asVOList(final Map<Flash, List<AbstractClassification>> models);
}
