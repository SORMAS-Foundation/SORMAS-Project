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
	private int unconfirmedContactsCount;
	private int confirmedContactsCount;
	private int notContactsCount;
	private int symptomaticContactsCount;

	Map<Disease, Map<String, Integer>> diseaseMap = new TreeMap<>();

	private DashboardContactFollowUpDto dashboardContactFollowUp;
	private DashboardContactVisitDto dashboardContactVisit;

	public DashboardContactStatisticDto(
		int contactsCount,
		int newContactsCount,
		int unconfirmedContactsCount,
		int confirmedContactsCount,
		int notContactsCount,
		int symptomaticContactsCount,
		Map<Disease, Map<String, Integer>> diseaseMap,
		DashboardContactFollowUpDto dashboardContactFollowUp,
		DashboardContactVisitDto dashboardContactVisit) {
		this.contactsCount = contactsCount;
		this.newContactsCount = newContactsCount;
		this.unconfirmedContactsCount = unconfirmedContactsCount;
		this.confirmedContactsCount = confirmedContactsCount;
		this.notContactsCount = notContactsCount;
		this.symptomaticContactsCount = symptomaticContactsCount;
		this.diseaseMap = diseaseMap;
		this.dashboardContactFollowUp = dashboardContactFollowUp;
		this.dashboardContactVisit = dashboardContactVisit;
	}

	public int getContactsCount() {
		return contactsCount;
	}

	public int getNewContactsCount() {
		return newContactsCount;
	}

	public int getUnconfirmedContactsCount() {
		return unconfirmedContactsCount;
	}

	public int getConfirmedContactsCount() {
		return confirmedContactsCount;
	}

	public int getNotContactsCount() {
		return notContactsCount;
	}

	public int getSymptomaticContactsCount() {
		return symptomaticContactsCount;
	}

	public Map<Disease, Map<String, Integer>> getDiseaseMap() {
		return diseaseMap;
	}

	public DashboardContactFollowUpDto getDashboardContactFollowUp() {
		return dashboardContactFollowUp;
	}

	public DashboardContactVisitDto getDashboardContactVisit() {
		return dashboardContactVisit;
	}
}
