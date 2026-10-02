package de.symeda.sormas.api.personaldata;

import java.util.List;
import java.util.Objects;

public class PersonalDataSearchResponse {

	private List<PersonalDataIndexDto> results;

	private Integer totalCount;

	public List<PersonalDataIndexDto> getResults() {
		return results;
	}

	public void setResults(List<PersonalDataIndexDto> results) {
		this.results = results;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PersonalDataSearchResponse that = (PersonalDataSearchResponse) o;
		return Objects.equals(results, that.results) && Objects.equals(totalCount, that.totalCount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(results, totalCount);
	}
}
