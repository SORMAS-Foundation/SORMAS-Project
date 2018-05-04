package de.symeda.sormas.ui.dashboard;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsGrowthElement extends VerticalLayout {
	
	private Label countLabel;
	private Label growthLabel;
	private Label percentageLabel;

	public DashboardStatisticsGrowthElement(String caption, String captionClass, Alignment alignment) {
		setDefaultComponentAlignment(alignment);
		CssStyles.style(this, CssStyles.VSPACE_3);
		
		HorizontalLayout growthLayout = new HorizontalLayout();
		
		countLabel = new Label();
		CssStyles.style(countLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_4);
		growthLayout.addComponent(countLabel);
		
		growthLabel = new Label();
		growthLabel.setHeightUndefined();
		growthLabel.setContentMode(ContentMode.HTML);
		CssStyles.style(growthLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_4);
		growthLayout.addComponent(growthLabel);
		growthLayout.setComponentAlignment(growthLabel, Alignment.MIDDLE_CENTER);
		
		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD);
		growthLayout.addComponent(percentageLabel);
		
		addComponent(growthLayout);
		
		Label captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_UPPERCASE, CssStyles.LABEL_BOLD, captionClass);
		
		addComponent(captionLabel);
	}
	
	public void update(int count, float percentage, boolean increaseIsPositive) {
		countLabel.setValue(Integer.toString(count));
		percentageLabel.setValue(percentage != Float.MIN_VALUE ? percentage % 1.0 != 0 ? String.format("%s", Float.toString(Math.abs(percentage))) + "%" : String.format("%.0f", percentage) + "%" : "");
		CssStyles.removeStyles(growthLabel, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_IMPORTANT);
		if (percentage > 0) {
			growthLabel.setValue(FontAwesome.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.LABEL_POSITIVE : CssStyles.LABEL_CRITICAL);
		} else if (percentage < 0) {
			growthLabel.setValue(FontAwesome.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.LABEL_CRITICAL : CssStyles.LABEL_POSITIVE);
		} else {
			growthLabel.setValue(FontAwesome.CHEVRON_RIGHT.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_IMPORTANT);
		}
	}
	
}
