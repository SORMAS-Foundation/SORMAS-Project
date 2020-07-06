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

import java.util.Date;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

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
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class SamplesView extends AbstractView {

	public static final String VIEW_NAME = "samples";

	private final SampleGridComponent sampleListComponent;
	private ViewConfiguration viewConfiguration;

	public SamplesView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		sampleListComponent = new SampleGridComponent(getViewTitleLabel(), this);
		setSizeFull();
		addComponent(sampleListComponent);

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EXPORT) && UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
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

			StreamResource streamResource = new GridExportStreamResource(
				sampleListComponent.getGrid(),
				"sormas_samples",
				"sormas_samples_" + DateHelper.formatDateForExport(new Date()) + ".csv",
				SampleGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(basicExportButton);

			StreamResource extendedExportStreamResource = DownloadUtil.createCsvExportStreamResource(
				SampleExportDto.class,
				null,
				(Integer start, Integer max) -> FacadeProvider.getSampleFacade()
					.getExportList(sampleListComponent.getGrid().getCriteria(), start, max),
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
				createFileNameWithCurrentDate("sormas_samples_", ".csv"),
				null);

			addExportButton(
				extendedExportStreamResource,
				exportButton,
				exportLayout,
				VaadinIcons.FILE_TEXT,
				Captions.exportDetailed,
				Strings.infoDetailedExport);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());

			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());

			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				sampleListComponent.getBulkOperationsDropdown().setVisible(true);
				viewConfiguration.setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				sampleListComponent.getSearchField().setEnabled(false);
				sampleListComponent.getGrid().setEagerDataProvider();
				sampleListComponent.getGrid().reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				sampleListComponent.getBulkOperationsDropdown().setVisible(false);
				viewConfiguration.setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				sampleListComponent.getSearchField().setEnabled(true);
				navigateTo(sampleListComponent.getCriteria());
			});
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		sampleListComponent.reload(event);
	}

	public ViewConfiguration getViewConfiguration() {
		return viewConfiguration;
	}
}
