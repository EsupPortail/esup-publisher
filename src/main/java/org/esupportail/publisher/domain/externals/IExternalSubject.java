package org.esupportail.publisher.domain.externals;

import java.util.List;
import java.util.Map;

/**
 * Created by jgribonvald on 03/06/15.
 */
public interface IExternalSubject {

    String getId();

    String getDisplayName();

    Map<String, List<String>> getAttributes();

    List<String> getAttribute(final String name);
}
