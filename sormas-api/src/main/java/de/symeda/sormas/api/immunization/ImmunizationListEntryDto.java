package de.symeda.sormas.api.immunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class ImmunizationListEntryDto extends PseudonymizableIndexDto implements IImmunization, Serializable, Cloneable {

	public static final String I18N_PREFIX = "Immunization";

	public static final String UUID = "uuid";
	public static final String DISEASE = "disease";
	public static final String MEANS_OF_IMMUNIZATION = "meansOfImmunization";
	public static final String IMMUNIZATION_STATUS = "immunizationStatus";
	public static final String IMMUNIZATION_MANAGEMENT_STATUS = "immunizationManagementStatus";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String IMMUNIZATION_PERIOD = "immunizationPeriod";

	private Disease disease;
	private MeansOfImmunization meansOfImmunization;
	private ImmunizationStatus immunizationStatus;
	private ImmunizationManagementStatus immunizationManagementStatus;
	private Date startDate;
	private Date endDate;

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
