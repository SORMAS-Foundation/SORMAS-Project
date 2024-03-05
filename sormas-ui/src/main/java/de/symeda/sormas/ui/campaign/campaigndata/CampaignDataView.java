/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.campaign.campaigndata;

import static de.symeda.sormas.ui.utils.FilteredGrid.ACTION_BTN_ID;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.campaign.components.CampaignSelector;
import de.symeda.sormas.ui.campaign.components.importancefilterswitcher.ImportanceFilterSwitcher;
import de.symeda.sormas.ui.campaign.importer.CampaignFormDataImportLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class CampaignDataView extends AbstractCampaignView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigndata";

	private final CampaignSelector campaignSelector;
	private final CampaignFormDataCriteria criteria;
	private final CampaignDataGrid grid;
	private CampaignFormDataFilterForm filterForm;
	private ImportanceFilterSwitcher importanceFilterSwitcher;
	private PopupButton newFormButton;
	private PopupButton importCampaignButton;

	@SuppressWarnings("deprecation")
	public CampaignDataView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(getClass()).get(CampaignFormDataCriteria.class);

		campaignSelector = new CampaignSelector();
		criteria.setCampaign(campaignSelector.getValue());
		addHeaderComponent(campaignSelector);
		grid = new CampaignDataGrid(criteria);

		VerticalLayout mainLayout = new VerticalLayout();
		HorizontalLayout filtersLayout = new HorizontalLayout();

		filtersLayout.setWidthFull();
		filtersLayout.setMargin(false);
		filtersLayout.setSpacing(true);

		CampaignFormDataFilterForm filterBar = createFilterBar();
		filtersLayout.addComponent(filterBar);
		filtersLayout.setComponentAlignment(filterBar, Alignment.TOP_LEFT);
		filtersLayout.setExpandRatio(filterBar, 0.8f);

		importanceFilterSwitcher = new ImportanceFilterSwitcher();
		importanceFilterSwitcher.setVisible(false);
		filtersLayout.addComponent(importanceFilterSwitcher);
		filtersLayout.setComponentAlignment(importanceFilterSwitcher, Alignment.TOP_RIGHT);
		filtersLayout.setExpandRatio(importanceFilterSwitcher, 0.2f);

		mainLayout.addComponent(filtersLayout);

		filterForm.getField(CampaignFormDataCriteria.CAMPAIGN_FORM_META).addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			importanceFilterSwitcher.setVisible(value != null);
		});

		importanceFilterSwitcher.addValueChangeListener(e -> {
			grid.reload();
			createFormMetaChangedCallback()
				.accept((CampaignFormMetaReferenceDto) filterForm.getField(CampaignFormDataCriteria.CAMPAIGN_FORM_META).getValue());
		});

		mainLayout.addComponent(grid);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();
		mainLayout.setExpandRatio(grid, 1);
		mainLayout.setStyleName("crud-main-layout");

		if (UiUtil.permitted(UserRight.CAMPAIGN_FORM_DATA_EXPORT)) {
			VerticalLayout exportLayout = new VerticalLayout();
			{
				exportLayout.setSpacing(true);
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(250, Unit.PIXELS);
			}

			PopupButton exportPopupButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
			addHeaderComponent(exportPopupButton);

			{
				StreamResource streamResource = GridExportStreamResource.createStreamResource(grid, ExportEntityName.CAMPAIGN_DATA, ACTION_BTN_ID);
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.export, Strings.infoBasicExport);
			}
		}

		Panel newFormPanel = new Panel();
		{
			VerticalLayout newFormLayout = new VerticalLayout();
			newFormLayout.setSpacing(true);
			newFormLayout.setMargin(true);
			newFormLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			newFormLayout.setWidth(350, Unit.PIXELS);

			newFormPanel.setContent(newFormLayout);
			fillNewFormDropdown(newFormPanel);

			newFormButton = ButtonHelper.createIconPopupButton(Captions.actionNewForm, VaadinIcons.PLUS_CIRCLE, newFormPanel);
			newFormButton.setId("new-form");
			addHeaderComponent(newFormButton);
		}

		Panel importFormPanel = new Panel();
		{
			VerticalLayout importFormLayout = new VerticalLayout();
			importFormLayout.setSpacing(true);
			importFormLayout.setMargin(true);
			importFormLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			importFormLayout.setWidth(350, Unit.PIXELS);

			importFormPanel.setContent(importFormLayout);
			fillImportDropdown(importFormPanel);

			importCampaignButton = ButtonHelper.createIconPopupButton(Captions.actionImport, VaadinIcons.PLUS_CIRCLE, importFormPanel);
			importCampaignButton.setId("campaign-form-import");
			addHeaderComponent(importCampaignButton);
		}

		campaignSelector.addValueChangeListener(e -> {
			((VerticalLayout) importFormPanel.getContent()).removeAllComponents();
			((VerticalLayout) newFormPanel.getContent()).removeAllComponents();
			if (!Objects.isNull(campaignSelector.getValue())) {
				fillImportDropdown(importFormPanel);
				fillNewFormDropdown(newFormPanel);
				importCampaignButton.setEnabled(true);
				newFormButton.setEnabled(true);
			} else {
				importCampaignButton.setEnabled(false);
				newFormButton.setEnabled(false);
			}
			criteria.setCampaignFormMeta(null);
			filterForm.setValue(criteria);
		});

		if (campaignSelector.getValue() == null) {
			importCampaignButton.setEnabled(false);
			newFormButton.setEnabled(false);
		}

		addComponent(mainLayout);
	}

	private void fillImportDropdown(Panel containerPanel) {

		CampaignReferenceDto campaignReferenceDto = campaignSelector.getValue();
		if (campaignReferenceDto != null) {
			List<CampaignFormMetaReferenceDto> campagaignFormReferences =
				FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetasAsReferencesByCampaign(campaignReferenceDto.getUuid());
			for (CampaignFormMetaReferenceDto campaignForm : campagaignFormReferences) {
				Button campaignFormButton = ButtonHelper.createButton(campaignForm.buildCaption(), e -> {
					importCampaignButton.setPopupVisible(false);
					try {
						Window popupWindow = VaadinUiUtil.showPopupWindow(new CampaignFormDataImportLayout(campaignForm, campaignReferenceDto));
						popupWindow.setCaption(I18nProperties.getString(Strings.headingImportCampaign));
						popupWindow.addCloseListener(c -> grid.reload());
					} catch (IOException ex) {
						throw new UncheckedIOException(ex);
					}
				});
				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				((VerticalLayout) containerPanel.getContent()).addComponent(campaignFormButton);
			}
			if (campagaignFormReferences.size() >= 10) {
				// setting a fixed height will enable a scrollbar. Increase width to accommodate it
				containerPanel.setHeight(400, Unit.PIXELS);
				containerPanel.setWidth(containerPanel.getContent().getWidth() + 20.0f, Unit.PIXELS);
			} else {
				containerPanel.setHeightUndefined();
				containerPanel.setWidth(containerPanel.getContent().getWidth(), Unit.PIXELS);
			}
		}
	}

	private void fillNewFormDropdown(Panel containerPanel) {

		CampaignReferenceDto campaignReferenceDto = campaignSelector.getValue();
		if (campaignReferenceDto != null) {
			List<CampaignFormMetaReferenceDto> campagaignFormReferences =
				FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetasAsReferencesByCampaign(campaignReferenceDto.getUuid());
			for (CampaignFormMetaReferenceDto campaignForm : campagaignFormReferences) {
				Button campaignFormButton = ButtonHelper.createButton(campaignForm.buildCaption(), e -> {
					ControllerProvider.getCampaignController().createCampaignDataForm(criteria.getCampaign(), campaignForm);
					newFormButton.setPopupVisible(false);
				});
				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				((VerticalLayout) containerPanel.getContent()).addComponent(campaignFormButton);
			}
			if (campagaignFormReferences.size() >= 10) {
				// setting a fixed height will enable a scrollbar. Increase width to accommodate it
				containerPanel.setHeight(400, Unit.PIXELS);
				containerPanel.setWidth(containerPanel.getContent().getWidth() + 20.0f, Unit.PIXELS);
			} else {
				containerPanel.setHeightUndefined();
				containerPanel.setWidth(containerPanel.getContent().getWidth(), Unit.PIXELS);
			}
		}
	}

	public CampaignFormDataFilterForm createFilterBar() {
		final UserDto user = UserProvider.getCurrent().getUser();
		criteria.setRegion(user.getRegion());
		criteria.setDistrict(user.getDistrict());
		criteria.setCommunity(user.getCommunity());
		filterForm = new CampaignFormDataFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter() && campaignSelector == null) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CampaignDataView.class).remove(CampaignFormDataCriteria.class);
			navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> {
			criteria.setCampaign(campaignSelector.getValue());
			grid.reload();
		});
		campaignSelector.addValueChangeListener(e -> {
			criteria.setCampaign(campaignSelector.getValue());
			grid.reload();
		});
		filterForm.setFormMetaChangedCallback(createFormMetaChangedCallback());

		return filterForm;
	}

	private Consumer<CampaignFormMetaReferenceDto> createFormMetaChangedCallback() {
		return formMetaReference -> {
			grid.removeAllColumns();
			grid.addDefaultColumns();
			if (formMetaReference != null) {
				CampaignFormMetaDto formMeta = FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetaByUuid(formMetaReference.getUuid());
				Language userLanguage = UserProvider.getCurrent().getUser().getLanguage();
				CampaignFormTranslations translations = null;
				if (userLanguage != null) {
					translations = formMeta.getCampaignFormTranslations()
						.stream()
						.filter(t -> t.getLanguageCode().equals(userLanguage.getLocale().toString()))
						.findFirst()
						.orElse(null);
				}
				final boolean onlyImportantFormElements = importanceFilterSwitcher.isImportantSelected();
				final List<CampaignFormElement> campaignFormElements = formMeta.getCampaignFormElements();
				for (CampaignFormElement element : campaignFormElements) {
					if (element.isImportant() || !onlyImportantFormElements) {
						String caption = null;
						if (translations != null) {
							caption = translations.getTranslations()
								.stream()
								.filter(t -> t.getElementId().equals(element.getId()))
								.map(TranslationElement::getCaption)
								.findFirst()
								.orElse(null);
						}
						if (caption == null) {
							caption = element.getCaption();
						}

						if (caption != null) {
							grid.addCustomColumn(element.getId(), caption);
						}
					}
				}
			}
		};
	}

	@Override
	public void enter(ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
			campaignSelector.setValue(criteria.getCampaign());
		}

		applyingCriteria = true;
		filterForm.setValue(criteria);
		applyingCriteria = false;

		grid.reload();

		super.enter(event);
	}

}
