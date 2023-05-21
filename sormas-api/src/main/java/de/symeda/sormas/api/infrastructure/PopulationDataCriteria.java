package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class PopulationDataCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = -876047405287325714L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private CampaignReferenceDto campaign;
	private boolean districtIsNull;
	private boolean communityIsNull;
	private boolean campaignIsNull;
	private Sex sex;
	private boolean sexIsNull;
	private AgeGroup ageGroup;
	private boolean ageGroupIsNull;

	public PopulationDataCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public PopulationDataCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public PopulationDataCriteria community(CommunityReferenceDto community) {
		this.community = community;
		return this;
	}
	
	public PopulationDataCriteria campaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
		return this;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public PopulationDataCriteria sex(Sex sex) {
		this.sex = sex;
		return this;
	}

	public Sex getSex() {
		return sex;
	}

	public PopulationDataCriteria ageGroup(AgeGroup ageGroup) {
		this.ageGroup = ageGroup;
		return this;
	}

	public AgeGroup getAgeGroup() {
		return ageGroup;
	}

	public boolean isDistrictIsNull() {
		return districtIsNull;
	}

	public PopulationDataCriteria districtIsNull(boolean districtIsNull) {
		this.districtIsNull = districtIsNull;
		return this;
	}
	public boolean isCommunityIsNull() {
		return communityIsNull;
	}

	public PopulationDataCriteria communityIsNull(boolean communityIsNull) {
		this.communityIsNull = communityIsNull;
		return this;
	}

	public boolean isSexIsNull() {
		return sexIsNull;
	}

	public PopulationDataCriteria sexIsNull(boolean sexIsNull) {
		this.sexIsNull = sexIsNull;
		return this;
	}

	public boolean isAgeGroupIsNull() {
		return ageGroupIsNull;
	}

	public PopulationDataCriteria ageGroupIsNull(boolean ageGroupIsNull) {
		this.ageGroupIsNull = ageGroupIsNull;
		return this;
	}

	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public boolean isCampaignIsNull() {
		return campaignIsNull;
	}
	

	public PopulationDataCriteria campaignIsNull(boolean campaignIsNull) {
		this.campaignIsNull = campaignIsNull;
		return this;
	}
	

	public void setCampaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
	}	
	
	
	
}
