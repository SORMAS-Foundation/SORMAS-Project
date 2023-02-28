package de.symeda.sormas.ui.report;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteriaNew;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.components.CampaignFormPhaseSelector;
import de.symeda.sormas.ui.configuration.infrastructure.CommunitiesView;
import de.symeda.sormas.ui.utils.AbstractView;

public class CampaignReportView extends AbstractView {

	public static final String VIEW_NAME = "reports";
	private static final long serialVersionUID = -3533557348144005469L;

	public static final String ACTIVE_FILTER = I18nProperties.getString(Strings.active);
	public static final String INACTIVE_FILTER = I18nProperties.getString(Strings.inactive);

	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;

	private CommunityCriteriaNew criteria;

	public CampaignReportView() {

		super(VIEW_NAME);

		// addHeaderComponent("");
		HorizontalLayout layt = new HorizontalLayout();
		TabSheet tabsheet = new TabSheet();
		layt.addComponent(tabsheet);
		layt.setSizeFull();
		tabsheet.setHeightFull();

		gridLayout = new VerticalLayout();
		tabsheet.addTab(gridLayout, "Aggregate Report");

		criteria = ViewModelProviders.of(CommunitiesView.class).get(CommunityCriteriaNew.class,
				new CommunityCriteriaNew().country(FacadeProvider.getCountryFacade().getServerCountry()));
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		TabSheet tabsheetx = new TabSheet();

		FormAccess frms[] = FormAccess.values();
		for (FormAccess lopper : frms) {

			gridLayout = new VerticalLayout();
			gridLayout.setHeightFull();
			CampaignReportTabSheets sheet = new CampaignReportTabSheets(criteria, lopper);
			gridLayout.addComponent(sheet);
			tabsheetx.addTab(gridLayout, lopper.toString());

		}

		tabsheetx.setHeightFull();

		tabsheet.addTab(tabsheetx, "User Analysis");

		TabSheet tabsheetxr = new TabSheet();
		FormAccess frmss[] = FormAccess.values();
	//	for (FormAccess lopper : frmss) {
		{
			gridLayout = new VerticalLayout();
			gridLayout.setHeightFull();
			CompletionAnalysisTabSheets compAnalysis = new CompletionAnalysisTabSheets(FormAccess.ICM);// CampaignReportTabSheets
																								// sheet = new
																								// CampaignReportTabSheets(criteria,
																								// lopper);
			gridLayout.addComponent(compAnalysis);
			tabsheetxr.addTab(gridLayout, "ICM Completion");
		}
		
		{
			
			gridLayout = new VerticalLayout();
			gridLayout.setHeightFull();
			CompletionAnalysisTabSheets compAnalysis = new CompletionAnalysisTabSheets(FormAccess.ICM);// CampaignReportTabSheets
																								// sheet = new
																								// CampaignReportTabSheets(criteria,
																								// lopper);
			gridLayout.addComponent(compAnalysis);
			tabsheetxr.addTab(gridLayout, "ICM Missing");
			
		}
	//	}

		tabsheetxr.setHeightFull();

		tabsheet.addTab(tabsheetxr, "Completion Analysis");

		tabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				int position = tabsheet.getTabPosition(tabsheet.getTab(tabsheet.getSelectedTab()));
				VaadinService.getCurrentRequest().getWrappedSession().setAttribute("indexTab", position);
			}
		});

		if (VaadinService.getCurrentRequest().getWrappedSession().getAttribute("indexTab") != null) {
			tabsheet.setSelectedTab(
					(int) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("indexTab"));
		}

		tabsheetx.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				int positionx = tabsheetx.getTabPosition(tabsheetx.getTab(tabsheetx.getSelectedTab()));
				VaadinService.getCurrentRequest().getWrappedSession().setAttribute("indexTabx", positionx);
			}
		});

		if (VaadinService.getCurrentRequest().getWrappedSession().getAttribute("indexTabx") != null) {
			tabsheetx.setSelectedTab(
					(int) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("indexTabx"));
		}

		layt.setStyleName("backgroudBrown");
		addComponent(layt);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}