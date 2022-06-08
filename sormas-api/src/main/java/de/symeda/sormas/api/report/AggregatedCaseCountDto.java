package de.symeda.sormas.api.report;

import java.io.Serializable;
import java.util.Objects;

import de.symeda.sormas.api.Disease;

public class AggregatedCaseCountDto implements Serializable {

	public static final String I18N_PREFIX = "AggregateReport";
	public static final String DISEASE = "disease";
	public static final String AGE_GROUP = "ageGroup";
	public static final String NEW_CASES = "newCases";
	public static final String LAB_CONFIRMATIONS = "labConfirmations";
	public static final String DEATHS = "deaths";
	private static final long serialVersionUID = -6857559727281292882L;
	private Disease disease;
	private int newCases;
	private int labConfirmations;
	private int deaths;
	private String ageGroup;

	public AggregatedCaseCountDto(Disease disease, int newCases, int labConfirmations, int deaths, String ageGroup) {

		this.disease = disease;
		this.newCases = newCases;
		this.labConfirmations = labConfirmations;
		this.deaths = deaths;
		this.ageGroup = ageGroup;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public int getNewCases() {
		return newCases;
	}

	public void setNewCases(int newCases) {
		this.newCases = newCases;
	}

	public int getLabConfirmations() {
		return labConfirmations;
	}

	public void setLabConfirmations(int labConfirmations) {
		this.labConfirmations = labConfirmations;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + deaths;
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + labConfirmations;
		result = prime * result + newCases;
		result = prime * result + ((ageGroup == null) ? 0 : ageGroup.hashCode());;
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregatedCaseCountDto other = (AggregatedCaseCountDto) obj;
		if (deaths != other.deaths)
			return false;
		if (disease != other.disease)
			return false;
		if (labConfirmations != other.labConfirmations)
			return false;
		if (newCases != other.newCases)
			return false;
		if (!Objects.equals(ageGroup, other.ageGroup))
			return false;
		return true;
	}
}
