package de.symeda.sormas.api.docgeneneration;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

import java.util.Date;

public class TemplateDto extends EntityDto {

    public static final String I18N_PREFIX = "Template";
    public static final String NAME = "name";

    private String name;

    public TemplateDto(
            Date creationDate,
            Date changeDate,
            String uuid,
            String name)
    {
        super(creationDate, changeDate, uuid);
        this.name = name;
    }

    public TemplateDto() {
        super();
    }

    public TemplateDto(String name){
        super();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TemplateDto build() {
        TemplateDto dto = new TemplateDto();
        dto.setUuid(DataHelper.createUuid());
        return dto;
    }
}
