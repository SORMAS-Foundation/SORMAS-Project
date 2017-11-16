package de.symeda.sormas.ui.dashboard;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsContentLayout extends HorizontalLayout {
	
	protected VerticalLayout contentLayout;
	
	public StatisticsContentLayout() {
		setWidth(100, Unit.PERCENTAGE);
		setHeightUndefined();
		setSpacing(false);
		CssStyles.style(this, CssStyles.VSPACE_TOP_2);
		
		contentLayout = new VerticalLayout();
		contentLayout.setSpacing(false);
		contentLayout.setWidth(100, Unit.PERCENTAGE);
		contentLayout.setHeightUndefined();
		addComponent(contentLayout);
	}
	
	public void addComponentToContent(AbstractComponent component) {
		component.setWidth(100, Unit.PERCENTAGE);
		contentLayout.addComponent(component);
	}
	
	@Override
	public void removeAllComponents() {
		contentLayout.removeAllComponents();
	}

}
