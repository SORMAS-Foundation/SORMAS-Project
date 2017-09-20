package de.symeda.sormas.ui.reports;

import java.util.Date;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

public class ReportsView extends AbstractView {

	private static final long serialVersionUID = -226852255434803180L;
	
	public static final String VIEW_NAME = "reports";
	
	private WeeklyReportGrid grid;
	private VerticalLayout gridLayout;
	private NativeSelect yearFilter;
	private NativeSelect epiWeekFilter;
	
	public ReportsView() {
		setSizeFull();
		addStyleName("crud-view");
		
		grid = new WeeklyReportGrid();
		
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createTopBar());
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		addComponent(gridLayout);
	}
	
	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.addStyleName(CssStyles.VSPACE3);
		
		Label header = new Label("Reports");
		header.setSizeUndefined();
		CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
		topLayout.addComponent(header);
		
		return topLayout;
	}
	
	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.addStyleName(CssStyles.VSPACE3);
		
		EpiWeek prevEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		int year = prevEpiWeek.getYear();
		int week = prevEpiWeek.getWeek();
		
		yearFilter = new NativeSelect();
		yearFilter.setWidth(200, Unit.PIXELS);
		yearFilter.addItems(DateHelper.getYearsToNow());
		yearFilter.select(year);
		yearFilter.setCaption("Year");
		yearFilter.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		filterLayout.addComponent(yearFilter);
		
		epiWeekFilter = new NativeSelect();
		epiWeekFilter.setWidth(200, Unit.PIXELS);
		epiWeekFilter.addItems(DateHelper.createWeeksList(year));
		epiWeekFilter.select(week);
		epiWeekFilter.setCaption("Epi Week");
		epiWeekFilter.addValueChangeListener(e -> {
			grid.reload((int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
		});
		filterLayout.addComponent(epiWeekFilter);
		
		Button lastWeekButton = new Button("Last week");
		lastWeekButton.addStyleName(CssStyles.FORCE_CAPTION);
		lastWeekButton.addClickListener(e -> {
            EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());
            yearFilter.select(epiWeek.getYear());
            epiWeekFilter.select(epiWeek.getWeek());
		});
		filterLayout.addComponent(lastWeekButton);
		
		Button thisWeekButton = new Button("This week");
		thisWeekButton.addStyleName(CssStyles.FORCE_CAPTION);
		thisWeekButton.addClickListener(e -> {
			EpiWeek epiWeek = DateHelper.getEpiWeek(new Date());
            yearFilter.select(epiWeek.getYear());
            epiWeekFilter.select(epiWeek.getWeek());
		});
		filterLayout.addComponent(thisWeekButton);
		
		return filterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload((int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
	}	

}
