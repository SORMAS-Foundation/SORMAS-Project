package com.cinoteck.application.views.myaccount;


//import com.cinoteck.application.data.entity.RegionsDataGrid;
//import com.cinoteck.application.views.admin.AdminView;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;

@Route( layout = MyAccountView.class)
public class TestView2 extends VerticalLayout {

    public TestView2() {

        Accordion accordion = new Accordion();


        Select<String> campaign = new Select<>();
        campaign.setLabel("Campaign");
        campaign.setId("jgcjgcjgcj");
        campaign.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
                "Price: low to high");
        campaign.setValue("Most recent first");


        Select<String> region = new Select<>();
        region.setLabel("Region");
        region.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
                "Price: low to high");
        region.setValue("");

        Select<String> province = new Select<>();
        province.setLabel("Province");
        province.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
                "Price: low to high");
        province.setValue("");

        Select<String> district = new Select<>();
        district.setLabel("District");
        district.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
                "Price: low to high");
        district.setValue("");

        Select<String> groupby = new Select<>();
        groupby.setLabel("Group By");
        groupby.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
                "Price: low to high");
        groupby.setValue("");

        Select<String> campaignPhase = new Select<>();
        campaignPhase.setLabel("Campaign Phase");
        campaignPhase.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
                "Price: low to high");
        campaignPhase.setValue("");

        HorizontalLayout personalInformationLayout = new HorizontalLayout(campaign,region,province,district,
                groupby, campaignPhase);
        personalInformationLayout.setSpacing(false);
        personalInformationLayout.setPadding(false);

        accordion.add("Filters ", personalInformationLayout);



        add(accordion);
        add("ldlbvlbdljvbldbvljdbvljbaljb");

//        Grid < RegionsDataGrid>
    }
}