package org.esupportail.publisher.domain.externals;

/**
 * Created by jgribonvald on 04/06/15.
 */
public class EmptyExternalGroupDisplayNameFormatter implements IExternalGroupDisplayNameFormatter {

    public EmptyExternalGroupDisplayNameFormatter() {
    }

    @Override
    public ExternalGroup format(ExternalGroup input) {
        return input;
    }

}
