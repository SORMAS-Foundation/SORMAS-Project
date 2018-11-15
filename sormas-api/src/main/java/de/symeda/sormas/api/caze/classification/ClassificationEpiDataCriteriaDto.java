package de.symeda.sormas.api.caze.classification;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ClassificationEpiDataCriteriaDto extends ClassificationCaseCriteriaDto {

	private static final long serialVersionUID = -1805242010549597591L;

	public ClassificationEpiDataCriteriaDto() {
		super();
	}
	
	public ClassificationEpiDataCriteriaDto(String propertyId) {
		super(propertyId, YesNoUnknown.YES);
	}

	public ClassificationEpiDataCriteriaDto(String propertyId, Object... propertyValues) {
		super(propertyId, propertyValues);
	}
	
	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return EpiDataDto.class;
	}

	@Override
	protected Object getInvokeObject(CaseDataDto caze) {
		return caze.getEpiData();
	}

	@Override
	public String buildDescription() {
		return I18nProperties.getPrefixFieldCaption(EpiDataDto.I18N_PREFIX, propertyId);
	}
	
}