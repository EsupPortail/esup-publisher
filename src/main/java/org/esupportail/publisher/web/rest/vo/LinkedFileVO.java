package org.esupportail.publisher.web.rest.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jgribonvald on 23/06/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(value = "file")
@XmlRootElement(name = "file")
@XmlAccessorType(XmlAccessType.FIELD)
public class LinkedFileVO {

    private String uri;

    private String fileName;

    private String contentType;
}
