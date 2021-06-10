package de.symeda.sormas.api.caze.classification;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;

public class ClassificationAllSymptomsCriteriaDto extends ClassificationCriteriaDto {

	private SymptomState symptomState;
	private Disease disease;

	public ClassificationAllSymptomsCriteriaDto(SymptomState symptomState, Disease disease) {
		this.symptomState = symptomState;
		this.disease = disease;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> pathogenTests, List<EventDto> events) {

		Disease caseDisease = caze.getDisease();

		for (Field field : SymptomsDto.class.getDeclaredFields()) {
			Diseases annotation = field.getAnnotation(Diseases.class);
			HideForCountries hideForCountriesAnnotation = field.getAnnotation(HideForCountries.class);
			HideForCountriesExcept hideForCountriesExceptAnnotation = field.getAnnotation(HideForCountriesExcept.class);

			SymptomsDto symptomsDto = caze.getSymptoms();
			if (field.getType() == SymptomState.class
				&& annotation != null
				&& ArrayUtils.contains(annotation.value(), caseDisease)
				&& (hideForCountriesAnnotation == null || !ArrayUtils.contains(hideForCountriesAnnotation.countries(), getCountryLocale()))
				&& (hideForCountriesExceptAnnotation == null
					|| ArrayUtils.contains(hideForCountriesExceptAnnotation.countries(), getCountryLocale()))) {

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
			Diseases annotation = field.getAnnotation(Diseases.class);
			HideForCountries hideForCountriesAnnotation = field.getAnnotation(HideForCountries.class);
			HideForCountriesExcept hideForCountriesExceptAnnotation = field.getAnnotation(HideForCountriesExcept.class);

			if (field.getType() == SymptomState.class
				&& annotation != null
				&& ArrayUtils.contains(annotation.value(), disease)
				&& (hideForCountriesAnnotation == null || !ArrayUtils.contains(hideForCountriesAnnotation.countries(), getCountryLocale()))
				&& (hideForCountriesExceptAnnotation == null
					|| ArrayUtils.contains(hideForCountriesExceptAnnotation.countries(), getCountryLocale()))) {

				stringBuilder.append(field.getName()).append("; ");
			}
		}
		return stringBuilder.toString();
	}

	private String getCountryLocale() {
		return FacadeProvider.getConfigFacade().getCountryLocale();
	}
}
