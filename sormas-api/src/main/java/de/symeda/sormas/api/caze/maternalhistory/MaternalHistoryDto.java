package de.symeda.sormas.api.caze.maternalhistory;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class MaternalHistoryDto extends EntityDto {

	private static final long serialVersionUID = -5534360436146186436L;

	public static final String I18N_PREFIX = "MaternalHistory";

	public static final String CHILDREN_NUMBER = "childrenNumber";
	public static final String AGE_AT_BIRTH = "ageAtBirth";
	public static final String CONJUNCTIVITIS = "conjunctivitis";
	public static final String CONJUNCTIVITIS_ONSET = "conjunctivitisOnset";
	public static final String CONJUNCTIVITIS_MONTH = "conjunctivitisMonth";
	public static final String MACULOPAPULAR_RASH = "maculopapularRash";
	public static final String MACULOPAPULAR_RASH_ONSET = "maculopapularRashOnset";
	public static final String MACULOPAPULAR_RASH_MONTH = "maculopapularRashMonth";
	public static final String SWOLLEN_LYMPHS = "swollenLymphs";
	public static final String SWOLLEN_LYMPHS_ONSET = "swollenLymphsOnset";
	public static final String SWOLLEN_LYMPHS_MONTH = "swollenLymphsMonth";
	public static final String ARTHRALGIA_ARTHRITIS = "arthralgiaArthritis";
	public static final String ARTHRALGIA_ARTHRITIS_ONSET = "arthralgiaArthritisOnset";
	public static final String ARTHRALGIA_ARTHRITIS_MONTH = "arthralgiaArthritisMonth";
	public static final String OTHER_COMPLICATIONS = "otherComplications";
	public static final String OTHER_COMPLICATIONS_ONSET = "otherComplicationsOnset";
	public static final String OTHER_COMPLICATIONS_MONTH = "otherComplicationsMonth";
	public static final String OTHER_COMPLICATIONS_DETAILS = "otherComplicationsDetails";
	public static final String RUBELLA = "rubella";
	public static final String RUBELLA_ONSET = "rubellaOnset";
	public static final String RASH_EXPOSURE = "rashExposure";
	public static final String RASH_EXPOSURE_DATE = "rashExposureDate";
	public static final String RASH_EXPOSURE_MONTH = "rashExposureMonth";
	public static final String RASH_EXPOSURE_REGION = "rashExposureRegion";
	public static final String RASH_EXPOSURE_DISTRICT = "rashExposureDistrict";
	public static final String RASH_EXPOSURE_COMMUNITY = "rashExposureCommunity";

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
	private RegionReferenceDto rashExposureRegion;
	private DistrictReferenceDto rashExposureDistrict;
	private CommunityReferenceDto rashExposureCommunity;

	public static MaternalHistoryDto build() {

		MaternalHistoryDto maternalHistory = new MaternalHistoryDto();
		maternalHistory.setUuid(DataHelper.createUuid());
		return maternalHistory;
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

	public RegionReferenceDto getRashExposureRegion() {
		return rashExposureRegion;
	}

	public void setRashExposureRegion(RegionReferenceDto rashExposureRegion) {
		this.rashExposureRegion = rashExposureRegion;
	}

	public DistrictReferenceDto getRashExposureDistrict() {
		return rashExposureDistrict;
	}

	public void setRashExposureDistrict(DistrictReferenceDto rashExposureDistrict) {
		this.rashExposureDistrict = rashExposureDistrict;
	}

	public CommunityReferenceDto getRashExposureCommunity() {
		return rashExposureCommunity;
	}

	public void setRashExposureCommunity(CommunityReferenceDto rashExposureCommunity) {
		this.rashExposureCommunity = rashExposureCommunity;
	}
}
