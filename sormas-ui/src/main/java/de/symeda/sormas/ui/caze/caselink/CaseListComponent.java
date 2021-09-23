package de.symeda.sormas.ui.caze.caselink;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class CaseListComponent extends SideComponent {

	public CaseListComponent(PersonReferenceDto personReferenceDto) {
		super(I18nProperties.getString(Strings.entityCases));

		CaseList caseList = new CaseList(personReferenceDto);
		addComponent(caseList);
		caseList.reload();
		if (!caseList.isEmpty()) {
			final Button seeCases = ButtonHelper.createButton(I18nProperties.getCaption(Captions.personLinkToCases));
			CssStyles.style(seeCases, ValoTheme.BUTTON_PRIMARY);
			CaseCriteria caseCriteria = new CaseCriteria().person(personReferenceDto);
			caseCriteria.setIncludeCasesFromOtherJurisdictions(true);
			seeCases.addClickListener(clickEvent -> ControllerProvider.getCaseController().navigateTo(caseCriteria));
			addComponent(seeCases);
			setComponentAlignment(seeCases, Alignment.MIDDLE_LEFT);
		}
	}
}
