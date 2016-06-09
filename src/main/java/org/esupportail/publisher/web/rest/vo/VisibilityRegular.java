package org.esupportail.publisher.web.rest.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
//@JsonTypeName(value = "regular")
@XmlRootElement(name = "regular")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisibilityRegular extends VisibilityAbstract implements Serializable {

    @NonNull
    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String attribute;

    @NonNull
    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String value;
}
