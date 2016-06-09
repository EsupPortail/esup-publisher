package org.esupportail.publisher.web.rest.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "actualites")
@XmlAccessorType(XmlAccessType.FIELD)
public class Actualite implements Serializable {

    @XmlElementWrapper(name = "rubriques")
    @XmlElement(name = "rubrique")
    private List<RubriqueVO> rubriques = new ArrayList<>();

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<ItemVO> items = new ArrayList<>();
}
