package org.esupportail.publisher.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StringEvaluationMode {
    CONTAINS,
    EQUALS,
    STARTS_WITH,
    ENDS_WITH,
    EXISTS,
    MATCH,
}
