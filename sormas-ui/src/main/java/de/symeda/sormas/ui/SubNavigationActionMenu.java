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
package de.symeda.sormas.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Link;

/**
 * A sub navigation menu presenting a list of options.
 * Options are bound to a click listener to take an action
 */
@SuppressWarnings("serial")
public class SubNavigationActionMenu extends CssLayout {

    private Map<String, AbstractComponent> viewMenuItemMap = new HashMap<String, AbstractComponent>();

    private CssLayout menuItemsLayout;
    
    public SubNavigationActionMenu() {
        setWidth(100, Unit.PERCENTAGE);
        setHeightUndefined();
        setPrimaryStyleName("v-tabsheet");

        menuItemsLayout = new CssLayout();
        menuItemsLayout.setPrimaryStyleName("v-tabsheet-tabcontainer");
        menuItemsLayout.setWidth(100, Unit.PERCENTAGE);
        menuItemsLayout.setHeightUndefined();
        addComponent(menuItemsLayout);
    }

    public void addView(final String name, String caption) {
    	addView(name, caption, null);
    }
    
    public void addView(final String name, String caption, LayoutClickListener onClick) {
    	CssLayout tabItemCell = new CssLayout();
    	tabItemCell.setSizeUndefined();
    	tabItemCell.setPrimaryStyleName("v-tabsheet-tabitemcell");

    	CssLayout tabItem = new CssLayout();
    	tabItem.setSizeUndefined();
    	tabItem.setPrimaryStyleName("v-tabsheet-tabitem");
    	tabItemCell.addComponent(tabItem);

    	Link link = new Link(caption, null);
    	link.addStyleName("v-caption");
    	
    	tabItem.addComponent(link);
    	tabItem.addLayoutClickListener(onClick);
    	tabItem.addLayoutClickListener((e) -> {
    		this.setActiveView(name);
    	});
    	
    	menuItemsLayout.addComponent(tabItemCell);
    	viewMenuItemMap.put(name, tabItem);
    }
    
    public void removeAllViews() {
    	menuItemsLayout.removeAllComponents();
    	viewMenuItemMap.clear();
    }

    /**
     * Highlights a view navigation button as the currently active view in the
     * menu. This method does not perform the actual navigation.
     *
     * @param viewName
     *            the name of the view to show as active
     */
    public void setActiveView(String viewName) {
        for (AbstractComponent button : viewMenuItemMap.values()) {
            button.removeStyleName("selected");
        }
        AbstractComponent selected = viewMenuItemMap.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
    }
}
