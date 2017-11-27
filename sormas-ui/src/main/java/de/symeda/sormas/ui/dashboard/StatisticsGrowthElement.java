package de.symeda.sormas.ui.dashboard;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsGrowthElement extends VerticalLayout {
	
	private Label countLabel;
	private Label growthLabel;
	private Label percentageLabel;

	public StatisticsGrowthElement(String caption, String captionClass, Alignment alignment) {
		setDefaultComponentAlignment(alignment);
		CssStyles.style(this, CssStyles.VSPACE_3);
		
		HorizontalLayout growthLayout = new HorizontalLayout();
		
		countLabel = new Label();
		CssStyles.style(countLabel, CssStyles.SIZE_LARGE, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_BOLD, CssStyles.HSPACE_RIGHT_4);
		growthLayout.addComponent(countLabel);
		
		growthLabel = new Label();
		growthLabel.setHeightUndefined();
		growthLabel.setContentMode(ContentMode.HTML);
		CssStyles.style(growthLabel, CssStyles.SIZE_MEDIUM, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_BOLD, CssStyles.HSPACE_RIGHT_4);
		growthLayout.addComponent(growthLabel);
		growthLayout.setComponentAlignment(growthLabel, Alignment.MIDDLE_CENTER);
		
		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.SIZE_LARGE, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_BOLD);
		growthLayout.addComponent(percentageLabel);
		
		addComponent(growthLayout);
		
		Label captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.SIZE_MEDIUM, CssStyles.TEXT_UPPERCASE, CssStyles.TEXT_BOLD, captionClass);
		
		addComponent(captionLabel);
	}
	
	public void update(int count, float percentage, boolean increaseIsPositive) {
		countLabel.setValue(Integer.toString(count));
		percentageLabel.setValue(percentage != Float.MIN_VALUE ? percentage % 1.0 != 0 ? String.format("%s", Float.toString(Math.abs(percentage))) + "%" : String.format("%.0f", percentage) + "%" : "");
		CssStyles.removeStyles(growthLabel, CssStyles.COLOR_CRITICAL, CssStyles.COLOR_POSITIVE, CssStyles.COLOR_IMPORTANT);
		if (percentage > 0) {
			growthLabel.setValue(FontAwesome.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.COLOR_POSITIVE : CssStyles.COLOR_CRITICAL);
		} else if (percentage < 0) {
			growthLabel.setValue(FontAwesome.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.COLOR_CRITICAL : CssStyles.COLOR_POSITIVE);
		} else {
			growthLabel.setValue(FontAwesome.CHEVRON_RIGHT.getHtml());
			CssStyles.style(growthLabel, CssStyles.COLOR_IMPORTANT);
		}
	}
	
}
