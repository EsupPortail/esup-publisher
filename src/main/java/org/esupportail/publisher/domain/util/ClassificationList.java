package org.esupportail.publisher.domain.util;

import org.esupportail.publisher.domain.AbstractClassification;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jgribonvald on 30/03/15.
 * Usefull to avoid Java Type Erasure on JsonSerialization of List< AbstractClassification >
 *     see https://github.com/FasterXML/jackson-databind/issues/699
 */
public class ClassificationList extends ArrayList<AbstractClassification> {

    public ClassificationList(Collection<? extends AbstractClassification> c) {
        super(c);
    }
}
