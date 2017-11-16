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

	public StatisticsGrowthElement(String caption, Alignment alignment) {
		setDefaultComponentAlignment(alignment);
		
		HorizontalLayout growthLayout = new HorizontalLayout();
		
		countLabel = new Label();
		CssStyles.style(countLabel, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_BOLD);
		growthLayout.addComponent(countLabel);
		
		growthLabel = new Label();
		growthLabel.setContentMode(ContentMode.HTML);
		CssStyles.style(growthLabel, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_BOLD);
		growthLayout.addComponent(growthLabel);
		
		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_BOLD);
		growthLayout.addComponent(percentageLabel);
		
		addComponent(growthLayout);
		
		Label captionLabel = new Label(caption);
		CssStyles.style(captionLabel, CssStyles.COLOR_SECONDARY, CssStyles.TEXT_BOLD);
		
		addComponent(captionLabel);
	}
	
	public void update(int count, int percentage, boolean growthUp, boolean increaseIsPositive) {
		countLabel.setValue(Integer.toString(count));
		percentageLabel.setValue(Integer.toString(percentage) + "%");
		if (growthUp) {
			growthLabel.setValue(FontAwesome.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.COLOR_INCREASE : CssStyles.COLOR_DECREASE);
		} else {
			growthLabel.setValue(FontAwesome.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.COLOR_DECREASE : CssStyles.COLOR_INCREASE);
		}
	}
	
}
