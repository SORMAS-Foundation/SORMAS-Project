/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.dashboard.AefiDashboardCriteria;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.MapAefiDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.AefiDashboardDataProvider;
import de.symeda.sormas.ui.dashboard.map.BaseDashboardMapComponent;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.CssStyles;

public class AefiDashboardMapComponent extends BaseDashboardMapComponent<AefiDashboardCriteria, AefiDashboardDataProvider> {

	public AefiDashboardMapComponent(AefiDashboardDataProvider dashboardDataProvider) {
		super(Strings.headingAefiDashboardMap, dashboardDataProvider, Strings.infoHeadingAefiDashboardMap);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
	}

	@Override
	protected Long getMarkerCount(Date fromDate, Date toDate, int maxCount) {
		return FacadeProvider.getAefiDashboardFacade().countAefiForMap(dashboardDataProvider.buildDashboardCriteriaWithDates());
	}

	@Override
	protected void loadMapData(Date fromDate, Date toDate) {
		String markerGroup = "adverseevents";
		map.removeGroup(markerGroup);

		AefiDashboardCriteria criteria = dashboardDataProvider.buildDashboardCriteriaWithDates();
		List<MapAefiDto> aefiMapData = FacadeProvider.getAefiDashboardFacade().getAefiForMap(criteria);

		//temporary fix: remove data without coordinates
		//ideally do this in the backend code
		List<MapAefiDto> filteredAefiMapData = new ArrayList<>();
		for (MapAefiDto mapAefiDto : aefiMapData) {
			if (mapAefiDto.getLatitude() != null && mapAefiDto.getLongitude() != null) {
				filteredAefiMapData.add(mapAefiDto);
			}
		}

		List<LeafletMarker> markers = new ArrayList<>(aefiMapData.size());

		markers.addAll(filteredAefiMapData.stream().map(mapAefiDto -> {
			LeafletMarker marker = new LeafletMarker();
			switch (mapAefiDto.getAefiType()) {
			case SERIOUS:
				marker.setIcon(MarkerIcon.AEFI_SERIOUS);
				break;
			case NON_SERIOUS:
				marker.setIcon(MarkerIcon.AEFI_NON_SERIOUS);
				break;
			default:
				marker.setIcon(MarkerIcon.AEFI_UNCLASSIFIED);
				break;
			}
			marker.setLatLon(mapAefiDto.getLatitude(), mapAefiDto.getLongitude());

			return marker;
		}).collect(Collectors.toList()));

		map.addMarkerGroup(markerGroup, markers);
	}

	@Override
	protected void addLayerOptions(VerticalLayout layersLayout) {

		CheckBox showSeriousAefiCheckBox = new CheckBox();
		showSeriousAefiCheckBox.setId(Captions.aefiDashboardShowSeriousAefi);
		showSeriousAefiCheckBox.setCaption(I18nProperties.getCaption(Captions.aefiDashboardShowSeriousAefi));
		showSeriousAefiCheckBox.setValue(shouldShowSeriousAefi());
		showSeriousAefiCheckBox.addValueChangeListener(e -> {
			if ((boolean) e.getProperty().getValue()) {
				dashboardDataProvider.buildDashboardCriteriaWithDates().aefiType(AefiType.SERIOUS);
			} else {
				dashboardDataProvider.buildDashboardCriteriaWithDates().aefiType(null);
			}

			refreshMap(true);
		});

		layersLayout.addComponent(showSeriousAefiCheckBox);

		CheckBox showNonSeriousAefiCheckBox = new CheckBox();
		showNonSeriousAefiCheckBox.setId(Captions.aefiDashboardShowNonSeriousAefi);
		showNonSeriousAefiCheckBox.setCaption(I18nProperties.getCaption(Captions.aefiDashboardShowNonSeriousAefi));
		showNonSeriousAefiCheckBox.setValue(shouldShowNonSeriousAefi());
		showNonSeriousAefiCheckBox.addValueChangeListener(e -> {
			if ((boolean) e.getProperty().getValue()) {
				dashboardDataProvider.buildDashboardCriteriaWithDates().aefiType(AefiType.NON_SERIOUS);
			} else {
				dashboardDataProvider.buildDashboardCriteriaWithDates().aefiType(null);
			}

			refreshMap(true);
		});

		layersLayout.addComponent(showNonSeriousAefiCheckBox);
	}

	@Override
	protected List<Component> getLegendComponents() {

		HorizontalLayout samplesLegendLayout = new HorizontalLayout();
		samplesLegendLayout.setSpacing(false);
		samplesLegendLayout.setMargin(false);

		HorizontalLayout seriousLegendEntry =
			buildMarkerLegendEntry(MarkerIcon.AEFI_SERIOUS, I18nProperties.getCaption(Captions.aefiDashboardSeriousAefi));
		CssStyles.style(seriousLegendEntry, CssStyles.HSPACE_RIGHT_3);
		samplesLegendLayout.addComponent(seriousLegendEntry);

		HorizontalLayout nonSeriousLegendEntry =
			buildMarkerLegendEntry(MarkerIcon.AEFI_NON_SERIOUS, I18nProperties.getCaption(Captions.aefiDashboardNonSeriousAefi));
		CssStyles.style(nonSeriousLegendEntry, CssStyles.HSPACE_RIGHT_3);
		samplesLegendLayout.addComponent(nonSeriousLegendEntry);

		return Collections.singletonList(samplesLegendLayout);
	}

	private boolean shouldShowSeriousAefi() {
		return dashboardDataProvider.buildDashboardCriteriaWithDates().getAefiType() == AefiType.SERIOUS;
	}

	private boolean shouldShowNonSeriousAefi() {
		return dashboardDataProvider.buildDashboardCriteriaWithDates().getAefiType() == AefiType.NON_SERIOUS;
	}

	@Override
	protected void onMarkerClicked(String groupId, int markerIndex) {

	}
}
