package de.symeda.sormas.api.visit;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class VisitIndexDto extends PseudonymizableIndexDto implements IVisit, Serializable {

	private static final long serialVersionUID = -2707325548819626469L;

	public static final String I18N_PREFIX = "Visit";

	public static final String UUID = "uuid";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_STATUS = "visitStatus";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String SYMPTOMATIC = "symptomatic";
	public static final String TEMPERATURE = "temperature";
	public static final String TEMPERATURE_SOURCE = "temperatureSource";
	public static final String ORIGIN = "origin";

	private Long id;
	private Date visitDateTime;
	private VisitStatus visitStatus;
	@SensitiveData
	private String visitRemarks;
	private Disease disease;
	private Boolean symptomatic;
	private Float temperature;
	private TemperatureSource temperatureSource;
	private VisitOrigin origin;
	private UserReferenceDto visitUser;

	private Boolean isInJurisdiction;

	public VisitIndexDto(
		Long id,
		String uuid,
		Date visitDateTime,
		VisitStatus visitStatus,
		String visitRemarks,
		Disease disease,
		Boolean symptomatic,
		Float temperature,
		TemperatureSource temperatureSource,
		VisitOrigin origin,
		String visitUserUuid,
		String visitUserFirstName,
		String visitUserLastName,
		boolean isInJurisdiction) {

		super(uuid);
		this.id = id;
		this.visitDateTime = visitDateTime;
		this.visitStatus = visitStatus;
		this.visitRemarks = visitRemarks;
		this.disease = disease;
		this.symptomatic = symptomatic;
		this.temperature = temperature;
		this.temperatureSource = temperatureSource;
		this.origin = origin;
		this.visitUser = visitUserUuid != null ? new UserReferenceDto(visitUserUuid, visitUserFirstName, visitUserLastName) : null;
		this.isInJurisdiction = isInJurisdiction;
	}

	public Long getId() {
		return id;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Boolean getSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public TemperatureSource getTemperatureSource() {
		return temperatureSource;
	}

	public void setTemperatureSource(TemperatureSource temperatureSource) {
		this.temperatureSource = temperatureSource;
	}

	public VisitOrigin getOrigin() {
		return origin;
	}

	public void setOrigin(VisitOrigin origin) {
		this.origin = origin;
	}

	public UserReferenceDto getVisitUser() {
		return visitUser;
	}

	public void setVisitUser(UserReferenceDto visitUser) {
		this.visitUser = visitUser;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}
}
