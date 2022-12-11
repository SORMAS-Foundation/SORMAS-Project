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
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.campaign.components.CampaignFormPhaseSelector;
import de.symeda.sormas.ui.campaign.components.CampaignSelector;
import de.symeda.sormas.ui.campaign.components.importancefilterswitcher.ImportanceFilterSwitcher;
import de.symeda.sormas.ui.campaign.importer.CampaignFormDataImportLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
@com.vaadin.annotations.JavaScript("jquerymini.js")
@CssImport("w3c.css")
public class CampaignDataView extends AbstractCampaignView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigndata"; /// dataform

	private final CampaignSelector campaignSelector;
	private final CampaignFormDataCriteria criteria;
	private final CampaignDataGrid grid;
	private CampaignFormDataFilterForm filterForm;
	private ImportanceFilterSwitcher importanceFilterSwitcher;
	private CampaignFormPhaseSelector campaignFormPhaseSelector;
	private PopupButton newFormButton;
	private PopupButton importCampaignButton;

	@SuppressWarnings("deprecation")
	public CampaignDataView() {
		super(VIEW_NAME);

		/*
		 * Wanted to implement this to make sure when the Campaigns menu icon is
		 * clicked, the criteria remains. But, this won't work because the lastcriteria
		 * session attribute used to manipulate the url is saved in the session as a
		 * String and cannot be parsed to the Criteria class to be used on page initial
		 * constructor. A more elegant approach to making the criteria stick should be
		 * looked into.
		 * 
		 * An approach can be to manipulate the lastcriteria string to retrieve the
		 * values of each filter element and then set/parse into criteria field. This
		 * might be a crude approach but should work
		 * 
		 * if(UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria") !=
		 * null){ criteria = (CampaignFormDataCriteria)
		 * UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria"); }else
		 * {
		 */

		criteria = ViewModelProviders.of(getClass()).get(CampaignFormDataCriteria.class);

		campaignSelector = new CampaignSelector();
		criteria.setCampaign(campaignSelector.getValue());
		addHeaderComponent(campaignSelector);

		campaignFormPhaseSelector = new CampaignFormPhaseSelector();
		criteria.setFormType(campaignFormPhaseSelector.getValue().toString());
		addHeaderComponent(campaignFormPhaseSelector);

		grid = new CampaignDataGrid(criteria);
		grid.setDescriptionGenerator(CampaignFormMetaReferenceDto -> grid.getCaption());

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
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria", criteria.toUrlParams().toString());
		});

		importanceFilterSwitcher.addValueChangeListener(e -> {
			grid.reload();
			createFormMetaChangedCallback().accept((CampaignFormMetaReferenceDto) filterForm
					.getField(CampaignFormDataCriteria.CAMPAIGN_FORM_META).getValue());
		});

		mainLayout.addComponent(grid);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();
		mainLayout.setExpandRatio(grid, 1);
		mainLayout.setStyleName("crud-main-layout");

		Panel newFormPanel = new Panel();
		{
			VerticalLayout newFormLayout = new VerticalLayout();
			newFormLayout.setSpacing(true);
			newFormLayout.setMargin(true);
			newFormLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			newFormLayout.setWidth(350, Unit.PIXELS);

			newFormPanel.setContent(newFormLayout);
			fillNewFormDropdown(newFormPanel);

			newFormButton = ButtonHelper.createIconPopupButton(Captions.actionNewForm, VaadinIcons.PLUS_CIRCLE,
					newFormPanel);
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

			importCampaignButton = ButtonHelper.createIconPopupButton(Captions.actionImport, VaadinIcons.PLUS_CIRCLE,
					importFormPanel);
			importCampaignButton.setId("campaign-form-import");
			addHeaderComponent(importCampaignButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_FORM_DATA_EXPORT)) {
			VerticalLayout exportLayout = new VerticalLayout();
			{
				exportLayout.setSpacing(true);
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(250, Unit.PIXELS);
			}

			PopupButton exportPopupButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD,
					exportLayout);
			addHeaderComponent(exportPopupButton);

			{
				StreamResource streamResource = GridExportStreamResource.createStreamResource("", "", grid,
						ExportEntityName.CAMPAIGN_DATA, EDIT_BTN_ID);
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.export,
						Strings.infoBasicExport);

			}

			exportPopupButton.addClickListener(e -> {
				exportLayout.removeAllComponents();
				String formNamee = criteria.getCampaignFormMeta() == null ? "All_Forms"
						: criteria.getCampaignFormMeta().getCaption();
				String camNamee = campaignSelector.getValue() == null ? "All_Campaigns"
						: campaignSelector.getValue().toString();
				StreamResource streamResourcex = GridExportStreamResource.createStreamResource(
						camNamee + "_" + formNamee, "APMIS", grid, ExportEntityName.CAMPAIGN_DATA, EDIT_BTN_ID);
				addExportButton(streamResourcex, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.export,
						Strings.infoBasicExport);
			});

		}

		campaignSelector.addValueChangeListener(e -> {
			campaignFormPhaseSelector.clear();
			((VerticalLayout) importFormPanel.getContent()).removeAllComponents();
			if (!Objects.isNull(campaignSelector.getValue())) {
				fillImportDropdown(importFormPanel);
				fillNewFormDropdown(newFormPanel);
				importCampaignButton.setEnabled(true);
				newFormButton.setEnabled(true);
				UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria",
						criteria.toUrlParams().toString());
				System.out.println("333333333333333333333 " + criteria.toUrlParams().toString());
			} else {
				importCampaignButton.setEnabled(false);
				newFormButton.setEnabled(false);
				UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria",
						criteria.toUrlParams().toString());
				System.out.println("44444444444444444 " + criteria.toUrlParams().toString());
			}
			criteria.setCampaignFormMeta(null);
			filterForm.setValue(criteria);
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria", criteria.toUrlParams().toString());
			System.out.println("5555555555555555 " + criteria.toUrlParams().toString());
		});

		campaignFormPhaseSelector.addValueChangeListener(e -> {
			((VerticalLayout) newFormPanel.getContent()).removeAllComponents();
			if (!Objects.isNull(campaignSelector.getValue())) {
				fillNewFormDropdown(newFormPanel);
				newFormButton.setEnabled(true);
			} else {
				newFormButton.setEnabled(false);
			}

			criteria.setFormType(e.getValue().toString());
			filterForm.setPhaseFilterContent(e.getValue().toString());
			filterForm.setValue(criteria);
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria", criteria.toUrlParams().toString());
			System.out.println("777777777777777777777 " + criteria.toUrlParams().toString());
			grid.reload();
		});

		if (campaignSelector.getValue() == null) {
			importCampaignButton.setEnabled(false);
			newFormButton.setEnabled(false);
		}

		addComponent(mainLayout);

		JavaScript js = Page.getCurrent().getJavaScript();
		js.execute("$(document).ready(function() {\n" + "	if ($(window).width() <= 825) {\n"
				+ "document.querySelector(\".v-label.v-widget.h1.v-label-h1.vspace-none.v-label-vspace-none.v-label-undef-w\").style.display='none';\n"

				+ "document.querySelector(\".v-horizontallayout-view-headerxxxx :nth-child(12)\").style.display='none';"
				+ "document.querySelector(\".v-horizontallayout-view-headerxxxx :nth-child(13)\").style.display='none';"
				+ "document.querySelector(\".v-horizontallayout-view-headerxxxx :nth-child(10)\").style.display='none';\n"
				+ "document.querySelector(\".v-horizontallayout-view-headerxxxx :nth-child(11)\").style.display='none';\n"
				+ "	}"

				+ "});");

	}

	private void fillImportDropdown(Panel containerPanel) {

		CampaignReferenceDto campaignReferenceDto = campaignSelector.getValue();

		String phase = campaignFormPhaseSelector.getValue();
		Set<FormAccess> userFormAccess = UserProvider.getCurrent().getFormAccess();
		((VerticalLayout) containerPanel.getContent()).removeAllComponents();

		if (campaignReferenceDto != null) {
			List<CampaignFormMetaReferenceDto> campagaignFormReferences;
			if (UserProvider.getCurrent().getUser().getUsertype().equals(UserType.WHO_USER)) {
				campagaignFormReferences = FacadeProvider.getCampaignFormMetaFacade()
						.getCampaignFormMetasAsReferencesByCampaign(campaignReferenceDto.getUuid());
			} else {
				campagaignFormReferences = FacadeProvider.getCampaignFormMetaFacade()
						.getCampaignFormMetaAsReferencesByCampaignIntraCamapaign(campaignReferenceDto.getUuid());
				;
			}

			Collections.sort(campagaignFormReferences);
			for (CampaignFormMetaReferenceDto campaignForm : campagaignFormReferences) {
				Button campaignFormButton = ButtonHelper.createButton(campaignForm.toString(), e -> {
					importCampaignButton.setPopupVisible(false);
					try {
						Window popupWindow = VaadinUiUtil
								.showPopupWindow(new CampaignFormDataImportLayout(campaignForm, campaignReferenceDto));
						popupWindow.setCaption(I18nProperties.getString(Strings.headingImportCampaign));
						popupWindow.addCloseListener(c -> grid.reload());
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				});
				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				campaignFormButton.setStyleName("nocapitalletter");
				campaignFormButton.removeStyleName("v-button");
				((VerticalLayout) containerPanel.getContent()).addComponent(campaignFormButton);
			}
			if (campagaignFormReferences.size() >= 10) {
				// setting a fixed height will enable a scrollbar. Increase width to accommodate
				// it
				containerPanel.setHeight(400, Unit.PIXELS);
				containerPanel.setWidth(containerPanel.getContent().getWidth() + 20.0f, Unit.PIXELS);
			} else {
				containerPanel.setHeightUndefined();
				containerPanel.setWidth(containerPanel.getContent().getWidth(), Unit.PIXELS);
			}
		}
	}

	private void fillNewFormDropdown(Panel containerPanel) {

		CampaignReferenceDto campaignReferenceDtx = campaignSelector.getValue();
		String phase = campaignFormPhaseSelector.getValue();
		Set<FormAccess> userFormAccess = UserProvider.getCurrent().getFormAccess();

		((VerticalLayout) containerPanel.getContent()).removeAllComponents();

		System.out.println(phase + " #############################");

		if (phase != null && campaignReferenceDtx != null) {
			List<CampaignFormMetaReferenceDto> campagaignFormReferences = FacadeProvider.getCampaignFormMetaFacade()
					.getAllCampaignFormMetasAsReferencesByRoundandCampaignandForm(phase.toLowerCase(),
							campaignReferenceDtx.getUuid(), userFormAccess);

			Collections.sort(campagaignFormReferences);

			for (CampaignFormMetaReferenceDto campaignForm : campagaignFormReferences) {
				Button campaignFormButton = ButtonHelper.createButton(campaignForm.toString(), el -> { 

					System.out.println(campaignReferenceDtx.getUuid()
							+ " ####################################################################"
							+ campaignForm.getUuid());

					ControllerProvider.getCampaignController().navigateToFormDataView(campaignReferenceDtx.getUuid(),
							campaignForm.getUuid());

					newFormButton.setPopupVisible(false);
				});

				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				// campaignFormButton.removeStyleName(VIEW_NAME);
				campaignFormButton.removeStyleName("v-button");
				campaignFormButton.setStyleName("nocapitalletter");
				((VerticalLayout) containerPanel.getContent()).addComponent(campaignFormButton);
			}
			if (campagaignFormReferences.size() >= 10) {
				// setting a fixed height will enable a scrollbar. Increase width to accommodate
				// it
				containerPanel.setHeight(400, Unit.PIXELS);
				containerPanel.setWidth(containerPanel.getContent().getWidth() + 20.0f, Unit.PIXELS);
			} else {
				containerPanel.setHeightUndefined();
				containerPanel.setWidth(containerPanel.getContent().getWidth(), Unit.PIXELS);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public CampaignFormDataFilterForm createFilterBar() {
		final UserDto user = UserProvider.getCurrent().getUser();
		criteria.setArea(user.getArea());
		criteria.setRegion(user.getRegion());
		criteria.setDistrict(user.getDistrict());
		criteria.setCommunity(null); // set to null for the initial filter bar
		filterForm = new CampaignFormDataFilterForm();

		if (filterForm.hasFilter()) {
			criteria.setArea(criteria.getArea());
			criteria.setRegion(criteria.getRegion());
			criteria.setDistrict(criteria.getDistrict());
			criteria.setCommunity(criteria.getCommunity());
		}

		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter() && campaignSelector == null) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CampaignDataView.class).remove(CampaignFormDataCriteria.class);
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria", "");
			navigateTo(null, true);
		});

		// apply button action
		filterForm.addApplyHandler(e -> {
			criteria.setCampaign(campaignSelector.getValue());
			criteria.setFormType(campaignFormPhaseSelector.getValue().toString());
			grid.reload();
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria", criteria.toUrlParams().toString());
			System.out.println(UI.getCurrent().getSession().getCurrent().getAttribute("lastcriteria"));
		});
		campaignSelector.addValueChangeListener(e -> {
			criteria.setCampaign(campaignSelector.getValue());
			grid.reload();
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria", criteria.toUrlParams().toString());
		});

		campaignFormPhaseSelector.addValueChangeListener(e -> {
			criteria.setFormType(e.getValue().toString());
			grid.reload();
			UI.getCurrent().getSession().getCurrent().setAttribute("lastcriteria",
					Page.getCurrent().getLocation().toString());
		});

		filterForm.setFormMetaChangedCallback(createFormMetaChangedCallback());

		return filterForm;
	}

	private Consumer<CampaignFormMetaReferenceDto> createFormMetaChangedCallback() {
		return formMetaReference -> {
			grid.removeAllColumns();
			grid.addDefaultColumns();
			if (formMetaReference != null) {
				CampaignFormMetaDto formMeta = FacadeProvider.getCampaignFormMetaFacade()
						.getCampaignFormMetaByUuid(formMetaReference.getUuid());
				Language userLanguage = UserProvider.getCurrent().getUser().getLanguage();
				CampaignFormTranslations translations = null;
				if (userLanguage != null) {
					translations = formMeta.getCampaignFormTranslations().stream()
							.filter(t -> t.getLanguageCode().equals(userLanguage.getLocale().toString())).findFirst()
							.orElse(null);
				}
				final boolean onlyImportantFormElements = importanceFilterSwitcher.isImportantSelected();
				final List<CampaignFormElement> campaignFormElements = formMeta.getCampaignFormElements();
				for (CampaignFormElement element : campaignFormElements) {
					if (element.isImportant() || !onlyImportantFormElements) {
						String caption = null;
						if (translations != null) {
							caption = translations.getTranslations().stream()
									.filter(t -> t.getElementId().equals(element.getId()))
									.map(TranslationElement::getCaption).findFirst().orElse(null);
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

	private Consumer<CampaignFormMetaReferenceDto> createFormMetaChangedCallbackPhase() {

		return formMetaReference -> {
			grid.removeAllColumns();
			grid.addDefaultColumns();
			if (formMetaReference != null) {
				CampaignFormMetaDto formMeta = FacadeProvider.getCampaignFormMetaFacade()
						.getCampaignFormMetaByUuid(formMetaReference.getUuid());
				Language userLanguage = UserProvider.getCurrent().getUser().getLanguage();
				CampaignFormTranslations translations = null;
				if (userLanguage != null) {
					translations = formMeta.getCampaignFormTranslations().stream()
							.filter(t -> t.getLanguageCode().equals(userLanguage.getLocale().toString())).findFirst()
							.orElse(null);
				}
				final boolean onlyImportantFormElements = importanceFilterSwitcher.isImportantSelected();
				final List<CampaignFormElement> campaignFormElements = formMeta.getCampaignFormElements();
				for (CampaignFormElement element : campaignFormElements) {
					if (element.isImportant() || !onlyImportantFormElements) {
						String caption = null;
						if (translations != null) {
							caption = translations.getTranslations().stream()
									.filter(t -> t.getElementId().equals(element.getId()))
									.map(TranslationElement::getCaption).findFirst().orElse(null);
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

		UserDto user = UserProvider.getCurrent().getUser();
		if (event.getParameters() != null) {
			// split at "&"
			String[] queryParameters = event.getParameters().split("&");
			for (String queryParameter : queryParameters) {
				String[] innerSplit = queryParameter.split("=");
				if (queryParameter.contains("area")) {
					AreaDto uu = FacadeProvider.getAreaFacade().getByUuid(innerSplit[1]);
					AreaReferenceDto areas = uu.toReference();
					criteria.setArea(areas);
				}
				if (queryParameter.contains("region")) {
					RegionReferenceDto regions = FacadeProvider.getRegionFacade()
							.getRegionReferenceByUuid(innerSplit[1]);
					criteria.setRegion(regions);
				}
				if (queryParameter.contains("district")) {
					DistrictReferenceDto districts = FacadeProvider.getDistrictFacade()
							.getDistrictReferenceByUuid(innerSplit[1]);
					criteria.setDistrict(districts);
				}
				if (queryParameter.contains("community")) {
					CommunityReferenceDto communitys = FacadeProvider.getCommunityFacade()
							.getCommunityReferenceByUuid(innerSplit[1]);
					criteria.setCommunity(communitys);
				}
				if (queryParameter.contains("formType")) {
					criteria.setFormType(innerSplit[1]);
				}
				if (queryParameter.contains("campaign")) { 
					CampaignReferenceDto campaign = FacadeProvider.getCampaignFacade()
							.getReferenceByUuid(innerSplit[1]);
					criteria.setCampaign(campaign);
					
				//	campaignSelector.setValue(criteria.getCampaign());
					
				}
				if (queryParameter.contains("campaignFormMeta")) { 
					CampaignFormMetaReferenceDto campaignsmeta = FacadeProvider.getCampaignFormMetaFacade()
							.getCampaignFormMetaReferenceByUuid(innerSplit[1]);
					criteria.setCampaignFormMeta(campaignsmeta);
					filterForm.cbCampaignForm.setValue(campaignsmeta);
					filterForm.cbCampaignForm.setInputPrompt(campaignsmeta.getCaption());
				}
			}
		}

//		if (params.startsWith("?")) {
//			params = params.substring(1);
//			criteria.fromUrlParams(params);
//			campaignSelector.setValue(criteria.getCampaign());
//
//			String district = "";
//			String campaignFormMeta = "";
//			String community = "";
//			String string = params;
//			String[] parts = string.split("=");
//			int ll = parts.length;
//			String formType = ll > 1 ? parts[1] : ""; // 004
//			String area = ll > 2 ? parts[2] : ""; // 004
//			String region = ll > 3 ? parts[3] : ""; // 004
//			String campaign = ll > 4 ? parts[4] : ""; // 004
//			if (string.contains("campaignFormMeta")) {
//				community = ll > 5 ? parts[5] : ""; // 004
//			} else {
//				community = ll > 5 ? parts[5] : "";
//			}
//			if (string.contains("campaignFormMeta")) {
//				campaignFormMeta = ll > 6 ? parts[6] : "";
//				district = ll > 7 ? parts[7] : ""; // 004
//			} else if (!string.contains("community")) {
//				district = ll > 5 ? parts[5] : "";
//			} else {
//				district = ll > 6 ? parts[6] : "";
//			}
//
//			System.out.println(ll + "------------------------------------" + params);
////			System.out.println(parts[6]);
////			System.out.println(campaignFormMeta);
//
////			System.out.println("------------------------------------" + formType.replace("&area", "")
////					+ "------------------------------------" + area.replace("&region", "")
////					+ "------------------------------------" + region.replace("&campaign", "")
////					+ "------------------------------------" + campaign.replace("&community", "")
////					+ "------------------------------------" + community.replace("&campaignFormMeta", "")
////					+ "------------------------------------" + campaignFormMeta.replace("&district", "")
////					+ "------------------------------------" + district);
//
//			if (!area.isEmpty() && params.contains("&area")) {
//				AreaDto uu = FacadeProvider.getAreaFacade().getByUuid(area.replace("&region", ""));
//				// uu.setUuid(area.replace("&region", ""));
//				AreaReferenceDto areas = uu.toReference();
//				criteria.setArea(areas);
//			}
//			if (!region.isEmpty() && params.contains("&region")) {
//				RegionReferenceDto regions = FacadeProvider.getRegionFacade()
//						.getRegionReferenceByUuid(region.replace("&campaign", ""));
//				criteria.setRegion(regions);
//			}
//			if (!district.isEmpty() && params.contains("&district")) {
//				DistrictReferenceDto districts = FacadeProvider.getDistrictFacade()
//						.getDistrictReferenceByUuid(district);
//				criteria.setDistrict(districts);
//			}
//			if (!community.isEmpty() && params.contains("&community") && params.contains("&campaignFormMeta")) {
//				CommunityReferenceDto communitys = FacadeProvider.getCommunityFacade()
//						.getCommunityReferenceByUuid(community.replace("&campaignFormMeta", ""));
//				criteria.setCommunity(communitys);
//			} else {
//				CommunityReferenceDto communitys = FacadeProvider.getCommunityFacade()
//						.getCommunityReferenceByUuid(community.replace("&district", ""));
//				criteria.setCommunity(communitys);
//			}
//			if (!formType.isEmpty() && params.contains("&formType")) {
//				criteria.setFormType(formType.replace("&area", ""));
//
//			}
//			if (!campaignFormMeta.isEmpty() && params.contains("&campaignFormMeta")) {
//				CampaignFormMetaReferenceDto campaignsmeta = FacadeProvider.getCampaignFormMetaFacade()
//						.getCampaignFormMetaReferenceByUuid(campaignFormMeta.replaceAll("&district", ""));
//				criteria.setCampaignFormMeta(campaignsmeta);
//				System.out.println(campaignFormMeta.replaceAll("&district", ""));
//			}
//		}

		applyingCriteria = true;
		filterForm.setValue(criteria);
		applyingCriteria = false;

		grid.reload();

		super.enter(event);
	}

}
