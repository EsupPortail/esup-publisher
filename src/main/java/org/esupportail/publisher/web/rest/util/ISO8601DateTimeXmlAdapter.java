package org.esupportail.publisher.web.rest.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by jgribonvald on 09/06/16.
 */
@Slf4j
public class ISO8601DateTimeXmlAdapter extends XmlAdapter<String, DateTime> {

    //private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    public DateTime unmarshal(String v) throws Exception {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(v);
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        return ISODateTimeFormat.dateTime().print(v);
    }
}
