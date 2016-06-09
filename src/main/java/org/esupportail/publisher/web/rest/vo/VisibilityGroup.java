package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeName(value = "group")
@XmlRootElement(name="group")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisibilityGroup extends VisibilityAbstract implements Serializable {

    @NonNull
    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String name;

}
