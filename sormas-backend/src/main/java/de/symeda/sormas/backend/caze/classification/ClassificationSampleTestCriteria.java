package de.symeda.sormas.backend.caze.classification;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class ClassificationSampleTestCriteria extends ClassificationCaseCriteria {

	public ClassificationSampleTestCriteria(String propertyId, Object... propertyValues) {
		super(propertyId, propertyValues);
	}

	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return SampleTestDto.class;
	}

	@Override
	boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		for (SampleTestDto sampleTest : sampleTests) {
			if (method == null) {
				try {
					method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
				} catch (NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}

			try {
				Object value = method.invoke(sampleTest);
				if (propertyValues.contains(value)) {
					return true;
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		
		return false;
	}

	@Override
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		stringBuilder.append(I18nProperties.getPrefixFieldCaption(SampleTestDto.I18N_PREFIX, propertyId));
		return stringBuilder;
	}

}
