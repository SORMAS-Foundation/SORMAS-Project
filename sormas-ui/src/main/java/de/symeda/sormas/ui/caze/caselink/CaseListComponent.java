package de.symeda.sormas.ui.caze.caselink;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseListComponent extends VerticalLayout {

	public CaseListComponent(PersonReferenceDto personReferenceDto) {
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Sizeable.Unit.PERCENTAGE);
		addComponent(componentHeader);

		Label eventLabel = new Label(I18nProperties.getString(Strings.entityCases));
		eventLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(eventLabel);

		CaseCriteria caseCriteria = new CaseCriteria().person(personReferenceDto);
		caseCriteria.setIncludeCasesFromOtherJurisdictions(true);
		CaseList caseList = new CaseList(personReferenceDto);
		addComponent(caseList);
		caseList.reload();

		if (!caseList.isEmpty()) {
			final Button seeCases = ButtonHelper.createButton(I18nProperties.getCaption(Captions.personLinkToCases));
			CssStyles.style(seeCases, ValoTheme.BUTTON_PRIMARY);
			seeCases.addClickListener(clickEvent -> ControllerProvider.getCaseController().navigateTo(caseCriteria));
			addComponent(seeCases);
			setComponentAlignment(seeCases, Alignment.MIDDLE_LEFT);
		}
	}
}
