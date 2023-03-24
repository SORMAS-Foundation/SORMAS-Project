package com.cinoteck.application.views.configurations;




import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("Regions")
@Route(value = "regions", layout = ConfigurationsView.class)
public class RegionView extends VerticalLayout implements RouterLayout {

//    final Grid<RegionsDataGrid> regionsData = new Grid<RegionsDataGrid>();
   
    public RegionView() {

//
//        final Grid<Movie> movies = new Grid<>(Movie.class);// (RegionsDataGrid.class)
//
//        movies.setItems(movieService.getMovies());
//
//        movies.setColumns("title", "releaseYear");
//
//        movies.addColumn(movie -> movie.getDirector().getName()).setHeader("Director");
//
//        // Add link to iMDB column; the TemplateRenderer allows us to use a HTML link.
//        movies.addColumn(
//                        LitRenderer.<Movie>of("<a href='${item.imbdLink}' target='_blank'>Click to IMBD site</a>").withProperty("imbdLink", Movie::getImbdLink))
//                .setHeader("IMBD Link");
//
//        // set one column to specific width
//        movies.getColumnByKey("releaseYear").setWidth("55px");
//
add("movies");
    }


}
