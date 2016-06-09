package org.esupportail.publisher.web.rest.vo.ns;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.esupportail.publisher.domain.util.CustomDateTimeSerializer;
import org.esupportail.publisher.web.rest.util.CustomDateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.util.ReadableDateTimeSerializer;
import org.esupportail.publisher.web.rest.util.ReadableDateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.util.SeparatorListXMLAdapter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@JsonTypeName(value = "article")
@XmlRootElement(name = "article")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleVO implements Serializable {

    private String title;

    private String link;

    private String enclosure;

    private String description;

    //Must be RFC2822 format
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = ReadableDateTimeSerializer.class)
    @XmlJavaTypeAdapter(ReadableDateTimeXmlAdapter.class)
    private DateTime pubDate;

    private int guid;

    @XmlElement(name="category")
    @XmlJavaTypeAdapter(SeparatorListXMLAdapter.class)
    private List<String> categories;

    @JsonProperty(value = "dc:creator")
    @XmlElement(name="creator", namespace = "http://purl.org/dc/elements/1.1/")
    private String creator;

    //Must be ISO8601 format
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonProperty(value = "dc:date")
    @XmlElement(name="date", namespace = "http://purl.org/dc/elements/1.1/")
    @XmlJavaTypeAdapter(CustomDateTimeXmlAdapter.class)
    private DateTime date;

}
