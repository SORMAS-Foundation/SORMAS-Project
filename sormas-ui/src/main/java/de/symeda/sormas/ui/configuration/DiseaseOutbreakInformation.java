package de.symeda.sormas.ui.configuration;

import java.util.HashSet;
import java.util.Set;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.login.LoginHelper;

public class DiseaseOutbreakInformation {
	
	private int totalDistricts;
	private RegionReferenceDto region;
	private Set<DistrictReferenceDto> affectedDistricts;
	
	public DiseaseOutbreakInformation(int totalDistricts, RegionReferenceDto region, Set<DistrictReferenceDto> affectedDistricts) {
		this.totalDistricts = totalDistricts;
		this.region = region;
		if (affectedDistricts != null) {
			this.affectedDistricts = affectedDistricts;
		} else {
			this.affectedDistricts = new HashSet<>();
		}
	}
	
	public int getTotalDistricts() {
		return totalDistricts;
	}
	
	public void setTotalDistricts(int totalDistricts) {
		this.totalDistricts = totalDistricts;
	}
	
	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public Set<DistrictReferenceDto> getAffectedDistricts() {
		return affectedDistricts;
	}
	
	public void setAffectedDistricts(Set<DistrictReferenceDto> affectedDistricts) {
		this.affectedDistricts = affectedDistricts;
	}
	
	@Override
	public String toString() {
		boolean styleAsButton = LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_ALL) || 
				(LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_RESTRICTED) && LoginHelper.getCurrentUser().getRegion().equals(region));
		boolean moreThanHalfOfDistricts = affectedDistricts.size( )>= totalDistricts / 2.0f;
		
		return affectedDistricts.isEmpty() ? styleAsButton ? "<div class=\"v-button v-button-primary v-button-neutral\">No</div>" : "No" : 
			styleAsButton ? "<div class=\"v-button v-button-primary " + (moreThanHalfOfDistricts ? "v-button-critical" : 
				"v-button-important") + "\">" + affectedDistricts.size() + "/" + totalDistricts + "</div>" : 
				"<div class=\"v-label " + (moreThanHalfOfDistricts ? "v-label-color-critical" : "v-label-color-important") 
				+ "\">" + affectedDistricts.size() + "/" + totalDistricts + "</div>";
	}

}