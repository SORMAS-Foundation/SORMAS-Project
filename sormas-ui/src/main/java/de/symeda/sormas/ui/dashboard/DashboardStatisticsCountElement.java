package de.symeda.sormas.ui.dashboard;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class DashboardStatisticsCountElement extends VerticalLayout {

	private Label countLabel;
	private Label captionLabel;
	
	public DashboardStatisticsCountElement(String caption, CountElementStyle countElementStyle) {
		addStyleName("count-element");
		addStyleName(countElementStyle.getCssClass());
		countLabel = new Label();
		countLabel.setSizeUndefined();
		CssStyles.style(countLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
		addComponent(countLabel);
		
		captionLabel = new Label(caption);
		captionLabel.setSizeUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_SMALL, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
		addComponent(captionLabel);
		
		setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);
		setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
	}

	public void updateCountLabel(int count) {
		countLabel.setValue(Integer.toString(count));
	}
	
	public void updateCountLabel(String count) {
		countLabel.setValue(count);
	}
	
}
