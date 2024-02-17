package de.symeda.sormas.api.travelentry;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class TravelEntryIndexDto extends PseudonymizableIndexDto implements IsTravelEntry, Serializable, Cloneable {

	public static final String I18N_PREFIX = "TravelEntry";

	public static final String UUID = "uuid";
	public static final String EXTERNAL_ID = "externalId";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String HOME_DISTRICT_NAME = "homeDistrictName";
	public static final String POINT_OF_ENTRY_NAME = "pointOfEntryName";
	public static final String RECOVERED = "recovered";
	public static final String VACCINATED = "vaccinated";
	public static final String TESTED_NEGATIVE = "testedNegative";
	public static final String QUARANTINE_TO = "quarantineTo";

	private String externalId;
	@PersonalData
	@SensitiveData
	private String personFirstName;
	@PersonalData
	@SensitiveData
	private String personLastName;

	private String homeDistrictName;
	private String pointOfEntryName;

	private boolean recovered;
	private boolean vaccinated;
	private boolean testedNegative;
	private Date quarantineTo;

	private DeletionReason deletionReason;
	private String otherDeletionReason;

	private boolean isInJurisdiction;

	public TravelEntryIndexDto(
		String uuid,
		String externalId,
		String personFirstName,
		String personLastName,
		String homeDistrictName,
		String pointOfEntryName,
		boolean recovered,
		boolean vaccinated,
		boolean testedNegative,
		Date quarantineTo,
		DeletionReason deletionReason,
		String otherDeletionReason,
		boolean isInJurisdiction) {
		super(uuid);
		this.externalId = externalId;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.homeDistrictName = homeDistrictName;
		this.pointOfEntryName = pointOfEntryName;
		this.recovered = recovered;
		this.vaccinated = vaccinated;
		this.testedNegative = testedNegative;
		this.quarantineTo = quarantineTo;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	public String getHomeDistrictName() {
		return homeDistrictName;
	}

	public void setHomeDistrictName(String homeDistrictName) {
		this.homeDistrictName = homeDistrictName;
	}

	public String getPointOfEntryName() {
		return pointOfEntryName;
	}

	public void setPointOfEntryName(String pointOfEntryName) {
		this.pointOfEntryName = pointOfEntryName;
	}

	public boolean isRecovered() {
		return recovered;
	}

	public void setRecovered(boolean recovered) {
		this.recovered = recovered;
	}

	public boolean isVaccinated() {
		return vaccinated;
	}

	public void setVaccinated(boolean vaccinated) {
		this.vaccinated = vaccinated;
	}

	public boolean isTestedNegative() {
		return testedNegative;
	}

	public void setTestedNegative(boolean testedNegative) {
		this.testedNegative = testedNegative;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}

	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
