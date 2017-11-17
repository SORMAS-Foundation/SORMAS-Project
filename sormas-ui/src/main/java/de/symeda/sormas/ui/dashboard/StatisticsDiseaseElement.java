package de.symeda.sormas.ui.dashboard;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsDiseaseElement extends HorizontalLayout {
	
	public StatisticsDiseaseElement(String caption, int count, int previousCount) {
		setSpacing(true);
		Label captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.COLOR_SECONDARY, CssStyles.TEXT_BOLD, CssStyles.SIZE_LARGE);
		addComponent(captionLabel);
		
		Label lineLabel = new Label();
		CssStyles.style(lineLabel, CssStyles.SEPARATOR_HORIZONTAL_BOTTOM);
		addComponent(lineLabel);
		
		Label countLabel = new Label(Integer.toString(count));
		countLabel.setWidthUndefined();
		CssStyles.style(countLabel, CssStyles.COLOR_SECONDARY, CssStyles.TEXT_BOLD, CssStyles.SIZE_LARGE);
		addComponent(countLabel);
		
		Label growthLabel = new Label();
		growthLabel.setContentMode(ContentMode.HTML);
		growthLabel.setWidth(15, Unit.PIXELS);
		
		CssStyles.removeStyles(growthLabel, CssStyles.COLOR_CRITICAL, CssStyles.COLOR_POSITIVE, CssStyles.COLOR_IMPORTANT);
		if (count > previousCount) {
			growthLabel.setValue(FontAwesome.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, CssStyles.COLOR_CRITICAL);
		} else if (count == previousCount) {
			growthLabel.setValue(FontAwesome.CHEVRON_RIGHT.getHtml());
			CssStyles.style(growthLabel, CssStyles.COLOR_IMPORTANT, CssStyles.ALIGN_CENTER);
		} else {
			growthLabel.setValue(FontAwesome.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, CssStyles.COLOR_POSITIVE);
		}
		
		CssStyles.style(growthLabel, CssStyles.TEXT_BOLD, CssStyles.SIZE_LARGE);
		addComponent(growthLabel);
		
		setExpandRatio(lineLabel, 1);
	}

}
