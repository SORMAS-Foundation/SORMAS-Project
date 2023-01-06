package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditedClass;
import io.swagger.v3.oas.annotations.media.Schema;

@AuditedClass
public class DashboardContactStatisticDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207649L;

	public static final String PREVIOUS_CONTACTS = "previousContacts";
	public static final String CURRENT_CONTACTS = "contacts";

	@Schema(description = "Total number of contacts registered in the primary time period")
	private int contactsCount;
	@Schema(description = "Number of contacts newly registered during the primary time period")
	private int newContactsCount;
	@Schema(description = "Rounded percentage of newly registered contacts during the primary time period")
	private int newContactsPercentage;
	@Schema(description = "Number of contacts that show symptoms of the reseached disease during the primary time period")
	private int symptomaticContactsCount;
	@Schema(description = "Rounded percentage of contacts that show symptoms of the researched disease during the primary time period")
	private int symptomaticContactsPercentage;
	@Schema(description = "Number of confirmed contacts registered during the primary time period")
	private int confirmedContactsCount;
	@Schema(description = "Rounded percentage of confirmed contacts registered during the primary time period")
	private int contactClassificationConfirmedPercentage;
	@Schema(description = "Number of unconfirmed contacts registered during the primary time period")
	private int unconfirmedContactsCount;
	@Schema(description = "Rounded percentage of unconfirmed contacts registered during the primary time period")
	private int contactClassificationUnconfirmedPercentage;
	@Schema(
		description = "Number of contact entries, that have been classified as actually not being a contact, registered during the primary time period")
	private int notContactsCount;
	@Schema(
		description = "Rounded percentageof contact entries, that have been classified as actually not being a contact, registered during the primary time period")
	private int contactClassificationNotAContactPercentage;

	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
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
