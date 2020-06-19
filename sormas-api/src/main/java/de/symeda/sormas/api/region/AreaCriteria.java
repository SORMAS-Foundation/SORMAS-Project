package de.symeda.sormas.api.region;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;

@SuppressWarnings("serial")
public class AreaCriteria extends BaseCriteria implements Serializable, Cloneable {

	private String textFilter;
	private EntityRelevanceStatus relevanceStatus;

	@IgnoreForUrl
	public String getTextFilter() {
		return textFilter;
	}

	public AreaCriteria textFilter(String textFilter) {
		this.textFilter = textFilter;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public AreaCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

}
