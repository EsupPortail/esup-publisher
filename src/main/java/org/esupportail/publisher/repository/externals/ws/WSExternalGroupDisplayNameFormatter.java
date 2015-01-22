package org.esupportail.publisher.repository.externals.ws;

import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.externals.ExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalGroupDisplayNameFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 04/06/15.
 */
@Slf4j
public class WSExternalGroupDisplayNameFormatter implements IExternalGroupDisplayNameFormatter {

    private static final String dnPattern = ".*:([^:]*)";

    private Pattern pattern = Pattern.compile(dnPattern);

    public WSExternalGroupDisplayNameFormatter() {
        super();
    }

    @Override
    public ExternalGroup format(ExternalGroup input) {
            // setting as new displayName the origin displayName formatted
         input.setDisplayName(format(input.getDisplayName()));

        return input;
    }

    private String format(String input) {
        if (input != null && !input.isEmpty()) {
            Matcher m = pattern.matcher(input);
            if (m.find()) {
                final String displayName = m.group(1);
                log.debug("Matcher found group displayName, value is : {}", displayName);
                return displayName.replaceAll("_", " ");
            }
            log.warn("No displayname formatting will be done as pattern isn't matching !");
        }
        return input;
    }
}
