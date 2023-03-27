/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.dashboard.sample.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.MapSampleDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.ui.dashboard.map.BaseDashboardMapComponent;
import de.symeda.sormas.ui.dashboard.sample.SampleDashboardDataProvider;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.CssStyles;

public class SampleDashboardMapComponent extends BaseDashboardMapComponent<SampleDashboardCriteria, SampleDashboardDataProvider> {

	private Set<SampleAssociationType> displayedSamples;

	public SampleDashboardMapComponent(SampleDashboardDataProvider dashboardDataProvider) {
		super(Strings.headingSampleDashboardMap, dashboardDataProvider, Strings.infoHeadingSampleDashboardMap);
	}

	@Override
	protected void addComponents() {
		displayedSamples =
			new HashSet<>(Arrays.asList(SampleAssociationType.CASE, SampleAssociationType.CONTACT, SampleAssociationType.EVENT_PARTICIPANT));

		super.addComponents();
	}

	@Override
	protected Long getMarkerCount(Date fromDate, Date toDate, int maxCount) {
		return FacadeProvider.getSampleDashboardFacade()
			.countSamplesForMap(dashboardDataProvider.buildDashboardCriteriaWithDates(), displayedSamples);
	}

	@Override
	protected void loadMapData(Date fromDate, Date toDate) {
		String markerGroup = "samples";
		map.removeGroup(markerGroup);

		List<MapSampleDto> samples =
			FacadeProvider.getSampleDashboardFacade().getSamplesForMap(dashboardDataProvider.buildDashboardCriteriaWithDates(), displayedSamples);

		List<LeafletMarker> markers = new ArrayList<>();
		for (MapSampleDto sample : samples) {
			LeafletMarker marker = new LeafletMarker();

			switch (sample.getAssociationType()) {
			case CASE:
				marker.setIcon(MarkerIcon.SAMPLE_CASE);
				break;
			case CONTACT:
				marker.setIcon(MarkerIcon.SAMPLE_CONTACT);
				break;
			default:
				marker.setIcon(MarkerIcon.SAMPLE_EVENT_PARTICIPANT);
			}
			marker.setLatLon(sample.getLatitude(), sample.getLongitude());

			markers.add(marker);
		}

		map.addMarkerGroup(markerGroup, markers);
	}

	@Override
	protected void addLayerOptions(VerticalLayout layersLayout) {
		CheckBox showCaseSamplesCheckBox = new CheckBox();
		showCaseSamplesCheckBox.setId(Captions.sampleDashboardShowCaseSamples);
		showCaseSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowCaseSamples));
		showCaseSamplesCheckBox.setValue(shouldShowCaseSamples());
		showCaseSamplesCheckBox.addValueChangeListener(e -> {
			if ((boolean) e.getProperty().getValue()) {
				displayedSamples.add(SampleAssociationType.CASE);
			} else {
				displayedSamples.remove(SampleAssociationType.CASE);
			}

			refreshMap(true);
		});

		layersLayout.addComponent(showCaseSamplesCheckBox);

		CheckBox showContactSamplesCheckBox = new CheckBox();
		showContactSamplesCheckBox.setId(Captions.sampleDashboardShowContactSamples);
		showContactSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowContactSamples));
		showContactSamplesCheckBox.setValue(shouldShowContactSamples());
		showContactSamplesCheckBox.addValueChangeListener(e -> {
			if ((boolean) e.getProperty().getValue()) {
				displayedSamples.add(SampleAssociationType.CONTACT);
			} else {
				displayedSamples.remove(SampleAssociationType.CONTACT);
			}

			refreshMap(true);
		});

		layersLayout.addComponent(showContactSamplesCheckBox);

		CheckBox showEventParticipantSamplesCheckBox = new CheckBox();
		showEventParticipantSamplesCheckBox.setId(Captions.sampleDashboardShowEventParticipantSamples);
		showEventParticipantSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowEventParticipantSamples));
		showEventParticipantSamplesCheckBox.setValue(shouldShowEventParticipantSamples());
		showEventParticipantSamplesCheckBox.addValueChangeListener(e -> {
			if ((boolean) e.getProperty().getValue()) {
				displayedSamples.add(SampleAssociationType.EVENT_PARTICIPANT);
			} else {
				displayedSamples.remove(SampleAssociationType.EVENT_PARTICIPANT);
			}

			refreshMap(true);
		});

		layersLayout.addComponent(showEventParticipantSamplesCheckBox);

	}

	@Override
	protected List<Component> getLegendComponents() {
		if (displayedSamples.size() == 0) {
			return Collections.emptyList();
		}

		HorizontalLayout samplesLegendLayout = new HorizontalLayout();
		samplesLegendLayout.setSpacing(false);
		samplesLegendLayout.setMargin(false);

		if (shouldShowCaseSamples()) {
			HorizontalLayout legendEntry =
				buildMarkerLegendEntry(MarkerIcon.SAMPLE_CASE, I18nProperties.getCaption(Captions.sampleDashboardCaseSamples));
			CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
			samplesLegendLayout.addComponent(legendEntry);
		}

		if (shouldShowContactSamples()) {
			HorizontalLayout legendEntry =
				buildMarkerLegendEntry(MarkerIcon.SAMPLE_CONTACT, I18nProperties.getCaption(Captions.sampleDashboardContactSamples));
			CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
			samplesLegendLayout.addComponent(legendEntry);
		}

		if (shouldShowEventParticipantSamples()) {
			HorizontalLayout legendEntry = buildMarkerLegendEntry(
				MarkerIcon.SAMPLE_EVENT_PARTICIPANT,
				I18nProperties.getCaption(Captions.sampleDashboardEventParticipantSamples));
			samplesLegendLayout.addComponent(legendEntry);
		}

		return Collections.singletonList(samplesLegendLayout);
	}

	private boolean shouldShowCaseSamples() {
		return displayedSamples.contains(SampleAssociationType.CASE);
	}

	private boolean shouldShowContactSamples() {
		return displayedSamples.contains(SampleAssociationType.CONTACT);
	}

	private boolean shouldShowEventParticipantSamples() {
		return displayedSamples.contains(SampleAssociationType.EVENT_PARTICIPANT);
	}

	@Override
	protected void onMarkerClicked(String groupId, int markerIndex) {

	}
}
