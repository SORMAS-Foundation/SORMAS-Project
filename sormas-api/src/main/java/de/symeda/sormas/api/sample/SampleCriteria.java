package de.symeda.sormas.api.sample;

import java.io.Serializable;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class SampleCriteria implements Serializable {

	private static final long serialVersionUID = 6981841762778471445L;

	private RegionReferenceDto caseRegion;
	private DistrictReferenceDto caseDistrict;
	private FacilityReferenceDto lab;
	private CaseClassification caseClassification;
	
	public RegionReferenceDto getCaseRegion() {
		return caseRegion;
	}
	
	public DistrictReferenceDto getCaseDistrict() {
		return caseDistrict;
	}
	
	public FacilityReferenceDto getLab() {
		return lab;
	}
	
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}
	
	public void caseRegionEquals(RegionReferenceDto caseRegion) {
		this.caseRegion = caseRegion;
	}
	
	public void caseDistrictEquals(DistrictReferenceDto caseDistrict) {
		this.caseDistrict = caseDistrict;
	}
	
	public void labEquals(FacilityReferenceDto lab) {
		this.lab = lab;
	}
	
	public SampleCriteria caseClassificationEquals(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
		return this;
	}
	
}
