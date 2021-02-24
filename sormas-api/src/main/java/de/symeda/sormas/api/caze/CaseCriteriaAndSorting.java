package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.utils.SortProperty;

import java.io.Serializable;
import java.util.List;

public class CaseCriteriaAndSorting implements Serializable {
    private CaseCriteria caseCriteria;
    private List<SortProperty> sortProperties;

    public CaseCriteria getCaseCriteria() {
        return caseCriteria;
    }

    public void setCaseCriteria(CaseCriteria caseCriteria) {
        this.caseCriteria = caseCriteria;
    }

    public List<SortProperty> getSortProperties() {
        return sortProperties;
    }

    public void setSortProperties(List<SortProperty> sortProperties) {
        this.sortProperties = sortProperties;
    }
}
