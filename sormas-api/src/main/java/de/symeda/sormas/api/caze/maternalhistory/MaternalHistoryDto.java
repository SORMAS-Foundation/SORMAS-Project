package de.symeda.sormas.api.caze.maternalhistory;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import io.swagger.v3.oas.annotations.media.Schema;

@DependingOnFeatureType(featureType = FeatureType.CASE_SURVEILANCE)
@Schema(description = "Data transfer object for data related to the mother's history of illness. Only relevant for Disease.RUBELLA")
public class MaternalHistoryDto extends PseudonymizableDto {

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

	@Schema(description = "Number of children the mother has given birth to")
	private Integer childrenNumber;
	@Schema(description = "Age of the mother when giving birth to the case person")
	private Integer ageAtBirth;
	@Schema(description = "Whether the mother suffers from conjunctivitis")
	private YesNoUnknown conjunctivitis;
	@Schema(description = "Date when the conjunctivitis set on")
	private Date conjunctivitisOnset;
	@Schema(description = "Month of pregnancy the conjunctivitis set on")
	private Integer conjunctivitisMonth;
	@Schema(description = "Whether the mother suffers form maculopapular skin rash")
	private YesNoUnknown maculopapularRash;
	@Schema(description = "Date when the maculopapular skin rash set on")
	private Date maculopapularRashOnset;
	@Schema(description = "Month of pregnancy when the maculopapular skin rash set on ")
	private Integer maculopapularRashMonth;
	@Schema(description = "Whether the mother has swollen lymph nodes")
	private YesNoUnknown swollenLymphs;
	@Schema(description = "Date when the swollen lymph nodes set on")
	private Date swollenLymphsOnset;
	@Schema(description = "Month of pregnancy when the swollen lymph nodes set on")
	private Integer swollenLymphsMonth;
	@Schema(description = "Whether the mother suffers from arthralgia or arthritis")
	private YesNoUnknown arthralgiaArthritis;
	@Schema(description = "Date when the arthralgia or arthritis set on")
	private Date arthralgiaArthritisOnset;
	@Schema(description = "Month of pregnancy the arthralgia or arthritis set on")
	private Integer arthralgiaArthritisMonth;
	@Schema(description = "Whether other complications occured during the pregnancy")
	private YesNoUnknown otherComplications;
	@Schema(description = "Date the other complications set on")
	private Date otherComplicationsOnset;
	@Schema(description = "Month of pregnancy the other complications set on")
	private Integer otherComplicationsMonth;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the other complications during the pregnancy")
	private String otherComplicationsDetails;
	@Schema(description = "Whether the mother has a laboratory-confirmed case of rubella")
	private YesNoUnknown rubella;
	@Schema(description = "Date the rubella set on")
	private Date rubellaOnset;
	@Schema(description = "Whether the mother was exposed to rash during the pregnancy")
	private YesNoUnknown rashExposure;
	@Schema(description = "Date when the mother was exposed to rash during the pregnancy")
	private Date rashExposureDate;
	@Schema(description = "Month of pregnancy when the mother was exposed to rash")
	private Integer rashExposureMonth;
	private RegionReferenceDto rashExposureRegion;
	private DistrictReferenceDto rashExposureDistrict;
	@SensitiveData
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
