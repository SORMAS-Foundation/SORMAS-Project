package de.symeda.sormas.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.caze.CasesView;
import de.symeda.sormas.ui.contact.ContactsView;
import de.symeda.sormas.ui.dashboard.DashboardView;
import de.symeda.sormas.ui.events.EventsView;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.task.TasksView;
import de.symeda.sormas.ui.user.UsersView;

/**
 * Content of the UI when the user is logged in.
 * 
 * 
 */
@SuppressWarnings("serial")
public class MainScreen extends HorizontalLayout {
    private Menu menu;

    public MainScreen(SormasUI ui) {

        setStyleName("main-screen");

        CssLayout viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        final Navigator navigator = new Navigator(ui, viewContainer);
        navigator.setErrorView(ErrorView.class);
        
        ControllerProvider.getCaseController().registerViews(navigator);
        ControllerProvider.getContactController().registerViews(navigator);
        ControllerProvider.getEventController().registerViews(navigator);
        ControllerProvider.getSampleController().registerViews(navigator);
        
        menu = new Menu(navigator);
        menu.addView(DashboardView.class, DashboardView.VIEW_NAME, "Dashboard", FontAwesome.DASHBOARD);
        menu.addView(TasksView.class, TasksView.VIEW_NAME, "Tasks", FontAwesome.TASKS);
        if (!LoginHelper.getCurrentUser().getUserRoles().contains(UserRole.LAB_USER)) {
        	menu.addView(CasesView.class, CasesView.VIEW_NAME, "Cases", FontAwesome.EDIT);
	        menu.addView(ContactsView.class, ContactsView.VIEW_NAME, "Contacts", FontAwesome.HAND_PAPER_O);
	        menu.addView(EventsView.class, EventsView.VIEW_NAME, "Alerts", FontAwesome.PHONE);
        }
        menu.addView(SamplesView.class, SamplesView.VIEW_NAME, "Samples", FontAwesome.DATABASE);
        menu.addView(UsersView.class, UsersView.VIEW_NAME, "Users", FontAwesome.USERS);
        menu.addView(AboutView.class, AboutView.VIEW_NAME, "About", FontAwesome.INFO_CIRCLE);
        
        navigator.addViewChangeListener(viewChangeListener);        
        
        ui.setNavigator(navigator);

        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
    }

    // notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
        	if (event.getViewName().isEmpty()) {
        		// redirect to default view
        		SormasUI.get().getNavigator().navigateTo(DashboardView.VIEW_NAME);
        		return false;
        	}
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }
    };
}
