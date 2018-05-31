package de.symeda.sormas.ui.statistics;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseAttributeGroup;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsFilterComponent extends VerticalLayout {

	private static final String SPECIFY_YOUR_SELECTION = "Specify your selection";

	private HorizontalLayout filterValuesLayout;
	private StatisticsCaseAttribute selectedAttribute;
	private StatisticsCaseSubAttribute selectedSubAttribute;
	private Map<Object, StatisticsFilterElement> filterElements = new HashMap<>();

	public StatisticsFilterComponent() {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		addComponent(createFilterAttributeElement());

		filterValuesLayout = new HorizontalLayout();
		filterValuesLayout.setSpacing(true);
		filterValuesLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(filterValuesLayout);
	}

	private HorizontalLayout createFilterAttributeElement() {
		HorizontalLayout filterAttributeLayout = new HorizontalLayout();
		filterAttributeLayout.setSpacing(true);
		filterAttributeLayout.setWidth(100, Unit.PERCENTAGE);

		MenuBar filterAttributeDropdown = new MenuBar();
		filterAttributeDropdown.setCaption("Attribute");
		MenuItem filterAttributeItem = filterAttributeDropdown.addItem("Select an attribute", null);
		MenuBar filterSubAttributeDropdown = new MenuBar();
		filterSubAttributeDropdown.setCaption("Attribute specification");
		MenuItem filterSubAttributeItem = filterSubAttributeDropdown.addItem(SPECIFY_YOUR_SELECTION, null);

		// Add attribute groups
		for (StatisticsCaseAttributeGroup attributeGroup : StatisticsCaseAttributeGroup.values()) {
			MenuItem attributeGroupItem = filterAttributeItem.addItem(attributeGroup.toString(), null);
			attributeGroupItem.setEnabled(false);

			// Add attributes belonging to the current group
			for (StatisticsCaseAttribute attribute : attributeGroup.getAttributes()) {
				Command attributeCommand = selectedItem -> {
					filterValuesLayout.removeAllComponents();
					filterElements.clear();
					selectedAttribute = attribute;
					selectedSubAttribute = null;
					filterAttributeItem.setText(attribute.toString());
					
					// Add style to keep chosen item selected and remove it from all other items
					for (MenuItem menuItem : filterAttributeItem.getChildren()) {
						menuItem.setStyleName("");
					}
					selectedItem.setStyleName("selected-filter");
					
					// Reset the sub attribute dropdown
					filterSubAttributeItem.removeChildren();
					filterSubAttributeItem.setText(SPECIFY_YOUR_SELECTION);

					if (attribute.getSubAttributes().length > 0) {
						for (StatisticsCaseSubAttribute subAttribute : attribute.getSubAttributes()) {
							if (subAttribute.isUsedForFilters()) {
								Command subAttributeCommand = selectedSubItem -> {
									filterValuesLayout.removeAllComponents();
									filterElements.clear();
									selectedSubAttribute = subAttribute;
									filterSubAttributeItem.setText(subAttribute.toString());
									
									// Add style to keep chosen item selected and remove it from all other items
									for (MenuItem menuItem : filterSubAttributeItem.getChildren()) {
										menuItem.setStyleName("");
									}
									selectedSubItem.setStyleName("selected-filter");
									
									updateFilterValuesElements();
								};

								filterSubAttributeItem.addItem(subAttribute.toString(), subAttributeCommand);
							}
						}

						// Only add the sub attribute dropdown if there are any sub attributes that are relevant for the filters section
						if (filterSubAttributeItem.getChildren() != null && filterSubAttributeItem.getChildren().size() > 0) {
							filterAttributeLayout.addComponent(filterSubAttributeDropdown);
							filterAttributeLayout.setExpandRatio(filterSubAttributeDropdown, 1);
						} else {
							filterAttributeLayout.removeComponent(filterSubAttributeDropdown);
							updateFilterValuesElements();
						}
					} else {
						filterAttributeLayout.removeComponent(filterSubAttributeDropdown);
						updateFilterValuesElements();
					}
				};

				filterAttributeItem.addItem(attribute.toString(), attributeCommand);
			}
		}

		filterAttributeLayout.addComponent(filterAttributeDropdown);
		filterAttributeLayout.setExpandRatio(filterAttributeDropdown, 0);
		return filterAttributeLayout;
	}

	private HorizontalLayout updateFilterValuesElements() {		
		if (selectedSubAttribute == StatisticsCaseSubAttribute.DATE_RANGE) {
			StatisticsFilterDateRangeElement element = new StatisticsFilterDateRangeElement();
			filterElements.put(StatisticsCaseSubAttribute.DATE_RANGE, element);
			filterValuesLayout.addComponent(element);
		} else if (selectedAttribute == StatisticsCaseAttribute.REGION_DISTRICT) {
			StatisticsFilterRegionDistrictElement element = new StatisticsFilterRegionDistrictElement();
			filterElements.put(StatisticsCaseAttribute.REGION_DISTRICT, element);
			filterValuesLayout.addComponent(element);
		} else {
			StatisticsFilterValuesElement element = new StatisticsFilterValuesElement(
					selectedAttribute.toString() + (selectedSubAttribute != null ? " (" + selectedSubAttribute.toString() + ")" : ""), 
					selectedAttribute, selectedSubAttribute);
			filterElements.put(selectedSubAttribute != null ? selectedSubAttribute : selectedAttribute, element);
			filterValuesLayout.addComponent(element);
		}

		return filterValuesLayout;
	}

	public StatisticsCaseAttribute getSelectedAttribute() {
		return selectedAttribute;
	}

	public StatisticsCaseSubAttribute getSelectedSubAttribute() {
		return selectedSubAttribute;
	}

	public Map<Object, StatisticsFilterElement> getFilterElements() {
		return filterElements;
	}

}
