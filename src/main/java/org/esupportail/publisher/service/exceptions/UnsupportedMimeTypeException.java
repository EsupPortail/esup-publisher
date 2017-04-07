package org.esupportail.publisher.service.exceptions;

import lombok.Getter;
import org.springframework.web.multipart.MultipartException;

/**
 * Exception to manage a specific error on unauthorized file ContentType when uploading.
 *
 * Created by jgribonvald on 06/04/17.
 */
public class UnsupportedMimeTypeException extends MultipartException {

    @Getter
    private String unsupportedMimeType;

    public UnsupportedMimeTypeException(final String unsupportedMimeType) {
        super("The content type '" + unsupportedMimeType + "' isn't supported !");
        this.unsupportedMimeType = unsupportedMimeType;
    }

    public UnsupportedMimeTypeException(final String message, final Throwable cause, final String unsupportedMimeType) {
        super(message, cause);
        this.unsupportedMimeType = unsupportedMimeType;
    }
}
