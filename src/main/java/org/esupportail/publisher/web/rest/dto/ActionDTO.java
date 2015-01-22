package org.esupportail.publisher.web.rest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by jgribonvald on 31/03/15.
 */
@Data
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ActionDTO implements Serializable {

    @NotNull
    private Long objectId;

    @NotNull
    @Size(min=1)
    private String attribute;

    @NotNull
    @Size(min=1)
    private String value;
}
