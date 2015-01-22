package org.esupportail.publisher.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ContextKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jgribonvald on 22/04/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {

    //private Publisher publisher;

    private Set<ContextKey> classifications = new HashSet<>();

    private AbstractItem item;

    private List<SubscriberFormDTO> targets = new ArrayList<>();

}
