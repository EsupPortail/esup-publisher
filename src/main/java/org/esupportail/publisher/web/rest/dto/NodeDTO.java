package org.esupportail.publisher.web.rest.dto;

import lombok.*;
import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.enums.PermissionType;

import java.io.Serializable;

/**
 * Created by jgribonvald on 28/05/15.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class NodeDTO implements Serializable {

    @NonNull
    private IContext contextObject;
    @NonNull
    private boolean hasChilds;
    @NonNull
    private PermissionType userPerms;


}
