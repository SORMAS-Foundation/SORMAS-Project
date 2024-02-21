package de.symeda.sormas.api.travelentry;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class TravelEntryListEntryDto extends PseudonymizableIndexDto implements IsTravelEntry, Serializable, Cloneable {

	public static final String I18N_PREFIX = "TravelEntry";
	public static final String POINT_OF_ENTRY_I18N_PREFIX = "PointOfEntry";

	public static final String REPORT_DATE = "reportDate";

	private Date reportDate;
	private Disease disease;
	private String pointOfEntryName;

	private boolean isInJurisdiction;

	public TravelEntryListEntryDto(String uuid, Date reportDate, Disease disease, String pointOfEntryName, boolean isInJurisdiction) {
		super(uuid);
		this.reportDate = reportDate;
		this.disease = disease;
		this.pointOfEntryName = pointOfEntryName;
		this.isInJurisdiction = isInJurisdiction;
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

	public String getPointOfEntryName() {
		return pointOfEntryName;
	}

	public void setPointOfEntryName(String pointOfEntryName) {
		this.pointOfEntryName = pointOfEntryName;
	}

	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
