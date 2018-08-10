package de.symeda.sormas.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.caze.CasesView;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.configuration.HealthFacilitiesView;
import de.symeda.sormas.ui.configuration.OutbreaksView;
import de.symeda.sormas.ui.contact.ContactsView;
import de.symeda.sormas.ui.dashboard.DashboardView;
import de.symeda.sormas.ui.events.EventsView;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.reports.ReportsView;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.statistics.StatisticsView;
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

		CssLayout viewContainer = new CssLayout();
		viewContainer.setSizeFull();
		viewContainer.addStyleName("sormas-content");

		final Navigator navigator = new Navigator(ui, viewContainer);
		navigator.setErrorProvider(new ViewProvider() {
			@Override
			public String getViewName(String viewAndParameters) {
				return viewAndParameters;
			}

			@Override
			public View getView(String viewName) {
				try {
					// Add new views to this clause to make sure that the right error page is shown
					if (viewName.equals(DashboardView.VIEW_NAME) || viewName.equals(TasksView.VIEW_NAME)
							|| viewName.equals(CasesView.VIEW_NAME) || viewName.equals(ContactsView.VIEW_NAME)
							|| viewName.equals(EventsView.VIEW_NAME) || viewName.equals(SamplesView.VIEW_NAME)
							|| viewName.equals(ReportsView.VIEW_NAME) || viewName.equals(StatisticsView.VIEW_NAME)
							|| viewName.equals(UsersView.VIEW_NAME) || viewName.equals(OutbreaksView.VIEW_NAME)) {
						return AccessDeniedView.class.newInstance();
					} else {
						return ErrorView.class.newInstance();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

		menu = new Menu(navigator);
		menu.addView(DashboardView.class, DashboardView.VIEW_NAME, "Dashboard", FontAwesome.DASHBOARD);
		if (LoginHelper.hasUserRight(UserRight.TASK_VIEW)) {
			menu.addView(TasksView.class, TasksView.VIEW_NAME, "Tasks", FontAwesome.TASKS);
		}
		if (LoginHelper.hasUserRight(UserRight.CASE_VIEW)) {
			ControllerProvider.getCaseController().registerViews(navigator);
			menu.addView(CasesView.class, CasesView.VIEW_NAME, "Cases", FontAwesome.EDIT);
		}
		if (LoginHelper.hasUserRight(UserRight.CONTACT_VIEW)) {
			ControllerProvider.getContactController().registerViews(navigator);
			menu.addView(ContactsView.class, ContactsView.VIEW_NAME, "Contacts", FontAwesome.HAND_PAPER_O);
		}
		if (LoginHelper.hasUserRight(UserRight.EVENT_VIEW)) {
			ControllerProvider.getEventController().registerViews(navigator);
			menu.addView(EventsView.class, EventsView.VIEW_NAME, "Events", FontAwesome.PHONE);
		}
		if (LoginHelper.hasUserRight(UserRight.SAMPLE_VIEW)) {
			ControllerProvider.getSampleController().registerViews(navigator);
			menu.addView(SamplesView.class, SamplesView.VIEW_NAME, "Samples", FontAwesome.DATABASE);
		}
		if (LoginHelper.hasUserRight(UserRight.WEEKLYREPORT_VIEW)) {
			menu.addView(ReportsView.class, ReportsView.VIEW_NAME, "Reports", FontAwesome.FILE_TEXT);
		}
		ControllerProvider.getStatisticsController().registerViews(navigator);
		menu.addView(StatisticsView.class, StatisticsView.VIEW_NAME, "Statistics", FontAwesome.BAR_CHART);
		if (LoginHelper.hasUserRight(UserRight.USER_VIEW)) {
			menu.addView(UsersView.class, UsersView.VIEW_NAME, "Users", FontAwesome.USERS);
		}
		if (LoginHelper.hasUserRight(UserRight.CONFIGURATION_ACCESS)) {
			AbstractConfigurationView.registerViews(navigator);
			if (LoginHelper.hasUserRight(UserRight.FACILITIES_VIEW)) {
				menu.addView(HealthFacilitiesView.class, HealthFacilitiesView.VIEW_NAME, "Configuration",
						FontAwesome.COGS);
			}else {
				menu.addView(OutbreaksView.class, OutbreaksView.VIEW_NAME, "Configuration",
						FontAwesome.COGS);
			}
		}
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
