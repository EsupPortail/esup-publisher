package org.esupportail.publisher.web.rest.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResource {
	@NonNull
	private String code;
	@NonNull
	private String message;

	private List<FieldErrorResource> fieldErrors;

}