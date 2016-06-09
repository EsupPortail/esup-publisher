package org.esupportail.publisher.web.rest.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JacksonXmlRootElement
@XmlRootElement(name = "categoryProfilesUrl")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoryProfilesUrl implements Serializable {

    //@JacksonXmlProperty(localName = "categoryProfile")
    //@JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "categoryProfile")
    private List<CategoryProfile> categoryProfilesUrl = new ArrayList<>();

}
