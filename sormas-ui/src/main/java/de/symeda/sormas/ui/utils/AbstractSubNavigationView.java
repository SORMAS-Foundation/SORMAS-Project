package de.symeda.sormas.ui.utils;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.SubNavigationMenu;

@SuppressWarnings("serial")
public abstract class AbstractSubNavigationView extends AbstractView {

    protected final String viewName;

    private SubNavigationMenu subNavigationMenu;
    private Label infoLabel;
    private Label infoLabelSub;
    private Component subComponent;
    private String params;

    protected AbstractSubNavigationView(String viewName) {
        setWidth(100, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);
        setMargin(true);
        this.viewName = viewName;

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setSpacing(true);
        
        subNavigationMenu = new SubNavigationMenu();
        layout.addComponent(subNavigationMenu);
        
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSizeUndefined();
        CssStyles.stylePrimary(vLayout, CssStyles.CALLOUT);
        
        infoLabel = new Label("");
        infoLabelSub = new Label("");
        CssStyles.style(infoLabelSub, CssStyles.LABEL_SMALL);
        
        vLayout.addComponent(infoLabel);
        vLayout.addComponent(infoLabelSub);
        layout.addComponent(vLayout);
        layout.setComponentAlignment(vLayout, Alignment.MIDDLE_RIGHT);
        
        layout.setExpandRatio(subNavigationMenu, 1);
        layout.setExpandRatio(vLayout, 0);
        
        addComponent(layout);
    }
    
    @Override
    public void enter(ViewChangeEvent event) {
    	params = event.getParameters();
    	refreshMenu(subNavigationMenu, infoLabel, infoLabelSub, params);
		selectInMenu();
    };
    
    public abstract void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params);
    
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
