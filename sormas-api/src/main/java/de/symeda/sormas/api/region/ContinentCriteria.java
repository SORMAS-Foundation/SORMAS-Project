package de.symeda.sormas.api.region;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class ContinentCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = -3172115054516586926L;

	private EntityRelevanceStatus relevanceStatus;
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
