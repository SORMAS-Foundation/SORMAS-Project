package de.symeda.sormas.api.region;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class CountryCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = -3172115054516586926L;

	private EntityRelevanceStatus relevanceStatus;
	private String nameCodeLike;
	private SubContinentReferenceDto subContinent;

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

	public SubContinentReferenceDto getSubContinent() {
		return subContinent;
	}

	public CountryCriteria nameCodeLike(String nameCodeLike) {
		this.nameCodeLike = nameCodeLike;
		return this;
	}
	public CountryCriteria subContinent(SubContinentReferenceDto subContinent) {
		this.subContinent = subContinent;
		return this;
	}
}
