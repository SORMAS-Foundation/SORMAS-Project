package de.symeda.sormas.api.travelentry;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class TravelEntryIndexDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "TravelEntry";

	public static final String UUID = "uuid";
	public static final String REPORT_DATE = "reportDate";
	public static final String DISEASE = "disease";
	public static final String EXTERNAL_ID = "externalId";
	public static final String POINT_OF_ENTRY_NAME = "pointOfEntryName";

	private String uuid;
	private Date reportDate;
	private Disease disease;
	private String externalId;
	@SensitiveData
	private String pointOfEntryName;

	public TravelEntryIndexDto(String uuid, Date reportDate, Disease disease, String externalId, String pointOfEntryName) {

		this.uuid = uuid;
		this.reportDate = reportDate;
		this.disease = disease;
		this.externalId = externalId;
		this.pointOfEntryName = pointOfEntryName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getPointOfEntryName() {
		return pointOfEntryName;
	}

	public void setPointOfEntryName(String pointOfEntryName) {
		this.pointOfEntryName = pointOfEntryName;
	}

	public TravelEntryReferenceDto toReference() {
		return new TravelEntryReferenceDto(uuid, pointOfEntryName, externalId);
	}
}
