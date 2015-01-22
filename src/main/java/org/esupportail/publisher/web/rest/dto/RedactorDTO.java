package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.util.CstPropertiesLength;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 13 oct. 2014
 */
@NoArgsConstructor
@ToString(callSuper=true)
public class RedactorDTO extends AbstractIdDTO<Long> implements IAbstractDTO<Long>, Serializable {

    /** This field corresponds to the database column name. */
    @Getter
    @Setter
    @NotNull
    @Size(min=1,max= CstPropertiesLength.NAME)
    private String name;

    /** This field corresponds to the database column display_name. */
    @Getter
    @Setter
    @NotNull
    @Size(min=1, max=CstPropertiesLength.DISPLAYNAME)
    private String displayName;

    /** This field corresponds to the database column description. */
    @Getter
    @Setter
    @NotNull
    @Size(min=5, max=CstPropertiesLength.DESCRIPTION)
    private String description;

    /** This field corresponds to the database column name. */
    @Getter
    @Setter
    @Min(1)
    @Max(2)
    private int nbLevelsOfClassification;

    /**
     * @param modelId
     * @param name
     * @param displayName
     * @param description
     * @param nbLevelsOfClassification
     */
    public RedactorDTO(@NotNull final Long modelId, @NotNull final String name, @NotNull final String displayName,
            @NotNull final String description, @NotNull final int nbLevelsOfClassification) {
        super(modelId);
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.nbLevelsOfClassification = nbLevelsOfClassification;
    }



}
