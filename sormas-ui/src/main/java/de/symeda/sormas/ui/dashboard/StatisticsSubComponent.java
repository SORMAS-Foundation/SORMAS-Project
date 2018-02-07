package de.symeda.sormas.ui.dashboard;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsSubComponent extends VerticalLayout {

	// Layouts
	private HorizontalLayout countLayout;
	private AbstractOrderedLayout contentLayout;
	private VerticalLayout leftContentColumnLayout;
	private VerticalLayout rightContentColumnLayout;
	
	// Components
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
			
			countLabel = new Label();
			CssStyles.style(countLabel, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_XXXLARGE, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			labelAndTotalLayout.addComponent(countLabel);
			
		}
		headerLayout.addComponent(labelAndTotalLayout);
		headerLayout.setComponentAlignment(labelAndTotalLayout, Alignment.BOTTOM_LEFT);
		headerLayout.setExpandRatio(labelAndTotalLayout, 1);
		
		if (icon != null) {
			headerLayout.addComponent(icon);
			headerLayout.setComponentAlignment(icon, Alignment.TOP_RIGHT);
		}

		addComponent(headerLayout);
		setExpandRatio(headerLayout, 0);
	}
	
	public void addCountLayout() {
		countLayout = new HorizontalLayout();
		countLayout.setWidthUndefined();
		countLayout.setSpacing(true);
		CssStyles.style(countLayout, CssStyles.VSPACE_4);
		
		addComponent(countLayout);
		setExpandRatio(countLayout, 0);
	}
	
	public void addComponentToCountLayout(AbstractComponent countElement) {
		countElement.setWidthUndefined();
		countLayout.addComponent(countElement);
	}
	
	public void addContent() {
		if (contentLayout == null) {
			contentLayout = new VerticalLayout();
			contentLayout.setSpacing(false);
			initializeContentLayout();
			addComponent(contentLayout);
			setExpandRatio(contentLayout, 1);
		}
	}
	
	public void addTwoColumnsContent(boolean showSeparator, int leftColumnPercentalWidth) {
		if (contentLayout == null) {
			contentLayout = new HorizontalLayout();
			contentLayout.setSpacing(true);
			initializeContentLayout();
			addComponent(contentLayout);
			setExpandRatio(contentLayout, 1);

			leftContentColumnLayout = new VerticalLayout();
			leftContentColumnLayout.setWidth(100, Unit.PERCENTAGE);
			leftContentColumnLayout.setHeightUndefined();
			contentLayout.addComponent(leftContentColumnLayout);
			
			if (showSeparator) {
				Label separator = new Label();
				separator.setHeight(100, Unit.PERCENTAGE);
				CssStyles.style(separator, CssStyles.VR, CssStyles.HSPACE_LEFT_3, CssStyles.HSPACE_RIGHT_3);
				contentLayout.addComponent(separator);
			}
			
			rightContentColumnLayout = new VerticalLayout();
			rightContentColumnLayout.setWidth(100, Unit.PERCENTAGE);
			rightContentColumnLayout.setHeightUndefined();
			contentLayout.addComponent(rightContentColumnLayout);

			contentLayout.setExpandRatio(leftContentColumnLayout, leftColumnPercentalWidth);
			contentLayout.setExpandRatio(rightContentColumnLayout, 100 - leftColumnPercentalWidth);
		}
	}
	
	public void addComponentToContent(AbstractComponent component) {
		if (contentLayout != null) {
			component.setWidth(100, Unit.PERCENTAGE);
			contentLayout.addComponent(component);
		}
	}
	
	public void addComponentToLeftContentColumn(AbstractComponent component) {
		if (leftContentColumnLayout != null) {
			component.setWidth(100, Unit.PERCENTAGE);
			leftContentColumnLayout.addComponent(component);
		}
	}
	
	public void addComponentToRightContentColumn(AbstractComponent component) {
		if (rightContentColumnLayout != null) {
			component.setWidth(100, Unit.PERCENTAGE);
			rightContentColumnLayout.addComponent(component);
		}
	}
	
	public void removeAllComponentsFromContent() {
		if (contentLayout != null) {
			contentLayout.removeAllComponents();
		}
	}
	
	public void removeAllComponentsFromLeftContentColumn() {
		if (leftContentColumnLayout != null) {
			leftContentColumnLayout.removeAllComponents();
		}
	}
	
	public void removeAllComponentsFromRightContentColumn() {
		if (rightContentColumnLayout != null) {
			rightContentColumnLayout.removeAllComponents();
		}
	}
	
	public void removeContent() {
		if (contentLayout != null) {
			removeComponent(contentLayout);
		}
		
		contentLayout = null;
		leftContentColumnLayout = null;
		rightContentColumnLayout = null;
	}
	
	public void updateCountLabel(int count) {
		countLabel.setValue(Integer.toString(count));
	}
	
	private void initializeContentLayout() {
		contentLayout.setWidth(100, Unit.PERCENTAGE);
		contentLayout.setHeightUndefined();
		CssStyles.style(contentLayout, CssStyles.VSPACE_TOP_2);
	}
	
}
