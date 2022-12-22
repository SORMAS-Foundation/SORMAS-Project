package de.symeda.sormas.ui;


import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.ui.dashboard.campaigns.CampaignDashboardDataProvider;
import de.symeda.sormas.ui.utils.ContactDownloadUtil;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.V7GridExportStreamResource;


public class CampaignReportView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "reports";

	protected CampaignDashboardDataProvider dataProvider;
	
	public CampaignReportView() {
//		dataProvider = new CampaignDashboardDataProvider();
//		filterLayout = new CampaignDashboardFilterLayout(this, dataProvider); 
//		filterLayout.setId("gisfilterr");
//		
//		//filterLayout.setHeight(1, Unit.PERCENTAGE);
//		filterLayout.setSpacing(false);
//		filterLayout.setSizeFull();
//		addComponent(filterLayout);
		
		
		HorizontalLayout layt = new HorizontalLayout();
		TabSheet tabsheet = new TabSheet();
		layt.addComponent(tabsheet);

		// Create the first tab
		VerticalLayout tab1 = new VerticalLayout();
	//	tab1.addComponent();
		
		// Textual link
		Button link = new Button("Export Campaign User analysis");
		FileDownloader fileDownloader = new FileDownloader(downloadCSVfromQuery());
		fileDownloader.extend(link);
		
		link.addClickListener(clickEvent ->{
			
	    Notification.show("Do not press this button again", Notification.TYPE_TRAY_NOTIFICATION);
		}
	    );
		
		
		tab1.addComponent(link);
		
		
		tabsheet.addTab(tab1, "User Analysis");

		// This tab gets its caption from the component caption
		VerticalLayout tab2 = new VerticalLayout();
		
		tab2.setCaption("Aggregate Report");
		tabsheet.addTab(tab2);
		
		layt.setStyleName("backgroudBrown");
		addComponent(layt);

	}

	private Resource downloadCSVfromQuery() {
		List alsl_result = FacadeProvider.getCampaignFacade().getUserAnalysis();
	//	List<String> dsx = new ArrayList<>();
		//dsx.addAll(alsl_result);
		for (int i = 1; i <= alsl_result.size() - 1; i++) {
			
			System.out.println("+++++++++ Native  +++++++++++" +alsl_result.size());
			String[] scc = alsl_result.get(i).toString().split("#");
		System.out.println("+++++++++ Native  +++++++++++" + scc[0].toLowerCase());
		}
		
//		
//		 try {
//		        FileWriter fw = new FileWriter("/c/c/opt/sample.csv");
//
//		        // Write the column names
//		        Indexed numColumns = alsl_result.size();
//		        for (int i = 1; i <= numColumns; i++) {
//		            fw.append(alsl_result.get(i));
//		            if (i < numColumns) {
//		                fw.append(",");
//		            }
//		        }
//		        fw.append("\n");
//
//
//		        fw.flush();
//		        fw.close();
//		    } catch (IOException e) {
//		        System.out.println("Error writing to CSV file: " + e);
//		    } 
////		
//		
		
		
		StreamResource streamResource = createGridExportStreamResourcsse(alsl_result,"/c/opt/sample.csv");
		
		
		
		return streamResource;
	}
	
	public static StreamResource createGridExportStreamResourcsse(List<String> lst, String fln) {

			return new V7GridExportStreamResource( lst,  fln);
		}

	private String createTab1Content() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}


}
