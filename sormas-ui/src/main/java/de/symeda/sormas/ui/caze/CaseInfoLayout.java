package de.symeda.sormas.ui.caze;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

@SuppressWarnings("serial")
public class CaseInfoLayout extends VerticalLayout {

	private final CaseDataDto caseDto;

	public CaseInfoLayout(CaseDataDto caseDto) {
		this.caseDto = caseDto;
		this.setSpacing(true);
		updateCaseInfo();
	}

	private void updateCaseInfo() {
		this.removeAllComponents();

		PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDto.getPerson().getUuid());

		addDescLabel(this, DataHelper.getShortUuid(caseDto.getUuid()),
				I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID))
		.setDescription(caseDto.getUuid());
		addDescLabel(this, caseDto.getPerson(),
				I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON));

		HorizontalLayout ageSexLayout = new HorizontalLayout();
		ageSexLayout.setSpacing(true);
		addDescLabel(ageSexLayout, ApproximateAgeHelper.formatApproximateAge(
				personDto.getApproximateAge(),personDto.getApproximateAgeType()),
				I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
		addDescLabel(ageSexLayout, personDto.getSex(),
				I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
		this.addComponent(ageSexLayout);

		addDescLabel(this, caseDto.getDisease(),
				I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
		addDescLabel(this, caseDto.getCaseClassification(),
				I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
		addDescLabel(this, DateHelper.formatDMY(caseDto.getSymptoms().getOnsetDate()),
				I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
	}

	private static Label addDescLabel(AbstractLayout layout, Object content, String caption) {
		String contentString = content != null ? content.toString() : "";
		Label label = new Label(contentString);
		label.setCaption(caption);
		layout.addComponent(label);
		return label;
	}

}
