package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.Flash;
import org.esupportail.publisher.web.rest.vo.FlashInfoVO;

import java.util.Collection;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
public interface FlashInfoVOFactory {

    FlashInfoVO from(final Flash model);

    List<FlashInfoVO> asVOList(final Collection<Flash> models);
}
