package de.symeda.sormas.ui.surveillance;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Case navigation menu presenting a list of available views to the user.
 */
@SuppressWarnings("serial")
public class CaseEditMenu extends CssLayout {

    private Map<String, Button> viewButtons = new HashMap<String, Button>();

    private HorizontalLayout menuItemsLayout;
    
    public CaseEditMenu() {
        menuItemsLayout = new HorizontalLayout();
        menuItemsLayout.setSpacing(true);
        menuItemsLayout.setWidth("100%");
        addComponent(menuItemsLayout);

    }

    public void addView(final String name, String caption) {
    	addView(name, caption, null);;
    }
    
    public void addView(final String name, String caption, String caseUuid) {
    	String target = name+(caseUuid!=null&&!caseUuid.isEmpty()?"/"+caseUuid:"");
    	
    	Button button = new Button(caption, e -> SurveillanceUI.get().getNavigator().navigateTo(target));
    	button.setPrimaryStyleName(ValoTheme.BUTTON_LINK);
    	
    	menuItemsLayout.addComponent(button);
    	viewButtons.put(name, button);
    }
    
    public void removeAllViews() {
    	menuItemsLayout.removeAllComponents();
    	viewButtons.clear();
    }

    /**
     * Highlights a view navigation button as the currently active view in the
     * menu. This method does not perform the actual navigation.
     *
     * @param viewName
     *            the name of the view to show as active
     */
    public void setActiveView(String viewName) {
        for (Button button : viewButtons.values()) {
            button.removeStyleName("selected");
        }
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
    }
    
    
    
    public void updateLinkTarget(String name, String uuid) {
    	removeAllClicklisteners(viewButtons.get(name));
    	viewButtons.get(name).addClickListener(e -> SurveillanceUI.get().getNavigator().navigateTo(name+"/"+uuid));
    }

	private void removeAllClicklisteners(Button button) {
		for (Object listener : button.getListeners(ClickEvent.class)) {
    		button.removeClickListener((ClickListener) listener);
		}
	}

}
