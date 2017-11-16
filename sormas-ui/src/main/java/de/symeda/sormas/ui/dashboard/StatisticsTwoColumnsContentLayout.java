package de.symeda.sormas.ui.dashboard;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsTwoColumnsContentLayout extends StatisticsContentLayout {

	private VerticalLayout rightColumnLayout;
	
	public StatisticsTwoColumnsContentLayout() {
		this(false, 50);
	}
	
	public StatisticsTwoColumnsContentLayout(boolean showSeparator, int leftColumnPercentalWidth) {
		super();
		setSpacing(true);
		
		if (showSeparator) {
			Label separator = new Label();
			separator.setHeight(100, Unit.PERCENTAGE);
			separator.setWidthUndefined();
			CssStyles.style(separator, CssStyles.SEPARATOR_VERTICAL, CssStyles.HSPACE_LEFT_3, CssStyles.HSPACE_RIGHT_3);
			addComponent(separator);
		}
		
		rightColumnLayout = new VerticalLayout();
		rightColumnLayout.setSpacing(false);
		rightColumnLayout.setWidth(100, Unit.PERCENTAGE);
		rightColumnLayout.setHeightUndefined();
		addComponent(rightColumnLayout);
		setExpandRatio(contentLayout, leftColumnPercentalWidth);
		setExpandRatio(rightColumnLayout, 100 - leftColumnPercentalWidth);
	}
	
	@Override
	public void addComponentToContent(AbstractComponent component) {
		throw new UnsupportedOperationException("This method is not supported for a StatisticsTwoColumnsSubComponent. "
				+ "Call addComponentToLeftContentColumn or addComponentToRightContentColumn instead.");
	}
	
	@Override
	public void removeAllComponents() {
		contentLayout.removeAllComponents();
		rightColumnLayout.removeAllComponents();
	}
	
	public void addComponentToLeftContentColumn(AbstractComponent component) {
		component.setWidth(100, Unit.PERCENTAGE);
		contentLayout.addComponent(component);
	}
	
	public void addComponentToRightContentColumn(AbstractComponent component) {
		component.setWidth(100, Unit.PERCENTAGE);
		rightColumnLayout.addComponent(component);
	}
	
	public void removeAllComponentsFromLeftContentColumn() {
		contentLayout.removeAllComponents();
	}
	
	public void removeAllComponentsFromRightContentColumn() {
		rightColumnLayout.removeAllComponents();
	}

}
