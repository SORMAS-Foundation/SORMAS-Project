package de.symeda.sormas.api.immunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class ImmunizationListEntryDto extends PseudonymizableIndexDto implements IsImmunization, Serializable, Cloneable {

	public static final String I18N_PREFIX = "Immunization";

	public static final String UUID = "uuid";
	public static final String DISEASE = "disease";
	public static final String MEANS_OF_IMMUNIZATION = "meansOfImmunization";
	public static final String IMMUNIZATION_STATUS = "immunizationStatus";
	public static final String IMMUNIZATION_MANAGEMENT_STATUS = "immunizationManagementStatus";
	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	public static final String START_DATE = "startDate";
	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	public static final String END_DATE = "endDate";
	public static final String IMMUNIZATION_PERIOD = "immunizationPeriod";
	public static final String VALID_FROM = "validFrom";
	public static final String VALID_UNTIL = "validUntil";
	public static final String VALIDITY_PERIOD = "validityPeriod";

	private Disease disease;
	private MeansOfImmunization meansOfImmunization;
	private ImmunizationStatus immunizationStatus;
	private ImmunizationManagementStatus immunizationManagementStatus;
	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	private Date startDate;
	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	private Date endDate;
	private Date validFrom;
	private Date validUntil;
	private Boolean relevantForVaccinationStatus;
	private String nonRelevantMessage;

	public ImmunizationListEntryDto(
		String uuid,
		Disease disease,
		MeansOfImmunization meansOfImmunization,
		ImmunizationStatus immunizationStatus,
		ImmunizationManagementStatus immunizationManagementStatus,
		Date startDate,
		Date endDate) {

		super(uuid);
		this.disease = disease;
		this.meansOfImmunization = meansOfImmunization;
		this.immunizationStatus = immunizationStatus;
		this.immunizationManagementStatus = immunizationManagementStatus;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public MeansOfImmunization getMeansOfImmunization() {
		return meansOfImmunization;
	}

	public void setMeansOfImmunization(MeansOfImmunization meansOfImmunization) {
		this.meansOfImmunization = meansOfImmunization;
	}

	public ImmunizationStatus getImmunizationStatus() {
		return immunizationStatus;
	}

	public void setImmunizationStatus(ImmunizationStatus immunizationStatus) {
		this.immunizationStatus = immunizationStatus;
	}

	public ImmunizationManagementStatus getImmunizationManagementStatus() {
		return immunizationManagementStatus;
	}

	public void setImmunizationManagementStatus(ImmunizationManagementStatus immunizationManagementStatus) {
		this.immunizationManagementStatus = immunizationManagementStatus;
	}

	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	public Date getStartDate() {
		return startDate;
	}

	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	public Date getEndDate() {
		return endDate;
	}

	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public Boolean getRelevantForVaccinationStatus() {
		return relevantForVaccinationStatus;
	}

	public void setRelevantForVaccinationStatus(Boolean relevantForVaccinationStatus) {
		this.relevantForVaccinationStatus = relevantForVaccinationStatus;
	}

	public String getNonRelevantMessage() {
		return nonRelevantMessage;
	}

	public void setNonRelevantMessage(String nonRelevantMessage) {
		this.nonRelevantMessage = nonRelevantMessage;
	}

	@Override
	public ImmunizationListEntryDto clone() {
		try {
			return (ImmunizationListEntryDto) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
