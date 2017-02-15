package org.esupportail.publisher.web.rest.util;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by jgribonvald on 09/06/16.
 */
public class RFC822DateTimeXmlAdapter extends XmlAdapter<String, DateTime> {

    // We need to set local to US for this RFC, as only few timezone are defined in this RFC
    public static DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z").withLocale(Locale.US);

    @Override
    public DateTime unmarshal(String v) throws Exception {
        return formatter.parseDateTime(v);
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        return formatter.print(v);
    }
}
