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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.samples.environmentsample.EnvironmentSampleGridComponent;
import de.symeda.sormas.ui.samples.humansample.HumanSampleGrid;
import de.symeda.sormas.ui.samples.humansample.HumanSampleGridComponent;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class SamplesView extends AbstractView implements HasName {

	public static final String VIEW_NAME = "samples";

	private final SampleGridComponent sampleListComponent;
	private SamplesViewConfiguration viewConfiguration;
	private Button btnEnterBulkEditMode;

	public SamplesView() {
		super(VIEW_NAME);

		boolean isHumanSamplePermitted = UiUtil.permitted(FeatureType.SAMPLES_LAB, UserRight.SAMPLE_VIEW);
		boolean isEnvironmentSamplePerimtted =
			isHumanSamplePermitted && UiUtil.permitted(FeatureType.ENVIRONMENT_MANAGEMENT, UserRight.ENVIRONMENT_SAMPLE_VIEW);

		viewConfiguration = ViewModelProviders.of(getClass()).get(SamplesViewConfiguration.class);
		if (viewConfiguration.getViewType() == null) {
			viewConfiguration.setViewType(isHumanSamplePermitted ? SampleViewType.HUMAN : SampleViewType.ENVIRONMENT);
		}

		if (isEnvironmentSamplePerimtted && isHumanSamplePermitted) {
			addViewSwitch();
		}

		sampleListComponent =
			isHumanSampleView() ? new HumanSampleGridComponent(getViewTitleLabel(), this) : new EnvironmentSampleGridComponent(this);
		setSizeFull();
		addComponent(sampleListComponent);

		if (isHumanSampleView() && UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EXPORT)) {
			addHumanSampleExportButton();
		}

		if (isEnvironmentSampleView() && UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EXPORT)) {
			Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			addHeaderComponent(exportButton);

			StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
				sampleListComponent.getGrid(),
				this::getSelectedEnvironmentSamples,
				ExportEntityName.ENVIRONMENT_SAMPLES);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
		}

		if ((isHumanSampleView() || isEnvironmentSampleView()) && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			addBulkModeButtons();
		}
	}

	private void addViewSwitch() {
		OptionGroup viewViewSwitch = new OptionGroup();
		viewViewSwitch.setId("viewViewSwitch");
		CssStyles.style(
			viewViewSwitch,
			CssStyles.FORCE_CAPTION,
			ValoTheme.OPTIONGROUP_HORIZONTAL,
			CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY,
			CssStyles.VSPACE_TOP_3);
		viewViewSwitch.addItem(SampleViewType.HUMAN);
		viewViewSwitch.setItemCaption(SampleViewType.HUMAN, I18nProperties.getCaption(Captions.humanSampleViewType));

		viewViewSwitch.addItem(SampleViewType.ENVIRONMENT);
		viewViewSwitch.setItemCaption(SampleViewType.ENVIRONMENT, I18nProperties.getCaption(Captions.environmentSampleViewType));

		viewViewSwitch.setValue(viewConfiguration.getViewType());
		viewViewSwitch.addValueChangeListener(e -> {
			SampleViewType viewType = (SampleViewType) e.getProperty().getValue();

			viewConfiguration.setViewType(viewType);
			SormasUI.get().getNavigator().navigateTo(SamplesView.VIEW_NAME);
		});
		addHeaderComponent(viewViewSwitch);
	}

	private boolean isHumanSampleView() {
		return viewConfiguration.getViewType() == SampleViewType.HUMAN;
	}

	private boolean isEnvironmentSampleView() {
		return viewConfiguration.getViewType() == SampleViewType.ENVIRONMENT;
	}

	private void addHumanSampleExportButton() {
		VerticalLayout exportLayout = new VerticalLayout();
		exportLayout.setSpacing(true);
		exportLayout.setMargin(true);
		exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		exportLayout.setWidth(200, Unit.PIXELS);

		PopupButton exportButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
		addHeaderComponent(exportButton);

		Button basicExportButton = ButtonHelper.createIconButton(Captions.exportBasic, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
		basicExportButton.setDescription(I18nProperties.getString(Strings.infoBasicExport));
		basicExportButton.setWidth(100, Unit.PERCENTAGE);

		exportLayout.addComponent(basicExportButton);

		HumanSampleGrid grid = ((HumanSampleGridComponent) sampleListComponent).getGrid();
		StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
			grid,
			() -> viewConfiguration.isInEagerMode() ? grid.asMultiSelect().getSelectedItems() : Collections.emptySet(),
			ExportEntityName.SAMPLES,
			HumanSampleGrid.ACTION_BTN_ID);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(basicExportButton);

		StreamResource extendedExportStreamResource =
			DownloadUtil.createCsvExportStreamResource(SampleExportDto.class, null, (Integer start, Integer max) -> {
				Set<String> selectedRowUuids = viewConfiguration.isInEagerMode()
					? grid.asMultiSelect().getSelectedItems().stream().map(SampleIndexDto::getUuid).collect(Collectors.toSet())
					: Collections.emptySet();
				return FacadeProvider.getSampleFacade().getExportList(grid.getCriteria(), selectedRowUuids, start, max);
			}, (propertyId, type) -> {
				String caption = I18nProperties.getPrefixCaption(
					SampleExportDto.I18N_PREFIX,
					propertyId,
					I18nProperties.getPrefixCaption(
						SampleDto.I18N_PREFIX,
						propertyId,
						I18nProperties.getPrefixCaption(
							CaseDataDto.I18N_PREFIX,
							propertyId,
							I18nProperties.getPrefixCaption(
								ContactDto.I18N_PREFIX,
								propertyId,
								I18nProperties.getPrefixCaption(
									PersonDto.I18N_PREFIX,
									propertyId,
									I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, propertyId))))));
				if (Date.class.isAssignableFrom(type)) {
					caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
				}
				return caption;
			}, ExportEntityName.SAMPLES, null);

		addExportButton(
			extendedExportStreamResource,
			exportButton,
			exportLayout,
			VaadinIcons.FILE_TEXT,
			Captions.exportDetailed,
			Strings.infoDetailedExport);
	}

	private void addBulkModeButtons() {
		btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
		btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());

		addHeaderComponent(btnEnterBulkEditMode);

		Button btnLeaveBulkEditMode =
			ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
		btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());

		addHeaderComponent(btnLeaveBulkEditMode);

		btnEnterBulkEditMode.addClickListener(e -> {
			sampleListComponent.getBulkOperationsDropdown().setVisible(true);
			ViewModelProviders.of(SamplesView.class).get(SamplesViewConfiguration.class).setInEagerMode(true);
			btnEnterBulkEditMode.setVisible(false);
			btnLeaveBulkEditMode.setVisible(true);
			sampleListComponent.getGrid().reload();
		});
		btnLeaveBulkEditMode.addClickListener(e -> {
			sampleListComponent.getBulkOperationsDropdown().setVisible(false);
			ViewModelProviders.of(SamplesView.class).get(SamplesViewConfiguration.class).setInEagerMode(false);
			btnLeaveBulkEditMode.setVisible(false);
			btnEnterBulkEditMode.setVisible(true);
			navigateTo(sampleListComponent.getCriteria());
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		sampleListComponent.reload(event);
	}

	public ViewConfiguration getViewConfiguration() {
		return viewConfiguration;
	}

	private Set<EnvironmentSampleIndexDto> getSelectedEnvironmentSamples() {
		return this.viewConfiguration.isInEagerMode()
			? ((EnvironmentSampleGridComponent) sampleListComponent).getGrid().asMultiSelect().getSelectedItems()
			: Collections.emptySet();
	}

	@Override
	public String getName() {
		return VIEW_NAME;
	}
}
