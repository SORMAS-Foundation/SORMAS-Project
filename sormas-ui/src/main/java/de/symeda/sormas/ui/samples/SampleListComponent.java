/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SampleListComponent extends VerticalLayout {

	private SampleList list;
	private Button createButton;

	public SampleListComponent(ContactReferenceDto contactRef) {
		createSampleListComponent(new SampleList(contactRef),
				e -> ControllerProvider.getSampleController().create(contactRef,
				() -> SormasUI.refreshView()));
	}

	public SampleListComponent(CaseReferenceDto caseRef) {
		createSampleListComponent(new SampleList(caseRef),
				e -> ControllerProvider.getSampleController().create(caseRef,
				() -> SormasUI.refreshView()));
	}

	private void createSampleListComponent(SampleList sampleList, Button.ClickListener clickListener) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		list = sampleList;
		addComponent(list);
		list.reload();

		Label tasksHeader = new Label(I18nProperties.getString(Strings.entitySamples));
		tasksHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(tasksHeader);

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE)) {
			createButton = new Button(I18nProperties.getCaption(Captions.sampleNewSample));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(clickListener);
			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}
	}

	public void reload() {
		list.reload();
	}
}
