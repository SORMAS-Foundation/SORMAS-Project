/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui;

import java.lang.reflect.Field;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignFacade;
import de.symeda.sormas.api.campaign.data.CampaignFormDataFacade;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaFacade;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.caseimport.CaseImportFacade;
import de.symeda.sormas.api.caze.classification.CaseClassificationFacade;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.dashboard.sample.SampleDashboardFacade;
import de.symeda.sormas.api.document.DocumentFacade;
import de.symeda.sormas.api.environment.EnvironmentFacade;
import de.symeda.sormas.api.environment.EnvironmentImportFacade;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.eventimport.EventImportFacade;
import de.symeda.sormas.api.externalemail.ExternalEmailFacade;
import de.symeda.sormas.api.externalmessage.ExternalMessageFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.geo.GeoShapeProvider;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.immunization.ImmunizationFacade;
import de.symeda.sormas.api.importexport.ExportFacade;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityFacade;
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.travelentry.travelentryimport.TravelEntryImportFacade;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRoleFacade;
import de.symeda.sormas.api.vaccination.VaccinationFacade;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb;
import de.symeda.sormas.backend.campaign.data.CampaignFormDataFacadeEjb;
import de.symeda.sormas.backend.campaign.form.CampaignFormMetaFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.caseimport.CaseImportFacadeEjb.CaseImportFacadeEjbLocal;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.dashboard.sample.SampleDashboardFacadeEjb;
import de.symeda.sormas.backend.document.DocumentFacadeEjb.DocumentFacadeEjbLocal;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentImportFacadeEjb;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.event.eventimport.EventImportFacadeEjb.EventImportFacadeEjbLocal;
import de.symeda.sormas.backend.externalemail.ExternalEmailFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.geo.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.importexport.ExportFacadeEjb.ExportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.travelentry.travelentryimport.TravelEntryImportFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserRoleFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.visit.VisitFacadeEjb.VisitFacadeEjbLocal;

public final class FacadeProviderMock extends FacadeProvider {

	private final AbstractBeanTest beanTest;

	public static void MockFacadeProvider(AbstractBeanTest beanTest) {
		try {
			Field instance = FacadeProvider.class.getDeclaredField("instance");
			instance.setAccessible(true);
			instance.set(instance, new FacadeProviderMock(beanTest));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public FacadeProviderMock(AbstractBeanTest beanTest) {
		this.beanTest = beanTest;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> P lookupEjbRemote(Class<P> clazz) {
		if (CaseFacade.class == clazz) {
			return (P) beanTest.getBean(CaseFacadeEjbLocal.class);
		} else if (ContactFacade.class == clazz) {
			return (P) beanTest.getBean(ContactFacadeEjbLocal.class);
		} else if (EventFacade.class == clazz) {
			return (P) beanTest.getBean(EventFacadeEjbLocal.class);
		} else if (EventParticipantFacade.class == clazz) {
			return (P) beanTest.getBean(EventParticipantFacadeEjbLocal.class);
		} else if (VisitFacade.class == clazz) {
			return (P) beanTest.getBean(VisitFacadeEjbLocal.class);
		} else if (PersonFacade.class == clazz) {
			return (P) beanTest.getBean(PersonFacadeEjbLocal.class);
		} else if (TaskFacade.class == clazz) {
			return (P) beanTest.getBean(TaskFacadeEjbLocal.class);
		} else if (SampleFacade.class == clazz) {
			return (P) beanTest.getBean(SampleFacadeEjbLocal.class);
		} else if (PathogenTestFacade.class == clazz) {
			return (P) beanTest.getBean(PathogenTestFacadeEjbLocal.class);
		} else if (SymptomsFacade.class == clazz) {
			return (P) beanTest.getBean(SymptomsFacadeEjbLocal.class);
		} else if (FacilityFacade.class == clazz) {
			return (P) beanTest.getBean(FacilityFacadeEjbLocal.class);
		} else if (CountryFacade.class == clazz) {
			return (P) beanTest.getBean(CountryFacadeEjb.CountryFacadeEjbLocal.class);
		} else if (RegionFacade.class == clazz) {
			return (P) beanTest.getBean(RegionFacadeEjbLocal.class);
		} else if (DistrictFacade.class == clazz) {
			return (P) beanTest.getBean(DistrictFacadeEjbLocal.class);
		} else if (CommunityFacade.class == clazz) {
			return (P) beanTest.getBean(CommunityFacadeEjbLocal.class);
		} else if (UserFacade.class == clazz) {
			return (P) beanTest.getBean(UserFacadeEjbLocal.class);
		} else if (HospitalizationFacade.class == clazz) {
			return (P) beanTest.getBean(HospitalizationFacadeEjbLocal.class);
		} else if (EpiDataFacade.class == clazz) {
			return (P) beanTest.getBean(EpiDataFacadeEjbLocal.class);
		} else if (WeeklyReportFacade.class == clazz) {
			return (P) beanTest.getBean(WeeklyReportFacadeEjbLocal.class);
		} else if (GeoShapeProvider.class == clazz) {
			return (P) beanTest.getBean(GeoShapeProviderEjbLocal.class);
		} else if (OutbreakFacade.class == clazz) {
			return (P) beanTest.getBean(OutbreakFacadeEjbLocal.class);
		} else if (ConfigFacade.class == clazz) {
			return (P) beanTest.getBean(ConfigFacadeEjbLocal.class);
		} else if (ExportFacade.class == clazz) {
			return (P) beanTest.getBean(ExportFacadeEjbLocal.class);
		} else if (ImportFacade.class == clazz) {
			return (P) beanTest.getBean(ImportFacadeEjbLocal.class);
		} else if (CaseClassificationFacade.class == clazz) {
			return (P) beanTest.getBean(CaseClassificationFacadeEjbLocal.class);
		} else if (PointOfEntryFacade.class == clazz) {
			return (P) beanTest.getBean(PointOfEntryFacadeEjbLocal.class);
		} else if (CampaignFacade.class == clazz) {
			return (P) beanTest.getBean(CampaignFacadeEjb.CampaignFacadeEjbLocal.class);
		} else if (CampaignFormMetaFacade.class == clazz) {
			return (P) beanTest.getBean(CampaignFormMetaFacadeEjb.CampaignFormMetaFacadeEjbLocal.class);
		} else if (CampaignFormDataFacade.class == clazz) {
			return (P) beanTest.getBean(CampaignFormDataFacadeEjb.CampaignFormDataFacadeEjbLocal.class);
		} else if (CaseImportFacade.class == clazz) {
			return (P) beanTest.getBean(CaseImportFacadeEjbLocal.class);
		} else if (EventImportFacade.class == clazz) {
			return (P) beanTest.getBean(EventImportFacadeEjbLocal.class);
		} else if (DocumentFacade.class == clazz) {
			return (P) beanTest.getBean(DocumentFacadeEjbLocal.class);
		} else if (ImmunizationFacade.class == clazz) {
			return (P) beanTest.getBean(ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal.class);
		} else if (VaccinationFacade.class == clazz) {
			return (P) beanTest.getBean(VaccinationFacadeEjb.VaccinationFacadeEjbLocal.class);
		} else if (TravelEntryFacade.class == clazz) {
			return (P) beanTest.getBean(TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal.class);
		} else if (TravelEntryImportFacade.class == clazz) {
			return (P) beanTest.getBean(TravelEntryImportFacadeEjb.TravelEntryImportFacadeEjbLocal.class);
		} else if (ExternalMessageFacade.class == clazz) {
			return (P) beanTest.getBean(ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal.class);
		} else if (FeatureConfigurationFacade.class == clazz) {
			return (P) beanTest.getBean(FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal.class);
		} else if (UserRoleFacade.class == clazz) {
			return (P) beanTest.getBean(UserRoleFacadeEjb.UserRoleFacadeEjbLocal.class);
		} else if (SampleDashboardFacade.class == clazz) {
			return (P) beanTest.getBean(SampleDashboardFacadeEjb.SampleDashboardFacadeEjbLocal.class);
		} else if (EnvironmentFacade.class == clazz) {
			return (P) beanTest.getBean(EnvironmentFacadeEjb.EnvironmentFacadeEjbLocal.class);
		} else if (EnvironmentSampleFacadeEjb.class == clazz) {
			return (P) beanTest.getBean(EnvironmentSampleFacadeEjb.EnvironmentSampleFacadeEjbLocal.class);
		} else if (EnvironmentImportFacade.class == clazz) {
			return (P) beanTest.getBean(EnvironmentImportFacadeEjb.EnvironmentImportFacadeEjbLocal.class);
		} else if (ExternalEmailFacade.class == clazz) {
			return (P) beanTest.getBean(ExternalEmailFacadeEjb.ExternalEmailFacadeEjbLocal.class);
		}

		return null;
	}

}
