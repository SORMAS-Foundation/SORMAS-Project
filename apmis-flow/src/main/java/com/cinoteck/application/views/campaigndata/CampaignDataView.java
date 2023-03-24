package com.cinoteck.application.views.campaigndata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

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

@PageTitle("Campaign Data")
@Route(value = "campaigndata", layout = MainLayout.class)
public class CampaignDataView extends VerticalLayout {

    public CampaignDataView() {



     //   final Grid<Movie> grid = new Grid<Movie>();
        final TextField filterr = new TextField();
//		final UserRepository repo;
        final Button addNewBtnn = new Button();
//		 final UserEditor editor


        //ComboBox<String> searchCampaign = new ComboBox<>("Search Campaign");
        // comboBox.setItems(DataService.getCountries());
        // comboBox.setItemLabelGenerator(Country::getName);




//        Grid grid = new Grid();
//        grid.addColumn("NAME"); //.addColumn().setHeader("First name");
//        grid.addColumn("START DATE");
//        grid.addColumn("END DAT E");
//        grid.addColumn("CAMPAIGN YEAR");

        Tab preCamp = new Tab("Pre");
        Tab intraCamp = new Tab("Intra");
        Tab postCamp = new Tab("Post");



        Tabs tabss = new Tabs(preCamp, intraCamp, postCamp);




        // Grid campaignsGrid = new Grid();
        //  campaignsGrid.addColumn("First Name").setHeader("Name");
        //   campaignsGrid.addColumn("Start Date").setHeader("Start Date");
        //   campaignsGrid.addColumn("End Date").setHeader("End Date");
        //    campaignsGrid.addColumn("Year").setHeader("CAMPAIGN YEAR");

        //List<Person> people = DataService.getPeople();
        // grid.setItems(people);
        HorizontalLayout campDatFil = new HorizontalLayout();
        campDatFil.setClassName("campDatFil");
        campDatFil.setWidthFull();
        Select<String> campaignStatuss = new Select<>();
        campaignStatuss.setLabel("Campaign");
        campaignStatuss.setItems( "", "Active", "Archived",
                "Closed");
        Button resetFilter = new Button("Reset Filters");
        Button allFilters = new Button("All Filters");


        VerticalLayout l1 = new VerticalLayout();



        Tab preCampp = new Tab("Pre");
        Tab intraCampp = new Tab("Intra");
        Tab postCampp = new Tab("Post");



        Tabs tabsss = new Tabs(preCamp, intraCamp, postCamp);
        l1.add( tabsss);



//
        Div topDiv = new Div();
        topDiv.setClassName("topdiv");
        campDatFil.add(topDiv);

        Div camp =new Div();
        camp.add(campaignStatuss, resetFilter,allFilters);

        Div phaseDiv =new Div();
        phaseDiv.add(l1);




        HorizontalLayout campDatFill = new HorizontalLayout();
        campDatFill.setClassName("campDatFill");
        //  campDatFill

        Select<String> form = new Select<>();
        form.setLabel("Form");
        form.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        form.setValue("Most recent first");

        Select<String> region = new Select<>();
        region.setLabel("Region");
        region.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        region.setValue("");

        Select<String> province = new Select<>();
        province.setLabel("Province");
        province.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        province.setValue("");

        Select<String> district = new Select<>();
        district.setLabel("District");
        district.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        district.setValue("");

        Select<String> cluster = new Select<>();
        cluster.setLabel("Cluster");
        cluster.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        cluster.setValue("");


        Div filterLayout = new Div(form, region, province, district);

        Button addNewFormm = new Button("Add New Forms");
        Button importButton = new Button("Import");
        Button exportButton = new Button("Export");

        Div actionLayout = new Div(addNewFormm, importButton, exportButton);
        actionLayout.setClassName("actionLayout");


        campDatFill.add(filterLayout,  phaseDiv);


        topDiv.add(camp,actionLayout);

        // create Grid component
    //    final Grid<Movie> movies = new Grid<>(Movie.class);
        //
//             // fetch all movies from our Service
    //    movies.setItems(movieService.getMovies());
        //
//             // Use these auto-generated columns
     //   movies.setColumns("title", "releaseYear");
        //
//             // Add 'Director' column
   //     movies.addColumn(movie -> movie.getDirector().getName()).setHeader("Director");
        //
//             // Add link to iMDB column; the TemplateRenderer allows us to use a HTML link.
    ////    movies.addColumn(
      //                  TemplateRenderer.<Movie>of("<a href='[[item.imbdLink]]' target='_blank'>Click to IMBD site</a>").withProperty("imbdLink", Movie::getImbdLink))
     //           .setHeader("IMBD Link");
        //
//             // set one column to specific width
//        movies.getColumnByKey("releaseYear").setWidth("55px");
////
//        grid.setHeight("300px");
//
//        filterr.setPlaceholder("Filter by email");
//
//
//
//        add( campDatFil, campDatFill, movies);
    }

    
}
