package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName(value = "item")
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemVOForRead implements Serializable {

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

    private String source;

    private String createdBy;
    private String pubBy;
    private String lastModifiedBy;
    private String validatedBy;
    private String body;
}
