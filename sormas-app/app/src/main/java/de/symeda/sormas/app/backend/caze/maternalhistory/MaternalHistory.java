package de.symeda.sormas.app.backend.caze.maternalhistory;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name = MaternalHistory.TABLE_NAME)
@DatabaseTable(tableName = MaternalHistory.TABLE_NAME)
@EmbeddedAdo
public class MaternalHistory extends PseudonymizableAdo {

	private static final long serialVersionUID = -5534360436146186436L;

	public static final String TABLE_NAME = "maternalHistory";
	public static final String I18N_PREFIX = "MaternalHistory";

	@Column
	private Integer childrenNumber;
	@Column
	private Integer ageAtBirth;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown conjunctivitis;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date conjunctivitisOnset;
	@Column
	private Integer conjunctivitisMonth;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown maculopapularRash;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date maculopapularRashOnset;
	@Column
	private Integer maculopapularRashMonth;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown swollenLymphs;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date swollenLymphsOnset;
	@Column
	private Integer swollenLymphsMonth;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown arthralgiaArthritis;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date arthralgiaArthritisOnset;
	@Column
	private Integer arthralgiaArthritisMonth;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown otherComplications;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date otherComplicationsOnset;
	@Column
	private Integer otherComplicationsMonth;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String otherComplicationsDetails;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown rubella;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date rubellaOnset;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown rashExposure;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date rashExposureDate;
	@Column
	private Integer rashExposureMonth;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region rashExposureRegion;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District rashExposureDistrict;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community rashExposureCommunity;

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Integer getChildrenNumber() {
		return childrenNumber;
	}

	public void setChildrenNumber(Integer childrenNumber) {
		this.childrenNumber = childrenNumber;
	}

	public Integer getAgeAtBirth() {
		return ageAtBirth;
	}

	public void setAgeAtBirth(Integer ageAtBirth) {
		this.ageAtBirth = ageAtBirth;
	}

	public YesNoUnknown getConjunctivitis() {
		return conjunctivitis;
	}

	public void setConjunctivitis(YesNoUnknown conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}

	public Date getConjunctivitisOnset() {
		return conjunctivitisOnset;
	}

	public void setConjunctivitisOnset(Date conjunctivitisOnset) {
		this.conjunctivitisOnset = conjunctivitisOnset;
	}

	public Integer getConjunctivitisMonth() {
		return conjunctivitisMonth;
	}

	public void setConjunctivitisMonth(Integer conjunctivitisMonth) {
		this.conjunctivitisMonth = conjunctivitisMonth;
	}

	public YesNoUnknown getMaculopapularRash() {
		return maculopapularRash;
	}

	public void setMaculopapularRash(YesNoUnknown maculopapularRash) {
		this.maculopapularRash = maculopapularRash;
	}

	public Date getMaculopapularRashOnset() {
		return maculopapularRashOnset;
	}

	public void setMaculopapularRashOnset(Date maculopapularRashOnset) {
		this.maculopapularRashOnset = maculopapularRashOnset;
	}

	public Integer getMaculopapularRashMonth() {
		return maculopapularRashMonth;
	}

	public void setMaculopapularRashMonth(Integer maculopapularRashMonth) {
		this.maculopapularRashMonth = maculopapularRashMonth;
	}

	public YesNoUnknown getSwollenLymphs() {
		return swollenLymphs;
	}

	public void setSwollenLymphs(YesNoUnknown swollenLymphs) {
		this.swollenLymphs = swollenLymphs;
	}

	public Date getSwollenLymphsOnset() {
		return swollenLymphsOnset;
	}

	public void setSwollenLymphsOnset(Date swollenLymphsOnset) {
		this.swollenLymphsOnset = swollenLymphsOnset;
	}

	public Integer getSwollenLymphsMonth() {
		return swollenLymphsMonth;
	}

	public void setSwollenLymphsMonth(Integer swollenLymphsMonth) {
		this.swollenLymphsMonth = swollenLymphsMonth;
	}

	public YesNoUnknown getArthralgiaArthritis() {
		return arthralgiaArthritis;
	}

	public void setArthralgiaArthritis(YesNoUnknown arthralgiaArthritis) {
		this.arthralgiaArthritis = arthralgiaArthritis;
	}

	public Date getArthralgiaArthritisOnset() {
		return arthralgiaArthritisOnset;
	}

	public void setArthralgiaArthritisOnset(Date arthralgiaArthritisOnset) {
		this.arthralgiaArthritisOnset = arthralgiaArthritisOnset;
	}

	public Integer getArthralgiaArthritisMonth() {
		return arthralgiaArthritisMonth;
	}

	public void setArthralgiaArthritisMonth(Integer arthralgiaArthritisMonth) {
		this.arthralgiaArthritisMonth = arthralgiaArthritisMonth;
	}

	public YesNoUnknown getOtherComplications() {
		return otherComplications;
	}

	public void setOtherComplications(YesNoUnknown otherComplications) {
		this.otherComplications = otherComplications;
	}

	public Date getOtherComplicationsOnset() {
		return otherComplicationsOnset;
	}

	public void setOtherComplicationsOnset(Date otherComplicationsOnset) {
		this.otherComplicationsOnset = otherComplicationsOnset;
	}

	public Integer getOtherComplicationsMonth() {
		return otherComplicationsMonth;
	}

	public void setOtherComplicationsMonth(Integer otherComplicationsMonth) {
		this.otherComplicationsMonth = otherComplicationsMonth;
	}

	public String getOtherComplicationsDetails() {
		return otherComplicationsDetails;
	}

	public void setOtherComplicationsDetails(String otherComplicationsDetails) {
		this.otherComplicationsDetails = otherComplicationsDetails;
	}

	public YesNoUnknown getRubella() {
		return rubella;
	}

	public void setRubella(YesNoUnknown rubella) {
		this.rubella = rubella;
	}

	public Date getRubellaOnset() {
		return rubellaOnset;
	}

	public void setRubellaOnset(Date rubellaOnset) {
		this.rubellaOnset = rubellaOnset;
	}

	public YesNoUnknown getRashExposure() {
		return rashExposure;
	}

	public void setRashExposure(YesNoUnknown rashExposure) {
		this.rashExposure = rashExposure;
	}

	public Date getRashExposureDate() {
		return rashExposureDate;
	}

	public void setRashExposureDate(Date rashExposureDate) {
		this.rashExposureDate = rashExposureDate;
	}

	public Integer getRashExposureMonth() {
		return rashExposureMonth;
	}

	public void setRashExposureMonth(Integer rashExposureMonth) {
		this.rashExposureMonth = rashExposureMonth;
	}

	public Region getRashExposureRegion() {
		return rashExposureRegion;
	}

	public void setRashExposureRegion(Region rashExposureRegion) {
		this.rashExposureRegion = rashExposureRegion;
	}

	public District getRashExposureDistrict() {
		return rashExposureDistrict;
	}

	public void setRashExposureDistrict(District rashExposureDistrict) {
		this.rashExposureDistrict = rashExposureDistrict;
	}

	public Community getRashExposureCommunity() {
		return rashExposureCommunity;
	}

	public void setRashExposureCommunity(Community rashExposureCommunity) {
		this.rashExposureCommunity = rashExposureCommunity;
	}
}
