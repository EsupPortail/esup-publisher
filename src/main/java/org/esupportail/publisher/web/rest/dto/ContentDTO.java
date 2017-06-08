package org.esupportail.publisher.web.rest.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.Attachment;
import org.esupportail.publisher.domain.ContextKey;
import org.hibernate.validator.constraints.ScriptAssert;

/**
 * Created by jgribonvald on 22/04/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ScriptAssert(lang = "javascript", message = "Linked files should not be emtpy", script = "org.esupportail.publisher.web.rest.dto.ContentDTO.isValid(_this.item, _this.linkedFiles")
public class ContentDTO {

    //private Publisher publisher;

    private Set<ContextKey> classifications = new HashSet<>();

    private AbstractItem item;

    private List<SubscriberFormDTO> targets = new ArrayList<>();

    private Set<LinkedFileItemDTO> linkedFiles = new HashSet<>();


    public static boolean isValid(final AbstractItem item, final Set<LinkedFileItemDTO> linkedFiles) {
        return !(item instanceof Attachment && (linkedFiles == null || linkedFiles.isEmpty()));
    }

}
