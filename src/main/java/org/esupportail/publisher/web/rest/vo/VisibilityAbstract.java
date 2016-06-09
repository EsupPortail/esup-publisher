package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Created by jgribonvald on 03/06/16.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
    @JsonSubTypes.Type(value = VisibilityGroup.class, name = "group"),
    @JsonSubTypes.Type(value = VisibilityRegex.class, name = "regex"),
    @JsonSubTypes.Type(value = VisibilityRegular.class, name = "regular")
})
@XmlTransient
public abstract class VisibilityAbstract implements Serializable {
}
