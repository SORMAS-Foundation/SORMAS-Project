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

package de.symeda.sormas.ui.dashboard.sample;

import java.util.Map;

import com.google.common.collect.Maps;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;

public class SampleDashboardDataProvider extends AbstractDashboardDataProvider<SampleDashboardCriteria> {

	private SampleDashboardFilterDateType dateType = SampleDashboardFilterDateType.MOST_RELEVANT;

	private SampleMaterial sampleMaterial;

	private EnvironmentSampleMaterial environmentSampleMaterial;

	private Boolean withNoDisease;

	private Map<PathogenTestResultType, Long> sampleCountsByResultType;
	private Map<SamplePurpose, Long> sampleCountsByPurpose;
	private Map<SpecimenCondition, Long> sampleCountsBySpecimenCondition;
	private Map<SampleShipmentStatus, Long> sampleCountsByShipmentStatus;
	private Map<PathogenTestResultType, Long> testResultCountsByResultType;
	// Environment specific count maps
	private Map<SpecimenCondition, Long> envSampleCountsBySpecimenCondition;
	private Map<SampleShipmentStatus, Long> envSampleCountsByShipmentStatus;
	private Map<PathogenTestResultType, Long> envTestResultCountsByResultType;
	private Map<EnvironmentSampleMaterial, Long> envSampleCount;

	@Override
	public void refreshData() {
		SampleDashboardCriteria sampleDashboardCriteria = buildDashboardCriteriaWithDates();
		// Invalid combination disease with environment
		if (sampleDashboardCriteria.getDisease() != null && sampleDashboardCriteria.getEnvironmentSampleMaterial() != null) {
			new Notification(
					I18nProperties.getString(Strings.headingSearchSample),
					I18nProperties.getString(Strings.messageSampleSearchWithDisease),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			return;
		}
		// If the sampleCriteria is for Humans
		if (sampleDashboardCriteria.getSampleMaterial() != null && sampleDashboardCriteria.getEnvironmentSampleMaterial() == null) {
			sampleCountsByResultType = FacadeProvider.getSampleDashboardFacade().getSampleCountsByResultType(sampleDashboardCriteria);
			sampleCountsByPurpose = FacadeProvider.getSampleDashboardFacade().getSampleCountsByPurpose(sampleDashboardCriteria);
			sampleCountsBySpecimenCondition = FacadeProvider.getSampleDashboardFacade().getSampleCountsBySpecimenCondition(sampleDashboardCriteria);
			sampleCountsByShipmentStatus = FacadeProvider.getSampleDashboardFacade().getSampleCountsByShipmentStatus(sampleDashboardCriteria);
			testResultCountsByResultType = FacadeProvider.getSampleDashboardFacade().getTestResultCountsByResultType(sampleDashboardCriteria);
			envSampleCountsBySpecimenCondition = Maps.newHashMap();
			envSampleCountsByShipmentStatus = Maps.newHashMap();
			envTestResultCountsByResultType = Maps.newHashMap();
			envSampleCount = Maps.newHashMap();
		} else if (sampleDashboardCriteria.getSampleMaterial() == null && sampleDashboardCriteria.getEnvironmentSampleMaterial() != null) {
			// If the sampleCriteria is for Environment
			envSampleCountsBySpecimenCondition = FacadeProvider.getSampleDashboardFacade().getEnvironmentalSampleCountsBySpecimenCondition(sampleDashboardCriteria);
			envSampleCountsByShipmentStatus = FacadeProvider.getSampleDashboardFacade().getEnvironmentalSampleCountsByShipmentStatus(sampleDashboardCriteria);
			envTestResultCountsByResultType = FacadeProvider.getSampleDashboardFacade().getEnvironmentalTestResultCountsByResultType(sampleDashboardCriteria);
			envSampleCount = FacadeProvider.getSampleDashboardFacade().getEnvironmentalSampleCounts(sampleDashboardCriteria);
			sampleCountsByResultType = Maps.newHashMap();
			sampleCountsByPurpose = Maps.newHashMap();
			sampleCountsBySpecimenCondition = Maps.newHashMap();
			sampleCountsByShipmentStatus = Maps.newHashMap();
			testResultCountsByResultType = Maps.newHashMap();
		} else {
			// for all other cases, search should return Humans and Environment samples
			sampleCountsByResultType = FacadeProvider.getSampleDashboardFacade().getSampleCountsByResultType(sampleDashboardCriteria);
			sampleCountsByPurpose = FacadeProvider.getSampleDashboardFacade().getSampleCountsByPurpose(sampleDashboardCriteria);
			sampleCountsBySpecimenCondition = FacadeProvider.getSampleDashboardFacade().getSampleCountsBySpecimenCondition(sampleDashboardCriteria);
			sampleCountsByShipmentStatus = FacadeProvider.getSampleDashboardFacade().getSampleCountsByShipmentStatus(sampleDashboardCriteria);
			testResultCountsByResultType = FacadeProvider.getSampleDashboardFacade().getTestResultCountsByResultType(sampleDashboardCriteria);
			// Environment samples will not have diseases
			if (sampleDashboardCriteria.getDisease() == null) {
				envSampleCountsBySpecimenCondition = FacadeProvider.getSampleDashboardFacade().getEnvironmentalSampleCountsBySpecimenCondition(sampleDashboardCriteria);
				envSampleCountsByShipmentStatus = FacadeProvider.getSampleDashboardFacade().getEnvironmentalSampleCountsByShipmentStatus(sampleDashboardCriteria);
				envTestResultCountsByResultType = FacadeProvider.getSampleDashboardFacade().getEnvironmentalTestResultCountsByResultType(sampleDashboardCriteria);
				envSampleCount = FacadeProvider.getSampleDashboardFacade().getEnvironmentalSampleCounts(sampleDashboardCriteria);
			} else {
				envSampleCountsBySpecimenCondition = Maps.newHashMap();
				envSampleCountsByShipmentStatus = Maps.newHashMap();
				envTestResultCountsByResultType = Maps.newHashMap();
				envSampleCount = Maps.newHashMap();
			}
		}
	}

	@Override
	protected SampleDashboardCriteria newCriteria() {
		return new SampleDashboardCriteria();
	}

	@Override
	protected SampleDashboardCriteria buildDashboardCriteria() {
		return super.buildDashboardCriteria().sampleDateType(dateType).sampleMaterial(sampleMaterial).environmentSampleMaterial(environmentSampleMaterial).withNoDisease(withNoDisease);
	}

	public SampleDashboardFilterDateType getDateType() {
		return dateType;
	}

	public void setDateType(SampleDashboardFilterDateType dateType) {
		this.dateType = dateType;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public Boolean getWithNoDisease() {
		return withNoDisease;
	}

	public void setWithNoDisease(Boolean withNoDisease) {
		this.withNoDisease = withNoDisease;
	}

	public EnvironmentSampleMaterial getEnvironmentSampleMaterial() {
		return environmentSampleMaterial;
	}

	public void setEnvironmentSampleMaterial(EnvironmentSampleMaterial environmentSampleMaterial) {
		this.environmentSampleMaterial = environmentSampleMaterial;
	}

	public Map<PathogenTestResultType, Long> getSampleCountsByResultType() {
		return sampleCountsByResultType;
	}

	public Map<SamplePurpose, Long> getSampleCountsByPurpose() {
		return sampleCountsByPurpose;
	}

	public Map<SpecimenCondition, Long> getSampleCountsBySpecimenCondition() {
		return sampleCountsBySpecimenCondition;
	}

	/**
	 * Returns the map of environment sample counts by specimen condition.
	 *
	 * @return
	 */
	public Map<SpecimenCondition, Long> getEnvSampleCountsBySpecimenCondition() {
		return envSampleCountsBySpecimenCondition;
	}

	public Map<SampleShipmentStatus, Long> getSampleCountsByShipmentStatus() {
		return sampleCountsByShipmentStatus;
	}

	/**
	 * Returns the map of environment sample counts by shipment status.
	 * This is used to display the counts of environment samples based on their shipment status.
	 *
	 * @return
	 */
	public Map<SampleShipmentStatus, Long> getEnvSampleCountsByShipmentStatus() {
		return envSampleCountsByShipmentStatus;
	}

	public Map<PathogenTestResultType, Long> getTestResultCountsByResultType() {
		return testResultCountsByResultType;
	}

	/**
	 * Returns the map of an environment test result counts by result type.
	 * This is used to display the counts of environment test results based on their result type.
	 *
	 * @return
	 */
	public Map<PathogenTestResultType, Long> getEnvironmentTestResultCountsByResultType() {
		return envTestResultCountsByResultType;
	}

	/**
	 * Returns the map of environment sample counts by its Material.
	 * This is used to display the counts of environment samples based on their material type.
	 *
	 * @return
	 */
	public Map<EnvironmentSampleMaterial, Long> getEnvironmentSampleCount() {
		return envSampleCount;
	}
}
