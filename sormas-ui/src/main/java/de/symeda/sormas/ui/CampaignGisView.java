package de.symeda.sormas.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.MapCampaignDataDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.ui.dashboard.campaigns.CampaignDashboardDataProvider;
import de.symeda.sormas.ui.dashboard.campaigns.CampaignDashboardFilterLayout;
import de.symeda.sormas.ui.map.CampaignLeafletMap;
import de.symeda.sormas.ui.map.LeafletMap;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.LeafletPolygon;
import de.symeda.sormas.ui.map.MarkerIcon;

public class CampaignGisView extends VerticalLayout implements View {

	public static final String MAP_ID = "map";

	public static final String VIEW_NAME = "maps";

	private LeafletMap map;
	
	protected CampaignDashboardFilterLayout filterLayout;
	protected CampaignDashboardDataProvider dataProvider;
	
	List<LeafletMarker> campaignMarkers = new ArrayList<LeafletMarker>(); //MainScreen height: 50%; */margin-top: 0px;

	public CampaignGisView() {
		dataProvider = new CampaignDashboardDataProvider();
		filterLayout = new CampaignDashboardFilterLayout(this, dataProvider); 
		filterLayout.setId("gisfilterr");
		
		//filterLayout.setHeight(1, Unit.PERCENTAGE);
		filterLayout.setSpacing(false);
		filterLayout.setSizeFull();
		addComponent(filterLayout);
				

		GeoLatLon nn = new GeoLatLon(34.543896, 69.160652);
		map = new LeafletMap();
		map.setWidthFull();
		map.setId(MAP_ID);
		map.setZoom(6);
		map.setCenter(nn);
		showCaseMarkers();
		map.addMarkerGroup("Tryyy", campaignMarkers);
		try {
			map.getUserLocation();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addComponent(map);

	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

	private void loadMapData() {
		FacadeProvider.getCampaignFormDataFacade().getCampaignDataforMaps();
	}

	private void showCaseMarkers() {
		List<MapCampaignDataDto> campaigndatum = FacadeProvider.getCampaignFormDataFacade().getCampaignDataforMaps();

		if (campaigndatum.size() > 0 && campaigndatum != null) {
			for (MapCampaignDataDto data : campaigndatum) {
				if (data.getReportLat() != null && data.getReportLon() != null) {
					LeafletMarker marker = new LeafletMarker();
					marker.setIcon(MarkerIcon.EVENT_OUTBREAK);
					marker.setLatLon(data.getReportLat(), data.getReportLon());
					marker.setDescription(data.getUuid());
					campaignMarkers.add(marker);
				}
			}
		}

	}

}
