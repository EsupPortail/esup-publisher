package org.esupportail.publisher.service.bean;

import java.util.List;

import lombok.*;

/**
 * Created by jgribonvald on 25/01/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadHelper {

    private String uploadDirectoryPath;

    private String resourceLocation;

    private String urlResourceMapping;

    private List<String> unremovablePaths;

    private boolean useDefaultPath;

    private long fileMaxSize;

    private List<String> authorizedMimeType;
}
