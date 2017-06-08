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

    @Getter
    private boolean inBody = true;

    @Getter
    private String contentType;

    public LinkedFileItemDTO(@NotNull final String uri, final String filename, final boolean inBody, final String contentType) {
        this.uri = uri;
        this.filename = filename;
        this.inBody = inBody;
        this.contentType = contentType;
    }
}
