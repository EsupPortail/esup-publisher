package org.esupportail.publisher.service.bean;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.util.CstPropertiesLength;

/**
 * Created by jgribonvald on 12/04/17.
 */
@Data
@ToString
@EqualsAndHashCode
public class HighlightedClassification implements Serializable {

    private Long id = 0L;

    @NotNull
    @Size(min = 3, max = CstPropertiesLength.CLASSIF_NAME)
    private String name;

    private String lang = "fr";

    private int ttl = 3600;

    private int displayOrder = 0;

    private AccessType accessView = AccessType.PUBLIC;

    @NotNull
    private String description;

    private DisplayOrderType defaultDisplayOrder = DisplayOrderType.START_DATE;

    private boolean rssAllowed = false;

    private boolean highlight = true;

    @NotNull
    private String color;

    /**
     * Empty Constructor of HighlightedClassification.
     */
    public HighlightedClassification() {
        super();
    }

    /**
     * Minimal constructor of HighlightedClassification.
     * @param name
     * @param description
     * @param color
     */
    public HighlightedClassification(final String name, final String description, final String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    /**
     * Constructor of HighlightedClassification.
     * @param rssAllowed
     * @param name
     * @param lang
     * @param ttl
     * @param displayOrder
     * @param accessView
     * @param description
     * @param highlight
     * @param defaultDisplayOrder
     */
    public HighlightedClassification(final boolean rssAllowed, final String name, final String lang,
                                     final int ttl, final int displayOrder, final AccessType accessView, final String description,
                                     final boolean highlight, final String color, final DisplayOrderType defaultDisplayOrder) {
        this.name = name;
        this.lang = lang;
        this.ttl = ttl;
        this.displayOrder = displayOrder;
        this.accessView = accessView;
        this.description = description;
        this.defaultDisplayOrder = defaultDisplayOrder;
        this.rssAllowed = rssAllowed;
        this.highlight = highlight;
        this.color = color;
    }
}
