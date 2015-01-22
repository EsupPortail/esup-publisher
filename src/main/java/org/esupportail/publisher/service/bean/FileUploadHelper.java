package org.esupportail.publisher.service.bean;

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

    private boolean useDefaultPath;
}
