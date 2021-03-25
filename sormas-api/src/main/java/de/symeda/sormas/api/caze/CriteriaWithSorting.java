package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.SortProperty;

import java.io.Serializable;
import java.util.List;

public class CriteriaWithSorting<T extends BaseCriteria> implements Serializable {

	private T criteria;
	private List<SortProperty> sortProperties;

	public T getCriteria() {
		return criteria;
	}

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
