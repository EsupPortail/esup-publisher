package org.esupportail.publisher.domain.util;

import org.esupportail.publisher.domain.AbstractItem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jgribonvald on 30/03/15.
 * Usefull to avoid Java Type Erasure on JsonSerialization of List< AbstractItem >
 *     see https://github.com/FasterXML/jackson-databind/issues/699
 */
public class ItemList extends ArrayList<AbstractItem> {

    public ItemList(Collection<? extends AbstractItem> c) {
        super(c);
    }
}
