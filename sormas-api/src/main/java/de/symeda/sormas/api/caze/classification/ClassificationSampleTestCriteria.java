package de.symeda.sormas.api.caze.classification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestType;

public class ClassificationSampleTestCriteria extends ClassificationCaseCriteria {

	private static final long serialVersionUID = 856637988490366395L;

	private final List<SampleTestType> testTypes;

	public ClassificationSampleTestCriteria(String propertyId, List<SampleTestType> testTypes, Object... propertyValues) {
		super(propertyId, propertyValues);
		this.testTypes = testTypes;
	}

	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return SampleTestDto.class;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests) {		
		for (SampleTestDto sampleTest : sampleTests) {
			if (!testTypes.contains(sampleTest.getTestType())) {
				continue;
			}

			Method method = null;
			try {
				method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
			} catch (NoSuchMethodException e) {
				try {
					method = getInvokeClass().getMethod("is" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
				} catch (NoSuchMethodException newE) {
					throw new RuntimeException(newE);
				}
			} catch (SecurityException e) {
				throw new RuntimeException(e);
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
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getPrefixFieldCaption(SampleTestDto.I18N_PREFIX, propertyId));
		if (testTypes != null && !testTypes.isEmpty()) {
			stringBuilder.append(" (for one of the following test types: ");
			for (int i = 0; i < testTypes.size(); i++) {
				if (i == testTypes.size() - 1) {
					stringBuilder.append(" <b>OR</b> ");
				} else if (i > 0) {
					stringBuilder.append(", ");
				}
				stringBuilder.append(testTypes.get(i).toString());
			}
			stringBuilder.append(")");
		}

		return stringBuilder.toString();
	}

}
