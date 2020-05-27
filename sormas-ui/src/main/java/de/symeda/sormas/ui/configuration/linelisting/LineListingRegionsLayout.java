package de.symeda.sormas.ui.configuration.linelisting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class LineListingRegionsLayout extends CssLayout {

	private Disease disease;
	private Map<String, List<String>> regions;
	private Map<String, String> regionNames;

	public LineListingRegionsLayout(List<FeatureConfigurationIndexDto> configurations, Disease disease) {
		this.disease = disease;
		this.regionNames = new HashMap<>();
		this.regions = new TreeMap<>((r1, r2) -> regionNames.get(r1).compareTo(regionNames.get(r2)));

		for (RegionReferenceDto region : FacadeProvider.getRegionFacade().getAllActiveAsReference()) {
			regionNames.put(region.getUuid(), region.toString());
			regions.put(region.getUuid(), new ArrayList<>());
		}

		if (configurations != null) {
			for (FeatureConfigurationIndexDto config : configurations) {
				regions.get(config.getRegionUuid()).add(config.getDistrictName());
			}
		}

		buildLayout();
	}

	private void buildLayout() {
		for (String regionUuid : regions.keySet()) {
			StringBuilder captionBuilder = new StringBuilder();
			captionBuilder.append("<b>").append(regionNames.get(regionUuid));
			if (regions.get(regionUuid).size() > 0) {
				captionBuilder.append(" (").append(regions.get(regionUuid).size()).append(")");
			}

			Button configButton = ButtonHelper.createButtonWithCaption("region-" + regionUuid, captionBuilder.toString(), e -> {
				SormasUI.get().getNavigator().navigateTo(LineListingConfigurationView.VIEW_NAME + "/" + regionUuid + "/?disease=" + disease.getName());
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.HSPACE_LEFT_4, CssStyles.VSPACE_4);
			configButton.setCaptionAsHtml(true);

			if (regions.get(regionUuid).size() > 0) {
				CssStyles.style(configButton, CssStyles.BUTTON_FILTER_ENABLED);
			} else {
				CssStyles.style(configButton, CssStyles.BUTTON_FILTER_DISABLED);
			}

			addComponent(configButton);
		}
	}

}
