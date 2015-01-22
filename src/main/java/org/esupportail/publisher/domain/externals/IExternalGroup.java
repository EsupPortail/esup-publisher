package org.esupportail.publisher.domain.externals;

import org.esupportail.publisher.domain.Subject;

import java.util.List;

/**
 * Created by jgribonvald on 24/04/15.
 */
public interface IExternalGroup extends IExternalSubject, Subject {

    List<String> getGroupMembers();
    List<String> getUserMembers();

    boolean hasMembers();

}
