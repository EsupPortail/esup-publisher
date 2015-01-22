package org.esupportail.publisher.domain.externals;

import javax.validation.constraints.NotNull;

/**
 * Created by jgribonvald on 04/06/15.
 */
public interface IExternalGroupDisplayNameFormatter {

    ExternalGroup format(@NotNull final ExternalGroup input);

}
