package de.symeda.sormas.api.region;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;

public class CommunityDto extends EntityDto {

	private static final long serialVersionUID = -8833267932522978860L;

	public static final String I18N_PREFIX = "Community";

	public static final String NAME = "name";
	public static final String DISTRICT = "district";
	
	private String name;
	private DistrictReferenceDto district;
	
	public CommunityDto(Date creationDate, Date changeDate, String uuid, String name, String districtUuid, String districtName) {
		super(creationDate, changeDate, uuid);
		this.name = name;
		this.district = new DistrictReferenceDto(districtUuid, districtName);
	}
	
	public CommunityDto() {
		super();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	
	public CommunityReferenceDto toReference() {
		return new CommunityReferenceDto(getUuid());
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public static CommunityDto build() {
		CommunityDto dto = new CommunityDto();
		return dto;
	}
	
}
