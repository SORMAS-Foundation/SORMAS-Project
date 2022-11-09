package de.symeda.sormas.ui.contact.components.linelisting.sharedinfo;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

public class SharedInfoFieldDto implements Serializable {

	public static final String CAZE = "caze";
	public static final String DISEASE = "disease";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";

	private CaseReferenceDto caze;
	private Disease disease;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
}
