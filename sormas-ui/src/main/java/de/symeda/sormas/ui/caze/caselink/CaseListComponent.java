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
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseListComponent extends VerticalLayout {

	private CaseList list;

	public CaseListComponent(PersonReferenceDto personReferenceDto) {
		createCaseListComponent(
			new CaseList(personReferenceDto),
			I18nProperties.getString(Strings.entityCases),
			clickEvent -> ControllerProvider.getCaseController().navigateTo(new CaseCriteria().person(personReferenceDto)));
	}

	private void createCaseListComponent(CaseList caseList, String heading, Button.ClickListener clickListener) {
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Sizeable.Unit.PERCENTAGE);
		addComponent(componentHeader);

		Label eventLabel = new Label(heading);
		eventLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(eventLabel);

		list = caseList;
		addComponent(list);
		list.reload();

		if (!list.isEmpty()) {
			final Button seeCases = new Button(I18nProperties.getCaption(Captions.personLinkToCases));
			CssStyles.style(seeCases, ValoTheme.BUTTON_PRIMARY);
			seeCases.addClickListener(clickListener);
			addComponent(seeCases);
			setComponentAlignment(seeCases, Alignment.MIDDLE_LEFT);
		}
	}
}
