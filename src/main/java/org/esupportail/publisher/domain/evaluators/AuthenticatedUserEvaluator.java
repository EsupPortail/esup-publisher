/**
 *
 */
package org.esupportail.publisher.domain.evaluators;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.service.evaluators.IEvaluationVisitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author GIP RECIA - Julien Gribonvald 17 nov. 2014
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class", visible = true)
@JsonTypeName("AUTHENTICATED")
@Entity
@DiscriminatorValue("Authenticated")
public class AuthenticatedUserEvaluator extends AbstractEvaluator {

	/** */
	private static final long serialVersionUID = 2558275213355338280L;

	/**
     *
     */
	public AuthenticatedUserEvaluator() {
		super();
	}

	public boolean isApplicable(IEvaluationVisitor visitor) {
		return visitor.isApplicable(this);
	}

}
