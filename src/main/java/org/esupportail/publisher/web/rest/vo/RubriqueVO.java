package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.esupportail.publisher.domain.util.Views;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName(value = "rubrique")
@XmlRootElement(name = "rubrique")
@XmlAccessorType(XmlAccessType.FIELD)
public class RubriqueVO implements Serializable {

    @NonNull
    private String uuid;

    @NonNull
    @JsonView(Views.Flash.class)
    private String name;

    @JsonView(Views.Flash.class)
    private String color;

    private String mediaUrl;

    private boolean highlight;
}
