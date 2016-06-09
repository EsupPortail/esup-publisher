package org.esupportail.publisher.web.rest.vo;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
//@JacksonXmlRootElement(localName = "visibility")
@XmlRootElement(name = "visibility")
@XmlAccessorType(XmlAccessType.FIELD)
public class Visibility implements Serializable {

    @XmlElementWrapper(name="allowed")
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> allowed = new ArrayList<>();
    @XmlElementWrapper(name="autoSubscribed")
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> autoSubscribed = new ArrayList<>();
    @XmlElementWrapper(name="obliged")
    @XmlElements({
            @XmlElement(name="group",type=VisibilityGroup.class),
            @XmlElement(name="regex",type=VisibilityRegex.class),
            @XmlElement(name="regular",type=VisibilityRegular.class),
    })
    private List<VisibilityAbstract> obliged = new ArrayList<>();

}
