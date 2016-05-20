package org.esupportail.publisher.web.rest.dto;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.esupportail.publisher.domain.ContextKey;

import java.util.List;

/**
 * Created by jgribonvald on 19/05/16.
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchSubjectFormDTO {

    private ContextKey context;

    private List<ContextKey> subContexts = Lists.newArrayList();

    private String search;

}
