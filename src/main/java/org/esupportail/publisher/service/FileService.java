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
package org.esupportail.publisher.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import com.google.common.io.Files;
import com.mysema.commons.lang.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.esupportail.publisher.service.exceptions.UnsupportedMimeTypeException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Service
@Slf4j
public class FileService {

    private static final DateFormat df = new SimpleDateFormat("yyyyMM");

    @Inject
    @Qualifier("publicFileUploadHelper")
    private FileUploadHelper publicFileUploadHelper;

    @Inject
    @Qualifier("protectedFileUploadHelper")
    @Getter
    private FileUploadHelper protectedFileUploadHelper;


    public boolean deleteInternalResource(final String urlPath) {
        if (urlPath != null && !urlPath.startsWith("http://") && !urlPath.startsWith("https://")) {
            String path = urlPath.replace(publicFileUploadHelper.getUrlResourceMapping(), "");
            for (String unremovablePath : publicFileUploadHelper.getUnremovablePaths()) {
                if (!path.isEmpty() && !unremovablePath.isEmpty() && path.matches(unremovablePath)) {
                    log.debug("Don't removing file in path {} as declared as unremovablePath in {} !", path, unremovablePath);
                    return false;
                }
            }
            path = publicFileUploadHelper.getUploadDirectoryPath() + path;
            final File file = new File(path);
            if (file.exists()) {
                final boolean deleted = file.delete();
                if (!deleted) {
                    log.error("Tried to delete the file {} failed, track errors!", urlPath);
                    return false;
                } else {
                    return true;
                }
            }
            log.error("Tried to delete the file {} but it doesn't exist, track errors!", urlPath);
        }
        return false;
    }


    public String uploadInternalResource(final Long entityId,final String name, final MultipartFile file) throws MultipartException {
        return this.uploadResource(entityId, name, file, publicFileUploadHelper);
    }

    public boolean deletePrivateResource(final String urlPath) {
        if (urlPath != null && !urlPath.startsWith("http://") && !urlPath.startsWith("https://")) {
            String path = urlPath.replace(protectedFileUploadHelper.getUrlResourceMapping(), "");
            for (String unremovablePath : protectedFileUploadHelper.getUnremovablePaths()) {
                if (!path.isEmpty() && !unremovablePath.isEmpty() && path.matches(unremovablePath)) {
                    log.debug("Don't removing file in path {} as declared as unremovablePath in {} !", path, unremovablePath);
                    return false;
                }
            }
            path = protectedFileUploadHelper.getUploadDirectoryPath() + path;
            final File file = new File(path);
            if (file.exists()) {
                final boolean deleted = file.delete();
                if (!deleted) {
                    log.error("Tried to delete the file {} failed, track errors!", urlPath);
                    return false;
                } else {
                    return true;
                }
            }
            log.error("Tried to delete the file {} but it doesn't exist, track errors!", urlPath);
        }
        return false;
    }

    public String uploadPrivateResource(final Long entityId,final String name, final MultipartFile file) throws MultipartException {
        return this.uploadResource(entityId, name, file, protectedFileUploadHelper);
    }

    private String uploadResource(final Long entityId,final String name, final MultipartFile file, final FileUploadHelper fileUploadHelper) throws MultipartException {
        // Checking size
        if (file.getSize() > fileUploadHelper.getFileMaxSize()) {
            throw new MaxUploadSizeExceededException(fileUploadHelper.getFileMaxSize());
        }
        // Checking ContentType
        Pair<Boolean, MultipartException> isAuthorized = isAuthorizedMimeType(file,fileUploadHelper);
        Assert.notNull(isAuthorized.getFirst(), "The result should not return a null boolean");
        if (!isAuthorized.getFirst()) {
            if (isAuthorized.getSecond() != null)
                throw isAuthorized.getSecond();
            return null;
        }

        try {
            final String fileExt = Files.getFileExtension(file.getOriginalFilename()).toLowerCase();
            String fname = new SimpleDateFormat("yyyyMMddHHmmss'." + fileExt + "'").format(new Date());
//            if (name != null && name.length() > 0 ) {
//                fname = Files.getNameWithoutExtension(name) + "-" + fname;
//            }
            final String internPath = fileUploadHelper.getUploadDirectoryPath();
            final String relativPath = String.valueOf(entityId).hashCode() + File.separator + df.format(new Date()) + File.separator;
            final String path = internPath + relativPath;
            File inFile = new File(path + fname);
            // checking if path is existing
            if (!inFile.getParentFile().exists()) {
                boolean error = !inFile.getParentFile().mkdirs();
                if (error) {
                    log.error("Can't create directory {} to upload file, track error!", inFile.getParentFile().getPath());
                    return null;
                }
            }
            // checking if file exist else renaming file name
            int i= 1;
            while (inFile.exists()) {
                inFile = new File(path + i + fname);
                i++;
            }
            log.debug("Uploading file as {}", inFile.getPath());
            // start upload
            file.transferTo(inFile);
            return fileUploadHelper.getUrlResourceMapping() + relativPath + fname;
        } catch (Exception e) {
            log.error("File Upload error", e);
            return null;
        }
    }

    private Pair<Boolean, MultipartException> isAuthorizedMimeType(final MultipartFile file, final FileUploadHelper fileUploadHelper) {
        if (file != null && !file.isEmpty()) {
            final String detectedType = file.getContentType();
            log.debug("Detected file Content informations {} ", detectedType);

            if (detectedType != null && fileUploadHelper.getAuthorizedMimeType() != null && fileUploadHelper.getAuthorizedMimeType().contains(detectedType)) {
                return new Pair<Boolean, MultipartException>(true, null);
            }
            log.warn("File {} with ContentType {} isn't authorized", file.getName(), detectedType);
            return new Pair<Boolean, MultipartException>(false, new UnsupportedMimeTypeException(detectedType));
        }
        return new Pair<Boolean, MultipartException>(false, new MultipartException("Unable to read the content type of an empty file", new FileNotFoundException("file")));
    }

}
