package de.symeda.sormas.api.externalpersondata;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ExternalPersonDataSearchResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String RESULTS = "results";
	public static final String TOTAL_COUNT = "totalCount";

	private List<ExternalPersonDataIndexDto> results;

	private Integer totalCount;

	/**
	 * Results for the requested offset and limit, in a stable order across pages.
	 */
	public List<ExternalPersonDataIndexDto> getResults() {
		return results;
	}

	public void setResults(List<ExternalPersonDataIndexDto> results) {
		this.results = results;
	}

	/**
	 * Total number of matching results before pagination. Providers must supply this for paginated requests.
	 */
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
		ExternalPersonDataSearchResponse that = (ExternalPersonDataSearchResponse) o;
		return Objects.equals(results, that.results) && Objects.equals(totalCount, that.totalCount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(results, totalCount);
	}
}
