package de.symeda.sormas.ui.statistics;

import java.util.Arrays;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseAttributeGroup;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationChartType;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationMapType;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsDisplayedAttributesElement extends HorizontalLayout {

	private static final String SPECIFY_YOUR_SELECTION = "Specify your selection";

	private StatisticsVisualizationType visualizationType;
	private StatisticsVisualizationMapType visualizationMapType;
	private StatisticsVisualizationChartType visualizationChartType;
	private StatisticsCaseAttribute rowsAttribute;
	private StatisticsCaseSubAttribute rowsSubAttribute;
	private StatisticsCaseAttribute columnsAttribute;
	private StatisticsCaseSubAttribute columnsSubAttribute;

	private final OptionGroup visualizationSelect;
	private final OptionGroup visualizationMapSelect;
	private final OptionGroup visualizationChartSelect;
	private HorizontalLayout rowsLayout;
	private HorizontalLayout columnsLayout;
//	private Button switchRowsAndColumnsButton;

	public StatisticsDisplayedAttributesElement() {
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);

		visualizationSelect = new OptionGroup("Type", Arrays.asList(StatisticsVisualizationType.values()));
		visualizationSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationType = (StatisticsVisualizationType)event.getProperty().getValue();
				updateComponentVisibility();
			}
		});
		CssStyles.style(visualizationSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
		visualizationSelect.setNullSelectionAllowed(false);
		addComponent(visualizationSelect);
		setExpandRatio(visualizationSelect, 0);
		
		visualizationMapSelect = new OptionGroup("Map type", Arrays.asList(StatisticsVisualizationMapType.values()));
		visualizationMapSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationMapType = (StatisticsVisualizationMapType)event.getProperty().getValue();
			}
		});
		CssStyles.style(visualizationMapSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
		visualizationMapSelect.setNullSelectionAllowed(false);
		addComponent(visualizationMapSelect);
		setExpandRatio(visualizationSelect, 0);
		
		visualizationChartSelect = new OptionGroup("Type", Arrays.asList(StatisticsVisualizationChartType.values()));
		visualizationChartSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationChartType = (StatisticsVisualizationChartType)event.getProperty().getValue();
				updateComponentVisibility();
			}
		});
		CssStyles.style(visualizationChartSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
		visualizationChartSelect.setNullSelectionAllowed(false);
		addComponent(visualizationChartSelect);
		setExpandRatio(visualizationChartSelect, 0);
		
		rowsLayout = createRowsOrColumnsLayout(true);
		addComponent(rowsLayout);
		setExpandRatio(rowsLayout, 0);
		
		columnsLayout = createRowsOrColumnsLayout(false);
		addComponent(columnsLayout);
		setExpandRatio(columnsLayout, 0);
		
//		switchRowsAndColumnsButton = new Button();
//		switchRowsAndColumnsButton.setIcon(FontAwesome.EXCHANGE);
//		switchRowsAndColumnsButton.setDescription("Exchange rows and columns");
//		switchRowsAndColumnsButton.addClickListener(new ClickListener() {
//			@Override
//			public void buttonClick(ClickEvent event) {
//				// TODO
//			}
//		});
//		addComponent(switchRowsAndColumnsButton);
//		setExpandRatio(switchRowsAndColumnsButton, 0);
		
		Label spacer = new Label();
		addComponent(spacer);
		setExpandRatio(spacer, 1);
		
		visualizationSelect.setValue(StatisticsVisualizationType.TABLE);
		visualizationChartSelect.setValue(StatisticsVisualizationChartType.STACKED_COLUMN);
		visualizationMapSelect.setValue(StatisticsVisualizationMapType.REGIONS);
	}
	
	private void updateComponentVisibility() {
		
		visualizationMapSelect.setVisible(visualizationType == StatisticsVisualizationType.MAP);
		visualizationChartSelect.setVisible(visualizationType == StatisticsVisualizationType.CHART);
		
		rowsLayout.setVisible(visualizationType == StatisticsVisualizationType.TABLE
				|| (visualizationType == StatisticsVisualizationType.CHART 
				&& visualizationChartType != StatisticsVisualizationChartType.PIE));

		columnsLayout.setVisible(visualizationType == StatisticsVisualizationType.TABLE
				|| visualizationType == StatisticsVisualizationType.CHART);
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
						rowsAttribute = attribute;
						rowsSubAttribute = null;
					} else {
						columnsAttribute = attribute;
						columnsSubAttribute = null;
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
										rowsSubAttribute = subAttribute;
									} else {
										columnsSubAttribute = subAttribute;
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

	public StatisticsVisualizationType getVisualizationType() {
		return visualizationType;
	}

	public StatisticsVisualizationMapType getVisualizationMapType() {
		return visualizationMapType;
	}

	public StatisticsVisualizationChartType getVisualizationChartType() {
		return visualizationChartType;
	}

	public StatisticsCaseAttribute getRowsAttribute() {
		switch (visualizationType) {
		case MAP:
			return StatisticsCaseAttribute.REGION_DISTRICT;
		case CHART:
			switch (visualizationChartType) {
			case PIE:
				return null;
			default:
				break;
			}
		default:
			break;
		}
		return rowsAttribute;
	}

	public StatisticsCaseSubAttribute getRowsSubAttribute() {
		switch (visualizationType) {
		case MAP:
			switch (visualizationMapType) {
			case REGIONS:
				return StatisticsCaseSubAttribute.REGION;
			case DISTRICTS:
				return StatisticsCaseSubAttribute.DISTRICT;
			default:
				throw new IllegalArgumentException(visualizationMapType.toString());
			}
		case CHART:
			switch (visualizationChartType) {
			case PIE:
				return null;
			default:
				break;
			}
		default:
			break;
		}
		return rowsSubAttribute;
	}

	public StatisticsCaseAttribute getColumnsAttribute() {
		switch (visualizationType) {
		case MAP:
			return null;			
		default:
			break;
		}
		return columnsAttribute;
	}

	public StatisticsCaseSubAttribute getColumnsSubAttribute() {
		switch (visualizationType) {
		case MAP:
			return null;			
		default:
			break;
		}
		return columnsSubAttribute;
	}
}