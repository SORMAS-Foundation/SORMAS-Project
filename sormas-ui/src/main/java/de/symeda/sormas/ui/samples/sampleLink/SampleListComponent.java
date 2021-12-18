/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples.sampleLink;

import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

@SuppressWarnings("serial")
public class SampleListComponent extends SideComponent {

	public SampleListComponent(
		SampleListCriteria sampleListCriteria,
		Consumer<Button.ClickEvent> clickListener,
		AbstractDetailView<? extends ReferenceDto> view) {
		super(I18nProperties.getString(Strings.entitySamples));

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE)) {
			Button createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.sampleNewSample));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> view.showNavigationConfirmPopupIfDirty(() -> clickListener.accept(e)));
			addCreateButton(createButton);
		}

		SampleList sampleList = new SampleList(sampleListCriteria);
		addComponent(sampleList);
		sampleList.reload();
	}
}
