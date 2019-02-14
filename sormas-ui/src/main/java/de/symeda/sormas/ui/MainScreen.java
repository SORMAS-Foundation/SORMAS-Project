/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui;

import java.util.Collection;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.caze.CasesView;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.configuration.infrastructure.CommunitiesView;
import de.symeda.sormas.ui.configuration.infrastructure.DistrictsView;
import de.symeda.sormas.ui.configuration.infrastructure.HealthFacilitiesView;
import de.symeda.sormas.ui.configuration.infrastructure.LaboratoriesView;
import de.symeda.sormas.ui.configuration.infrastructure.RegionsView;
import de.symeda.sormas.ui.configuration.outbreak.OutbreaksView;
import de.symeda.sormas.ui.configuration.userrights.UserRightsView;
import de.symeda.sormas.ui.contact.ContactsView;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.contacts.DashboardContactsView;
import de.symeda.sormas.ui.dashboard.surveillance.DashboardSurveillanceView;
import de.symeda.sormas.ui.events.EventsView;
import de.symeda.sormas.ui.reports.ReportsView;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.statistics.AbstractStatisticsView;
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
					if (viewName.equals(DashboardSurveillanceView.VIEW_NAME) || viewName.equals(DashboardContactsView.VIEW_NAME) 
							|| viewName.equals(TasksView.VIEW_NAME) || viewName.equals(CasesView.VIEW_NAME)
							|| viewName.equals(ContactsView.VIEW_NAME) || viewName.equals(EventsView.VIEW_NAME)
							|| viewName.equals(SamplesView.VIEW_NAME) || viewName.equals(ReportsView.VIEW_NAME) 
							|| viewName.equals(StatisticsView.VIEW_NAME) || viewName.equals(UsersView.VIEW_NAME)
							|| viewName.equals(OutbreaksView.VIEW_NAME) || viewName.equals(RegionsView.VIEW_NAME) 
							|| viewName.equals(DistrictsView.VIEW_NAME) || viewName.equals(CommunitiesView.VIEW_NAME) 
							|| viewName.equals(HealthFacilitiesView.VIEW_NAME) || viewName.equals(LaboratoriesView.VIEW_NAME)
							|| viewName.equals(UserRightsView.VIEW_NAME)) {
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
		if (UserProvider.getCurrent().hasUserRight(UserRight.DASHBOARD_VIEW)) {
			ControllerProvider.getDashboardController().registerViews(navigator);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.DASHBOARD_SURVEILLANCE_ACCESS)) {
			menu.addView(DashboardSurveillanceView.class, AbstractDashboardView.ROOT_VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuDashboard), FontAwesome.DASHBOARD);
		} else if (UserProvider.getCurrent().hasUserRight(UserRight.DASHBOARD_CONTACT_ACCESS)) {
			menu.addView(DashboardContactsView.class, AbstractDashboardView.ROOT_VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuDashboard), FontAwesome.DASHBOARD);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_VIEW)) {
			menu.addView(TasksView.class, TasksView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuTasks), FontAwesome.TASKS);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
			ControllerProvider.getCaseController().registerViews(navigator);
			menu.addView(CasesView.class, CasesView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuCases), FontAwesome.EDIT);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			ControllerProvider.getContactController().registerViews(navigator);
			menu.addView(ContactsView.class, ContactsView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuContacts), FontAwesome.HAND_PAPER_O);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_VIEW)) {
			ControllerProvider.getEventController().registerViews(navigator);
			menu.addView(EventsView.class, EventsView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuEvents), FontAwesome.PHONE);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			ControllerProvider.getSampleController().registerViews(navigator);
			menu.addView(SamplesView.class, SamplesView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuSamples), FontAwesome.DATABASE);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.WEEKLYREPORT_VIEW)) {
			menu.addView(ReportsView.class, ReportsView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuReports), FontAwesome.FILE_TEXT);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.STATISTICS_ACCESS)) {
			ControllerProvider.getStatisticsController().registerViews(navigator);
			menu.addView(StatisticsView.class, AbstractStatisticsView.ROOT_VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuStatistics), FontAwesome.BAR_CHART);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.USER_VIEW)) {
			menu.addView(UsersView.class, UsersView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuUsers), FontAwesome.USERS);
		}
		if (UserProvider.getCurrent().hasUserRight(UserRight.CONFIGURATION_ACCESS)) {
			AbstractConfigurationView.registerViews(navigator);
			if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_VIEW)) {
				menu.addView(RegionsView.class, AbstractConfigurationView.ROOT_VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuConfiguration), FontAwesome.COGS);
			} else {
				menu.addView(OutbreaksView.class, AbstractConfigurationView.ROOT_VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuConfiguration), FontAwesome.COGS);
			}
		}
		menu.addView(AboutView.class, AboutView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuAbout), FontAwesome.INFO_CIRCLE);

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

			// Would be better to do this check BEFORE the view is created, but the Navigator can't be extended that way
			
			if (!event.getParameters().contains("?")) {
				StringBuilder urlParams = new StringBuilder();
				Collection<Object> viewModels = ViewModelProviders.of(event.getNewView().getClass()).getAll();
				for (Object viewModel : viewModels) {
					if (viewModel instanceof BaseCriteria) {
						if (urlParams.length() > 0) {
							urlParams.append('&');
						}
						urlParams.append(((BaseCriteria)viewModel).toUrlParams());
						if (urlParams.length() > 0 && urlParams.charAt(urlParams.length()-1) == '&') {
							urlParams.deleteCharAt(urlParams.length()-1);
						}
					}
				}
				if (urlParams.length() > 0) {
					String url = event.getViewName() + "/";
					if (!DataHelper.isNullOrEmpty(event.getParameters())) {
						url += event.getParameters();
					}
					url += "?" + urlParams.toString();
					SormasUI.get().getNavigator().navigateTo(url);
					return false;
				}
			}
			
			if (event.getViewName().isEmpty()) {
				// redirect to default view
				if (UserProvider.getCurrent().hasUserRight(UserRight.DASHBOARD_VIEW)) {
					SormasUI.get().getNavigator().navigateTo(DashboardSurveillanceView.VIEW_NAME);
				} else if (UserProvider.getCurrent().hasUserRole(UserRole.EXTERNAL_LAB_USER)) {
					SormasUI.get().getNavigator().navigateTo(SamplesView.VIEW_NAME);
				} else if (UserProvider.getCurrent().hasUserRight(UserRight.TASK_VIEW)) {
					SormasUI.get().getNavigator().navigateTo(TasksView.VIEW_NAME);
				} else {
					SormasUI.get().getNavigator().navigateTo(AboutView.VIEW_NAME);
				}
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
