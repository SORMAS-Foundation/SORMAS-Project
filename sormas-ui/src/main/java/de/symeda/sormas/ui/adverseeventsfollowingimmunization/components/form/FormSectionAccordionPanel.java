/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class FormSectionAccordionPanel extends VerticalLayout {

	private final Button titleButton;
	private final VerticalLayout mainLayout;
	private final FormSectionAccordion formSectionAccordion;
	private boolean expanded;

	public FormSectionAccordionPanel(String titleButtonCaption, boolean expanded, FormSectionAccordion formSectionAccordion) {

		this.expanded = expanded;
		this.formSectionAccordion = formSectionAccordion;

		setMargin(false);
		setSpacing(false);
		setWidth(99, Unit.PERCENTAGE);
		addStyleNames(CssStyles.VIEW_SECTION_MARGIN_X_4, CssStyles.VSPACE_3);

		titleButton = ButtonHelper.createButton(titleButtonCaption, (event) -> {
			formSectionAccordion.toggleFormSection(FormSectionAccordionPanel.this);
		}, ValoTheme.BUTTON_LINK, CssStyles.FORM_SECTION_ACCORDION_PANEL_TITLE_BUTTON);
		addComponent(titleButton);

		mainLayout = new VerticalLayout();
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		//mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setVisible(expanded);
		mainLayout.addStyleNames(CssStyles.VSPACE_TOP_3);

		addComponent(mainLayout);
	}

	public void addComponentToMainLayout(Component component) {
		mainLayout.addComponent(component);
	}

	public Button getTitleButton() {
		return titleButton;
	}

	public VerticalLayout getMainLayout() {
		return mainLayout;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
}
