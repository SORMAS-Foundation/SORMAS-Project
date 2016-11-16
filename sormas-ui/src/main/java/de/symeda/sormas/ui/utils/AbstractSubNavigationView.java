package de.symeda.sormas.ui.utils;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public abstract class AbstractSubNavigationView extends AbstractView {

    protected final String viewName;

    private SubNavigationMenu subNavigationMenu;
    private CssLayout editLayout;
	private String entityUuid;

    protected AbstractSubNavigationView(String viewName) {
        setWidth(900, Unit.PIXELS);
        setHeight(100, Unit.PERCENTAGE);
        setMargin(true);
        this.viewName = viewName;
        
        subNavigationMenu = new SubNavigationMenu();
    	addComponent(subNavigationMenu);
    	setExpandRatio(subNavigationMenu, 0);
        
        editLayout = new CssLayout();
        editLayout.setWidth(100, Unit.PERCENTAGE);
        editLayout.setHeightUndefined();
        addComponent(editLayout);
    	setExpandRatio(editLayout, 1);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
    	entityUuid = event.getParameters();
    	refreshMenu(subNavigationMenu, entityUuid);
		selectInMenu();
    };
    
    public abstract void refreshMenu(SubNavigationMenu menu, String entityUuid);
    
    protected String getEntityUuid() {
		return entityUuid;
	}
    
    protected void setEditComponent(Component newComponent) {
    	editLayout.removeAllComponents();
    	editLayout.addComponent(newComponent);
    }

    public void selectInMenu() {
    	subNavigationMenu.setActiveView(viewName);
    }
}
