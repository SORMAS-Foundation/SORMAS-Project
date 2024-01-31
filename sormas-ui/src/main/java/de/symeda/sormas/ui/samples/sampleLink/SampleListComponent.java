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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class SampleListComponent extends SideComponent {

	public SampleListComponent(
		SampleCriteria sampleCriteria,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed,
		SampleAssociationType sampleAssociationType) {
		super(I18nProperties.getString(Strings.entitySamples), actionCallback);

		SampleList sampleList = new SampleList(sampleCriteria, isEditAllowed);

		if (isEditAllowed && sampleCriteria.getSampleAssociationType() != SampleAssociationType.PERSON) {
			addCreateButton(I18nProperties.getCaption(Captions.sampleNewSample), () -> {
				switch (sampleCriteria.getSampleAssociationType()) {
				case CASE:
					ControllerProvider.getSampleController().create(sampleCriteria.getCaze(), sampleCriteria.getDisease(), SormasUI::refreshView);
					break;
				case CONTACT:
					ControllerProvider.getSampleController().create(sampleCriteria.getContact(), sampleCriteria.getDisease(), SormasUI::refreshView);
					break;
				case EVENT_PARTICIPANT:
					ControllerProvider.getSampleController()
						.create(sampleCriteria.getEventParticipant(), sampleCriteria.getDisease(), SormasUI::refreshView);
					break;
				default:
					throw new IllegalArgumentException("Invalid sample association type:" + sampleCriteria.getSampleAssociationType());
				}
			}, UserRight.SAMPLE_CREATE);
		}
		addComponent(sampleList);
		sampleList.reload();
		if (!sampleList.isEmpty()) {

			String buttonCaption = null;
			switch (sampleAssociationType) {
			case CASE:
				buttonCaption = I18nProperties.getCaption(Captions.caseLinkToSamples);
				break;
			case CONTACT:
				buttonCaption = I18nProperties.getCaption(Captions.contactLinkToSamples);
				break;
			case EVENT_PARTICIPANT:
				buttonCaption = I18nProperties.getCaption(Captions.eventParticipantLinkToSamples);
				break;
			default:
				buttonCaption = I18nProperties.getCaption(Captions.personLinkToSamples);
			}
			final Button seeSamples = ButtonHelper.createButton(buttonCaption);

			CssStyles.style(seeSamples, ValoTheme.BUTTON_PRIMARY);
			seeSamples.addClickListener(clickEvent -> ControllerProvider.getSampleController().navigateTo(sampleCriteria));
			addComponent(seeSamples);
			setComponentAlignment(seeSamples, Alignment.MIDDLE_LEFT);
		}
	}
}
