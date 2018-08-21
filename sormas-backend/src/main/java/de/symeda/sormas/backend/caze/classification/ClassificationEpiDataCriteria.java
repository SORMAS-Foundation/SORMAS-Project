package de.symeda.sormas.backend.caze.classification;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ClassificationEpiDataCriteria extends ClassificationCaseCriteria {

	public ClassificationEpiDataCriteria(String propertyId) {
		super(propertyId, YesNoUnknown.YES);
	}

	public ClassificationEpiDataCriteria(String propertyId, Object... propertyValues) {
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
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		stringBuilder.append(I18nProperties.getPrefixFieldCaption(EpiDataDto.I18N_PREFIX, propertyId));
		return stringBuilder;
	}
	
}