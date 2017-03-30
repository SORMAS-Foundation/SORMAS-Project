package de.symeda.sormas.api.epidata;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.PreciseDateAdapter;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EpiDataBurialDto extends DataTransferObject {

	private static final long serialVersionUID = -6274798743525238569L;

	public static final String I18N_PREFIX = "EpiDataBurial";
	
	public static final String BURIAL_PERSON_NAME = "burialPersonName";
	public static final String BURIAL_RELATION = "burialRelation";
	public static final String BURIAL_DATE_FROM = "burialDateFrom";
	public static final String BURIAL_DATE_TO = "burialDateTo";
	public static final String BURIAL_ADDRESS = "burialAddress";
	public static final String BURIAL_ILL = "burialIll";
	public static final String BURIAL_TOUCHING = "burialTouching";
	
	@Diseases({Disease.EVD,Disease.LASSA})
	private String burialPersonName;
	@Diseases({Disease.EVD,Disease.LASSA})
	private String burialRelation;
	@Diseases({Disease.EVD,Disease.LASSA})
	private Date burialDateFrom;
	@Diseases({Disease.EVD,Disease.LASSA})
	private Date burialDateTo;
	@Diseases({Disease.EVD,Disease.LASSA})
	private LocationDto burialAddress;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown burialIll;
	@Diseases({Disease.EVD,Disease.LASSA})
	private YesNoUnknown burialTouching;
	
	public String getBurialPersonName() {
		return burialPersonName;
	}
	public void setBurialPersonName(String burialPersonName) {
		this.burialPersonName = burialPersonName;
	}
	
	public String getBurialRelation() {
		return burialRelation;
	}
	public void setBurialRelation(String burialRelation) {
		this.burialRelation = burialRelation;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getBurialDateFrom() {
		return burialDateFrom;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setBurialDateFrom(Date burialDateFrom) {
		this.burialDateFrom = burialDateFrom;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getBurialDateTo() {
		return burialDateTo;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setBurialDateTo(Date burialDateTo) {
		this.burialDateTo = burialDateTo;
	}
	
	public LocationDto getBurialAddress() {
		return burialAddress;
	}
	public void setBurialAddress(LocationDto burialAddress) {
		this.burialAddress = burialAddress;
	}
	
	public YesNoUnknown getBurialIll() {
		return burialIll;
	}
	public void setBurialIll(YesNoUnknown burialIll) {
		this.burialIll = burialIll;
	}
	
	public YesNoUnknown getBurialTouching() {
		return burialTouching;
	}
	public void setBurialTouching(YesNoUnknown burialTouching) {
		this.burialTouching = burialTouching;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EpiDataBurialDto other = (EpiDataBurialDto) obj;
		if (burialAddress == null) {
			if (other.burialAddress != null)
				return false;
		} else if (!burialAddress.equals(other.burialAddress))
			return false;
		if (burialDateFrom == null) {
			if (other.burialDateFrom != null)
				return false;
		} else if (!burialDateFrom.equals(other.burialDateFrom))
			return false;
		if (burialDateTo == null) {
			if (other.burialDateTo != null)
				return false;
		} else if (!burialDateTo.equals(other.burialDateTo))
			return false;
		if (burialIll != other.burialIll)
			return false;
		if (burialPersonName == null) {
			if (other.burialPersonName != null)
				return false;
		} else if (!burialPersonName.equals(other.burialPersonName))
			return false;
		if (burialRelation == null) {
			if (other.burialRelation != null)
				return false;
		} else if (!burialRelation.equals(other.burialRelation))
			return false;
		if (burialTouching != other.burialTouching)
			return false;
		return true;
	}
	
}
