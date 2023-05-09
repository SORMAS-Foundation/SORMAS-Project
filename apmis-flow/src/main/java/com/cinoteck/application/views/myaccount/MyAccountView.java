package com.cinoteck.application.views.myaccount;

import com.cinoteck.application.views.MainLayout;
//import com.cinoteck.application.views.admin.TestView2;
import com.cinoteck.application.views.admin.TestView3;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.util.LinkedHashMap;
import java.util.Map;

@PageTitle("My Account")
@Route(value = "useraccount", layout = MainLayout.class)

public class MyAccountView extends VerticalLayout implements RouterLayout {
	private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

	public MyAccountView() {


//
//        Image img = new Image("images/empty-plant.png", "placeholder plant");
//        img.setWidth("200px");
//        add(img);
//
//        add(new H2("This place intentionally left empty"));
//        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
//
//        setSizeFull();
//        setJustifyContentMode(JustifyContentMode.CENTER);
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
//        getStyle().set("text-align", "center");
//
//
//
//    	Div userSet = new Div();
//    	userSet.setClassName("subtabBackground");
//    	 Tab userSettingsTab = new Tab("User settings");
//         Tab pwdSec = new Tab("Password & Security");
//
//         Tabs tabs = new Tabs(userSettingsTab, pwdSec);
//
//         userSet.add(tabs);
//
//         Div personalInfo = new Div();
//         Paragraph info =new Paragraph("Personal Information");
//
//         HorizontalLayout firstname  = new HorizontalLayout();
//         firstname.add("First Name");
//         firstname.getElement().appendChild(ElementFactory.createBr());
//         firstname.add("Jibril");
//
//         HorizontalLayout lastname  = new HorizontalLayout();
//         lastname.add("Last Name");
//         lastname.getElement().appendChild(ElementFactory.createBr());
//         lastname.add("Joanna");
//
//
//         HorizontalLayout emailAddress  = new HorizontalLayout();
//         emailAddress.add("Email Address");
//         emailAddress.getElement().appendChild(ElementFactory.createBr());
//         emailAddress.add("example@gmail.com");
//
//         HorizontalLayout phoneNumber  = new HorizontalLayout();
//         phoneNumber.add("Phone number");
//         phoneNumber.getElement().appendChild(ElementFactory.createBr());
//         phoneNumber.add("+93 0000 0000 000");
//
//         HorizontalLayout position  = new HorizontalLayout();
//         position.add("Position");
//         position.getElement().appendChild(ElementFactory.createBr());
//         position.add("Admin");
//
//         HorizontalLayout address  = new HorizontalLayout();
//         address.add("Address");
//         address.getElement().appendChild(ElementFactory.createBr());
//         address.add("Kabul, Afghanistan.");
//
//
//         FormLayout dataView = new FormLayout();
//         dataView.add( firstname,lastname, emailAddress, phoneNumber, position, address);
//
//
//
//
//
//         Div fieldInfo = new Div();
//         Paragraph infodata =new Paragraph("Field Information");
//
//         HorizontalLayout province  = new HorizontalLayout();
//         province.add("Province");
//         province.getElement().appendChild(ElementFactory.createBr());
//         province.add("Badhakshan");
//
//         HorizontalLayout region  = new HorizontalLayout();
//         region.add("Region");
//         region.getElement().appendChild(ElementFactory.createBr());
//         region.add("Badhakshan");
//
//
//         HorizontalLayout district  = new HorizontalLayout();
//         district.add("District");
//         district.getElement().appendChild(ElementFactory.createBr());
//         district.add("Yawan");
//
//         HorizontalLayout cluster  = new HorizontalLayout();
//         cluster.add("Cluster");
//         cluster.getElement().appendChild(ElementFactory.createBr());
//         cluster.add("Eefch");
//
//         HorizontalLayout street  = new HorizontalLayout();
//         street.add("Street");
//         street.getElement().appendChild(ElementFactory.createBr());
//         street.add("Example Street");
//
//         HorizontalLayout houseNum  = new HorizontalLayout();
//         houseNum.add("House Number");
//         houseNum.getElement().appendChild(ElementFactory.createBr());
//         houseNum.add("35");
//
//
//         HorizontalLayout addInfo  = new HorizontalLayout();
//         addInfo.add("Additional information");
//         addInfo.getElement().appendChild(ElementFactory.createBr());
//         addInfo.add("None");
//
//         HorizontalLayout postalCode  = new HorizontalLayout();
//         postalCode.add("Postal code");
//         postalCode.getElement().appendChild(ElementFactory.createBr());
//         postalCode.add("1004");
//
//         HorizontalLayout city  = new HorizontalLayout();
//         city.add("City");
//         city.getElement().appendChild(ElementFactory.createBr());
//         city.add("Badhakshan");
//
//
//         HorizontalLayout areaType  = new HorizontalLayout();
//         areaType.add("Area type (Urban/Rural)");
//         areaType.getElement().appendChild(ElementFactory.createBr());
//         areaType.add("Urban");
//
//         HorizontalLayout contacPerson  = new HorizontalLayout();
//         contacPerson.add("Cluster contact person");
//         contacPerson.getElement().appendChild(ElementFactory.createBr());
//         contacPerson.add("Admin");
//
//         HorizontalLayout gpslat  = new HorizontalLayout();
//         gpslat.add("GPS Latittude");
//         gpslat.getElement().appendChild(ElementFactory.createBr());
//         gpslat.add("0.44");
//
//         HorizontalLayout gpslong  = new HorizontalLayout();
//         gpslong.add("GPS Longitude");
//         gpslong.getElement().appendChild(ElementFactory.createBr());
//         gpslong.add("3.91");
//
//         HorizontalLayout gpsaccuracy  = new HorizontalLayout();
//         gpsaccuracy.add("GPS Accuracy in M");
//         gpsaccuracy.getElement().appendChild(ElementFactory.createBr());
//         gpsaccuracy.add("5");
//
//
//         FormLayout gpsdataView = new FormLayout();
//         gpsdataView.setResponsiveSteps(
//        	        // Use one column by default
//        	        new ResponsiveStep("0", 1),
//        	        // Use two columns, if the layout's width exceeds 320px
//        	        new ResponsiveStep("320px", 2),
//        	        // Use three columns, if the layout's width exceeds 500px
//        	        new ResponsiveStep("500px", 3));
//         gpsdataView.add( gpslat,gpslong,gpsaccuracy);
//
//
//
//         FormLayout fielddataView = new FormLayout();
//         fielddataView.setResponsiveSteps(
//        	        // Use one column by default
//        	        new ResponsiveStep("0", 1),
//        	        // Use two columns, if the layout's width exceeds 320px
//        	        new ResponsiveStep("320px", 2),
//        	        // Use three columns, if the layout's width exceeds 500px
//        	        new ResponsiveStep("500px", 3));
//         fielddataView.add( province,region, district, cluster, street, houseNum, addInfo, postalCode, city, areaType, contacPerson,gpsdataView );
//
//
//         Button editProfile = new Button("Edit Profile");
//         add(userSet,info,  dataView, infodata, fieldInfo, fielddataView , editProfile) ;
//
//
//
//
//
//
//
//
//
//
		Div userentry = new Div();
		userentry.setClassName("subtabBackground");

		Paragraph infooo = new Paragraph("Username");
		infooo.getStyle().set("color", "green");
		infooo.getStyle().set("font-size", "20px");
		infooo.getStyle().set("font-weight", "600");
		infooo.getStyle().set("margin", "20px");

		Paragraph infoood = new Paragraph("Admin Admin");
		infoood.getStyle().set("margin", "20px");

		Div personalInfoo = new Div();
		Paragraph infoo = new Paragraph("Personal Information");
		infoo.getStyle().set("color", "green");
		infoo.getStyle().set("font-size", "20px");
		infoo.getStyle().set("font-weight", "600");
		infoo.getStyle().set("margin", "20px");

		TextField firstnamee = new TextField("");
		firstnamee.setLabel("First Name");
	

		TextField lastnamee = new TextField("");
		lastnamee.setLabel("Last Name");

		EmailField emailAddresss = new EmailField();
		emailAddresss.setLabel("Email address");

		TextField phoneNumberr = new TextField();
		phoneNumberr.setLabel("Phone number");

		Select<String> positionn = new Select<>();
		positionn.setLabel("Position");
		positionn.setItems("");
		positionn.setValue("");

		TextField addresss = new TextField();
		addresss.setLabel("Address");

		FormLayout dataVieww = new FormLayout();
		dataVieww.add(firstnamee, lastnamee, emailAddresss, phoneNumberr, positionn, addresss);
		dataVieww.getStyle().set("margin", "20px");
		
		Div fieldInfoo = new Div();
		Paragraph infodataa = new Paragraph("Field Information");
		infodataa.getStyle().set("color", "green");
		infodataa.getStyle().set("font-size", "20px");
		infodataa.getStyle().set("font-weight", "600");
		infodataa.getStyle().set("margin", "20px");

		Select<String> provincee = new Select<>();
		provincee.setLabel("Province");
		provincee.setItems("", "Province", "Province", "Province", "Province", "Province");
		provincee.setValue("");

		Select<String> regionn = new Select<>();
		regionn.setLabel("Region");
		regionn.setItems("", "Region", "Region", "Region", "Region", "Region", "Region");
		regionn.setValue("");

		Select<String> districtt = new Select<>();
		districtt.setLabel("District");
		districtt.setItems("", "District", "District", "District", "District", "District");
		districtt.setValue("");

		Select<String> clusterr = new Select();
		clusterr.setLabel("Cluster");
		clusterr.setItems("", "Cluster", "Cluster", "Cluster", "Cluster", "Cluster");

		TextField streett = new TextField();
		streett.setLabel("Street");

		TextField houseNumm = new TextField();
		houseNumm.setLabel("House Number");

		TextField addInfoo = new TextField();
		addInfoo.setLabel("Additional information");

		TextField postalCodee = new TextField();
		postalCodee.setLabel("Postal code");

		TextField cityy = new TextField();
		cityy.setLabel("City");

		Select<String> areaTypee = new Select<>();
		areaTypee.setLabel("Area type (Urban/Rural)");
		areaTypee.setItems("", "Urban", "Rural");
		areaTypee.setValue("");

		TextField contacPersonn = new TextField();
		contacPersonn.setLabel("Cluster contact person");

//          TextField gpslatt = new TextField();
//          gpslatt.setLabel("GPS Longitude");
//
//
//
//
//
//
//          TextField gpslongg = new TextField();
//          gpslongg.setLabel("GPS Longitude");
//
//
//
//
//          TextField gpsaccuracyy = new TextField();
//          gpsaccuracyy.setLabel("GPS Accuracy in M");
//

//
//          FormLayout gpsdataVieww = new FormLayout();
//          gpsdataVieww.setResponsiveSteps(
//         	        // Use one column by default
//         	        new ResponsiveStep("0", 1),
//         	        // Use two columns, if the layout's width exceeds 320px
//         	        new ResponsiveStep("320px", 2),
//         	        // Use three columns, if the layout's width exceeds 500px
//         	        new ResponsiveStep("500px", 3));
//          gpsdataVieww.add( gpslatt,gpslongg,gpsaccuracyy);

		FormLayout fielddataVieww = new FormLayout();
		fielddataVieww.setResponsiveSteps(
				// Use one column by default
				new ResponsiveStep("0", 1),
				// Use two columns, if the layout's width exceeds 320px
				new ResponsiveStep("320px", 2),
				// Use three columns, if the layout's width exceeds 500px
				new ResponsiveStep("500px", 3));
		fielddataVieww.add(provincee, regionn, districtt, clusterr, streett, houseNumm, addInfoo, postalCodee, cityy,
				areaTypee, contacPersonn);
		fielddataVieww.getStyle().set("margin", "20px");
		
		Paragraph security = new Paragraph("Password & Security");
		security.getStyle().set("color", "green");
		security.getStyle().set("font-size", "20px");
		security.getStyle().set("font-weight", "600");
		security.getStyle().set("margin", "20px");

		Div pwdSecc = new Div();
		pwdSecc.setClassName("superDiv");

		Div lang = new Div();
		lang.setClassName("langDiv");
		Select<String> language = new Select<>();
		language.setLabel("Language");
		language.setItems("", "English", "Dari", "Pashto");
		language.setValue("");
		language.getStyle().set("width", "350%");
		lang.getStyle().set("margin", "20px");

		lang.add(language);
		Div anch = new Div();
		anch.setClassName("anchDiv");
		Anchor changePwd = new Anchor();
		changePwd.setText("Change Password");
		anch.add(changePwd);

		pwdSecc.add(lang, anch);

		Div actionss = new Div();

		Icon vadIc = new Icon(VaadinIcon.CLOSE_CIRCLE_O);
		vadIc.setId("fghf");
		vadIc.getStyle().set("color", "green !important");

		Icon vadIcc = new Icon(VaadinIcon.CHECK_CIRCLE_O);
		vadIc.getStyle().set("color", "white");

		Button discard = new Button("Discard Changes", vadIc);
		discard.getStyle().set("margin-right", "20px");
		discard.getStyle().set("color", "green");
		discard.getStyle().set("background", "white");
		discard.getStyle().set("border", "1px solid green");

		Button savee = new Button("Save", vadIcc);

		actionss.getStyle().set("margin", "20px");
		actionss.add(discard, savee);
		add(userentry, infooo, infoood, infoo, dataVieww, infodataa, fieldInfoo, fielddataVieww, security, pwdSecc,
				actionss);
	}
// Tabs tabs = createTabs();
// Div contentContainer = new Div();
//        contentContainer.setWidth("100%");
// add(tabs, contentContainer);
//
//        tabs.addSelectedChangeListener(e -> {
//  contentContainer.removeAll();
//  contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
// });
// // Set initial content
//        contentContainer.add(tabComponentMap.get(tabs.getSelectedTab()));
//}
//
// private Tabs createTabs() {
//  tabComponentMap.put(new Tab("Show some text"), new UserReadView());
//  tabComponentMap.put(new Tab("Show a Combo Box"), new TestView2());
//  tabComponentMap.put(new Tab("Show a button"), new TestView3());
//  return new Tabs(tabComponentMap.keySet().toArray(new Tab[]{}));
// }

}
