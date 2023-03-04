package com.cinoteck.application.views.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cinoteck.application.views.MainLayout;
//import com.cinoteck.application.views.campaign.MonthlyExpense.DailyExpenses;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("All Campaigns")
@Route(value = "campaign", layout = MainLayout.class)
public class CampaignView extends VerticalLayout {

    public CampaignView() {
        //ComboBox<String> searchCampaign = new ComboBox<>("Search Campaign");
        // comboBox.setItems(DataService.getCountries());
        // comboBox.setItemLabelGenerator(Country::getName);

      //  final Grid<Movie> grid = new Grid<Movie>();
        final TextField filterr = new TextField();
//		final UserRepository repo;
        final Button addNewBtnn = new Button();
//		 final UserEditor editor

        HorizontalLayout campaignAction = new HorizontalLayout();
        campaignAction.setClassName("actions");
        TextField searchField = new TextField();

        // searchField.setWidth("50%");
        searchField.setLabel("Search Campaign");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);


        Select<String> campaignStatus = new Select<>();
        campaignStatus.setLabel("Campaign Status");
        campaignStatus.setItems( "", "Active", "Archived",
                "Closed");

        Button validateForms = new Button("Validate Forms");
        Button addNewForm = new Button("Add New Forms");



        campaignAction.add(campaignStatus, validateForms, addNewForm);



        // Create and add header text

        // create Grid component
      //  final Grid<Campaigns> movies = new Grid<>(Campaigns.class);
        //
//             // fetch all movies from our Service
     //   movies.setItems(campaignsService.getCampaigns());
        //
//             // Use these auto-generated columns
      //  movies.setColumns("name", "startDate", "endDate", "campaignYear");


        //
//             // Add 'Director' column
//        movies.addColumn(movie -> movie.getDirector().getName()).setHeader("Director");
//        //
////             // Add link to iMDB column; the TemplateRenderer allows us to use a HTML link.
//        movies.addColumn(
//                        TemplateRenderer.<Movie>of("<a href='[[item.imbdLink]]' target='_blank'>Click to IMBD site</a>").withProperty("imbdLink", Movie::getImbdLink))
//                .setHeader("IMBD Link");
        //
//             // set one column to specific width
//        movies.getColumnByKey("releaseYear").setWidth("55px");
////


//        filterr.setPlaceholder("Filter by email");
//





   //     add(campaignAction, searchField, movies);
    }


}
