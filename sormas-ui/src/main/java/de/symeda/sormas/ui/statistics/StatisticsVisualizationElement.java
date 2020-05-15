/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.statistics;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
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
		displayedAttributeDropdown.setId("visualizationType");
		displayedAttributeDropdown.setCaption(type.toString(visualizationType));
		displayedAttributeItem = displayedAttributeDropdown.addItem(type.getEmptySelectionString(visualizationType), null);
		
		displayedSubAttributeDropdown = new MenuBar();
		displayedSubAttributeDropdown.setId("displayedSubAttribute");
		CssStyles.style(displayedSubAttributeDropdown, CssStyles.FORCE_CAPTION);
		displayedSubAttributeItem = displayedSubAttributeDropdown.addItem(I18nProperties.getCaption(Captions.statisticsSpecifySelection), null);
		
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
			childItem.setStyleName(null);
		}
	}
	
	private void resetSubAttributeDropdown() {
		displayedSubAttributeItem.removeChildren();
		displayedSubAttributeItem.setText(I18nProperties.getCaption(Captions.statisticsSpecifySelection));
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
