package org.esupportail.publisher.domain;

import lombok.*;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = "parent")
public abstract class AbstractFeed extends AbstractClassification implements
		Serializable {

	/** */
	private static final long serialVersionUID = -4460812621868603330L;

	/** This field corresponds to the database column parent_id. */
	@NotNull
	@NonNull
	@ManyToOne
	@JoinColumn(name = "parent_id")
	protected Category parent;

	public AbstractFeed() {
		super();
	}

	public AbstractFeed(final boolean rssAllowed, final String name,
			final String iconUrl, final String lang, final int ttl,
			final int displayOrder, final AccessType accessView,
			final String description,
			final DisplayOrderType defaultDisplayOrder,
			final Publisher publisher, final Category parent) {
		super(rssAllowed, name, iconUrl, lang, ttl, displayOrder, accessView,
				description, defaultDisplayOrder, publisher);
		this.parent = parent;
	}

}
