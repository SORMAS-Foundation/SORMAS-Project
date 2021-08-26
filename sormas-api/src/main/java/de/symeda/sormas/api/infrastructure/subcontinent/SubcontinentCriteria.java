package de.symeda.sormas.api.infrastructure.subcontinent;

import java.io.Serializable;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class SubcontinentCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = -3172115054516586926L;

	private EntityRelevanceStatus relevanceStatus;
	private String nameLike;
	private ContinentReferenceDto continent;

	public SubcontinentCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
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

	public ContinentReferenceDto getContinent() {
		return continent;
	}

	public SubcontinentCriteria nameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}

	public SubcontinentCriteria continent(ContinentReferenceDto continent) {
		this.continent = continent;
		return this;
	}
}
