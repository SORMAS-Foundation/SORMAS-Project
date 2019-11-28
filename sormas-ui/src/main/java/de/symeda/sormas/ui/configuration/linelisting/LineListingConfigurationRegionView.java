package de.symeda.sormas.ui.configuration.linelisting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class LineListingConfigurationRegionView extends AbstractConfigurationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/linelisting";

	private VerticalLayout contentLayout;
	private LineListingAddDiseaseLayout addDiseaseLayout;
	private VerticalLayout lineListingConfigurationsLayout;

	private Map<Disease, List<FeatureConfigurationIndexDto>> configurationMap;
	
	private RegionReferenceDto region;

	public LineListingConfigurationRegionView() {
		super(VIEW_NAME);

		this.region = UserProvider.getCurrent().getUser().getRegion();
		
		configurationMap = new TreeMap<>((d1, d2) -> {
			return d1.toString().compareTo(d2.toString());
		});

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);
		contentLayout.setWidth(100, Unit.PERCENTAGE);
		contentLayout.setStyleName("crud-main-layout");

		buildView();

		addComponent(contentLayout);
	}

	private void buildView() {
		Label infoTextLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoLineListingConfigurationRegion), ContentMode.HTML);
		CssStyles.style(infoTextLabel, CssStyles.LABEL_MEDIUM);
		contentLayout.addComponent(infoTextLabel);

		lineListingConfigurationsLayout = new VerticalLayout();
		lineListingConfigurationsLayout.setWidth(100, Unit.PERCENTAGE);
		lineListingConfigurationsLayout.setSpacing(true);
		lineListingConfigurationsLayout.setMargin(false);
		CssStyles.style(lineListingConfigurationsLayout, CssStyles.VSPACE_TOP_3, CssStyles.VSPACE_3);
		contentLayout.addComponent(lineListingConfigurationsLayout);

		// Retrieve existing line listing configurations from the database
		List<Disease> diseasesWithoutConfigurations = new ArrayList<>();
		diseasesWithoutConfigurations.addAll(Arrays.asList(Disease.values()));

		List<FeatureConfigurationIndexDto> lineListingConfigurations = FacadeProvider.getFeatureConfigurationFacade().getFeatureConfigurations(
				new FeatureConfigurationCriteria().featureType(FeatureType.LINE_LISTING).region(region), false);

		for (FeatureConfigurationIndexDto configuration : lineListingConfigurations) {
			if (!configurationMap.containsKey(configuration.getDisease())) {
				configurationMap.put(configuration.getDisease(), new ArrayList<>());
				diseasesWithoutConfigurations.remove(configuration.getDisease());
			}

			configurationMap.get(configuration.getDisease()).add(configuration);
		}

		configurationMap.keySet().stream().forEach(disease -> {
			lineListingConfigurationsLayout.addComponent(createDiseaseAndDistrictsLayout(disease, configurationMap.get(disease)));
		});

		addDiseaseLayout = new LineListingAddDiseaseLayout(diseasesWithoutConfigurations);
		addDiseaseLayout.setWidth(600, Unit.PIXELS);

		addDiseaseLayout.setAddDiseaseCallback(disease -> {
			HorizontalLayout diseaseLayout = createDiseaseAndDistrictsLayout(disease, null);
			lineListingConfigurationsLayout.addComponent(diseaseLayout);
			addDiseaseLayout.removeDiseaseFromList(disease);
		});

		contentLayout.addComponent(addDiseaseLayout);
	}

	private void showConfirmDisableAllWindow(HorizontalLayout diseaseAndDistrictLayout, Disease disease) {
		VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingDisableLineListing), new Label(I18nProperties.getString(Strings.confirmationDisableAllLineListingRegion)), 
				I18nProperties.getCaption(Captions.actionConfirm), I18nProperties.getCaption(Captions.actionCancel), 
				480, result -> {
					if (Boolean.TRUE.equals(result)) {
						FeatureConfigurationCriteria criteria = new FeatureConfigurationCriteria().disease(disease).region(region).featureType(FeatureType.LINE_LISTING);
						FacadeProvider.getFeatureConfigurationFacade().deleteAllFeatureConfigurations(criteria);
						lineListingConfigurationsLayout.removeComponent(diseaseAndDistrictLayout);
						addDiseaseLayout.addDiseaseToList(disease);
						Notification.show(null, I18nProperties.getString(Strings.messageLineListingDisabled), Type.TRAY_NOTIFICATION);
					}
				});
	}

	private HorizontalLayout createDiseaseAndDistrictsLayout(Disease disease, List<FeatureConfigurationIndexDto> configurations) {
		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.setWidth(100, Unit.PERCENTAGE);

		LineListingDiseaseLayout diseaseLayout = new LineListingDiseaseLayout(disease);
		{
			diseaseLayout.setWidth(300, Unit.PIXELS);
			CssStyles.style(diseaseLayout, CssStyles.VSPACE_4, CssStyles.HSPACE_RIGHT_4);

			diseaseLayout.setEditCallback(() -> {
				Window editWindow = VaadinUiUtil.createPopupWindow();

				FeatureConfigurationCriteria criteria = new FeatureConfigurationCriteria().disease(disease).region(region).featureType(FeatureType.LINE_LISTING);
				LineListingConfigurationEditLayout editLayout = new LineListingConfigurationEditLayout(FacadeProvider.getFeatureConfigurationFacade().getFeatureConfigurations(criteria, true), disease, region.toString());

				editLayout.setSaveCallback(() -> {
					Notification.show(null, I18nProperties.getString(Strings.messageLineListingSaved), Type.TRAY_NOTIFICATION);
					UI.getCurrent().getPage().reload();
				});
				editLayout.setDiscardCallback(() -> {
					editWindow.close();
				});

				editWindow.setWidth(1024, Unit.PIXELS);
				editWindow.setCaption(I18nProperties.getString(Strings.headingEditLineListing));
				editWindow.setContent(editLayout);
				UI.getCurrent().addWindow(editWindow);
			});

			diseaseLayout.setDisableAllCallback(() -> {
				showConfirmDisableAllWindow(contentLayout, disease);
			});
		}
		contentLayout.addComponent(diseaseLayout);
		contentLayout.setExpandRatio(diseaseLayout, 0);

		if (configurations != null) {
			LineListingActiveDistrictsLayout districtsLayout = new LineListingActiveDistrictsLayout(configurations);
			districtsLayout.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(districtsLayout, CssStyles.VSPACE_4, CssStyles.HSPACE_LEFT_1);
			contentLayout.addComponent(districtsLayout);
			contentLayout.setExpandRatio(districtsLayout, 1);
		}

		return contentLayout;
	}

}
