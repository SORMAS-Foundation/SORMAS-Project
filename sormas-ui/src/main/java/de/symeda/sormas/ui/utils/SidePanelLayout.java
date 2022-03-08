package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

public class SidePanelLayout extends CustomLayout {

	private static final String CONTENT_COMPONENT = "contentComponent";
	private static final String SIDE_PANEL = "sidePanel";
	private static final String HTML_LAYOUT =
		LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, CONTENT_COMPONENT), LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SIDE_PANEL));

	private final CustomLayout sidePanel;
	private final CommitDiscardWrapperComponent<?> editComponent;

	public SidePanelLayout(CommitDiscardWrapperComponent editComponent, String... sideComponentLocs) {
		this.editComponent = editComponent;

		addStyleName(CssStyles.ROOT_COMPONENT);
		setWidth(100, Unit.PERCENTAGE);
		setHeightUndefined();

		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);

		setTemplateContents(HTML_LAYOUT);
		addComponent(editComponent, CONTENT_COMPONENT);

		sidePanel = new CustomLayout();

		List<LayoutUtil.FluidColumn> sidePanelComponentsHtml =
			Arrays.stream(sideComponentLocs).map(loc -> LayoutUtil.fluidColumnLoc(12, 0, loc)).collect(Collectors.toList());

		sidePanel.setTemplateContents(LayoutUtil.fluidRow(sidePanelComponentsHtml.toArray(new LayoutUtil.FluidColumn[] {})));
		addComponent(sidePanel, SIDE_PANEL);
	}

	public void addSidePanelComponent(Component component, String loc) {
		sidePanel.addComponent(component, loc);
	}

	public CustomLayout getSidePanelComponent() {
		return sidePanel;
	}

	public void disable(String... excludedButtons) {
		editComponent.setEditable(false, excludedButtons);
		sidePanel.setEnabled(false);
	}
}
