package de.symeda.sormas.api.report;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;

public class AggregatedCaseCountDto implements Serializable {

	private static final long serialVersionUID = -6857559727281292882L;

	public static final String I18N_PREFIX = "AggregateReport";
	
	public static final String DISEASE = "disease";
	public static final String NEW_CASES = "newCases";
	public static final String LAB_CONFIRMATIONS = "labConfirmations";
	public static final String DEATHS = "deaths";
	
	private Disease disease;
	private int newCases;
	private int labConfirmations;
	private int deaths;

	public AggregatedCaseCountDto(Disease disease, int newCases, int labConfirmations, int deaths) {
		this.disease = disease;
		this.newCases = newCases;
		this.labConfirmations = labConfirmations;
		this.deaths = deaths;
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
	
}
