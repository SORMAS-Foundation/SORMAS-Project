package com.cinoteck.application.views.user;

import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("User Management")
@Route(value = "user", layout = MainLayout.class)

public class UserView extends VerticalLayout {
	private UsersFilter userFilter = new UsersFilter();
	
	
	

	public UserView() {
	
		add(userFilter);
	
		
		
	}

	

	
}
