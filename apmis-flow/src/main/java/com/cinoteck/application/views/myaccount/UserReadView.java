package com.cinoteck.application.views.myaccount;

//import com.cinoteck.application.views.admin.AdminView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ElementFactory;

@Route(layout = MyAccountView.class)

public class UserReadView extends VerticalLayout {

	public UserReadView() {

    

       Div userentry = new Div();
       userentry.setClassName("subtabBackground");


        Div personalInfoo = new Div();
        Paragraph infoo =new Paragraph("Personal Information");

        TextField firstnamee  = new TextField("");
        firstnamee.setLabel("First Name");

        TextField lastnamee  = new TextField("");
        lastnamee.setLabel("Last Name");


        EmailField emailAddresss  = new EmailField();
        emailAddresss.setLabel("Email address");

        TextField phoneNumberr = new TextField();
        phoneNumberr.setLabel("Phone number");


        Select<String> positionn = new Select<>();
        positionn.setLabel("Position");
        positionn.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        positionn.setValue("");


        TextField addresss = new TextField();
        addresss.setLabel("Address");



        FormLayout dataVieww = new FormLayout();
        dataVieww.add( firstnamee,lastnamee, emailAddresss, phoneNumberr, positionn, addresss);





        Div fieldInfoo = new Div();
        Paragraph infodataa =new Paragraph("Field Information");



        Select<String> provincee = new Select<>();
        provincee.setLabel("Province");
        provincee.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        provincee.setValue("");



        Select<String> regionn = new Select<>();
        regionn.setLabel("Region");
        regionn.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        regionn.setValue("");




        Select<String> districtt = new Select<>();
        districtt.setLabel("District");
        districtt.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        districtt.setValue("");

        TextField clusterr = new TextField();
        clusterr.setLabel("Cluster");


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
        areaTypee.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        areaTypee.setValue("");


        TextField contacPersonn = new TextField();
        contacPersonn.setLabel("Cluster contact person");

        TextField gpslatt = new TextField();
        gpslatt.setLabel("GPS Longitude");






        TextField gpslongg = new TextField();
        gpslongg.setLabel("GPS Longitude");




        TextField gpsaccuracyy = new TextField();
        gpsaccuracyy.setLabel("GPS Accuracy in M");




        FormLayout gpsdataVieww = new FormLayout();
        gpsdataVieww.setResponsiveSteps(
       	        // Use one column by default
       	        new ResponsiveStep("0", 1),
       	        // Use two columns, if the layout's width exceeds 320px
       	        new ResponsiveStep("320px", 2),
       	        // Use three columns, if the layout's width exceeds 500px
       	        new ResponsiveStep("500px", 3));
        gpsdataVieww.add( gpslatt,gpslongg,gpsaccuracyy);



        FormLayout fielddataVieww = new FormLayout();
        fielddataVieww.setResponsiveSteps(
       	        // Use one column by default
       	        new ResponsiveStep("0", 1),
       	        // Use two columns, if the layout's width exceeds 320px
       	        new ResponsiveStep("320px", 2),
       	        // Use three columns, if the layout's width exceeds 500px
       	        new ResponsiveStep("500px", 3));
        fielddataVieww.add( provincee,regionn, districtt, clusterr, streett, houseNumm, addInfoo, postalCodee, cityy, areaTypee, contacPersonn,gpsdataVieww );

        Paragraph security =new Paragraph("Password & Security");



        Div pwdSecc = new Div();
        pwdSecc.setClassName("superDiv");


        Div lang = new Div();
        lang.setClassName("langDiv");
        Select<String> language = new Select<>();
        language.setLabel("Language");
        language.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        language.setValue("");

        lang.add(language);
        Div anch = new Div();
        anch.setClassName("anchDiv");
        Anchor changePwd = new Anchor();
        changePwd.setText("Change Password");
        anch.add(changePwd);




        pwdSecc.add(lang, anch);

        Div actionss = new Div();

        Button discard = new Button("Discard Changes");
        Button savee = new Button("Save");

        actionss.add(discard,savee );
        add(userentry,infoo,  dataVieww, infodataa, fieldInfoo, fielddataVieww ,security, pwdSecc, actionss) ;
	}
}