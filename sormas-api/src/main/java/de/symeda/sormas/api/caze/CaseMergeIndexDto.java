package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;

/**
 * equals method was overridden in order to allow same case being loaded in the TreeData data type multiple times
 * (it allows to display same case being duplicate against multiple other cases)
 *
 */
public class CaseMergeIndexDto extends CaseIndexDto {

	//@formatter:off
	public CaseMergeIndexDto(
			long id, String uuid, String epidNumber, String externalID, String externalToken, String internalToken, String personUuid, String personFirstName, String personLastName, Disease disease,
			DiseaseVariant diseaseVariant, String diseaseDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus,
			PresentCondition presentCondition, Date reportDate, Date creationDate, String regionUuid,
			String districtUuid, String healthFacilityUuid, String healthFacilityName, String healthFacilityDetails,
			String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails, String surveillanceOfficerUuid, CaseOutcome outcome,
			Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Sex sex, Date quarantineTo,
			Float completeness, FollowUpStatus followUpStatus, Date followUpUntil, SymptomJournalStatus symptomJournalStatus, VaccinationStatus vaccinationStatus, Date changeDate, Long facilityId,
			// responsible jurisdiction
			String responsibleRegionUuid, String responsibleDistrictUuid, String responsibleDistrictName, DeletionReason deletionReason, String otherDeletionReason,  boolean isInJurisdiction
			) {
		super(
			id,
			uuid,
			epidNumber,
			externalID,
			externalToken,
			internalToken,
			personUuid,
			personFirstName,
			personLastName,
			disease,
			diseaseVariant,
			diseaseDetails,
			caseClassification,
			investigationStatus,
			presentCondition,
			reportDate,
			creationDate,
			regionUuid,
			districtUuid,
			healthFacilityUuid,
			healthFacilityName,
			healthFacilityDetails,
			pointOfEntryUuid,
			pointOfEntryName,
			pointOfEntryDetails,
			surveillanceOfficerUuid,
			outcome,
			age,
			ageType,
			birthdateDD,
			birthdateMM,
			birthdateYYYY,
			sex,
			quarantineTo,
			completeness,
			followUpStatus,
			followUpUntil,
			symptomJournalStatus,
			vaccinationStatus,
			changeDate,
			facilityId,
			responsibleRegionUuid,
			responsibleDistrictUuid,
			responsibleDistrictName,
			deletionReason,
			otherDeletionReason,
			isInJurisdiction,
			null
		);
	}
	//@formatter:on

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

}
