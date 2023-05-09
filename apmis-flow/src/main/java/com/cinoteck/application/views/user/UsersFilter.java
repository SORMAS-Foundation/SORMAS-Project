package com.cinoteck.application.views.user;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class UsersFilter extends HorizontalLayout{
	private final Button newUser = new Button("New User");
	private final Button exportUserRoles = new Button("Export User Roles");
	private final Button bulkMode = new Button("Enter Bulk Edit Mode");
	private TextField searchField = new TextField();
	
	
	
	public UsersFilter() {
		add(newUser, exportUserRoles, bulkMode, searchField );
		
	}

}
