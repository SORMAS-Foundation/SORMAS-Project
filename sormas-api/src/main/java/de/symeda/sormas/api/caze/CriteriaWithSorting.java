package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Criteria object for querying and sorting objects over indexList endpoints")
public class CriteriaWithSorting<T extends BaseCriteria> implements Serializable {

	private T criteria;
	private List<SortProperty> sortProperties;

	public T getCriteria() {
		return criteria;
	}

	@Hidden
	public void setCaseCriteria(T criteria) {
		this.criteria = criteria;
	}

	public List<SortProperty> getSortProperties() {
		return sortProperties;
	}

	public void setSortProperties(List<SortProperty> sortProperties) {
		this.sortProperties = sortProperties;
	}
}
