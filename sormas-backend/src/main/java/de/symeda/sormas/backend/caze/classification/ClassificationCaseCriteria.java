package de.symeda.sormas.backend.caze.classification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ClassificationCaseCriteria extends Criteria {

	protected final String propertyId;
	protected final List<Object> propertyValues;
	protected Method method;

	public ClassificationCaseCriteria(String propertyId, Object... propertyValues) {
		this.propertyId = propertyId;
		this.propertyValues = Arrays.asList(propertyValues);
	}

	protected Class<? extends EntityDto> getInvokeClass() {
		return CaseDataDto.class;
	}

	protected Object getInvokeObject(CaseDataDto caze) {
		return caze;
	}

	@Override
	boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		if (method == null) {
			try {
				method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		
		try {
			Object value = method.invoke(getInvokeObject(caze));
			return propertyValues.contains(value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected StringBuilder appendDescValues(StringBuilder stringBuilder) {
		if (propertyValues.size() == 1 && propertyValues.get(0) instanceof YesNoUnknown)
			return stringBuilder;

		stringBuilder.append(" ");
		for (int i=0; i<propertyValues.size(); i++) {
			if (i > 0) stringBuilder.append(", ");
			stringBuilder.append(propertyValues.get(i).toString());
		}
		return stringBuilder;
	}

	@Override
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		stringBuilder.append(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, propertyId));
		appendDescValues(stringBuilder);
		return stringBuilder;
	}
	
}