package de.symeda.sormas.ui.statistics;

import java.util.Arrays;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseAttributeGroup;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsDisplayedAttributesElement extends HorizontalLayout {

	private static final String SPECIFY_YOUR_SELECTION = "Specify your selection";

	private StatisticsVisualizationType selectedVisualizationType;
	private StatisticsCaseAttribute selectedRowsAttribute;
	private StatisticsCaseSubAttribute selectedRowsSubAttribute;
	private StatisticsCaseAttribute selectedColumnsAttribute;
	private StatisticsCaseSubAttribute selectedColumnsSubAttribute;

	private final OptionGroup visualizationSelect;
	private HorizontalLayout rowsLayout;
	private HorizontalLayout columnsLayout;

	public StatisticsDisplayedAttributesElement() {
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);

		visualizationSelect = new OptionGroup("Type", Arrays.asList(StatisticsVisualizationType.values()));
		visualizationSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				setSelectedVisualizationType((StatisticsVisualizationType)event.getProperty().getValue());
			}
		});
		CssStyles.style(visualizationSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
		visualizationSelect.setNullSelectionAllowed(false);
		addComponent(visualizationSelect);
		setExpandRatio(visualizationSelect, 0);
		
		rowsLayout = createRowsOrColumnsLayout(true);
		addComponent(rowsLayout);
		setExpandRatio(rowsLayout, 0);
		
		columnsLayout = createRowsOrColumnsLayout(false);
		addComponent(columnsLayout);
		setExpandRatio(columnsLayout, 1);
		
		visualizationSelect.setValue(StatisticsVisualizationType.TABLE);
	}
	
	private void setSelectedVisualizationType(StatisticsVisualizationType value) {
		if (selectedVisualizationType == value) {
			return;
		}
		selectedVisualizationType = value;
		visualizationSelect.setValue(selectedVisualizationType);
		switch (selectedVisualizationType) {
		case REGIONS_MAP:
		case DISTRICTS_MAP:
			rowsLayout.setVisible(false);
			columnsLayout.setVisible(false);
			break;
		default:
			rowsLayout.setVisible(true);
			columnsLayout.setVisible(true);
			break;
		}
	}


	private HorizontalLayout createRowsOrColumnsLayout(boolean rows) {
		HorizontalLayout rowsOrColumnsLayout = new HorizontalLayout();
		CssStyles.style(rowsOrColumnsLayout, CssStyles.LAYOUT_MINIMAL);
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

	public StatisticsVisualizationType getSelectedVisualizationType() {
		return selectedVisualizationType;
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