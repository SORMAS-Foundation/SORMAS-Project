package de.symeda.sormas.ui.utils;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.ui.SubNavigationMenu;

@SuppressWarnings("serial")
public abstract class AbstractSubNavigationView extends AbstractView {
	
    private SubNavigationMenu subNavigationMenu;
    private Label infoLabel;
    private Label infoLabelSub;
    private OptionGroup viewModeToggle;
    private Component subComponent;
    private String params;

    protected AbstractSubNavigationView(String viewName) {
        super(viewName);

        subNavigationMenu = new SubNavigationMenu();
        addComponent(subNavigationMenu);
        setExpandRatio(subNavigationMenu, 0);
        
        viewModeToggle = new OptionGroup();
        CssStyles.style(viewModeToggle, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY, CssStyles.VSPACE_TOP_3, CssStyles.HSPACE_RIGHT_3);
        viewModeToggle.addItems((Object[]) ViewMode.values());
        viewModeToggle.setItemCaption(ViewMode.OUTBREAK, I18nProperties.getEnumCaption(ViewMode.OUTBREAK));
        viewModeToggle.setItemCaption(ViewMode.NORMAL, I18nProperties.getEnumCaption(ViewMode.NORMAL));
        viewModeToggle.setValue(ViewMode.OUTBREAK);
        // View mode toggle is hidden by default
        viewModeToggle.setVisible(false);
        addHeaderComponent(viewModeToggle);
        
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
    	refreshMenu(subNavigationMenu, infoLabel, infoLabelSub, viewModeToggle, params);
		selectInMenu();
    }
    
    public abstract void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, OptionGroup viewModeToggle, String params);
    
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
