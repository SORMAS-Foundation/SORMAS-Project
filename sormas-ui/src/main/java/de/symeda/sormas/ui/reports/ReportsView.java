package de.symeda.sormas.ui.reports;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

public class ReportsView extends AbstractView {

	private static final long serialVersionUID = -226852255434803180L;
	
	public static final String VIEW_NAME = "reports";
	
	private WeeklyReportGrid grid;
	private VerticalLayout gridLayout;
	private AbstractSelect yearFilter;
	private AbstractSelect epiWeekFilter;
	
	public ReportsView() {
    	super(VIEW_NAME);
    	
		grid = new WeeklyReportGrid();
		grid.setHeightMode(HeightMode.UNDEFINED);
		
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		
		addComponent(gridLayout);
	}
	
	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		
		EpiWeek prevEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		int year = prevEpiWeek.getYear();
		int week = prevEpiWeek.getWeek();
		
		yearFilter = new ComboBox();
		yearFilter.setWidth(200, Unit.PIXELS);
		yearFilter.addItems(DateHelper.getYearsToNow());
		yearFilter.select(year);
		yearFilter.setCaption("Year");
		yearFilter.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		filterLayout.addComponent(yearFilter);
		
		epiWeekFilter = new ComboBox();
		epiWeekFilter.setWidth(200, Unit.PIXELS);
		List<EpiWeek> epiWeekList = DateHelper.createEpiWeekList(year);
		for (EpiWeek epiWeek : epiWeekList) {
			epiWeekFilter.addItem(epiWeek.getWeek());
			epiWeekFilter.setItemCaption(epiWeek.getWeek(), epiWeek.getWeek() + " (" + DateHelper.formatShortDate(DateHelper.getEpiWeekStart(epiWeek)) + " - " + DateHelper.formatShortDate(DateHelper.getEpiWeekEnd(epiWeek)) + ")");
		}
		epiWeekFilter.select(week);
		epiWeekFilter.setCaption("Epi Week");
		epiWeekFilter.addValueChangeListener(e -> {
			reloadGrid();
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
		reloadGrid();
	}	
	
	private void reloadGrid() {
		RegionReferenceDto region = null;
		if (!LoginHelper.isUserInRole(UserRole.NATIONAL_USER)) {
			region = LoginHelper.getCurrentUser().getRegion();
		}
		grid.reload(region, (int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
	}
}
