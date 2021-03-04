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

import static de.symeda.sormas.ui.utils.FilteredGrid.EDIT_BTN_ID;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import de.symeda.sormas.ui.utils.ExportEntityName;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormElementImportance;
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
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.campaign.importer.CampaignFormDataImportLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class CampaignDataView extends AbstractCampaignView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigndata";

	private final CampaignFormDataCriteria criteria;
	private final CampaignDataGrid grid;
	private CampaignFormDataFilterForm filterForm;
	private final ComboBox<CampaignReferenceDto> campaignCombo;
	protected OptionGroup campaignFormElementImportance;

	public static final String ONLY_IMPORTANT_FORM_ELEMENTS = "onlyImportantFormElements";

	@SuppressWarnings("deprecation")
	public CampaignDataView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(getClass()).get(CampaignFormDataCriteria.class);

		HorizontalLayout campaignLayout = new HorizontalLayout();
		{
			campaignLayout.setMargin(false);
			campaignLayout.setSpacing(false);

			Label campaignLabel = new Label(I18nProperties.getCaption(Captions.Campaign));
			campaignLabel.addStyleName("v-caption");
			campaignLabel.addStyleName(CssStyles.HSPACE_RIGHT_4);
			campaignLayout.addComponent(campaignLabel);
			campaignLayout.setComponentAlignment(campaignLabel, Alignment.MIDDLE_CENTER);

			campaignCombo = new ComboBox<>(" ");
			List<CampaignReferenceDto> campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
			campaignCombo.setItems(campaigns);
			campaignCombo.setEmptySelectionCaption(I18nProperties.getCaption(Captions.campaignAllCampaigns));
			final CampaignReferenceDto lastStartedCampaign = FacadeProvider.getCampaignFacade().getLastStartedCampaign();
			if (lastStartedCampaign != null) {
				campaignCombo.setValue(lastStartedCampaign);
			}
			criteria.setCampaign(campaignCombo.getValue());
			CssStyles.style(campaignCombo, CssStyles.SOFT_REQUIRED);
			campaignLayout.addComponent(campaignCombo);
		}
		addHeaderComponent(campaignLayout);
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

		createImportanceFilterSwitch();
		filtersLayout.addComponent(campaignFormElementImportance);
		filtersLayout.setComponentAlignment(campaignFormElementImportance, Alignment.TOP_RIGHT);
		filtersLayout.setExpandRatio(campaignFormElementImportance, 0.2f);

		mainLayout.addComponent(filtersLayout);

		filterForm.getField(CampaignFormDataCriteria.CAMPAIGN_FORM_META).addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			campaignFormElementImportance.setVisible(value != null);
		});

		campaignFormElementImportance.addValueChangeListener(e -> {
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

		if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_FORM_DATA_EXPORT)) {
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
				StreamResource streamResource = GridExportStreamResource.createStreamResource(grid, ExportEntityName.CAMPAIGN_DATA, EDIT_BTN_ID);
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.export, Strings.infoBasicExport);
			}
		}

		VerticalLayout newFormLayout = new VerticalLayout();
		PopupButton newFormButton;
		{
			newFormLayout.setSpacing(true);
			newFormLayout.setMargin(true);
			newFormLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			newFormLayout.setWidth(350, Unit.PIXELS);

			newFormButton = ButtonHelper.createIconPopupButton(Captions.actionNewForm, VaadinIcons.PLUS_CIRCLE, newFormLayout);
			newFormButton.setId("new-form");

			createNewFormLayout(newFormLayout);

			addHeaderComponent(newFormButton);
		}

		VerticalLayout importFormLayout = new VerticalLayout();
		importFormLayout.setSpacing(true);
		importFormLayout.setMargin(true);
		importFormLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		importFormLayout.setWidth(350, Unit.PIXELS);

		Button importCampaignButton = ButtonHelper.createIconPopupButton(Captions.actionImport, VaadinIcons.PLUS_CIRCLE, importFormLayout);
		importCampaignButton.setId("campaign-form-import");
		createImportLayout(importFormLayout);
		addHeaderComponent(importCampaignButton);
		campaignCombo.addValueChangeListener(e -> {
			importFormLayout.removeAllComponents();
			newFormLayout.removeAllComponents();
			if (!Objects.isNull(campaignCombo.getValue())) {
				createImportLayout(importFormLayout);
				createNewFormLayout(newFormLayout);
				importCampaignButton.setEnabled(true);
				newFormButton.setEnabled(true);
			} else {
				importCampaignButton.setEnabled(false);
				newFormButton.setEnabled(false);
			}
			criteria.setCampaignFormMeta(null);
			filterForm.setValue(criteria);
		});

		if (campaignCombo.getValue() == null) {
			importCampaignButton.setEnabled(false);
			newFormButton.setEnabled(false);
		}

		addComponent(mainLayout);
	}

	private void createImportLayout(VerticalLayout importFormLayout) {

		if (campaignCombo.getValue() != null) {
			for (CampaignFormMetaReferenceDto campaignForm : FacadeProvider.getCampaignFormMetaFacade()
				.getCampaignFormMetasAsReferencesByCampaign(campaignCombo.getValue().getUuid())) {

				Button campaignFormButton = ButtonHelper.createButton(campaignForm.toString(), e -> {
					try {
						Window popupWindow = VaadinUiUtil.showPopupWindow(new CampaignFormDataImportLayout(campaignForm, campaignCombo.getValue()));
						popupWindow.setCaption(I18nProperties.getString(Strings.headingImportCampaign));
						popupWindow.addCloseListener(c -> grid.reload());
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				});
				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				importFormLayout.addComponent(campaignFormButton);
			}
		}
	}

	private void createNewFormLayout(VerticalLayout newFormLayout) {

		if (campaignCombo.getValue() != null) {
			for (CampaignFormMetaReferenceDto campaignForm : FacadeProvider.getCampaignFormMetaFacade()
				.getCampaignFormMetasAsReferencesByCampaign(campaignCombo.getValue().getUuid())) {
				Button campaignFormButton = ButtonHelper.createButton(
					campaignForm.toString(),
					e -> ControllerProvider.getCampaignController().createCampaignDataForm(criteria.getCampaign(), campaignForm));
				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				newFormLayout.addComponent(campaignFormButton);
			}
		}
	}

	private void createImportanceFilterSwitch() {

		campaignFormElementImportance = new OptionGroup();
		CssStyles.style(campaignFormElementImportance, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		campaignFormElementImportance.setId(ONLY_IMPORTANT_FORM_ELEMENTS);
		campaignFormElementImportance.addItem(CampaignFormElementImportance.IMPORTANT);
		campaignFormElementImportance
			.setItemCaption(CampaignFormElementImportance.IMPORTANT, I18nProperties.getEnumCaption(CampaignFormElementImportance.IMPORTANT));
		campaignFormElementImportance.addItem(CampaignFormElementImportance.ALL);
		campaignFormElementImportance
			.setItemCaption(CampaignFormElementImportance.ALL, I18nProperties.getEnumCaption(CampaignFormElementImportance.ALL));

		campaignFormElementImportance.setValue(CampaignFormElementImportance.ALL);
		campaignFormElementImportance.setVisible(false);
	}

	public CampaignFormDataFilterForm createFilterBar() {
		final UserDto user = UserProvider.getCurrent().getUser();
		criteria.setRegion(user.getRegion());
		criteria.setDistrict(user.getDistrict());
		criteria.setCommunity(user.getCommunity());
		filterForm = new CampaignFormDataFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter() && campaignCombo == null) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CampaignDataView.class).remove(CampaignFormDataCriteria.class);
			navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> {
			criteria.setCampaign(campaignCombo.getValue());
			grid.reload();
		});
		campaignCombo.addValueChangeListener(e -> {
			criteria.setCampaign(campaignCombo.getValue());
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
				final boolean onlyImportantFormElements = CampaignFormElementImportance.IMPORTANT.equals(campaignFormElementImportance.getValue());
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
			campaignCombo.setValue(criteria.getCampaign());
		}

		applyingCriteria = true;
		filterForm.setValue(criteria);
		applyingCriteria = false;

		grid.reload();

		super.enter(event);
	}

}
