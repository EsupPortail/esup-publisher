package org.esupportail.publisher.web.rest.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.SubjectType;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode(of = "id")
public class GroupDTO extends SubjectDTO {

    @Getter
    private boolean hasChilds;

	@Getter
	private Map<String, List<String>> attributes;
	@Getter
	private boolean foundOnExternalSource;

	public GroupDTO(@NotNull final String groupId, final String displayName, final boolean foundOnExternalSource) {
        super(new SubjectKeyDTO(groupId, SubjectType.GROUP), displayName, foundOnExternalSource);
	}

	/**
	 * Constructor à utiliser lors de la convertion d'un objet ExternalSource.
	 */
	public GroupDTO(@NotNull final String groupId, final String displayName,
                    final boolean hasChilds, final Map<String, List<String>> attributes) {
        super(new SubjectKeyDTO(groupId, SubjectType.GROUP), displayName, displayName != null);
        this.hasChilds = hasChilds;
        this.attributes = attributes;
		if (displayName != null)
            this.foundOnExternalSource = true;
	}
    }
