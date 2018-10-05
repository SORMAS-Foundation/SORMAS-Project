package de.symeda.sormas.ui.dashboard;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsGraphicalGrowthElement extends VerticalLayout {
	
	private SvgBarElement svgBarElement;
	private Label countLabel;
	private Label growthLabel;
	private Label percentageLabel;
	
	public DashboardStatisticsGraphicalGrowthElement(String caption, String svgFillClass) {
		HorizontalLayout captionAndValueLayout = new HorizontalLayout();
		captionAndValueLayout.setWidth(100, Unit.PERCENTAGE);
		
		Label captionLabel = new Label(caption);
		CssStyles.style(captionLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_BOLD);
		captionAndValueLayout.addComponent(captionLabel);
		
		countLabel = new Label();
		CssStyles.style(countLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_4);
		countLabel.setWidthUndefined();
		captionAndValueLayout.addComponent(countLabel);
		growthLabel = new Label();
		growthLabel.setHeightUndefined();
		growthLabel.setWidthUndefined();
		growthLabel.setContentMode(ContentMode.HTML);
		CssStyles.style(growthLabel, CssStyles.LABEL_SMALL, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_4);
		captionAndValueLayout.addComponent(growthLabel);
		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD);
		percentageLabel.setWidthUndefined();
		captionAndValueLayout.addComponent(percentageLabel);
		
		captionAndValueLayout.setComponentAlignment(captionLabel, Alignment.MIDDLE_LEFT);
		captionAndValueLayout.setComponentAlignment(countLabel, Alignment.MIDDLE_RIGHT);
		captionAndValueLayout.setComponentAlignment(growthLabel, Alignment.MIDDLE_RIGHT);
		captionAndValueLayout.setComponentAlignment(percentageLabel, Alignment.MIDDLE_RIGHT);
		captionAndValueLayout.setExpandRatio(captionLabel, 1);
		
		addComponent(captionAndValueLayout);
		
		svgBarElement = new SvgBarElement(svgFillClass);
		svgBarElement.setWidth(100, Unit.PERCENTAGE);
		addComponent(svgBarElement);
	}

	public void update(int count, int percentage, float growthPercentage, boolean increaseIsPositive) {
		countLabel.setValue(Integer.toString(count));
		svgBarElement.updateSvg(percentage);
		percentageLabel.setValue(growthPercentage != Float.MIN_VALUE ? growthPercentage % 1.0 != 0 ? String.format("%s", Float.toString(Math.abs(growthPercentage))) + "%" : String.format("%.0f", growthPercentage) + "%" : "");
		CssStyles.removeStyles(growthLabel, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_POSITIVE, CssStyles.LABEL_IMPORTANT);
		if (growthPercentage > 0) {
			growthLabel.setValue(FontAwesome.CHEVRON_UP.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.LABEL_POSITIVE : CssStyles.LABEL_CRITICAL);
		} else if (growthPercentage < 0) {
			growthLabel.setValue(FontAwesome.CHEVRON_DOWN.getHtml());
			CssStyles.style(growthLabel, increaseIsPositive ? CssStyles.LABEL_CRITICAL : CssStyles.LABEL_POSITIVE);
		} else {
			growthLabel.setValue(FontAwesome.CHEVRON_RIGHT.getHtml());
			CssStyles.style(growthLabel, CssStyles.LABEL_IMPORTANT);
		}
	}

}
