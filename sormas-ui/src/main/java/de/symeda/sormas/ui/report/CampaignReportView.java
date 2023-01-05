package de.symeda.sormas.ui.report;


import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import java.util.List;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.user.UserGrid;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.V7GridExportStreamResource;


public class CampaignReportView extends AbstractView {

	public static final String VIEW_NAME = "reports";
	private static final long serialVersionUID = -3533557348144005469L;

	public static final String ACTIVE_FILTER = I18nProperties.getString(Strings.active);
	public static final String INACTIVE_FILTER = I18nProperties.getString(Strings.inactive);

	private UserReportGrid grid;
	private Button syncButton;

	private VerticalLayout gridLayout;

	private RowCount rowsCount;
	
	public CampaignReportView() { //Weekly Reports
		
		
		super(VIEW_NAME);
		
		//addHeaderComponent("");
		HorizontalLayout layt = new HorizontalLayout();
		TabSheet tabsheet = new TabSheet();
		layt.addComponent(tabsheet);
		layt.setSizeFull();
		

		tabsheet.setHeightFull();

		
		//Second TAB
		gridLayout = new VerticalLayout();
		
		
		grid = new UserReportGrid();
		
		rowsCount = new RowCount(Strings.labelNumberOfUsers, grid.getItemCount());
		gridLayout.addComponent(rowsCount);
		

		gridLayout.addComponent(grid);
		
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		

		Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null,
				ValoTheme.BUTTON_PRIMARY);
		exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
		addHeaderComponent(exportButton);
		//System.out.println("_______----- "+grid.getColumn(ACTIVE_FILTER));
		StreamResource streamResource = GridExportStreamResource.createStreamResource("", "", grid,
				ExportEntityName.USERS, UserReportGrid.EDIT_BTN_ID);
		FileDownloader fileDownloaderx = new FileDownloader(streamResource);
		fileDownloaderx.extend(exportButton);
		//gridLayout.addComponent(exportButton);
		
		

	//	addComponent(gridLayout);
		
		gridLayout.setCaption("User Analysis");
		tabsheet.addTab(gridLayout);
		
		
		gridLayout = new VerticalLayout();
		gridLayout.setCaption("Pivot Table");
		tabsheet.addTab(gridLayout);
		
		

		// Create the first tab
		gridLayout = new VerticalLayout();
	//	tab1.addComponent();
		
		// Textual link
	//	Button link = new Button("Export Campaign User analysis");
//		FileDownloader fileDownloader = new FileDownloader(downloadCSVfromQuery());
//		fileDownloader.extend(link);
		
//		link.addClickListener(clickEvent ->{
//			
//	    Notification.show("Do not press this button again", Notification.TYPE_TRAY_NOTIFICATION);
//		}
	  //  );
		
		
		//gridLayout.addComponent(link);
		
		
		tabsheet.addTab(gridLayout, "Aggregate Report");
		
		
		
		layt.setStyleName("backgroudBrown");
		addComponent(layt);

	}

	private Resource downloadCSVfromQuery() {
	//	List alsl_result = FacadeProvider.getCampaignFacade().getUserAnalysis();
	//	List<String> dsx = new ArrayList<>();
		//dsx.addAll(alsl_result);
//		for (int i = 1; i <= alsl_result.size() - 1; i++) {
//			
//			System.out.println("+++++++++ Native  +++++++++++" +alsl_result.size());
//			String[] scc = alsl_result.get(i).toString().split("#");
//		System.out.println("+++++++++ Native  +++++++++++" + scc[0].toLowerCase());
//		}
		
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
		
		
	//	StreamResource streamResource = createGridExportStreamResourcsse(alsl_result,"/c/opt/sample.csv");
		
		
		
		return null;//streamResource;
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
