package de.symeda.sormas.backend.caze.maternalhistory;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity
@Audited
public class MaternalHistory extends AbstractDomainObject {

	private static final long serialVersionUID = -5534360436146186436L;

	public static final String TABLE_NAME = "maternalhistory";

	private Integer childrenNumber;
	private Integer ageAtBirth;
	private YesNoUnknown conjunctivitis;
	private Date conjunctivitisOnset;
	private Integer conjunctivitisMonth;
	private YesNoUnknown maculopapularRash;
	private Date maculopapularRashOnset;
	private Integer maculopapularRashMonth;
	private YesNoUnknown swollenLymphs;
	private Date swollenLymphsOnset;
	private Integer swollenLymphsMonth;
	private YesNoUnknown arthralgiaArthritis;
	private Date arthralgiaArthritisOnset;
	private Integer arthralgiaArthritisMonth;
	private YesNoUnknown otherComplications;
	private Date otherComplicationsOnset;
	private Integer otherComplicationsMonth;
	private String otherComplicationsDetails;
	private YesNoUnknown rubella;
	private Date rubellaOnset;
	private YesNoUnknown rashExposure;
	private Date rashExposureDate;
	private Integer rashExposureMonth;
	private Region rashExposureRegion;
	private District rashExposureDistrict;
	private Community rashExposureCommunity;

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

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getConjunctivitis() {
		return conjunctivitis;
	}

	public void setConjunctivitis(YesNoUnknown conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getMaculopapularRash() {
		return maculopapularRash;
	}

	public void setMaculopapularRash(YesNoUnknown maculopapularRash) {
		this.maculopapularRash = maculopapularRash;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSwollenLymphs() {
		return swollenLymphs;
	}

	public void setSwollenLymphs(YesNoUnknown swollenLymphs) {
		this.swollenLymphs = swollenLymphs;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getArthralgiaArthritis() {
		return arthralgiaArthritis;
	}

	public void setArthralgiaArthritis(YesNoUnknown arthralgiaArthritis) {
		this.arthralgiaArthritis = arthralgiaArthritis;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getOtherComplications() {
		return otherComplications;
	}

	public void setOtherComplications(YesNoUnknown otherComplications) {
		this.otherComplications = otherComplications;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getOtherComplicationsDetails() {
		return otherComplicationsDetails;
	}

	public void setOtherComplicationsDetails(String otherComplicationsDetails) {
		this.otherComplicationsDetails = otherComplicationsDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRubella() {
		return rubella;
	}

	public void setRubella(YesNoUnknown rubella) {
		this.rubella = rubella;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRubellaOnset() {
		return rubellaOnset;
	}

	public void setRubellaOnset(Date rubellaOnset) {
		this.rubellaOnset = rubellaOnset;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRashExposure() {
		return rashExposure;
	}

	public void setRashExposure(YesNoUnknown rashExposure) {
		this.rashExposure = rashExposure;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@ManyToOne(cascade = {})
	public Region getRashExposureRegion() {
		return rashExposureRegion;
	}

	public void setRashExposureRegion(Region rashExposureRegion) {
		this.rashExposureRegion = rashExposureRegion;
	}

	@ManyToOne(cascade = {})
	public District getRashExposureDistrict() {
		return rashExposureDistrict;
	}

	public void setRashExposureDistrict(District rashExposureDistrict) {
		this.rashExposureDistrict = rashExposureDistrict;
	}

	@ManyToOne(cascade = {})
	public Community getRashExposureCommunity() {
		return rashExposureCommunity;
	}

	public void setRashExposureCommunity(Community rashExposureCommunity) {
		this.rashExposureCommunity = rashExposureCommunity;
	}

}
