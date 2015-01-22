package org.esupportail.publisher.web.rest.dto.evaluators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.OperatorType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class OperatorEvaluatorDTO extends EvaluatorDTO implements Serializable {

    /** */
    private static final long serialVersionUID = 8235163884629100798L;

    @NotNull
    private OperatorType type;

    /** List of evaluators to evaluate, must contains more than one evaluator. */
    @NotNull
    @Size(min=1)
    private Set<EvaluatorDTO> evaluators = new HashSet<EvaluatorDTO>();

    /**
     * @param type
     * @param evaluators
     */
    public OperatorEvaluatorDTO(@NotNull final OperatorType type, @NotNull final Set<EvaluatorDTO> evaluators) {
        super();
        this.type = type;
        this.evaluators = evaluators;
    }

    /**
     * @param modelId
     * @param type
     * @param evaluators
     */
    public OperatorEvaluatorDTO(@NotNull final Long modelId, @NotNull final OperatorType type,
            @NotNull @Size(min=1) final Set<EvaluatorDTO> evaluators) {
        super(modelId);
        this.type = type;
        this.evaluators = evaluators;
    }


}
