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

import static com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import java.util.List;
import java.util.function.Consumer;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormElementImportance;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslation;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.GridExportStreamResource;

@SuppressWarnings("serial")
public class CampaignDataView extends AbstractCampaignView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigndata";

	private final CampaignFormDataCriteria criteria;
	private final CampaignDataGrid grid;
	private CampaignFormDataFilterForm filterForm;

	protected OptionGroup campaignFormElementImportance;

	public static final String ONLY_IMPORTANT_FORM_ELEMENTS = "onlyImportantFormElements";

	public CampaignDataView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(getClass()).get(CampaignFormDataCriteria.class);
		grid = new CampaignDataGrid(criteria);

		VerticalLayout mainLayout = new VerticalLayout();
		HorizontalLayout filtersLayout = new HorizontalLayout();

		filtersLayout.addComponent(createFilterBar());
		final HorizontalLayout importanceFilterLayout = createImportanceFilterLayout();
		filtersLayout.addComponent(importanceFilterLayout);

		filtersLayout.setComponentAlignment(importanceFilterLayout, Alignment.MIDDLE_RIGHT);

		mainLayout.addComponent(filtersLayout);

		filterForm.getField(CampaignFormDataCriteria.CAMPAIGN_FORM_META).addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			if (value == null) {
				campaignFormElementImportance.setEnabled(false);
				campaignFormElementImportance.setValue(CampaignFormElementImportance.IMPORTANT);
			} else {
				campaignFormElementImportance.setValue(CampaignFormElementImportance.IMPORTANT);
				campaignFormElementImportance.setEnabled(true);
			}
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
				StreamResource streamResource =
					new GridExportStreamResource(grid, "sormas_campaign_data", createFileNameWithCurrentDate("sormas_campaign_data_", ".csv"));
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.exportBasic, Strings.infoBasicExport);
			}
		}

		VerticalLayout newFormLayout = new VerticalLayout();
		{
			newFormLayout.setSpacing(true);
			newFormLayout.setMargin(true);
			newFormLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			newFormLayout.setWidth(350, Unit.PIXELS);

			PopupButton newFormButton = ButtonHelper.createIconPopupButton(Captions.actionNewForm, VaadinIcons.PLUS_CIRCLE, newFormLayout);
			newFormButton.setId("new-form");

			for (CampaignFormMetaReferenceDto campaignForm : FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences()) {
				Button campaignFormButton = ButtonHelper
					.createButton(campaignForm.toString(), e -> ControllerProvider.getCampaignController().createCampaignDataForm(campaignForm));
				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				newFormLayout.addComponent(campaignFormButton);
			}

			addHeaderComponent(newFormButton);
		}

		addComponent(mainLayout);
	}

	private HorizontalLayout createImportanceFilterLayout() {

		final HorizontalLayout importanceFilterLayout = new HorizontalLayout();
		importanceFilterLayout.setSpacing(true);

		campaignFormElementImportance = new OptionGroup();
		CssStyles.style(
			campaignFormElementImportance,
			CssStyles.FORCE_CAPTION,
			ValoTheme.OPTIONGROUP_HORIZONTAL,
			CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		campaignFormElementImportance.addItem(CampaignFormElementImportance.ALL);
		campaignFormElementImportance
			.setItemCaption(CampaignFormElementImportance.ALL, I18nProperties.getEnumCaption(CampaignFormElementImportance.ALL));
		campaignFormElementImportance.addItem(CampaignFormElementImportance.IMPORTANT);
		campaignFormElementImportance
			.setItemCaption(CampaignFormElementImportance.IMPORTANT, I18nProperties.getEnumCaption(CampaignFormElementImportance.IMPORTANT));
		campaignFormElementImportance.setId(ONLY_IMPORTANT_FORM_ELEMENTS);
		campaignFormElementImportance.setWidth(140, Unit.PIXELS);
		campaignFormElementImportance.setNullSelectionAllowed(false);

		importanceFilterLayout.addComponent(campaignFormElementImportance);
		importanceFilterLayout.setComponentAlignment(campaignFormElementImportance, Alignment.TOP_RIGHT);

		campaignFormElementImportance.setEnabled(false);

		return importanceFilterLayout;
	}

	public CampaignFormDataFilterForm createFilterBar() {
		filterForm = new CampaignFormDataFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!navigateTo(criteria, false)) {
				filterForm.updateResetButtonState();
				grid.reload();
			}
		});

		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CampaignDataView.class).remove(CampaignFormDataCriteria.class);
			navigateTo(null, true);
		});

		filterForm.setFormMetaChangedCallback(createFormMetaChangedCallback());

		return filterForm;
	}

	private Consumer<CampaignFormMetaReferenceDto> createFormMetaChangedCallback() {
		grid.removeAllColumns();
		grid.addDefaultColumns();
		return formMetaReference -> {
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
					if ((onlyImportantFormElements && element.isImportant()) || !onlyImportantFormElements) {
						String caption = null;
						if (translations != null) {
							caption = translations.getTranslations()
								.stream()
								.filter(t -> t.getElementId().equals(element.getId()))
								.map(CampaignFormTranslation::getCaption)
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
		}
		filterForm.setValue(criteria);
		grid.reload();

		super.enter(event);
	}

}
