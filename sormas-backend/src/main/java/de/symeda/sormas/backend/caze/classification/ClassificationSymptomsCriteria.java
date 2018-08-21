package de.symeda.sormas.backend.caze.classification;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;

public class ClassificationSymptomsCriteria extends ClassificationCaseCriteria {

	public ClassificationSymptomsCriteria(String propertyId) {
		super(propertyId, SymptomState.YES);
	}

	public ClassificationSymptomsCriteria(String propertyId, Object... propertyValues) {
		super(propertyId, propertyValues);
	}

	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return SymptomsDto.class;
	}

	@Override
	protected Object getInvokeObject(CaseDataDto caze) {
		return caze.getSymptoms();
	}

	@Override
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		stringBuilder.append(I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, propertyId));
		if (!(propertyValues.get(0) instanceof SymptomState)) {
			appendDescValues(stringBuilder);
		}
		return stringBuilder;
	}
	
}