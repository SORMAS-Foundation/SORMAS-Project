package de.symeda.sormas.ui.utils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.I18nProperties;

public abstract class AbstractView extends VerticalLayout implements View {

	private static final long serialVersionUID = -1L;
	
    protected final String viewName;
	private final HorizontalLayout viewHeader;
	private final Label viewTitleLabel;

    protected AbstractView(String viewName) {
        this.viewName = viewName;
        
        setSizeFull();
        setMargin(false);
        setSpacing(false);
        
        viewHeader = new HorizontalLayout();
        viewHeader.setWidth(100, Unit.PERCENTAGE);
        viewHeader.setHeightUndefined();
        viewHeader.setMargin(new MarginInfo(false, true));
        viewHeader.setSpacing(true);
        CssStyles.style(viewHeader, "view-header");

        VerticalLayout viewTitleLayout = new VerticalLayout();
        {
	        viewTitleLayout.setSizeUndefined();
	        viewTitleLayout.setSpacing(false);
	
	        // note: splitting title and subtitle into labels does not work with the css
	        String viewTitle = I18nProperties.getPrefixFragment("View", viewName.replaceAll("/", "."));
	        String viewSubTitle = I18nProperties.getPrefixFragment("View", viewName.replaceAll("/", ".") + ".sub", "");
	        viewTitleLabel = new Label(viewTitle);
	        viewTitleLabel.setSizeUndefined();
	        CssStyles.style(viewTitleLabel, CssStyles.H1, CssStyles.VSPACE_NONE);
	        viewTitleLayout.addComponent(viewTitleLabel);
	        Label viewSubTitleLabel = new Label(viewSubTitle);
	        viewSubTitleLabel.setSizeUndefined();
	        CssStyles.style(viewSubTitleLabel, CssStyles.H4, CssStyles.VSPACE_TOP_NONE);
	        viewTitleLayout.addComponent(viewSubTitleLabel);
        }
        viewHeader.addComponent(viewTitleLayout);
        viewHeader.setExpandRatio(viewTitleLayout, 1);

        addComponent(viewHeader);
        setExpandRatio(viewHeader, 0);
    }
    
    protected void addHeaderComponent(Component c) {
        viewHeader.addComponent(c);
        viewHeader.setComponentAlignment(c, Alignment.MIDDLE_RIGHT);
    }
    
    @Override
    public void addComponent(Component c) {
    	super.addComponent(c);
    	// set expansion to 1 by default
    	setExpandRatio(c, 1);
    }
    
    @Override
    public abstract void enter(ViewChangeEvent event);
    
    public Label getViewTitleLabel() {
    	return viewTitleLabel;
    }
}
