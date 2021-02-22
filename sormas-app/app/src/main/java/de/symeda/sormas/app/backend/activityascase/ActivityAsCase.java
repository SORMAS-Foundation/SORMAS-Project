package de.symeda.sormas.app.backend.activityascase;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureRole;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = ActivityAsCase.TABLE_NAME)
@DatabaseTable(tableName = ActivityAsCase.TABLE_NAME)
@EmbeddedAdo(parentAccessor = ActivityAsCase.EPI_DATA)
public class ActivityAsCase extends PseudonymizableAdo {

	private static final long serialVersionUID = -5570515874416024605L;

	public static final String TABLE_NAME = "activityascase";
	public static final String I18N_PREFIX = "ActivityAsCase";

	public static final String EPI_DATA = "epiData";
	public static final String LOCATION = "location";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private EpiData epiData;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date startDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date endDate;
	@Column(columnDefinition = "text")
	private String description;
	@Enumerated(EnumType.STRING)
	private ActivityAsCaseType activityAsCaseType;
	@Column(columnDefinition = "text")
	private String activityAsCaseTypeDetails;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Location location;
	@Enumerated(EnumType.STRING)
	private ExposureRole role;

	// Type of Place
	@Enumerated(EnumType.STRING)
	private TypeOfPlace typeOfPlace;
	@Column(columnDefinition = "text")
	private String typeOfPlaceDetails;
	@Enumerated(EnumType.STRING)
	private MeansOfTransport meansOfTransport;
	@Column(columnDefinition = "text")
	private String meansOfTransportDetails;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String connectionNumber;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String seatNumber;
	@Enumerated(EnumType.STRING)
	private WorkEnvironment workEnvironment;

	// Exposure sub-types
	@Enumerated(EnumType.STRING)
	private GatheringType gatheringType;
	@Column(columnDefinition = "text")
	private String gatheringDetails;
	@Enumerated(EnumType.STRING)
	private HabitationType habitationType;
	@Column(columnDefinition = "text")
	private String habitationDetails;

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public EpiData getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ActivityAsCaseType getActivityAsCaseType() {
		return activityAsCaseType;
	}

	public void setActivityAsCaseType(ActivityAsCaseType activityAsCaseType) {
		this.activityAsCaseType = activityAsCaseType;
	}

	public String getActivityAsCaseTypeDetails() {
		return activityAsCaseTypeDetails;
	}

	public void setActivityAsCaseTypeDetails(String activityAsCaseTypeDetails) {
		this.activityAsCaseTypeDetails = activityAsCaseTypeDetails;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public ExposureRole getRole() {
		return role;
	}

	public void setRole(ExposureRole role) {
		this.role = role;
	}

	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}

	public String getTypeOfPlaceDetails() {
		return typeOfPlaceDetails;
	}

	public void setTypeOfPlaceDetails(String typeOfPlaceDetails) {
		this.typeOfPlaceDetails = typeOfPlaceDetails;
	}

	public MeansOfTransport getMeansOfTransport() {
		return meansOfTransport;
	}

	public void setMeansOfTransport(MeansOfTransport meansOfTransport) {
		this.meansOfTransport = meansOfTransport;
	}

	public String getMeansOfTransportDetails() {
		return meansOfTransportDetails;
	}

	public void setMeansOfTransportDetails(String meansOfTransportDetails) {
		this.meansOfTransportDetails = meansOfTransportDetails;
	}

	public String getConnectionNumber() {
		return connectionNumber;
	}

	public void setConnectionNumber(String connectionNumber) {
		this.connectionNumber = connectionNumber;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public WorkEnvironment getWorkEnvironment() {
		return workEnvironment;
	}

	public void setWorkEnvironment(WorkEnvironment workEnvironment) {
		this.workEnvironment = workEnvironment;
	}

	public GatheringType getGatheringType() {
		return gatheringType;
	}

	public void setGatheringType(GatheringType gatheringType) {
		this.gatheringType = gatheringType;
	}

	public String getGatheringDetails() {
		return gatheringDetails;
	}

	public void setGatheringDetails(String gatheringDetails) {
		this.gatheringDetails = gatheringDetails;
	}

	public HabitationType getHabitationType() {
		return habitationType;
	}

	public void setHabitationType(HabitationType habitationType) {
		this.habitationType = habitationType;
	}

	public String getHabitationDetails() {
		return habitationDetails;
	}

	public void setHabitationDetails(String habitationDetails) {
		this.habitationDetails = habitationDetails;
	}

}
