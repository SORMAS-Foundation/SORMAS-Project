package de.symeda.sormas.ui.dashboard;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsSubComponent extends VerticalLayout {

	private Label dateLabel;
	private Label countLabel;
	
	public StatisticsSubComponent() {
		this.setMargin(new MarginInfo(false, true, false, true));
	}
	
	public void addHeader(String headline, Image icon) {
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setWidth(100, Unit.PERCENTAGE);
		headerLayout.setSpacing(true);
		CssStyles.style(headerLayout, CssStyles.VSPACE_4);
		
		VerticalLayout labelAndTotalLayout = new VerticalLayout();
		{
			Label headlineLabel = new Label(headline);
			CssStyles.style(headlineLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			labelAndTotalLayout.addComponent(headlineLabel);
			
			dateLabel = new Label();
			CssStyles.style(dateLabel, CssStyles.H4, CssStyles.VSPACE_TOP_NONE);
			labelAndTotalLayout.addComponent(dateLabel);
			
			countLabel = new Label();
			CssStyles.style(countLabel, CssStyles.H1, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			labelAndTotalLayout.addComponent(countLabel);
			
		}
		headerLayout.addComponent(labelAndTotalLayout);
		headerLayout.setComponentAlignment(labelAndTotalLayout, Alignment.BOTTOM_LEFT);
		headerLayout.setExpandRatio(labelAndTotalLayout, 1);
		
		headerLayout.addComponent(icon);
		headerLayout.setComponentAlignment(icon, Alignment.TOP_RIGHT);

		addComponent(headerLayout);
	}
	
	public void addOverview() {
		
	}
	
	public void addContent() {
		
	}
	
	public void updateCountLabel(int count) {
		countLabel.setValue(Integer.toString(count));
	}
	
	public Label getDateLabel() {
		return dateLabel;
	}
	
}
