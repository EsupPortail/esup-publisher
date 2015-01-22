package org.esupportail.publisher.web.rest.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class FieldErrorResource {
	private String resource;
	private String field;
	private String code;
	private String message;

}
