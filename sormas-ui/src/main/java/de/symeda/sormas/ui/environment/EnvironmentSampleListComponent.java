/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.environment;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.PaginationList;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class EnvironmentSampleListComponent extends SideComponent {

	private static final long serialVersionUID = -4946315430355463915L;

	private final EnvironmentSampleList eventList;

	public EnvironmentSampleListComponent(EnvironmentDto environment, boolean isEditAllowed, Consumer<Runnable> actionCallback) {
		super(I18nProperties.getString(Strings.entityEnvironmentSamples), actionCallback);

		if (isEditAllowed && UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_SAMPLE_CREATE)) {
			addCreateButton(
				I18nProperties.getCaption(Captions.environmentSampleNewSample),
				() -> ControllerProvider.getEnvironmentSampleController().create(environment, this::reload),
				UserRight.ENVIRONMENT_SAMPLE_CREATE);
		}

		eventList = new EnvironmentSampleList(environment.toReference(), isEditAllowed);
		addComponent(eventList);
		eventList.reload();
	}

	private void reload() {
		eventList.reload();
	}

	class EnvironmentSampleList extends PaginationList<EnvironmentSampleIndexDto> {

		private static final long serialVersionUID = 8811945277567228049L;
		private static final int MAX_DISPLAYED_ENTRIES = 5;

		private final EnvironmentReferenceDto environmentRef;

		private final boolean isEditAllowed;

		public EnvironmentSampleList(EnvironmentReferenceDto environmentRef, boolean isEditAllowed) {
			super(MAX_DISPLAYED_ENTRIES);
			this.environmentRef = environmentRef;
			this.isEditAllowed = isEditAllowed;
		}

		public void reload() {
			List<EnvironmentSampleIndexDto> samples = FacadeProvider.getEnvironmentSampleFacade()
				.getIndexList(new EnvironmentSampleCriteria().withEnvironment(environmentRef), null, null, null);

			setEntries(samples);
			if (!samples.isEmpty()) {
				showPage(1);
			} else {
				listLayout.removeAllComponents();
				updatePaginationLayout();
				Label noSamplesLabel = new Label(I18nProperties.getString(Strings.infoNoEnvironmentSamples));
				listLayout.addComponent(noSamplesLabel);
			}

		}

		@Override
		protected void drawDisplayedEntries() {
			for (EnvironmentSampleIndexDto sample : getDisplayedEntries()) {
				EnvironmentSampleListEntry listEntry = new EnvironmentSampleListEntry(sample);

				String sampleUuid = sample.getUuid();
				if (UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EDIT) && isEditAllowed) {
					listEntry.addEditButton(
						"edit-environment-sample-" + sampleUuid,
						(Button.ClickListener) event -> ControllerProvider.getEnvironmentSampleController().navigateToSample(sampleUuid));
				} else {
					listEntry.addViewButton(
						"view-environment-sample" + sampleUuid,
						(Button.ClickListener) event -> ControllerProvider.getEnvironmentSampleController().navigateToSample(sampleUuid));
				}

				listEntry.setEnabled(isEditAllowed);

				listLayout.addComponent(listEntry);
			}
		}
	}

	class EnvironmentSampleListEntry extends SideComponentField {

		private static final long serialVersionUID = -7096062233938610947L;

		public EnvironmentSampleListEntry(EnvironmentSampleIndexDto sampleIndex) {
			HorizontalLayout topLayout = new HorizontalLayout();
			topLayout.setWidth(100, Unit.PERCENTAGE);
			topLayout.setMargin(false);
			topLayout.setSpacing(false);
			addComponentToField(topLayout);

			VerticalLayout topLeftLayout = new VerticalLayout();

			topLeftLayout.setMargin(false);
			topLeftLayout.setSpacing(false);

			Label materialLabel = new Label(DataHelper.toStringNullable(sampleIndex.getSampleMaterial()));
			CssStyles.style(materialLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			materialLabel.setWidth(50, Unit.PERCENTAGE);
			topLeftLayout.addComponent(materialLabel);

			if (sampleIndex.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
				Label resultLabel = new Label();
				CssStyles.style(resultLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);

				resultLabel.setValue(DataHelper.toStringNullable(sampleIndex.getSpecimenCondition()));
				resultLabel.addStyleName(CssStyles.LABEL_WARNING);

				topLeftLayout.addComponent(resultLabel);
			}

			Label dispatchRecieveLabel = new Label();
			CssStyles.style(dispatchRecieveLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);

			if (sampleIndex.isReceived()) {
				dispatchRecieveLabel.setValue(
					I18nProperties.getCaption(Captions.environmentSampleReceived) + " " + DateFormatHelper.formatDate(sampleIndex.getReceivalDate()));
			} else if (sampleIndex.isDispatched()) {
				dispatchRecieveLabel.setValue(
					I18nProperties.getCaption(Captions.environmentSampleShipped) + " "
						+ DateFormatHelper.formatDate((sampleIndex.getDispatchDate())));
			} else {
				dispatchRecieveLabel.setValue(I18nProperties.getCaption(Captions.environmentSampleNotShipped));
			}

			topLeftLayout.addComponent(dispatchRecieveLabel);

			Label dateTimeLabel = new Label(
				I18nProperties.getPrefixCaption(EnvironmentSampleIndexDto.I18N_PREFIX, EnvironmentSampleIndexDto.SAMPLE_DATE_TIME) + ": "
					+ DateFormatHelper.formatDate(sampleIndex.getSampleDateTime()));
			topLeftLayout.addComponent(dateTimeLabel);

			Label labLabel = new Label(DataHelper.toStringNullable(sampleIndex.getLaboratory()));
			topLeftLayout.addComponent(labLabel);

			Label testCountLabel = new Label(
				I18nProperties.getPrefixCaption(EnvironmentSampleIndexDto.I18N_PREFIX, EnvironmentSampleIndexDto.NUMBER_OF_TESTS) + ": "
					+ sampleIndex.getNumberOfTests());
			topLeftLayout.addComponent(testCountLabel);

			if (sampleIndex.getNumberOfTests() > 0) {
				VerticalLayout latestTestLayout = new VerticalLayout();
				latestTestLayout.setMargin(false);
				latestTestLayout.setSpacing(false);

				Label heading = new Label(I18nProperties.getCaption(Captions.latestPathogenTest));
				CssStyles.style(heading, CssStyles.LABEL_BOLD);

				Label testDate = new Label(sampleIndex.getLatestPathogenTest());
				latestTestLayout.addComponents(heading, testDate);
				topLeftLayout.addComponent(latestTestLayout);
			}

			topLayout.addComponent(topLeftLayout);
			topLayout.setComponentAlignment(topLeftLayout, Alignment.TOP_LEFT);
		}
	}
}
