package de.symeda.sormas.ui.dashboard.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.MapCampaignDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.geo.GeoShapeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.map.LeafletMap;
import de.symeda.sormas.ui.map.LeafletMapUtil;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.LeafletPolygon;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({ "myscript.js", "https://maps.googleapis.com/maps/api/js?v=3.exp" })
@StyleSheet({ "mystyle.css" })
@SuppressWarnings("serial")
public class CampaignDashboardMapComponent extends AbstractJavaScriptComponent{

	float param1 = 123;
	float param2 = 456;

	public CampaignDashboardMapComponent() {
	       callFunction("myfunction",param1,param2); //Pass parameters to your function
	    }

}
