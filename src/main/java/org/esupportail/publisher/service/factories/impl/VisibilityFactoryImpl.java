package org.esupportail.publisher.service.factories.impl;

import org.apache.commons.lang3.StringEscapeUtils;
import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.service.factories.VisibilityFactory;
import org.esupportail.publisher.web.rest.vo.Visibility;
import org.esupportail.publisher.web.rest.vo.VisibilityAbstract;
import org.esupportail.publisher.web.rest.vo.VisibilityRegex;
import org.esupportail.publisher.web.rest.vo.VisibilityRegular;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Component
public class VisibilityFactoryImpl implements VisibilityFactory {

    @Inject
    private ExternalUserHelper externalUserHelper;

    @Override
    public Visibility from(@NotNull List<Subscriber> models){
        Visibility visibility = new Visibility();
        for (Subscriber subscriber : models) {
            switch (subscriber.getSubscribeType()) {
                case FORCED:
                    //visibility.getObliged().getList().add(from(subscriber.getSubjectCtxId().getSubject()));
                    visibility.getObliged().add(from(subscriber.getSubjectCtxId().getSubject()));
                    break;
                case FREE:
                    visibility.getAllowed().add(from(subscriber.getSubjectCtxId().getSubject()));
                    break;
                case PRE:
                    visibility.getAutoSubscribed().add(from(subscriber.getSubjectCtxId().getSubject()));
                    break;
                default: throw new IllegalArgumentException("Unknown SubcriberType not managed :" + subscriber.getSubscribeType());
            }
        }
        return visibility;
    }

    private VisibilityAbstract from(@NotNull SubjectKey model) {
        switch (model.getKeyType()) {
            case GROUP:
                return new VisibilityRegex(externalUserHelper.getUserGroupAttribute(), Pattern.quote(StringEscapeUtils.escapeXml10(model.getKeyId())));
            case PERSON:
                return new VisibilityRegular(externalUserHelper.getUserIdAttribute(), model.getKeyId());
            default: throw new IllegalArgumentException("Unknown SubjectType not managed :" + model.getKeyType());
        }
    }
}
