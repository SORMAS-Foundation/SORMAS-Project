package de.symeda.sormas.api.infrastructure.area;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class AreaDto extends EntityDto {

	public static final String I18N_PREFIX = "Area";
	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";
	public static final String POPULATION_DATA = "populationData";
	public static final String UUID = "uuid";
	

	private static final long serialVersionUID = -6241927331721175673L;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String name;
	//@NotNull(message = "Please enter valid externalID")
	private Long externalId;
	private boolean archived;
	private Long populationData;
	private Long areaid;
	private String uuid_;
	
//	@Autowired
//	public RegionDto regionDto;
	private List<RegionDto> regionData = new ArrayList<>();

	public static AreaDto build() {
		AreaDto area = new AreaDto();
		area.setUuid(DataHelper.createUuid());
		return area;
	}
	
	public AreaDto() {
		
	};

	public AreaDto(@Size(max = 512, message = "textTooLong") String name, Long populationData, Long areaid, String uuid_) {
		this.name = name;
		this.populationData = populationData;
		this.areaid = areaid;
		this.uuid_ = uuid_;
	}



	public AreaReferenceDto toReference() {
		return new AreaReferenceDto(getUuid());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public Long getPopulationData() {
		return populationData;
	}

	public void setPopulationData(Long populationData) {
		this.populationData = populationData;
	}


	public Long getAreaid() {
		return areaid;
	}

	public void setAreaid(Long areaid) {
		this.areaid = areaid;
	}

	public List<RegionDto> getRegionData() {
		List<RegionDto> regions_ = FacadeProvider.getRegionFacade().getAllActiveAsReferenceAndPopulation(getAreaid());
		regionData.addAll(regions_);
		return regionData;
	}

	public void setRegionData(List<RegionDto> regionData) {
		this.regionData = regionData;
	}

	public String getUuid_() {
		return uuid_;
	}

	public void setUuid_(String uuid_) {
		this.uuid_ = uuid_;
	}
	
	
	
	
}
