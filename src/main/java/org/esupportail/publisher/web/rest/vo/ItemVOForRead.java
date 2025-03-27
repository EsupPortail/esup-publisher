package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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

    @XmlElementWrapper(name = "rubriques")
    private List<Long> rubriques;

    private String type;

    private String pubDate;
    private String createdDate;
    private String modifiedDate;

    private String createdBy;
    private String pubBy;
    private String lastModifiedBy;

    private String validatedBy;
    private String body;

    private String ressourceUrl;

}
