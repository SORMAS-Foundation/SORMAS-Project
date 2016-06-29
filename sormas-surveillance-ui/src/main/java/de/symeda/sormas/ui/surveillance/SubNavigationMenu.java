package de.symeda.sormas.ui.surveillance;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Link;

/**
 * A sub navigation menu presenting a list of available views to the user.
 * It emulates the HTML components of a tabsheet to use it's styling.
 */
@SuppressWarnings("serial")
public class SubNavigationMenu extends CssLayout {

    private Map<String, AbstractComponent> viewMenuItemMap = new HashMap<String, AbstractComponent>();

    private CssLayout menuItemsLayout;
    
    public SubNavigationMenu() {
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
    
    public void addView(final String name, String caption, String subItemUuid) {
    	String target = "#!" + name+(subItemUuid != null ? "/"+subItemUuid : "");

    	CssLayout tabItemCell = new CssLayout();
    	tabItemCell.setSizeUndefined();
    	tabItemCell.setPrimaryStyleName("v-tabsheet-tabitemcell");

    	CssLayout tabItem = new CssLayout();
    	tabItem.setSizeUndefined();
    	tabItem.setPrimaryStyleName("v-tabsheet-tabitem");
    	tabItemCell.addComponent(tabItem);

    	Link link = new Link(caption, new ExternalResource(target));
    	link.addStyleName("v-caption");
    	if (subItemUuid == null)
    		link.setIcon(FontAwesome.ARROW_CIRCLE_LEFT);
    	tabItem.addComponent(link);
    	
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
