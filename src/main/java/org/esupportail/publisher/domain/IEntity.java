/**
 *
 */
package org.esupportail.publisher.domain;

import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 18 juil. 2014
 */
public interface IEntity<ID extends Serializable> {

	ID getId();

}
