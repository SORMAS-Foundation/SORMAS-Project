package com.cinoteck.application.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("") 
@PageTitle("Login | APMIS")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	Div containerDiv = new Div();
	
	Div logoDiv = new Div();

	
	Image imgApmis = new Image("images/apmislogo.png", "APMIS-LOGO");

	String content =  "<div class=" + "apmisDesc>" + "<p class=" + "apmisText>AFGHANISTHAN POLIO MANAGEMENT INFORMATION SYSTEM.<p>"+"</div>";


    Html html = new Html(content);
    
    private final LoginFormInput login = new LoginFormInput();
    
    
	
	public LoginView(){

		addClassName("login-view");
		addClassName("loginView");
		

		setSizeFull(); 
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setClassName("loginfORM");

		logoDiv.setClassName("logoContainer");
		imgApmis.setWidth("20%");
		addClassName("loginOverlay");
		logoDiv.add(imgApmis);

		containerDiv.setClassName("loginContainer");
		containerDiv.add(logoDiv, html, login);
		add(containerDiv); 	
	}



	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
//		// inform the user about an authentication error
//		if(beforeEnterEvent.getLocation()  
//        .getQueryParameters()
//        .getParameters()
////        .containsKey("error")) {
////            login.setError(true);
////        }
//	}
}
