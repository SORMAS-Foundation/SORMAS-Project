package de.symeda.sormas.ui.configuration.linelisting;

import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class LineListingActiveDistrictsLayout extends CssLayout {

	private List<FeatureConfigurationIndexDto> configurations;
	
	public LineListingActiveDistrictsLayout(List<FeatureConfigurationIndexDto> configurations) {
		this.configurations = configurations;
	
		buildLayout();
	}
	
	private void buildLayout() {
		for (FeatureConfigurationIndexDto config : configurations) {
			StringBuilder captionBuilder = new StringBuilder();
			captionBuilder.append("<b>").append(config.getDistrictName()).append("</b><br/>");
			if (config.getEndDate() != null) {
				captionBuilder.append(I18nProperties.getString(Strings.until)).append(" ").append(DateFormatHelper.formatDate(config.getEndDate()));
			} else {
				captionBuilder.append(I18nProperties.getString(Strings.messageNoEndDate));
			}
			
			Label configLabel = new Label(captionBuilder.toString(), ContentMode.HTML);
			CssStyles.style(configLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, CssStyles.ALIGN_CENTER,
					CssStyles.HSPACE_LEFT_4, CssStyles.VSPACE_4);
			addComponent(configLabel);
		}
	}
	
}
