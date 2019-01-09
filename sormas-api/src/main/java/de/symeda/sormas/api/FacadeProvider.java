/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api;

import javax.naming.InitialContext;
import javax.naming.NamingException;

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
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleTestFacade;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRoleConfigFacade;
import de.symeda.sormas.api.visit.VisitFacade;

public class FacadeProvider {

	private static final String JNDI_PREFIX = "java:global/sormas-ear/sormas-backend/";

	private final InitialContext ic;

	private static FacadeProvider instance;

	protected FacadeProvider() {
		try {
			ic = new InitialContext();
		} catch (NamingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static FacadeProvider get() {
		if (instance == null) {
			instance = new FacadeProvider();
		}
		return instance;
	}

	public static CaseFacade getCaseFacade() {
		return get().lookupEjbRemote(CaseFacade.class);
	}

	public static ContactFacade getContactFacade() {
		return get().lookupEjbRemote(ContactFacade.class);
	}

	public static EventFacade getEventFacade() {
		return get().lookupEjbRemote(EventFacade.class);
	}

	public static EventParticipantFacade getEventParticipantFacade() {
		return get().lookupEjbRemote(EventParticipantFacade.class);
	}

	public static VisitFacade getVisitFacade() {
		return get().lookupEjbRemote(VisitFacade.class);
	}

	public static PersonFacade getPersonFacade() {
		return get().lookupEjbRemote(PersonFacade.class);
	}

	public static TaskFacade getTaskFacade() {
		return get().lookupEjbRemote(TaskFacade.class);
	}

	public static SampleFacade getSampleFacade() {
		return get().lookupEjbRemote(SampleFacade.class);
	}

	public static SampleTestFacade getSampleTestFacade() {
		return get().lookupEjbRemote(SampleTestFacade.class);
	}

	public static SymptomsFacade getSymptomsFacade() {
		return get().lookupEjbRemote(SymptomsFacade.class);
	}

	public static FacilityFacade getFacilityFacade() {
		return get().lookupEjbRemote(FacilityFacade.class);
	}

	public static RegionFacade getRegionFacade() {
		return get().lookupEjbRemote(RegionFacade.class);
	}

	public static DistrictFacade getDistrictFacade() {
		return get().lookupEjbRemote(DistrictFacade.class);
	}

	public static CommunityFacade getCommunityFacade() {
		return get().lookupEjbRemote(CommunityFacade.class);
	}

	public static UserFacade getUserFacade() {
		return get().lookupEjbRemote(UserFacade.class);
	}

	public static UserRoleConfigFacade getUserRoleConfigFacade() {
		return get().lookupEjbRemote(UserRoleConfigFacade.class);
	}

	public static HospitalizationFacade getHospitalizationFacade() {
		return get().lookupEjbRemote(HospitalizationFacade.class);
	}

	public static EpiDataFacade getEpiDataFacade() {
		return get().lookupEjbRemote(EpiDataFacade.class);
	}

	public static WeeklyReportFacade getWeeklyReportFacade() {
		return get().lookupEjbRemote(WeeklyReportFacade.class);
	}

	public static GeoShapeProvider getGeoShapeProvider() {
		return get().lookupEjbRemote(GeoShapeProvider.class);
	}

	public static OutbreakFacade getOutbreakFacade() {
		return get().lookupEjbRemote(OutbreakFacade.class);
	}

	public static ConfigFacade getConfigFacade() {
		return get().lookupEjbRemote(ConfigFacade.class);
	}

	public static ExportFacade getExportFacade() {
		return get().lookupEjbRemote(ExportFacade.class);
	}

	public static ImportFacade getImportFacade() {
		return get().lookupEjbRemote(ImportFacade.class);
	}

	public static CaseClassificationFacade getCaseClassificationFacade() {
		return get().lookupEjbRemote(CaseClassificationFacade.class);
	}

	@SuppressWarnings("unchecked")
	public <P> P lookupEjbRemote(Class<P> clazz) {
		try {
			return (P) get().ic.lookup(buildJndiLookupName(clazz));
		} catch (NamingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String buildJndiLookupName(Class<?> clazz) {
		return JNDI_PREFIX + clazz.getSimpleName();
	}
}