package org.esupportail.publisher.service.factories;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.web.rest.vo.ItemVO;

/**
 * Created by jgribonvald on 03/06/16.
 */
public interface ItemVOFactory {

    ItemVO from(final AbstractItem item, final List<AbstractClassification> classifications, final List<Subscriber> subscribers, final HttpServletRequest request);
}
