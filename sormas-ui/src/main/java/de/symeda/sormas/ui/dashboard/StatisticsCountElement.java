package de.symeda.sormas.ui.dashboard;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsCountElement extends VerticalLayout {

	private Label countLabel;
	
	public StatisticsCountElement(String caption, String labelClass) {
		countLabel = new Label();
		countLabel.setSizeUndefined();
		CssStyles.style(countLabel, CssStyles.COLOR_PRIMARY, CssStyles.SIZE_MEDIUM, CssStyles.TEXT_BOLD, CssStyles.TEXT_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
		addComponent(countLabel);
		
		Label captionLabel = new Label(caption);
		captionLabel.setSizeUndefined();
		CssStyles.style(captionLabel, CssStyles.COLOR_SECONDARY, CssStyles.SIZE_SMALL, CssStyles.TEXT_BOLD, CssStyles.TEXT_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
		captionLabel.addStyleName(labelClass);
		addComponent(captionLabel);
		
		setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);
		setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
	}

	public void updateCountLabel(int count) {
		countLabel.setValue(Integer.toString(count));
	}
	
}
