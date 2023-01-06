package de.symeda.sormas.api.infrastructure.area;

import java.io.Serializable;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("serial")
public class AreaCriteria extends BaseCriteria implements Serializable, Cloneable {

	@Schema(description = "Free-text filter-pattern for area name")
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
