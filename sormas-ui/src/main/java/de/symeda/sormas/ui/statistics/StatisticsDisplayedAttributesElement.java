package de.symeda.sormas.ui.statistics;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseAttributeGroup;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsDisplayedAttributesElement extends HorizontalLayout {

	private static final String SPECIFY_YOUR_SELECTION = "Specify your selection";

	private StatisticsCaseAttribute selectedRowsAttribute;
	private StatisticsCaseSubAttribute selectedRowsSubAttribute;
	private StatisticsCaseAttribute selectedColumnsAttribute;
	private StatisticsCaseSubAttribute selectedColumnsSubAttribute;

	public StatisticsDisplayedAttributesElement() {
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout rowsLayout = createRowsOrColumnsLayout(true);
		HorizontalLayout columnsLayout = createRowsOrColumnsLayout(false);
		addComponent(rowsLayout);
		addComponent(columnsLayout);
		setExpandRatio(rowsLayout, 0);
		setExpandRatio(columnsLayout, 1);
	}

	private HorizontalLayout createRowsOrColumnsLayout(boolean rows) {
		HorizontalLayout rowsOrColumnsLayout = new HorizontalLayout();
		rowsOrColumnsLayout.setSpacing(true);
		rowsOrColumnsLayout.setWidthUndefined();

		MenuBar displayedAttributeDropdown = new MenuBar();
		displayedAttributeDropdown.setCaption(rows ? "Rows" : "Columns");
		MenuItem displayedAttributeItem = displayedAttributeDropdown.addItem("Select an attribute", null);
		MenuBar displayedSubAttributeDropdown = new MenuBar();
		CssStyles.style(displayedSubAttributeDropdown, CssStyles.FORCE_CAPTION);
		MenuItem displayedSubAttributeItem = displayedSubAttributeDropdown.addItem(SPECIFY_YOUR_SELECTION, null);

		// Add attribute groups
		for (StatisticsCaseAttributeGroup attributeGroup : StatisticsCaseAttributeGroup.values()) {
			MenuItem attributeGroupItem = displayedAttributeItem.addItem(attributeGroup.toString(), null);
			attributeGroupItem.setEnabled(false);

			// Add attributes belonging to the current group
			for (StatisticsCaseAttribute attribute : attributeGroup.getAttributes()) {
				Command attributeCommand = selectedItem -> {
					// Reset the sub attribute dropdown
					displayedSubAttributeItem.removeChildren();
					displayedSubAttributeItem.setText(SPECIFY_YOUR_SELECTION);
					rowsOrColumnsLayout.removeComponent(displayedSubAttributeDropdown);
					if (rows) {
						selectedRowsAttribute = attribute;
						selectedRowsSubAttribute = null;
					} else {
						selectedColumnsAttribute = attribute;
						selectedColumnsSubAttribute = null;
					}
					displayedAttributeItem.setText(attribute.toString());
					// Add style to keep chosen item selected and remove it from all other items
					for (MenuItem menuItem : displayedAttributeItem.getChildren()) {
						menuItem.setStyleName("");
					}
					selectedItem.setStyleName("selected-filter");

					if (attribute.getSubAttributes().length > 0) {
						for (StatisticsCaseSubAttribute subAttribute : attribute.getSubAttributes()) {
							if (subAttribute.isUsedForGrouping()) {
								Command subAttributeCommand = selectedSubItem -> {
									if (rows) {
										selectedRowsSubAttribute = subAttribute;
									} else {
										selectedColumnsSubAttribute = subAttribute;
									}
									displayedSubAttributeItem.setText(subAttribute.toString());
									// Add style to keep chosen item selected and remove it from all other items
									for (MenuItem menuItem : displayedSubAttributeItem.getChildren()) {
										menuItem.setStyleName("");
									}
									selectedSubItem.setStyleName("selected-filter");
								};

								displayedSubAttributeItem.addItem(subAttribute.toString(), subAttributeCommand);
							}
						}

						rowsOrColumnsLayout.addComponent(displayedSubAttributeDropdown);
					}
				};

				displayedAttributeItem.addItem(attribute.toString(), attributeCommand);
			}
		}

		rowsOrColumnsLayout.addComponent(displayedAttributeDropdown);
		return rowsOrColumnsLayout;
	}

	public StatisticsCaseAttribute getSelectedRowsAttribute() {
		return selectedRowsAttribute;
	}

	public StatisticsCaseSubAttribute getSelectedRowsSubAttribute() {
		return selectedRowsSubAttribute;
	}

	public StatisticsCaseAttribute getSelectedColumnsAttribute() {
		return selectedColumnsAttribute;
	}

	public StatisticsCaseSubAttribute getSelectedColumnsSubAttribute() {
		return selectedColumnsSubAttribute;
	}

}