package de.symeda.sormas.api.caze.classification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Classification criteria that is applicable when the given property, which needs to be date, is within
 * the range specified by the case start date and a number of days before this case start date. The case
 * start date is either the symptom onset date, reception date or case report date, depending on which
 * of this date types is available. The number of days before the case start date will usually be the
 * incubation period of the respective disease.
 */
public class ClassificationNotInStartDateRangeCriteria extends ClassificationCaseCriteria {

	private static final long serialVersionUID = -8817472226784147694L;
	
	private final int daysBeforeStartDate;
	
	public ClassificationNotInStartDateRangeCriteria(String propertyId, int daysBeforeStartDate) {
		super(propertyId);
		this.daysBeforeStartDate = daysBeforeStartDate;
	}
	
	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests) {
		try {
			Method method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
			Object value = method.invoke(getInvokeObject(caze));
			if (value instanceof Date) {
				Date startDate = CaseLogic.getStartDate(caze.getSymptoms().getOnsetDate(), caze.getReceptionDate(), caze.getReportDate());
				Date lowerThresholdDate = DateHelper.subtractDays(startDate, daysBeforeStartDate);
				
				return !(((Date) value).equals(lowerThresholdDate) 
						|| ((Date) value).equals(startDate)
						|| (((Date) value).after(lowerThresholdDate) 
								&& ((Date) value).before(startDate)));
			} else {
				return true;
			}
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, propertyId));
		stringBuilder.append(" ").append(I18nProperties.getText("notWithin")).append(" ").append(daysBeforeStartDate).append(" ").append(I18nProperties.getText("daysBeforeCaseStart"));
		return stringBuilder.toString();
	}
	
}
