package org.esupportail.publisher.web.rest.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

/**
 * Custom Jackson serializer for displaying Joda DateTime objects.
 */
public class RFC822DateTimeSerializer extends JsonSerializer<DateTime> {

    //see in RFC822DateTimeXmlAdapter.java for the reference
    //private static DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z").withLocale(Locale.US);

    @Override
    public void serialize(DateTime value, JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
        generator.writeString(RFC822DateTimeXmlAdapter.formatter.print(value));
    }
}
