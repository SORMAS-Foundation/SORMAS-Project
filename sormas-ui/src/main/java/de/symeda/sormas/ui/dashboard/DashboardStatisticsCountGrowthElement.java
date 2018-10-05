package de.symeda.sormas.ui.dashboard;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsCountGrowthElement extends DashboardStatisticsGrowthElement {
	
	public DashboardStatisticsCountGrowthElement(String caption, CountElementStyle countElementStyle) {
		super(caption);
		addStyleName("count-element");
		addStyleName(countElementStyle.getCssClass());
		
		setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
		setComponentAlignment(growthLayout, Alignment.MIDDLE_CENTER);
	}
	
	@Override
	protected void createCountLabel() {
		countLabel = new Label();
		countLabel.setSizeUndefined();
		CssStyles.style(countLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.HSPACE_RIGHT_4, CssStyles.VSPACE_TOP_NONE);
	}
	
	@Override
	protected void createGrowthLabel() {
		growthLabel = new Label();
		growthLabel.setHeightUndefined();
		growthLabel.setContentMode(ContentMode.HTML);
		CssStyles.style(growthLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_4, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
	}
	
	@Override
	protected void createPercentageLabel() {
		percentageLabel = new Label();
		CssStyles.style(percentageLabel, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
	}
	
	@Override
	protected void createCaptionLabel() {
		captionLabel = new Label(caption);
		captionLabel.setSizeUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_SMALL, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
	}
	
}
