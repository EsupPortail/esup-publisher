package org.esupportail.publisher.domain.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * Created by jgribonvald on 01/04/15.
 */
public class PermissionTypeDeserializer extends JsonDeserializer<PermissionType> {
    @Override
    public PermissionType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        PermissionType type = PermissionType.fromName(jp.getValueAsString());
        if (type != null) {
            return type;
        }
        throw new JsonMappingException(String.format("Invalid value '%s' for %s, must be in range of %s", jp.getValueAsString(),
            PermissionType.class.getSimpleName(), PermissionType.values().toString()));
    }
}
