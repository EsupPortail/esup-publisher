package org.esupportail.publisher.service;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Service
@Slf4j
public class FileService {

    private static final DateFormat df = new SimpleDateFormat("yyyyMM");

    @Inject
    private FileUploadHelper internalFileUploadHelper;


    public boolean deleteInternalResource(final String urlPath) {
        if (urlPath != null && !urlPath.startsWith("http://") && !urlPath.startsWith("https://")) {
            String path = urlPath.replace(internalFileUploadHelper.getUrlResourceMapping(), "");
            path = internalFileUploadHelper.getUploadDirectoryPath() + path;
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


    public String uploadInternalResource(final Long entityId,final String name, final MultipartFile file) {
        try {
            //byte[] bytes = file.getBytes();
            final String fileExt = Files.getFileExtension(file.getOriginalFilename()).toLowerCase();
            String fname = (name != null && name.length() > 0) ? name : new SimpleDateFormat("yyyyMMddHHmmss'." + fileExt + "'").format(new Date());
            final String internPath = internalFileUploadHelper.getUploadDirectoryPath();
            String relativPath = String.valueOf(entityId).hashCode() + File.separator + df.format(new Date()) + File.separator;
            String path = internPath + relativPath;
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
//            BufferedOutputStream stream =
//                new BufferedOutputStream(new FileOutputStream(inFile));
//            stream.write(bytes);
//            stream.close();
            return internalFileUploadHelper.getUrlResourceMapping() + relativPath + fname;
        } catch (Exception e) {
            log.error("File Upload error", e);
            return null;
        }
    }

    public boolean deletePrivateResource(final String urlPath) {
        if (urlPath != null && !urlPath.startsWith("http://") && !urlPath.startsWith("https://")) {
            String path = urlPath.replace(internalFileUploadHelper.getUrlResourceMapping(), "");
            path = internalFileUploadHelper.getUploadDirectoryPath() + path;
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

    public String uploadPrivateResource(final Long entityId,final String name, final MultipartFile file) {
        return this.uploadInternalResource(entityId, name, file);
    }
}
