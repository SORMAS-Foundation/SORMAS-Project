package de.symeda.sormas.api.infrastructure.country;

import java.io.Serializable;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class CountryCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = -3172115054516586926L;

	private EntityRelevanceStatus relevanceStatus;
	private String nameCodeLike;
	private SubcontinentReferenceDto subcontinent;

	public CountryCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	@IgnoreForUrl
	public String getNameCodeLike() {
		return nameCodeLike;
	}

	public SubcontinentReferenceDto getSubcontinent() {
		return subcontinent;
	}

	public CountryCriteria nameCodeLike(String nameCodeLike) {
		this.nameCodeLike = nameCodeLike;
		return this;
	}

	public CountryCriteria subcontinent(SubcontinentReferenceDto subcontinent) {
		this.subcontinent = subcontinent;
		return this;
	}
}
