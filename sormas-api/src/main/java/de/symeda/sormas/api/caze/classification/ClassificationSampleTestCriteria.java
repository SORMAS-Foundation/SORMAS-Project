package de.symeda.sormas.api.caze.classification;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;

public class ClassificationSampleTestCriteria extends ClassificationCaseCriteria {

	private static final long serialVersionUID = 856637988490366395L;

	public ClassificationSampleTestCriteria(String propertyId, Object... propertyValues) {
		super(propertyId, propertyValues);
	}

	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return SampleTestDto.class;
	}

	@Override
	public boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {		
		for (SampleTestDto sampleTest : sampleTests) {
			if (propertyId == SampleTestDto.FOUR_FOLD_INCREASE_ANTIBODY_TITER
					&& sampleTest.getTestType() != SampleTestType.IGM_SERUM_ANTIBODY) {
				continue;
			}
			
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
	public String buildDescription() {
		return I18nProperties.getPrefixFieldCaption(SampleTestDto.I18N_PREFIX, propertyId);
	}

}
