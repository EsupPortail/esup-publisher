package org.esupportail.publisher.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.util.CustomEnumSerializer;

/**
 * Created by jgribonvald on 19/06/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberFormDTO {

    private SubjectDTO subject;

    @JsonSerialize(using = CustomEnumSerializer.class)
    private SubscribeType subscribeType;

}
