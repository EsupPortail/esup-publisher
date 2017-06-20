package org.esupportail.publisher.web.rest.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
//@JacksonXmlRootElement(localName = "visibility")
@XmlRootElement(name = "visibility")
@XmlAccessorType(XmlAccessType.FIELD)
public class Visibility implements Serializable {

    @XmlElementWrapper(name="allowed", required = true)
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> allowed = new ArrayList<>();
    @XmlElementWrapper(name="autoSubscribed", required = true)
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> autoSubscribed = new ArrayList<>();
    @XmlElementWrapper(name="obliged", required = true)
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> obliged = new ArrayList<>();

}
