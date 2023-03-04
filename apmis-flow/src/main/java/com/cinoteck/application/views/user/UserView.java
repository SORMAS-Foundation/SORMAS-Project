package com.cinoteck.application.views.user;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cinoteck.application.views.MainLayout;
import com.cinoteck.application.views.admin.TestView1;
//import com.cinoteck.application.views.admin.TestView2;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("User Management")
@Route(value = "user", layout = MainLayout.class)

public class UserView extends VerticalLayout implements RouterLayout {
	private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

	public Tabs createTabss() {
		//tabComponentMap.put(new Tab("User List View"), new UserListView());
		tabComponentMap.put(new Tab("User Data View"), new TestView1());
//		tabComponentMap.put(new Tab("User Edit View"), new TestView2());

		return new Tabs(tabComponentMap.keySet().toArray(new Tab[] {}));

	}

	public UserView() {
		Tabs tabs = createTabss();
		Div contentContainer = new Div();
		contentContainer.setWidthFull();

		tabs.addSelectedChangeListener(e -> {
			contentContainer.removeAll();
			contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
		});
		// Set initial content
		contentContainer.add(tabComponentMap.get(tabs.getSelectedTab()));

		add(tabs, contentContainer);
	}

}
