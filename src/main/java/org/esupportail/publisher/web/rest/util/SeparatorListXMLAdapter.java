package org.esupportail.publisher.web.rest.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 09/06/16.
 */
public class SeparatorListXMLAdapter extends XmlAdapter<String, List<String>> {

    private static final String SEPARATOR = "/";

    @Override
    public List<String> unmarshal(final String string) {
        final List<String> strings = new ArrayList<>();

        for (final String s : string.split(SEPARATOR)) {
            final String trimmed = s.trim();

            if (trimmed.length() > 0) {
                strings.add(trimmed);
            }
        }

        return strings;
    }

    @Override
    public String marshal(final List<String> strings) {
        final StringBuilder sb = new StringBuilder();

        for (final String string : strings) {
            if (sb.length() > 0) {
                sb.append(SEPARATOR);
            }

            sb.append(string);
        }

        return sb.toString();
    }
}
