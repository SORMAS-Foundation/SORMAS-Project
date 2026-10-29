package de.symeda.sormas.api.externalpersondata;

import java.io.Serializable;
import java.util.Objects;

public class ExternalPersonDataForDisplayDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String html;
	private ExternalPersonDataSummaryDto summary;

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public ExternalPersonDataSummaryDto getSummary() {
		return summary;
	}

	public void setSummary(ExternalPersonDataSummaryDto summary) {
		this.summary = summary;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalPersonDataForDisplayDto that = (ExternalPersonDataForDisplayDto) o;
		return Objects.equals(html, that.html) && Objects.equals(summary, that.summary);
	}

	@Override
	public int hashCode() {
		return Objects.hash(html, summary);
	}
}
