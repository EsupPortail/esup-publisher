package org.esupportail.publisher.web.rest.exception;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.esupportail.publisher.service.bean.FileUploadHelper;
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

    @Inject
    private FileUploadHelper fileUploadHelper;

    //StandardServletMultipartResolver
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    ResponseEntity<?> handleException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        log.debug("Handle MultipartException with exception :", ex);
        // to be able to obtain the sizelimit cause when using default MultiPart
        if (ex.getCause() != null && ex.getCause().getCause() != null
            && ex.getCause().getCause() instanceof FileUploadBase.SizeException) {
            return new ResponseEntity<Object>(new ErrorMessage("errors.upload.filesize",
                ImmutableMap.of("size", fileUploadHelper.getImageMaxSize())),status);
        }
        return new ResponseEntity<Object>(new ErrorMessage("errors.upload.generic"),status);
    }
    //CommonsMultipartResolver
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    ResponseEntity<?> handleException2(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Object>(new ErrorMessage("errors.upload.filesize",
            ImmutableMap.of("size",fileUploadHelper.getImageMaxSize())),status);
        //return new ResponseEntity<>(new CustomErrorType(status.value(), ex.getMessage()), status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
