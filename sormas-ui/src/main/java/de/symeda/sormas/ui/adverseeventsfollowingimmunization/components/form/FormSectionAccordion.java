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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class FormSectionAccordion extends VerticalLayout {

	private final List<FormSectionAccordionPanel> formSectionAccordionPanelList = new ArrayList<>();

	public FormSectionAccordion() {
		setMargin(false);
		setSpacing(false);
		setWidth(100, Unit.PERCENTAGE);
	}

	public void addFormSectionPanel(String sectionTitleCaption, boolean expanded, Component component) {

		FormSectionAccordionPanel formSectionAccordionPanel = new FormSectionAccordionPanel(sectionTitleCaption, expanded, FormSectionAccordion.this);
		formSectionAccordionPanel.addComponentToMainLayout(component);

		formSectionAccordionPanelList.add(formSectionAccordionPanel);

		addComponent(formSectionAccordionPanel);
	}

	public void toggleFormSection(FormSectionAccordionPanel formSectionAccordionPanel) {
		formSectionAccordionPanel.getMainLayout().setVisible(!formSectionAccordionPanel.isExpanded());
		formSectionAccordionPanel.setExpanded(!formSectionAccordionPanel.isExpanded());
	}

	public void toggleAllFormSections(boolean expanded) {
		for (FormSectionAccordionPanel formSectionAccordionPanel : formSectionAccordionPanelList) {
			formSectionAccordionPanel.getMainLayout().setVisible(!expanded);
			formSectionAccordionPanel.setExpanded(!expanded);
		}
	}
}
