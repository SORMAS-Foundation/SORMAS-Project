package com.cinoteck.application.views.myaccount;

import com.cinoteck.application.views.MainLayout;
import com.cinoteck.application.views.admin.TestView1;
//import com.cinoteck.application.views.admin.TestView2;
import com.cinoteck.application.views.admin.TestView3;
import com.cinoteck.application.views.configurations.RegionView;
import com.cinoteck.application.views.dashboard.AdminCovByDayGridView;
import com.cinoteck.application.views.dashboard.AdminCovByDosesGridView;
import com.cinoteck.application.views.dashboard.CampaignSummaryGridView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLayout;

import java.util.LinkedHashMap;
import java.util.Map;

@PageTitle("My Account")
@Route(value = "useraccount", layout = MainLayout.class)

public class MyAccountView extends VerticalLayout  implements RouterLayout {
 private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

    public MyAccountView() {

//        setSpacing(false);
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
//         Div userentry = new Div();
//         userentry.setClassName("subtabBackground");
//
//
//          Div personalInfoo = new Div();
//          Paragraph infoo =new Paragraph("Personal Information");
//
//          TextField firstnamee  = new TextField("");
//          firstnamee.setLabel("First Name");
//
//          TextField lastnamee  = new TextField("");
//          lastnamee.setLabel("Last Name");
//
//
//          EmailField emailAddresss  = new EmailField();
//          emailAddresss.setLabel("Email address");
//
//          TextField phoneNumberr = new TextField();
//          phoneNumberr.setLabel("Phone number");
//
//
//          Select<String> positionn = new Select<>();
//          positionn.setLabel("Position");
//          positionn.setItems("Most recent first", "Rating: high to low",
//                  "Rating: low to high", "Price: high to low",
//                  "Price: low to high");
//          positionn.setValue("");
//
//
//          TextField addresss = new TextField();
//          addresss.setLabel("Address");
//
//
//
//          FormLayout dataVieww = new FormLayout();
//          dataVieww.add( firstnamee,lastnamee, emailAddresss, phoneNumberr, positionn, addresss);
//
//
//
//
//
//          Div fieldInfoo = new Div();
//          Paragraph infodataa =new Paragraph("Field Information");
//
//
//
//          Select<String> provincee = new Select<>();
//          provincee.setLabel("Province");
//          provincee.setItems("Most recent first", "Rating: high to low",
//                  "Rating: low to high", "Price: high to low",
//                  "Price: low to high");
//          provincee.setValue("");
//
//
//
//          Select<String> regionn = new Select<>();
//          regionn.setLabel("Region");
//          regionn.setItems("Most recent first", "Rating: high to low",
//                  "Rating: low to high", "Price: high to low",
//                  "Price: low to high");
//          regionn.setValue("");
//
//
//
//
//          Select<String> districtt = new Select<>();
//          districtt.setLabel("District");
//          districtt.setItems("Most recent first", "Rating: high to low",
//                  "Rating: low to high", "Price: high to low",
//                  "Price: low to high");
//          districtt.setValue("");
//
//          TextField clusterr = new TextField();
//          clusterr.setLabel("Cluster");
//
//
//          TextField streett = new TextField();
//          streett.setLabel("Street");
//
//
//          TextField houseNumm = new TextField();
//          houseNumm.setLabel("House Number");
//
//
//          TextField addInfoo = new TextField();
//          addInfoo.setLabel("Additional information");
//
//
//          TextField postalCodee = new TextField();
//          postalCodee.setLabel("Postal code");
//
//
//
//
//          TextField cityy = new TextField();
//          cityy.setLabel("City");
//
//
//          Select<String> areaTypee = new Select<>();
//          areaTypee.setLabel("Area type (Urban/Rural)");
//          areaTypee.setItems("Most recent first", "Rating: high to low",
//                  "Rating: low to high", "Price: high to low",
//                  "Price: low to high");
//          areaTypee.setValue("");
//
//
//          TextField contacPersonn = new TextField();
//          contacPersonn.setLabel("Cluster contact person");
//
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
//
//
//
//          FormLayout fielddataVieww = new FormLayout();
//          fielddataVieww.setResponsiveSteps(
//         	        // Use one column by default
//         	        new ResponsiveStep("0", 1),
//         	        // Use two columns, if the layout's width exceeds 320px
//         	        new ResponsiveStep("320px", 2),
//         	        // Use three columns, if the layout's width exceeds 500px
//         	        new ResponsiveStep("500px", 3));
//          fielddataVieww.add( provincee,regionn, districtt, clusterr, streett, houseNumm, addInfoo, postalCodee, cityy, areaTypee, contacPersonn,gpsdataVieww );
//
//          Paragraph security =new Paragraph("Password & Security");
//
//
//
//          Div pwdSecc = new Div();
//          pwdSecc.setClassName("superDiv");
//
//
//          Div lang = new Div();
//          lang.setClassName("langDiv");
//          Select<String> language = new Select<>();
//          language.setLabel("Language");
//          language.setItems("Most recent first", "Rating: high to low",
//                  "Rating: low to high", "Price: high to low",
//                  "Price: low to high");
//          language.setValue("");
//
//          lang.add(language);
//          Div anch = new Div();
//          anch.setClassName("anchDiv");
//          Anchor changePwd = new Anchor();
//          changePwd.setText("Change Password");
//          anch.add(changePwd);
//
//
//
//
//          pwdSecc.add(lang, anch);
//
//          Div actionss = new Div();
//
//          Button discard = new Button("Discard Changes");
//          Button savee = new Button("Save");
//
//          actionss.add(discard,savee );
//          add(userentry,infoo,  dataVieww, infodataa, fieldInfoo, fielddataVieww ,security, pwdSecc, actionss) ;
//    }
 Tabs tabs = createTabs();
 Div contentContainer = new Div();
        contentContainer.setWidth("100%");
 add(tabs, contentContainer);

        tabs.addSelectedChangeListener(e -> {
  contentContainer.removeAll();
  contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
 });
 // Set initial content
        contentContainer.add(tabComponentMap.get(tabs.getSelectedTab()));
}

 private Tabs createTabs() {
  tabComponentMap.put(new Tab("Show some text"), new TestView1());
  tabComponentMap.put(new Tab("Show a Combo Box"), new TestView2());
  tabComponentMap.put(new Tab("Show a button"), new TestView3());
  return new Tabs(tabComponentMap.keySet().toArray(new Tab[]{}));
 }

}
