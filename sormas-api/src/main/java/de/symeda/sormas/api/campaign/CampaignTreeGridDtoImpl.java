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

public class CampaignTreeGridDtoImpl extends CampaignTreeGridDto {
	
	private Long populationData;
	private String savedSelectionData;

	public CampaignTreeGridDtoImpl(String name, Long populationData, Long id, String parentUuid, String uuid, String levelAssessed, String savedSelectionData) {
        super(name, id, parentUuid, uuid, levelAssessed);
        this.populationData = populationData;
        this.savedSelectionData = savedSelectionData;
    }
	
	 @Override
     public Long getPopulationData() {
         return populationData;
     }
	 
	 @Override
     public String getSavedData() {
         return savedSelectionData;
     }
	 
}
