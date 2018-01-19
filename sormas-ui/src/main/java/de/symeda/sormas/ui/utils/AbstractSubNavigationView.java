package de.symeda.sormas.ui.utils;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.SubNavigationMenu;

@SuppressWarnings("serial")
public abstract class AbstractSubNavigationView extends AbstractView {
	
    private String params;

    private SubNavigationMenu subNavigationMenu;
    private Label infoLabel;
    private Label infoLabelSub;
    private Component subComponent;

    protected AbstractSubNavigationView(String viewName) {
        super(viewName);

        subNavigationMenu = new SubNavigationMenu();
        addComponent(subNavigationMenu);
        setExpandRatio(subNavigationMenu, 0);
        
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSizeUndefined();
        CssStyles.stylePrimary(infoLayout, CssStyles.CALLOUT);
        infoLabel = new Label("");
        infoLabelSub = new Label("");
        CssStyles.style(infoLabelSub, ValoTheme.LABEL_SMALL);
        infoLayout.addComponent(infoLabel);
        infoLayout.addComponent(infoLabelSub);
        addHeaderComponent(infoLayout);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
    	params = event.getParameters();
    	refreshMenu(subNavigationMenu, infoLabel, infoLabelSub, params);
		selectInMenu();
    }
    
    public abstract void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params);
    
    protected void setSubComponent(Component newComponent) {
    	if (subComponent != null) {
    		removeComponent(subComponent);
    	}
    	subComponent = newComponent;
    	if (subComponent != null) {
    		// Make sure that the sub component is always the first component below the navigation
	    	addComponent(subComponent, 2);
	    	setExpandRatio(subComponent, 1);
    	}
    }

    public void selectInMenu() {
    	subNavigationMenu.setActiveView(viewName);
    }
}
