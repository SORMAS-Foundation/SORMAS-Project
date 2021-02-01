package de.symeda.sormas.api.externaljournal.patientdiary;

import java.io.Serializable;
import java.util.List;

public class PatientDiaryQueryResponse implements Serializable {

	private static final long serialVersionUID = -1089320137577059438L;

	private int total;
	private int count;
	private List<PatientDiaryPersonData> results;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<PatientDiaryPersonData> getResults() {
		return results;
	}

	public void setResults(List<PatientDiaryPersonData> results) {
		this.results = results;
	}
}
