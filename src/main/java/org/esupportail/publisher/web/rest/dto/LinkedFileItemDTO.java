package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by jgribonvald on 11/04/17.
 */
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LinkedFileItemDTO implements Serializable {

    @NotNull
    @Getter
    private String uri;

    @Getter
    private String filename;

    public LinkedFileItemDTO(@NotNull final String uri, final String filename) {
        this.uri = uri;
        this.filename = filename;
    }
}
