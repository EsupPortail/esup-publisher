package org.esupportail.publisher.web.rest.vo;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(value = "item")
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemVO implements Serializable {

    private ArticleVO article;

    private String type;

    private String creator;

    private String pubDate;

    private String createdDate;

    private String modifiedDate;

    private String uuid;

    @XmlElementWrapper(name = "rubriques")
    @XmlElement(name = "uuid")
    private List<Long> rubriques;

    private Visibility visibility;


}
