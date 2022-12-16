package de.symeda.sormas.api.infrastructure.continent;

import java.io.Serializable;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import io.swagger.v3.oas.annotations.media.Schema;

public class ContinentCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = -3172115054516586926L;

	private EntityRelevanceStatus relevanceStatus;
	@Schema(description = "Search-term for continent name")
	private String nameLike;

	public ContinentCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	@IgnoreForUrl
	public String getNameLike() {
		return nameLike;
	}

	public ContinentCriteria nameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}
}
