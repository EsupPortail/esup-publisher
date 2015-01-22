package org.esupportail.publisher.service.factories;

import org.esupportail.publisher.domain.SubjectKey;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyDTO;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 17 oct. 2014
 */
public interface SubjectDTOFactory extends GenericDTOFactory<SubjectDTO, User, SubjectKeyDTO, SubjectKey> {

}
