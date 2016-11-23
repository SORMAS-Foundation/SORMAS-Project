package de.symeda.sormas.ui.utils;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;

import de.symeda.sormas.ui.SubNavigationMenu;

@SuppressWarnings("serial")
public abstract class AbstractSubNavigationView extends AbstractView {

    protected final String viewName;

    private SubNavigationMenu subNavigationMenu;
    private Component subComponent;
    private String params;

    protected AbstractSubNavigationView(String viewName) {
        setWidth(900, Unit.PIXELS);
        setHeight(100, Unit.PERCENTAGE);
        setMargin(true);
        this.viewName = viewName;
        
        subNavigationMenu = new SubNavigationMenu();
    	addComponent(subNavigationMenu);
    	setExpandRatio(subNavigationMenu, 0);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
    	params = event.getParameters();
    	refreshMenu(subNavigationMenu, params);
		selectInMenu();
    };
    
    public abstract void refreshMenu(SubNavigationMenu menu, String params);
    
    protected void setSubComponent(Component newComponent) {
    	if (subComponent != null) {
    		removeComponent(subComponent);
    	}
    	subComponent = newComponent;
    	if (subComponent != null) {
	    	addComponent(subComponent);
	    	setExpandRatio(subComponent, 1);
    	}
    }

    public void selectInMenu() {
    	subNavigationMenu.setActiveView(viewName);
    }
}
