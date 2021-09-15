package de.symeda.sormas.api.caze.classification;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class ClassificationAllSymptomsCriteriaDto extends ClassificationCriteriaDto {

	private SymptomState symptomState;
	private FieldVisibilityCheckers fieldVisibilityCheckers;

	public ClassificationAllSymptomsCriteriaDto(SymptomState symptomState, Disease disease, String countryLocale) {
		this.symptomState = symptomState;
		fieldVisibilityCheckers = FieldVisibilityCheckers.withDisease(disease).andWithCountry(countryLocale);
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> pathogenTests, List<EventDto> events, Date lastVaccinationDate) {

		for (Field field : SymptomsDto.class.getDeclaredFields()) {

			SymptomsDto symptomsDto = caze.getSymptoms();
			if (field.getType() == SymptomState.class && fieldVisibilityCheckers.isVisible(SymptomsDto.class, field.getName())) {
				field.setAccessible(true);
				try {
					boolean matchedFieldState = field.get(symptomsDto) == symptomState;
					field.setAccessible(false);
					if (!matchedFieldState) {
						return false;
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<b> ").append(I18nProperties.getString(Strings.classificationSymptomsAllOf).toUpperCase()).append("</b>");
		stringBuilder.append(" ").append(I18nProperties.getString(Strings.setTo)).append(" ");

		if (symptomState == null) {
			stringBuilder.append("<b>").append(I18nProperties.getString(Strings.none).toUpperCase()).append("</b>").append("</br>");
		} else {
			stringBuilder.append("<b>").append(symptomState.toString().toUpperCase()).append("</b>").append("</br>");
		}

		for (Field field : SymptomsDto.class.getDeclaredFields()) {
			if (field.getType() == SymptomState.class && fieldVisibilityCheckers.isVisible(SymptomsDto.class, field.getName())) {
				stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, field.getName())).append("; ");
			}
		}
		return stringBuilder.toString();
	}
}
