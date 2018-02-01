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
		CssStyles.style(countLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_MEDIUM, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
		addComponent(countLabel);
		
		Label captionLabel = new Label(caption);
		captionLabel.setSizeUndefined();
		CssStyles.style(captionLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_SMALL, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_5, CssStyles.VSPACE_TOP_NONE);
		captionLabel.addStyleName(labelClass);
		addComponent(captionLabel);
		
		setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);
		setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
	}

	public void updateCountLabel(int count) {
		countLabel.setValue(Integer.toString(count));
	}
	
}
