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
import java.util.stream.Collectors;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.MapSampleDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.map.BaseDashboardMapComponent;
import de.symeda.sormas.ui.dashboard.sample.SampleDashboardDataProvider;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.CssStyles;

public class SampleDashboardMapComponent extends BaseDashboardMapComponent<SampleDashboardCriteria, SampleDashboardDataProvider> {

	private Set<SampleAssociationType> displayedHumanSamples;
	private boolean showEnvironmentalSamples;

	public SampleDashboardMapComponent(SampleDashboardDataProvider dashboardDataProvider) {
		super(Strings.headingSampleDashboardMap, dashboardDataProvider, Strings.infoHeadingSampleDashboardMap);
	}

	@Override
	protected void addComponents() {
		displayedHumanSamples =
			new HashSet<>(Arrays.asList(SampleAssociationType.CASE, SampleAssociationType.CONTACT, SampleAssociationType.EVENT_PARTICIPANT));
		if (UiUtil.permitted(FeatureType.ENVIRONMENT_MANAGEMENT, UserRight.ENVIRONMENT_SAMPLE_VIEW)) {
			showEnvironmentalSamples = true;
		}

		super.addComponents();

		if (showEnvironmentalSamples) {
			appendHeadingInfo(" " + I18nProperties.getString(Strings.infoHeadingEnvironmentSampleDashboardMap));
		}
	}

	@Override
	protected Long getMarkerCount(Date fromDate, Date toDate, int maxCount) {
		return FacadeProvider.getSampleDashboardFacade()
			.countSamplesForMap(dashboardDataProvider.buildDashboardCriteriaWithDates(), displayedHumanSamples)
			+ (showEnvironmentalSamples
				? FacadeProvider.getSampleDashboardFacade().countEnvironmentalSamplesForMap(dashboardDataProvider.buildDashboardCriteriaWithDates())
				: 0);
	}

	@Override
	protected void loadMapData(Date fromDate, Date toDate) {
		String markerGroup = "samples";
		map.removeGroup(markerGroup);

		SampleDashboardCriteria criteria = dashboardDataProvider.buildDashboardCriteriaWithDates();
		List<MapSampleDto> humanSamples = FacadeProvider.getSampleDashboardFacade().getSamplesForMap(criteria, displayedHumanSamples);
		List<MapSampleDto> environmentSamples =
			showEnvironmentalSamples ? FacadeProvider.getSampleDashboardFacade().getEnvironmentalSamplesForMap(criteria) : Collections.emptyList();

		List<LeafletMarker> markers = new ArrayList<>(environmentSamples.size() + environmentSamples.size());

		markers.addAll(humanSamples.stream().map(sample -> {
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

			return marker;
		}).collect(Collectors.toList()));

		markers.addAll(environmentSamples.stream().map(sample -> {
			LeafletMarker marker = new LeafletMarker();
			marker.setIcon(MarkerIcon.SAMPLE_ENVIRONMENT);
			marker.setLatLon(sample.getLatitude(), sample.getLongitude());

			return marker;
		}).collect(Collectors.toList()));

		map.addMarkerGroup(markerGroup, markers);
	}

	@Override
	protected void addLayerOptions(VerticalLayout layersLayout) {

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
			CheckBox showCaseSamplesCheckBox = new CheckBox();
			showCaseSamplesCheckBox.setId(Captions.sampleDashboardShowCaseSamples);
			showCaseSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowCaseSamples));
			showCaseSamplesCheckBox.setValue(shouldShowCaseSamples());
			showCaseSamplesCheckBox.addValueChangeListener(e -> {
				if ((boolean) e.getProperty().getValue()) {
					displayedHumanSamples.add(SampleAssociationType.CASE);
				} else {
					displayedHumanSamples.remove(SampleAssociationType.CASE);
				}

				refreshMap(true);
			});
			layersLayout.addComponent(showCaseSamplesCheckBox);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			CheckBox showContactSamplesCheckBox = new CheckBox();
			showContactSamplesCheckBox.setId(Captions.sampleDashboardShowContactSamples);
			showContactSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowContactSamples));
			showContactSamplesCheckBox.setValue(shouldShowContactSamples());
			showContactSamplesCheckBox.addValueChangeListener(e -> {
				if ((boolean) e.getProperty().getValue()) {
					displayedHumanSamples.add(SampleAssociationType.CONTACT);
				} else {
					displayedHumanSamples.remove(SampleAssociationType.CONTACT);
				}

				refreshMap(true);
			});
			layersLayout.addComponent(showContactSamplesCheckBox);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_VIEW)) {
			CheckBox showEventParticipantSamplesCheckBox = new CheckBox();
			showEventParticipantSamplesCheckBox.setId(Captions.sampleDashboardShowEventParticipantSamples);
			showEventParticipantSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowEventParticipantSamples));
			showEventParticipantSamplesCheckBox.setValue(shouldShowEventParticipantSamples());
			showEventParticipantSamplesCheckBox.addValueChangeListener(e -> {
				if ((boolean) e.getProperty().getValue()) {
					displayedHumanSamples.add(SampleAssociationType.EVENT_PARTICIPANT);
				} else {
					displayedHumanSamples.remove(SampleAssociationType.EVENT_PARTICIPANT);
				}

				refreshMap(true);
			});
			layersLayout.addComponent(showEventParticipantSamplesCheckBox);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.ENVIRONMENT_VIEW)) {
			CheckBox showEnvironmentSamplesCheckBox = new CheckBox();
			showEnvironmentSamplesCheckBox.setId(Captions.sampleDashboardShowEnvironmentSamples);
			showEnvironmentSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowEnvironmentSamples));
			showEnvironmentSamplesCheckBox.setValue(shouldShowEventParticipantSamples());
			showEnvironmentSamplesCheckBox.addValueChangeListener(e -> {
				if ((boolean) e.getProperty().getValue()) {
					showEnvironmentalSamples = true;
				} else {
					showEnvironmentalSamples = false;
				}

				refreshMap(true);
			});
			layersLayout.addComponent(showEnvironmentSamplesCheckBox);
		}
	}

	@Override
	protected List<Component> getLegendComponents() {
		if (displayedHumanSamples.size() == 0) {
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
			CssStyles.style(legendEntry, CssStyles.HSPACE_RIGHT_3);
			samplesLegendLayout.addComponent(legendEntry);
		}

		if (showEnvironmentalSamples) {
			HorizontalLayout legendEntry =
				buildMarkerLegendEntry(MarkerIcon.SAMPLE_ENVIRONMENT, I18nProperties.getCaption(Captions.sampleDashboardEnvironmentsSamples));
			samplesLegendLayout.addComponent(legendEntry);
		}

		return Collections.singletonList(samplesLegendLayout);
	}

	private boolean shouldShowCaseSamples() {
		return displayedHumanSamples.contains(SampleAssociationType.CASE);
	}

	private boolean shouldShowContactSamples() {
		return displayedHumanSamples.contains(SampleAssociationType.CONTACT);
	}

	private boolean shouldShowEventParticipantSamples() {
		return displayedHumanSamples.contains(SampleAssociationType.EVENT_PARTICIPANT);
	}

	@Override
	protected void onMarkerClicked(String groupId, int markerIndex) {

	}
}
