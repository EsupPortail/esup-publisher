package org.esupportail.publisher.web.rest.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by jgribonvald on 09/06/16.
 */
public class ReadableDateTimeXmlAdapter extends XmlAdapter<String, DateTime> {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("EEEE dd MMMM yyyy HH:mm:ss 'GMT'ZZ");

    @Override
    public DateTime unmarshal(String v) throws Exception {
        return formatter.parseDateTime(v);
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        return formatter.print(v);
    }
}
