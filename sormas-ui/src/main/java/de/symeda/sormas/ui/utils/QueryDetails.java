package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class QueryDetails extends BaseCriteria {

    public static final String RESULT_LIMIT = "resultLimit";

    private Integer resultLimit;

    public Integer getResultLimit() {
        return resultLimit;
    }

    public void setResultLimit(Integer resultLimit) {
        this.resultLimit = resultLimit;
    }
}
