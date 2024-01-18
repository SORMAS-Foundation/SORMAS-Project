package de.symeda.sormas.ui.campaign.campaignstatistics;

import static de.symeda.sormas.ui.utils.FilteredGrid.ACTION_BTN_ID;

import java.util.List;
import java.util.function.Consumer;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsCriteria;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.campaign.components.CampaignSelector;
import de.symeda.sormas.ui.campaign.components.JurisdictionSelector;
import de.symeda.sormas.ui.campaign.components.importancefilterswitcher.ImportanceFilterSwitcher;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;

public class CampaignStatisticsView extends AbstractCampaignView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaignstatistics";

	private final CampaignSelector campaignSelector;
	private final CampaignStatisticsCriteria criteria;
	private final CampaignStatisticsGrid grid;

	private CampaignStatisticsFilterForm filterForm;
	private ImportanceFilterSwitcher importanceFilterSwitcher;

	public CampaignStatisticsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(getClass()).get(CampaignStatisticsCriteria.class);

		campaignSelector = new CampaignSelector();
		criteria.setCampaign(campaignSelector.getValue());
		criteria.setGroupingLevel(CampaignJurisdictionLevel.AREA);
		addHeaderComponent(campaignSelector);
		grid = new CampaignStatisticsGrid(criteria);

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
					GridExportStreamResource.createStreamResource(grid, ExportEntityName.CAMPAIGN_STATISTICS, ACTION_BTN_ID);
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.export, Strings.infoBasicExport);
			}
		}

		VerticalLayout mainLayout = new VerticalLayout();
		JurisdictionSelector jurisdictionSelector = new JurisdictionSelector();

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			jurisdictionSelector.setVisible(false);
		} else {
			HorizontalLayout jurisdictionLayout = new HorizontalLayout();
			jurisdictionSelector.addValueChangeListener(e -> {
				CampaignJurisdictionLevel groupingValue = (CampaignJurisdictionLevel) e.getValue();
				criteria.setGroupingLevel(groupingValue);
				grid.setColumnsVisibility(groupingValue);
				grid.reload();
			});
			jurisdictionLayout.addComponent(jurisdictionSelector);
			mainLayout.addComponent(jurisdictionLayout);
		}
		HorizontalLayout filtersLayout = new HorizontalLayout();

		filtersLayout.setWidthFull();
		filtersLayout.setMargin(false);
		filtersLayout.setSpacing(true);

		filterForm = createFilterForm();
		filtersLayout.addComponent(filterForm);
		filtersLayout.setComponentAlignment(filterForm, Alignment.TOP_LEFT);
		filtersLayout.setExpandRatio(filterForm, 0.8f);

		importanceFilterSwitcher = new ImportanceFilterSwitcher();
		importanceFilterSwitcher.setVisible(false);
		filtersLayout.addComponent(importanceFilterSwitcher);
		filtersLayout.setComponentAlignment(importanceFilterSwitcher, Alignment.TOP_RIGHT);
		filtersLayout.setExpandRatio(importanceFilterSwitcher, 0.2f);

		mainLayout.addComponent(filtersLayout);

		filterForm.getField(CampaignStatisticsCriteria.CAMPAIGN_FORM_META).addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			importanceFilterSwitcher.setVisible(value != null);
			grid.setColumnsVisibility(criteria.getGroupingLevel());
			grid.reload();
		});

		importanceFilterSwitcher.addValueChangeListener(e -> {
			grid.reload();
			createFormMetaChangedCallback()
				.accept((CampaignFormMetaReferenceDto) filterForm.getField(CampaignStatisticsCriteria.CAMPAIGN_FORM_META).getValue());
		});

		mainLayout.addComponent(grid);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();
		mainLayout.setExpandRatio(grid, 1);
		mainLayout.setStyleName("crud-main-layout");

		campaignSelector.addValueChangeListener(e -> {
			criteria.setCampaignFormMeta(null);
			criteria.setCampaign(campaignSelector.getValue());
			filterForm.setValue(criteria);
			grid.reload();
		});

		addComponent(mainLayout);
	}

	public CampaignStatisticsFilterForm createFilterForm() {
		final UserDto user = UserProvider.getCurrent().getUser();
		criteria.setRegion(user.getRegion());
		criteria.setDistrict(user.getDistrict());
		criteria.setCommunity(user.getCommunity());
		CampaignStatisticsFilterForm filterForm = new CampaignStatisticsFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter() && campaignSelector == null) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CampaignStatisticsView.class).remove(CampaignStatisticsCriteria.class);
			navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> {
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
						String type = element.getType();
						if (type != null) {
							CampaignFormElementType campaignFormElementType = CampaignFormElementType.fromString(type);
							if (campaignFormElementType == CampaignFormElementType.NUMBER
								|| campaignFormElementType == CampaignFormElementType.YES_NO) {
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
				}
			}
			grid.setColumnsVisibility(criteria.getGroupingLevel());
		};
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
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
