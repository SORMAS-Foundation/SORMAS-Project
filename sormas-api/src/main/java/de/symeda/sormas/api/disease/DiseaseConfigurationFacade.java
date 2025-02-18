package de.symeda.sormas.api.disease;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface DiseaseConfigurationFacade {

	List<DiseaseConfigurationDto> getAllAfter(Date date);

	DiseaseConfigurationDto getByUuid(String uuid);

	List<DiseaseConfigurationDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	/**
	 * Returns a list containing all diseases whose configurations match the defined attributes
	 * (e.g. if active and primary are both true, only diseases that are both active and primary
	 * are returned). Attributes that are set to null are ignored.
	 */
	List<Disease> getAllDiseases(Boolean active, Boolean primary, boolean caseSurveillance, boolean aggregateReporting);

	List<Disease> getAllDiseases(Boolean active, Boolean primary, boolean caseSurveillance);

	boolean isActiveDisease(Disease disease);

	List<Disease> getAllActiveDiseases();

	boolean isPrimaryDisease(Disease disease);

	boolean hasFollowUp(Disease disease);

	List<Disease> getAllDiseasesWithFollowUp();

	int getFollowUpDuration(Disease disease);

	List<String> getAgeGroups(Disease disease);

	String getFirstAgeGroup(Disease disease);

	int getCaseFollowUpDuration(Disease disease);

	int getEventParticipantFollowUpDuration(Disease disease);

	Integer getAutomaticSampleAssignmentThreshold(Disease disease);

	void saveDiseaseConfiguration(DiseaseConfigurationDto configuration);

	Disease getDefaultDisease();

	List<Disease> getAllDiseasesWithFollowUp(Boolean active, Boolean primary, boolean caseBased);

	boolean usesExtendedClassification(Disease disease);

	boolean usesExtendedClassificationMulti(Disease disease);

	long count(DiseaseConfigurationCriteria criteria);

	List<DiseaseConfigurationIndexDto> getIndexList(
		DiseaseConfigurationCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties);
}
