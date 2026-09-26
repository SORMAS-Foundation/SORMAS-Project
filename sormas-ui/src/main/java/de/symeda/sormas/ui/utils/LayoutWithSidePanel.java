package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class LayoutWithSidePanel extends CustomLayout {

	private static final String CONTENT_COMPONENT = "contentComponent";
	private static final String SIDE_PANEL = "sidePanel";
	private static final int SIDE_PANEL_SPAN_SMALL_DEFAULT = 6;
	private static final int SIDE_PANEL_SPAN_SMALL_FULL_WIDTH = 12;

	private final CustomLayout sidePanel;
	private final CommitDiscardWrapperComponent<?> editComponent;
	private Map<String, Component> sideComponents = new HashMap<>();

	public LayoutWithSidePanel(CommitDiscardWrapperComponent editComponent, String... sideComponentLocs) {
		this(editComponent, false, sideComponentLocs);
	}

	/**
	 * @param fullWidthSidePanelOnSmallScreens
	 *            Whether the side panel takes the full width on small screens instead of half of it
	 */
	public LayoutWithSidePanel(CommitDiscardWrapperComponent editComponent, boolean fullWidthSidePanelOnSmallScreens, String... sideComponentLocs) {
		this.editComponent = editComponent;

		addStyleName(CssStyles.ROOT_COMPONENT);
		setWidth(100, Unit.PERCENTAGE);
		setHeightUndefined();

		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);

		int sidePanelSpanSmall = fullWidthSidePanelOnSmallScreens ? SIDE_PANEL_SPAN_SMALL_FULL_WIDTH : SIDE_PANEL_SPAN_SMALL_DEFAULT;
		setTemplateContents(
			LayoutUtil.fluidRow(
				LayoutUtil.fluidColumnLoc(8, 0, 12, 0, CONTENT_COMPONENT),
				LayoutUtil.fluidColumnLoc(4, 0, sidePanelSpanSmall, 0, SIDE_PANEL)));
		addComponent(editComponent, CONTENT_COMPONENT);

		sidePanel = new CustomLayout();

		List<LayoutUtil.FluidColumn> sidePanelComponentsHtml =
			Arrays.stream(sideComponentLocs).map(loc -> LayoutUtil.fluidColumnLoc(12, 0, loc)).collect(Collectors.toList());

		sidePanel.setTemplateContents(LayoutUtil.fluidRow(sidePanelComponentsHtml.toArray(new LayoutUtil.FluidColumn[] {})));
		addComponent(sidePanel, SIDE_PANEL);
	}

	public void addSidePanelComponent(Component component, String loc) {
		sidePanel.addComponent(component, loc);
		sideComponents.put(loc, component);
	}

	public CustomLayout getSidePanelComponent() {
		return sidePanel;
	}

	public void disable() {
		disableWithViewAllow();
		sidePanel.setEnabled(false);
	}

	//excludeButtons: represent the buttons from the CommitDiscardComponent that we intend to exclude from disabling
	public void disableWithViewAllow() {
		editComponent.setNonEditable();
	}

	//excludeButtons: represent the buttons from the CommitDiscardComponent that we intend to exclude from disabling
	public void disable(boolean excludePresentDocuments, String... excludedButtons) {
		editComponent.setEditable(false, excludedButtons);
		if (excludePresentDocuments) {
			sideComponents.keySet().stream().forEach(loc -> {
				if (!loc.equals(CaseDataView.DOCUMENTS_LOC)) {
					sideComponents.get(loc).setEnabled(false);
				} else {
					((DocumentListComponent) ((SideComponentLayout) sideComponents.get(loc)).getComponent()).getMainButton().setEnabled(false);
				}
			});
		} else {
			sidePanel.setEnabled(false);
		}
	}

	public void disableIfNecessary(boolean deleted, EditPermissionType editAllowed) {
		if (deleted) {
			editComponent.addToActiveButtonsList(CommitDiscardWrapperComponent.DELETE_RESTORE);
			disable();
		} else if (editAllowed != null) {
			disableBasedOnPermissionTypes(editAllowed);
		}
	}

	public void disableBasedOnPermissionTypes(EditPermissionType editAllowed) {
		if (editAllowed.equals(EditPermissionType.ARCHIVING_STATUS_ONLY)) {
			editComponent.addToActiveButtonsList(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
			disableWithViewAllow();
		} else if (editAllowed.equals(EditPermissionType.OUTSIDE_JURISDICTION) || editAllowed.equals(EditPermissionType.REFUSED)) {
			disableWithViewAllow();
		} else if (editAllowed.equals(EditPermissionType.WITHOUT_OWNERSHIP)) {
			editComponent.addToActiveButtonsList(CommitDiscardWrapperComponent.DELETE_RESTORE);
			disableWithViewAllow();
		}
	}
}
