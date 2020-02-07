/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.web.rest.exception;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.esupportail.publisher.service.exceptions.UnsupportedMimeTypeException;
import org.esupportail.publisher.web.rest.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by jgribonvald on 21/02/17.
 */
@ControllerAdvice
@Slf4j
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //StandardServletMultipartResolver
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    ResponseEntity<?> handleException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        log.debug("Handle MultipartException with exception :", ex);
        // to be able to obtain the sizelimit cause when using default MultiPart
        if (ex.getCause() != null && ex.getCause().getCause() != null
            && ex.getCause().getCause() instanceof SizeException) {
            return new ResponseEntity<Object>(new ErrorMessage("errors.upload.filesize",
                ImmutableMap.of("size", ((SizeException) ex.getCause().getCause()).getPermittedSize())),status);
        }
        return new ResponseEntity<Object>(new ErrorMessage("errors.upload.generic"),status);
    }
    //CommonsMultipartResolver
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    ResponseEntity<?> handleException2(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Object>(new ErrorMessage("errors.upload.filesize",
            ImmutableMap.of("size",((MaxUploadSizeExceededException) ex).getMaxUploadSize())),status);
    }

    @ExceptionHandler(UnsupportedMimeTypeException.class)
    @ResponseBody
    ResponseEntity<?> handleException3(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Object>(new ErrorMessage("errors.upload.filetype",
            ImmutableMap.of("type",((UnsupportedMimeTypeException) ex).getUnsupportedMimeType())),status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
