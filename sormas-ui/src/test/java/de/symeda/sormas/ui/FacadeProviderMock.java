package de.symeda.sormas.ui;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.classification.CaseClassificationFacade;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.importexport.ExportFacade;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryFacade;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ExportFacadeEjb.ExportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitFacadeEjb.VisitFacadeEjbLocal;
import info.novatec.beantest.api.BeanProviderHelper;

public final class FacadeProviderMock extends FacadeProvider {
	@SuppressWarnings("unchecked")
	@Override
	public <P> P lookupEjbRemote(Class<P> clazz) {
		BeanProviderHelper bm = BeanProviderHelper.getInstance();
		if (CaseFacade.class == clazz) {
			return (P) bm.getBean(CaseFacadeEjbLocal.class);
		} else if (ContactFacade.class == clazz) {
			return (P) bm.getBean(ContactFacadeEjbLocal.class);
		} else if (EventFacade.class == clazz) {
			return (P) bm.getBean(EventFacadeEjbLocal.class);
		} else if (EventParticipantFacade.class == clazz) {
			return (P) bm.getBean(EventParticipantFacadeEjbLocal.class);
		} else if (VisitFacade.class == clazz) {
			return (P) bm.getBean(VisitFacadeEjbLocal.class);
		} else if (PersonFacade.class == clazz) {
			return (P) bm.getBean(PersonFacadeEjbLocal.class);
		} else if (TaskFacade.class == clazz) {
			return (P) bm.getBean(TaskFacadeEjbLocal.class);
		} else if (SampleFacade.class == clazz) {
			return (P) bm.getBean(SampleFacadeEjbLocal.class);
		} else if (PathogenTestFacade.class == clazz) {
			return (P) bm.getBean(PathogenTestFacadeEjbLocal.class);
		} else if (SymptomsFacade.class == clazz) {
			return (P) bm.getBean(SymptomsFacadeEjbLocal.class);
		} else if (FacilityFacade.class == clazz) {
			return (P) bm.getBean(FacilityFacadeEjbLocal.class);
		} else if (RegionFacade.class == clazz) {
			return (P) bm.getBean(RegionFacadeEjbLocal.class);
		} else if (DistrictFacade.class == clazz) {
			return (P) bm.getBean(DistrictFacadeEjbLocal.class);
		} else if (CommunityFacade.class == clazz) {
			return (P) bm.getBean(CommunityFacadeEjbLocal.class);
		} else if (UserFacade.class == clazz) {
			return (P) bm.getBean(UserFacadeEjbLocal.class);
		} else if (HospitalizationFacade.class == clazz) {
			return (P) bm.getBean(HospitalizationFacadeEjbLocal.class);
		} else if (EpiDataFacade.class == clazz) {
			return (P) bm.getBean(EpiDataFacadeEjbLocal.class);
		} else if (WeeklyReportFacade.class == clazz) {
			return (P) bm.getBean(WeeklyReportFacadeEjbLocal.class);
		} else if (GeoShapeProvider.class == clazz) {
			return (P) bm.getBean(GeoShapeProviderEjbLocal.class);
		} else if (OutbreakFacade.class == clazz) {
			return (P) bm.getBean(OutbreakFacadeEjbLocal.class);
		} else if (ConfigFacade.class == clazz) {
			return (P) bm.getBean(ConfigFacadeEjbLocal.class);
		} else if (ExportFacade.class == clazz) {
			return (P) bm.getBean(ExportFacadeEjbLocal.class);
		} else if (ImportFacade.class == clazz) {
			return (P) bm.getBean(ImportFacadeEjbLocal.class);
		} else if (CaseClassificationFacade.class == clazz) {
			return (P) bm.getBean(CaseClassificationFacadeEjbLocal.class);
		} else if (PointOfEntryFacade.class == clazz) {
			return (P) bm.getBean(PointOfEntryFacadeEjbLocal.class);
		}
		return null;
	}
}