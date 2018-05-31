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
public class StatisticsVisualizationElement extends HorizontalLayout {

	private MenuBar displayedAttributeDropdown;
	private MenuBar displayedSubAttributeDropdown;
	private MenuItem displayedAttributeItem;
	private MenuItem emptySelectionItem;
	private MenuItem displayedSubAttributeItem;

	private StatisticsVisualizationElementType type;
	private StatisticsVisualizationType visualizationType;
	private StatisticsCaseAttribute attribute;
	private StatisticsCaseSubAttribute subAttribute;
	
	public StatisticsVisualizationElement(StatisticsVisualizationElementType type, StatisticsVisualizationType visualizationType) {
		this.type = type;
		this.visualizationType = visualizationType;
		
		CssStyles.style(this, CssStyles.LAYOUT_MINIMAL);
		setSpacing(true);
		setWidthUndefined();
		
		createAndAddComponents();
	}
	
	private void createAndAddComponents() {
		displayedAttributeDropdown = new MenuBar();
		displayedAttributeDropdown.setCaption(type.toString(visualizationType));
		displayedAttributeItem = displayedAttributeDropdown.addItem(type.getEmptySelectionString(visualizationType), null);
		
		displayedSubAttributeDropdown = new MenuBar();
		CssStyles.style(displayedSubAttributeDropdown, CssStyles.FORCE_CAPTION);
		displayedSubAttributeItem = displayedSubAttributeDropdown.addItem("Specify your selection", null);
		
		// Empty selections
		Command emptyItemCommand = selectedItem -> {
			attribute = null;
			subAttribute = null;
			resetSubAttributeDropdown();
			displayedAttributeItem.setText(type.getEmptySelectionString(visualizationType));
			removeSelections(displayedAttributeItem);
		};
		emptySelectionItem = displayedAttributeItem.addItem(type.getEmptySelectionString(visualizationType), emptyItemCommand);
		
		// Add attribute groups
		for (StatisticsCaseAttributeGroup attributeGroup : StatisticsCaseAttributeGroup.values()) {
			MenuItem attributeGroupItem = displayedAttributeItem.addItem(attributeGroup.toString(), null);
			attributeGroupItem.setEnabled(false);
			
			// Add attributes belonging to the current group
			for (StatisticsCaseAttribute attribute : attributeGroup.getAttributes()) {
				Command attributeCommand = selectedItem -> {
					resetSubAttributeDropdown();
					this.attribute = attribute;
					this.subAttribute = null;
					displayedAttributeItem.setText(attribute.toString());
					removeSelections(displayedAttributeItem);
					selectedItem.setStyleName("selected-filter");
					
					// Build sub attribute dropdown
					if (attribute.getSubAttributes().length > 0) {
						for (StatisticsCaseSubAttribute subAttribute : attribute.getSubAttributes()) {
							if (subAttribute.isUsedForGrouping()) {
								Command subAttributeCommand = selectedSubItem -> {
									this.subAttribute = subAttribute;
									displayedSubAttributeItem.setText(subAttribute.toString());
									removeSelections(displayedSubAttributeItem);
									selectedSubItem.setStyleName("selected-filter");
								};
								
								displayedSubAttributeItem.addItem(subAttribute.toString(), subAttributeCommand);
							}
						}
						
						addComponent(displayedSubAttributeDropdown);
					}
				};
				
				displayedAttributeItem.addItem(attribute.toString(), attributeCommand);
			}		
		}
		
		addComponent(displayedAttributeDropdown);
	}
	
	private void removeSelections(MenuItem parentItem) {
		for (MenuItem childItem : parentItem.getChildren()) {
			childItem.setStyleName("");
		}
	}
	
	private void resetSubAttributeDropdown() {
		displayedSubAttributeItem.removeChildren();
		displayedSubAttributeItem.setText("Specify your selection");
		removeComponent(displayedSubAttributeDropdown);
	}

	public StatisticsCaseAttribute getAttribute() {
		return attribute;
	}

	public StatisticsCaseSubAttribute getSubAttribute() {
		return subAttribute;
	}

	public StatisticsVisualizationElementType getType() {
		return type;
	}
	
	public void setType(StatisticsVisualizationElementType type, StatisticsVisualizationType visualizationType) {
		this.type = type;
		this.visualizationType = visualizationType;
		displayedAttributeDropdown.setCaption(type.toString(visualizationType));
		emptySelectionItem.setText(type.getEmptySelectionString(visualizationType));
		if (attribute == null && subAttribute == null) {
			displayedAttributeItem.setText(type.getEmptySelectionString(visualizationType));
		}
	}
	
}
