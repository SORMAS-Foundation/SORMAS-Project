package de.symeda.sormas.ui.dashboard;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsPercentageElement extends VerticalLayout {
	
	private SvgBarElement svgBarElement;
	private Label percentageLabel;
	
	public StatisticsPercentageElement(String caption, String svgFillClass) {
		HorizontalLayout captionAndValueLayout = new HorizontalLayout();
		captionAndValueLayout.setWidth(100, Unit.PERCENTAGE);
		
		Label captionLabel = new Label(caption);
		captionLabel.setWidthUndefined();
		CssStyles.style(captionLabel, CssStyles.COLOR_SECONDARY, CssStyles.TEXT_BOLD);
		captionAndValueLayout.addComponent(captionLabel);
		
		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_BOLD);
		percentageLabel.setWidthUndefined();
		captionAndValueLayout.addComponent(percentageLabel);
		
		captionAndValueLayout.setComponentAlignment(captionLabel, Alignment.MIDDLE_LEFT);
		captionAndValueLayout.setComponentAlignment(percentageLabel, Alignment.MIDDLE_RIGHT);
		
		addComponent(captionAndValueLayout);
		
		svgBarElement = new SvgBarElement(svgFillClass);
		svgBarElement.setWidth(100, Unit.PERCENTAGE);
		addComponent(svgBarElement);
	}
	
	public void updatePercentageValue(int percentageValue) {
		percentageLabel.setValue(Integer.toString(percentageValue) + "%");
		svgBarElement.updateSvg(percentageValue);
	}

}
