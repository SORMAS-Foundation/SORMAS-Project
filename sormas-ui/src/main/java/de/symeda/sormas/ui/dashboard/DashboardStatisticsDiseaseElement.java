package de.symeda.sormas.ui.dashboard;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsDiseaseElement extends HorizontalLayout {
	
	public DashboardStatisticsDiseaseElement(String caption, int count, int previousCount) {
		setSpacing(true);
		Label captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE);
		addComponent(captionLabel);
		
		Label lineLabel = new Label("&nbsp;", ContentMode.HTML);
		CssStyles.style(lineLabel, CssStyles.LABEL_BOTTOM_LINE);
		addComponent(lineLabel);
		
		Label countLabel = new Label(Integer.toString(count));
		countLabel.setWidthUndefined();
		CssStyles.style(countLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE);
		addComponent(countLabel);
		
		Label growthLabel = new Label();
		growthLabel.setContentMode(ContentMode.HTML);
		growthLabel.setWidth(15, Unit.PIXELS);
		
		CssStyles.removeStyles(growthLabel, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_IMPORTANT);
		if (count > previousCount) {
			growthLabel.setValue(FontAwesome.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_CRITICAL);
		} else if (count == previousCount) {
			growthLabel.setValue(FontAwesome.CHEVRON_RIGHT.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_IMPORTANT, CssStyles.ALIGN_CENTER);
		} else {
			growthLabel.setValue(FontAwesome.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_POSITIVE);
		}
		
		CssStyles.style(growthLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE);
		addComponent(growthLabel);
		
		setExpandRatio(lineLabel, 1);
	}

}
