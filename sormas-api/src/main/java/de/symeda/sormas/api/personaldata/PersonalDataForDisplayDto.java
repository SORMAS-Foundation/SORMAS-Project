package de.symeda.sormas.api.personaldata;

import java.util.List;
import java.util.Objects;

public class PersonalDataForDisplayDto {

	private String html;
	private PersonalDataSummaryDto personalDataSummary;
	private List<CreatableEntityDto> creatableEntityDtos;

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public PersonalDataSummaryDto getPersonalDataSummary() {
		return personalDataSummary;
	}

	public void setPersonalDataSummary(PersonalDataSummaryDto personalDataSummary) {
		this.personalDataSummary = personalDataSummary;
	}

	public List<CreatableEntityDto> getCreatableEntityDtos() {
		return creatableEntityDtos;
	}

	public void setCreatableEntityDtos(List<CreatableEntityDto> creatableEntityDtos) {
		this.creatableEntityDtos = creatableEntityDtos;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PersonalDataForDisplayDto that = (PersonalDataForDisplayDto) o;
		return Objects.equals(html, that.html)
			&& Objects.equals(personalDataSummary, that.personalDataSummary)
			&& Objects.equals(creatableEntityDtos, that.creatableEntityDtos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(html, personalDataSummary, creatableEntityDtos);
	}
}
