package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import de.symeda.sormas.api.Disease;

public class DashboardContactStatisticDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207649L;

	public static final String PREVIOUS_CONTACTS = "previousContacts";
	public static final String CURRENT_CONTACTS = "contacts";

	private int contactsCount;
	private int newContactsCount;
	private int newContactsPercentage;
	private int symptomaticContactsCount;
	private int symptomaticContactsPercentage;
	private int confirmedContactsCount;
	private int contactClassificationConfirmedPercentage;
	private int unconfirmedContactsCount;
	private int contactClassificationUnconfirmedPercentage;
	private int notContactsCount;
	private int contactClassificationNotAContactPercentage;

	Map<Disease, Map<String, Integer>> diseaseMap = new TreeMap<>();

	private DashboardContactFollowUpDto dashboardContactFollowUp;
	private DashboardContactStoppedFollowUpDto dashboardContactStoppedFollowUp;
	private DashboardContactVisitDto dashboardContactVisit;

	public DashboardContactStatisticDto(
		int contactsCount,
		int newContactsCount,
		int newContactsPercentage,
		int symptomaticContactsCount,
		int symptomaticContactsPercentage,
		int confirmedContactsCount,
		int contactClassificationConfirmedPercentage,
		int unconfirmedContactsCount,
		int contactClassificationUnconfirmedPercentage,
		int notContactsCount,
		int contactClassificationNotAContactPercentage,
		Map<Disease, Map<String, Integer>> diseaseMap,
		DashboardContactFollowUpDto dashboardContactFollowUp,
		DashboardContactStoppedFollowUpDto dashboardContactStoppedFollowUp,
		DashboardContactVisitDto dashboardContactVisit) {
		this.contactsCount = contactsCount;
		this.newContactsCount = newContactsCount;
		this.newContactsPercentage = newContactsPercentage;
		this.symptomaticContactsCount = symptomaticContactsCount;
		this.symptomaticContactsPercentage = symptomaticContactsPercentage;
		this.confirmedContactsCount = confirmedContactsCount;
		this.contactClassificationConfirmedPercentage = contactClassificationConfirmedPercentage;
		this.unconfirmedContactsCount = unconfirmedContactsCount;
		this.contactClassificationUnconfirmedPercentage = contactClassificationUnconfirmedPercentage;
		this.notContactsCount = notContactsCount;
		this.contactClassificationNotAContactPercentage = contactClassificationNotAContactPercentage;
		this.diseaseMap = diseaseMap;
		this.dashboardContactFollowUp = dashboardContactFollowUp;
		this.dashboardContactStoppedFollowUp = dashboardContactStoppedFollowUp;
		this.dashboardContactVisit = dashboardContactVisit;
	}

	public int getContactsCount() {
		return contactsCount;
	}

	public int getNewContactsCount() {
		return newContactsCount;
	}

	public int getNewContactsPercentage() {
		return newContactsPercentage;
	}

	public int getSymptomaticContactsCount() {
		return symptomaticContactsCount;
	}

	public int getSymptomaticContactsPercentage() {
		return symptomaticContactsPercentage;
	}

	public int getConfirmedContactsCount() {
		return confirmedContactsCount;
	}

	public int getContactClassificationConfirmedPercentage() {
		return contactClassificationConfirmedPercentage;
	}

	public int getUnconfirmedContactsCount() {
		return unconfirmedContactsCount;
	}

	public int getContactClassificationUnconfirmedPercentage() {
		return contactClassificationUnconfirmedPercentage;
	}

	public int getNotContactsCount() {
		return notContactsCount;
	}

	public int getContactClassificationNotAContactPercentage() {
		return contactClassificationNotAContactPercentage;
	}

	public Map<Disease, Map<String, Integer>> getDiseaseMap() {
		return diseaseMap;
	}

	public DashboardContactFollowUpDto getDashboardContactFollowUp() {
		return dashboardContactFollowUp;
	}

	public DashboardContactStoppedFollowUpDto getDashboardContactStoppedFollowUp() {
		return dashboardContactStoppedFollowUp;
	}

	public DashboardContactVisitDto getDashboardContactVisit() {
		return dashboardContactVisit;
	}
}
