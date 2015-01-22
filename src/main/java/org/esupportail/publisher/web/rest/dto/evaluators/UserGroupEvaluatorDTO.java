package org.esupportail.publisher.web.rest.dto.evaluators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.util.CstPropertiesLength;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=false)
public class UserGroupEvaluatorDTO extends EvaluatorDTO implements Serializable {

    /** */
    private static final long serialVersionUID = 503305126832517554L;

    @NotNull
    @Size(min=5, max= CstPropertiesLength.SUBJECTID)
    private String group;

    /**
     * Constructor to use when creating a new object.
     * @param role
     */
    public UserGroupEvaluatorDTO(@NotNull final String role) {
        super();
        this.group = role;
    }

    /**
     * Constructor to use when creating the object from JPA model.
     * @param modelId
     * @param role
     */
    public UserGroupEvaluatorDTO(@NotNull final Long modelId, @NotNull final String role) {
        super(modelId);
        this.group = role;
    }


}
