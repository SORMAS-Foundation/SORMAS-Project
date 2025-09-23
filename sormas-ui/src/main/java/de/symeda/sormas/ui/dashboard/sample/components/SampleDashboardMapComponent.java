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
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.dashboard.map.BaseDashboardMapComponent;
import de.symeda.sormas.ui.dashboard.sample.SampleDashboardDataProvider;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The SampleDashboardMapComponent displays sample data on a map for the Sample Dashboard,
 * including human and environmental samples, with independent toggles for each.
 */
public class SampleDashboardMapComponent extends BaseDashboardMapComponent<SampleDashboardCriteria, SampleDashboardDataProvider> {

    /** The set of currently displayed human sample types. */
    private Set<SampleAssociationType> displayedHumanSamples;

    /** Whether environmental samples are shown on the map. */
    private boolean showEnvironmentalSamples;

    /**
     * Constructs a new SampleDashboardMapComponent.
     * @param dashboardDataProvider The data provider for dashboard criteria and sample data.
     */
    public SampleDashboardMapComponent(SampleDashboardDataProvider dashboardDataProvider) {
        super(Strings.headingSampleDashboardMap, dashboardDataProvider, Strings.infoHeadingSampleDashboardMap);
    }

    /**
     * Adds map and control components to the dashboard map.
     * Initializes the toggles for human and environmental samples.
     */
    @Override
    protected void addComponents() {
        displayedHumanSamples =
            new HashSet<>(Arrays.asList(SampleAssociationType.CASE, SampleAssociationType.CONTACT, SampleAssociationType.EVENT_PARTICIPANT));
        if (UiUtil.permitted(FeatureType.ENVIRONMENT_MANAGEMENT, UserRight.ENVIRONMENT_SAMPLE_VIEW)) {
            showEnvironmentalSamples = false; // Environment samples are OFF by default
        }

        super.addComponents();

        if (showEnvironmentalSamples) {
            appendHeadingInfo(" " + I18nProperties.getString(Strings.infoHeadingEnvironmentSampleDashboardMap));
        }
    }

    /**
     * Returns the total count of markers to display on the map for the given date range and max count.
     * @param fromDate The start date for filtering.
     * @param toDate The end date for filtering.
     * @param maxCount The maximum number of markers to return.
     * @return The total number of markers.
     */
    @Override
    protected Long getMarkerCount(Date fromDate, Date toDate, int maxCount) {
        return FacadeProvider.getSampleDashboardFacade()
            .countSamplesForMap(dashboardDataProvider.buildDashboardCriteriaWithDates(), displayedHumanSamples)
            + (showEnvironmentalSamples
                ? FacadeProvider.getSampleDashboardFacade().countEnvironmentalSamplesForMap(dashboardDataProvider.buildDashboardCriteriaWithDates())
                : 0);
    }

    /**
     * Checks if the sample criteria is null, meaning that no human sample material or environment sample material is selected.
     * @param criteria The dashboard criteria to check.
     * @return true if no sample or environment material and no disease is selected.
     */
    private boolean isEmptySampleCriteriaWithoutDisease(SampleDashboardCriteria criteria) {
        return criteria.getSampleMaterial() == null && criteria.getEnvironmentSampleMaterial() == null && criteria.getDisease() == null;
    }

    /**
     * Checks if the sample criteria is null, meaning that no human sample material or environment sample material is selected,
     * but a disease is selected.
     * @param criteria The dashboard criteria to check.
     * @return true if no sample or environment material but a disease is selected.
     */
    private boolean isEmptySampleCriteriaWithDisease(SampleDashboardCriteria criteria) {
        return criteria.getSampleMaterial() == null && criteria.getEnvironmentSampleMaterial() == null && criteria.getDisease() != null;
    }

    /**
     * Checks if the sample criteria is only for environment samples, meaning no human sample material is selected.
     * @param criteria The dashboard criteria to check.
     * @return true if only environment sample material is selected.
     */
    private boolean isOnlyEnvironmentSampleCriteria(SampleDashboardCriteria criteria) {
        return criteria.getEnvironmentSampleMaterial() != null && criteria.getSampleMaterial() == null;
    }

    /**
     * Checks if the sample criteria is only for human samples, meaning no environment sample material is selected.
     * @param criteria The dashboard criteria to check.
     * @return true if only human sample material is selected.
     */
    private boolean isOnlyHumanSampleCriteria(SampleDashboardCriteria criteria) {
        return criteria.getSampleMaterial() != null && criteria.getEnvironmentSampleMaterial() == null;
    }

    /**
     * Checks if the sample criteria is for "other" sample materials (either human or environment).
     * @param criteria The dashboard criteria to check.
     * @return true if either human or environment sample material is set to OTHER.
     */
    private boolean isSampleCriteriaForOther(SampleDashboardCriteria criteria) {
        return (criteria.getSampleMaterial() == SampleMaterial.OTHER || criteria.getEnvironmentSampleMaterial() == EnvironmentSampleMaterial.OTHER);
    }

    /**
     * Loads and displays sample and environment sample data on the map for the given date range.
     * @param fromDate The start date.
     * @param toDate The end date.
     */
    @Override
    protected void loadMapData(Date fromDate, Date toDate) {
        String markerGroup = "samples";
        map.removeGroup(markerGroup);

        SampleDashboardCriteria criteria = dashboardDataProvider.buildDashboardCriteriaWithDates();
        List<MapSampleDto> humanSamples = List.of();
        List<MapSampleDto> environmentSamples = List.of();
        if (isSampleCriteriaForOther(criteria)) {
            humanSamples = FacadeProvider.getSampleDashboardFacade().getSamplesForMap(criteria, displayedHumanSamples);
            environmentSamples =
                    showEnvironmentalSamples ? FacadeProvider.getSampleDashboardFacade().getEnvironmentalSamplesForMap(criteria) : Collections.emptyList();
        } else if (isEmptySampleCriteriaWithoutDisease(criteria)) {
            // If no sample material is selected, we want to show all samples
            humanSamples = FacadeProvider.getSampleDashboardFacade().getSamplesForMap(criteria, displayedHumanSamples);
            environmentSamples =
                    showEnvironmentalSamples ? FacadeProvider.getSampleDashboardFacade().getEnvironmentalSamplesForMap(criteria) : Collections.emptyList();
        } else if (isOnlyHumanSampleCriteria(criteria) || isEmptySampleCriteriaWithDisease(criteria)) {
            // If only human sample is selected, we want to show only human samples
            humanSamples = FacadeProvider.getSampleDashboardFacade().getSamplesForMap(criteria, displayedHumanSamples);
        } else if (isOnlyEnvironmentSampleCriteria(criteria)) {
            // If only an environment sample is selected, we want to show only environment samples
            environmentSamples =
                    showEnvironmentalSamples ? FacadeProvider.getSampleDashboardFacade().getEnvironmentalSamplesForMap(criteria) : Collections.emptyList();
        } else{
            // In the case of mixed sample criteria, we want to show both human and environment samples; for now this is not supported
        }

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

    /**
     * Adds the toggle controls for layer options (case, contact, event participant, environment) to the map UI.
     * @param layersLayout The layout to which components are added.
     */
    @Override
    protected void addLayerOptions(VerticalLayout layersLayout) {

        if (UiUtil.permitted(UserRight.CASE_VIEW)) {
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

        if (UiUtil.permitted(UserRight.CONTACT_VIEW)) {
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

        if (UiUtil.permitted(UserRight.EVENTPARTICIPANT_VIEW)) {
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

        if (UiUtil.permitted(UserRight.ENVIRONMENT_VIEW)) {
            CheckBox showEnvironmentSamplesCheckBox = new CheckBox();
            showEnvironmentSamplesCheckBox.setId(Captions.sampleDashboardShowEnvironmentSamples);
            showEnvironmentSamplesCheckBox.setCaption(I18nProperties.getCaption(Captions.sampleDashboardShowEnvironmentSamples));
            showEnvironmentSamplesCheckBox.setValue(showEnvironmentalSamples); // Use the correct state!
            showEnvironmentSamplesCheckBox.addValueChangeListener(e -> {
                showEnvironmentalSamples = (boolean) e.getProperty().getValue();
                refreshMap(true);
            });
            layersLayout.addComponent(showEnvironmentSamplesCheckBox);
        }
    }

    /**
     * Returns the legend components for the map, showing human and environmental sample icons.
     * @return A list of legend components.
     */
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

    /**
     * Determines whether case samples should be displayed based on the toggle state.
     * @return true if case samples are to be shown, false otherwise.
     */
    private boolean shouldShowCaseSamples() {
        return displayedHumanSamples.contains(SampleAssociationType.CASE);
    }

    /**
     * Determines whether contact samples should be displayed based on the toggle state.
     * @return true if contact samples are to be shown, false otherwise.
     */
    private boolean shouldShowContactSamples() {
        return displayedHumanSamples.contains(SampleAssociationType.CONTACT);
    }

    /**
     * Determines whether event participant samples should be displayed based on the toggle state.
     * @return true if event participant samples are to be shown, false otherwise.
     */
    private boolean shouldShowEventParticipantSamples() {
        return displayedHumanSamples.contains(SampleAssociationType.EVENT_PARTICIPANT);
    }

    /**
     * Callback for when a marker on the map is clicked.
     * This implementation does nothing, but can be overridden to provide custom marker click behavior.
     * @param groupId The group ID of the marker.
     * @param markerIndex The index of the marker in the group.
     */
    @Override
    protected void onMarkerClicked(String groupId, int markerIndex) {
        // No action on marker click.
    }
}
