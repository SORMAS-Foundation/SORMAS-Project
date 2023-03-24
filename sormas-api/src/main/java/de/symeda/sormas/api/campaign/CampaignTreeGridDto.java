package de.symeda.sormas.api.campaign;

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

public class CampaignTreeGridDto {
	
	
	private List<CampaignTreeGridDto> regionData = new ArrayList<>();
    private String name;
    private Long id;
    private String parentUuid;
    private String uuid;
    private Long isClicked; //let leave this to a Long type
    private String levelAssessed;

    public CampaignTreeGridDto(String name, Long id, String parentUuid, String uuid, String levelAssessed) {
        this.name = name;
        this.id = id;
        this.parentUuid = parentUuid;
        this.uuid = uuid;
        this.levelAssessed = levelAssessed;
    }
    
    public CampaignTreeGridDto(String name, Long id, String parentUuid, String uuid) {
        this.name = name;
        this.id = id;
        this.parentUuid = parentUuid;
        this.uuid = uuid;
        }

    public CampaignTreeGridDto() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
        return name;
    }

    public List<CampaignTreeGridDto> getRegionData() {
        return regionData;
    }

    public void setRegionData(List<CampaignTreeGridDto> regionData) {
        this.regionData = regionData;
    }	
    
    
    public void addRegionData(CampaignTreeGridDto regionData_sub) {
    	regionData.add(regionData_sub);
    }

    public Long getPopulationData() {
        return getRegionData().stream()
                .map(region -> region.getPopulationData())
                .reduce(0L, Long::sum);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getIsClicked() {
		return isClicked;
	}

	public void setIsClicked(Long isClicked) {
		this.isClicked = isClicked;
	}

	public String getLevelAssessed() {
		return levelAssessed;
	}

	public void setLevelAssessed(String levelAssessed) {
		this.levelAssessed = levelAssessed;
	}
	
	

    
}
