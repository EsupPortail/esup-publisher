package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.web.rest.util.CustomLCEnumSerializer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by jgribonvald on 06/06/16.
 */
@Data
@NoArgsConstructor
@JsonTypeName("categoryProfile")
@XmlRootElement(name = "categoryProfile")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoryProfile implements Serializable {

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String name;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private long id;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String urlActualites;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String urlCategory;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private boolean trustCategory;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    @JsonSerialize(using = CustomLCEnumSerializer.class)
    private AccessType access;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private int ttl;

    //@JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private int timeout;

    private Visibility visibility;


}
