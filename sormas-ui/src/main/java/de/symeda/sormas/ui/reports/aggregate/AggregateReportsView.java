package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class AggregateReportsView extends AbstractView {

	public static final String VIEW_NAME = "aggregatereports";

	private AggregateReportCriteria criteria;
	
	private AggregateReportsGrid grid;
	private VerticalLayout gridLayout;
	private Button btnExport;

	public AggregateReportsView() {
		super(VIEW_NAME);
		
		criteria = ViewModelProviders.of(AggregateReportsView.class).get(AggregateReportCriteria.class);
		criteria.year(2019).epiWeek(50);

		grid = new AggregateReportsGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		addComponent(gridLayout);
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.AGGREGATE_REPORT_EXPORT)) {
			btnExport = new Button(I18nProperties.getCaption(Captions.export));
			btnExport.addStyleName(ValoTheme.BUTTON_PRIMARY);
			btnExport.setIcon(VaadinIcons.DOWNLOAD);
//			exportButton.addClickListener(e -> ControllerProvider.getUserController().create());
			addHeaderComponent(btnExport);
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// Nothing to do here
	}
	
	private void reloadGrid() {
		grid.reload();
	}
	
}
