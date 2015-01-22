package org.esupportail.publisher.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Type operator to evaluate the list of evaluators.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OperatorType {
    OR,
    AND,
    NOT;
}
