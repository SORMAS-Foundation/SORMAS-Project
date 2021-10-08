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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.labmessage.LabMessagesView;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class SamplesView extends AbstractView {

	public static final String VIEW_NAME = "samples";

	private final SampleGridComponent sampleListComponent;
	private ViewConfiguration viewConfiguration;
	private Button btnEnterBulkEditMode;

	public SamplesView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		sampleListComponent = new SampleGridComponent(getViewTitleLabel(), this);
		setSizeFull();
		addComponent(sampleListComponent);

		if (UserProvider.getCurrent().hasUserRight(UserRight.LAB_MESSAGES)) {
			OptionGroup samplesViewSwitcher = new OptionGroup();
			samplesViewSwitcher.setId("samplesViewSwitcher");
			CssStyles.style(samplesViewSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
			for (SamplesViewType type : SamplesViewType.values()) {
				samplesViewSwitcher.addItem(type);
				samplesViewSwitcher.setItemCaption(type, I18nProperties.getEnumCaption(type));
			}

			samplesViewSwitcher.setValue(SamplesViewType.SAMPLES);
			samplesViewSwitcher.addValueChangeListener(e -> {
				SormasUI.get().getNavigator().navigateTo(LabMessagesView.VIEW_NAME);
			});
			addHeaderComponent(samplesViewSwitcher);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EXPORT)) {
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
			StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
				sampleListComponent.getGrid(),
				() -> viewConfiguration.isInEagerMode()
					? this.sampleListComponent.getGrid().asMultiSelect().getSelectedItems()
					: Collections.emptySet(),
				ExportEntityName.SAMPLES,
				SampleGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(basicExportButton);

			StreamResource extendedExportStreamResource = DownloadUtil.createCsvExportStreamResource(
				SampleExportDto.class,
				null,
				(Integer start, Integer max) -> FacadeProvider.getSampleFacade()
					.getExportList(sampleListComponent.getGrid().getCriteria(), this.getSelectedRows(), start, max),
				(propertyId, type) -> {
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
				},
				ExportEntityName.SAMPLES,
				null);

			addExportButton(
				extendedExportStreamResource,
				exportButton,
				exportLayout,
				VaadinIcons.FILE_TEXT,
				Captions.exportDetailed,
				Strings.infoDetailedExport);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS_CASE_SAMPLES)) {
			btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());

			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());

			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				sampleListComponent.getBulkOperationsDropdown().setVisible(true);
				ViewModelProviders.of(SamplesView.class).get(ViewConfiguration.class).setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				sampleListComponent.getSearchField().setEnabled(false);
				sampleListComponent.getGrid().reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				sampleListComponent.getBulkOperationsDropdown().setVisible(false);
				ViewModelProviders.of(SamplesView.class).get(ViewConfiguration.class).setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				sampleListComponent.getSearchField().setEnabled(true);
				navigateTo(sampleListComponent.getCriteria());
			});
		}
	}

	private Set<String> getSelectedRows() {
		return viewConfiguration.isInEagerMode()
			? this.sampleListComponent.getGrid().asMultiSelect().getSelectedItems().stream().map(SampleIndexDto::getUuid).collect(Collectors.toSet())
			: Collections.emptySet();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		sampleListComponent.reload(event);
	}

	public ViewConfiguration getViewConfiguration() {
		return viewConfiguration;
	}
}
