package de.symeda.sormas.ui.statistics;

import java.util.Arrays;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationChartType;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationMapType;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsVisualizationComponent extends HorizontalLayout {

	private StatisticsVisualizationType visualizationType;
	private StatisticsVisualizationMapType visualizationMapType;
	private StatisticsVisualizationChartType visualizationChartType;

	private final OptionGroup visualizationSelect;
	private final OptionGroup visualizationMapSelect;
	private final OptionGroup visualizationChartSelect;
	private StatisticsVisualizationElement rowsElement;
	private StatisticsVisualizationElement columnsElement;
	private Button switchRowsAndColumnsButton;

	public StatisticsVisualizationComponent() {
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);

		visualizationSelect = new OptionGroup("Type", Arrays.asList(StatisticsVisualizationType.values()));
		visualizationSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationType = (StatisticsVisualizationType) event.getProperty().getValue();
				updateComponentVisibility();
				if (rowsElement != null) {
					rowsElement.setType(rowsElement.getType(), visualizationType);
				}
				if (columnsElement != null) {
					columnsElement.setType(columnsElement.getType(), visualizationType);
				}
			}
		});
		CssStyles.style(visualizationSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL,
				CssStyles.SOFT_REQUIRED);
		visualizationSelect.setNullSelectionAllowed(false);
		addComponent(visualizationSelect);
		setExpandRatio(visualizationSelect, 0);

		visualizationMapSelect = new OptionGroup("Map type", Arrays.asList(StatisticsVisualizationMapType.values()));
		visualizationMapSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationMapType = (StatisticsVisualizationMapType) event.getProperty().getValue();
			}
		});
		CssStyles.style(visualizationMapSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL,
				CssStyles.SOFT_REQUIRED);
		visualizationMapSelect.setNullSelectionAllowed(false);
		addComponent(visualizationMapSelect);
		setExpandRatio(visualizationSelect, 0);

		visualizationChartSelect = new OptionGroup("Chart type",
				Arrays.asList(StatisticsVisualizationChartType.values()));
		visualizationChartSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				visualizationChartType = (StatisticsVisualizationChartType) event.getProperty().getValue();
				updateComponentVisibility();
			}
		});
		CssStyles.style(visualizationChartSelect, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL,
				CssStyles.SOFT_REQUIRED);
		visualizationChartSelect.setNullSelectionAllowed(false);
		addComponent(visualizationChartSelect);
		setExpandRatio(visualizationChartSelect, 0);

		rowsElement = new StatisticsVisualizationElement(StatisticsVisualizationElementType.ROWS, visualizationType);
		addComponent(rowsElement);
		setExpandRatio(rowsElement, 0);

		switchRowsAndColumnsButton = new Button();
		CssStyles.style(switchRowsAndColumnsButton, CssStyles.FORCE_CAPTION);
		switchRowsAndColumnsButton.setIcon(FontAwesome.EXCHANGE);
		switchRowsAndColumnsButton.setDescription("Exchange rows and columns");
		switchRowsAndColumnsButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				StatisticsVisualizationElement newRowsElement = columnsElement;
				newRowsElement.setType(StatisticsVisualizationElementType.ROWS, visualizationType);
				StatisticsVisualizationElement newColumnsElement = rowsElement;
				newColumnsElement.setType(StatisticsVisualizationElementType.COLUMNS, visualizationType);
				removeComponent(rowsElement);
				removeComponent(columnsElement);
				addComponent(newRowsElement, getComponentIndex(switchRowsAndColumnsButton));
				addComponent(newColumnsElement, getComponentIndex(switchRowsAndColumnsButton) + 1);
				replaceComponent(rowsElement, newRowsElement);
				replaceComponent(columnsElement, newColumnsElement);		
				rowsElement = newRowsElement;
				columnsElement = newColumnsElement;
			}
		});
		addComponent(switchRowsAndColumnsButton);
		setExpandRatio(switchRowsAndColumnsButton, 0);

		columnsElement = new StatisticsVisualizationElement(StatisticsVisualizationElementType.COLUMNS, visualizationType);
		addComponent(columnsElement);
		setExpandRatio(columnsElement, 0);

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

		rowsElement.setVisible(visualizationType == StatisticsVisualizationType.TABLE
				|| visualizationType == StatisticsVisualizationType.CHART);

		switchRowsAndColumnsButton.setVisible(visualizationType == StatisticsVisualizationType.TABLE
				|| (visualizationType == StatisticsVisualizationType.CHART
				&& visualizationChartType != StatisticsVisualizationChartType.PIE));

		columnsElement.setVisible(visualizationType == StatisticsVisualizationType.TABLE
				|| (visualizationType == StatisticsVisualizationType.CHART
				&& visualizationChartType != StatisticsVisualizationChartType.PIE));
	}

	public StatisticsCaseAttribute getRowsAttribute() {
		switch (visualizationType) {
		case MAP:
			return StatisticsCaseAttribute.REGION_DISTRICT;
		default:
			break;
		}
		return rowsElement.getAttribute();
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
		default:
			break;
		}
		return rowsElement.getSubAttribute();
	}

	public StatisticsCaseAttribute getColumnsAttribute() {
		switch (visualizationType) {
		case MAP:
			return null;
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
		return columnsElement.getAttribute();
	}

	public StatisticsCaseSubAttribute getColumnsSubAttribute() {
		switch (visualizationType) {
		case MAP:
			return null;
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
		return columnsElement.getSubAttribute();
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

}