package de.symeda.sormas.api.statistics;

import java.io.Serializable;
import java.util.List;

public class StatisticsCaseCriteria implements Serializable {

	private static final long serialVersionUID = 4997176351789123549L;

	private List<Integer> onsetYears;


	public List<Integer> getOnsetYears() {
		return onsetYears;
	}

	public StatisticsCaseCriteria onsetYears(List<Integer> onsetYears) {
		this.onsetYears = onsetYears;
		return this;
	}
}
