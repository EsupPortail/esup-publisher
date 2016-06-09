package org.esupportail.publisher.web.rest.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.web.rest.vo.ns.ArticleVO;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

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
