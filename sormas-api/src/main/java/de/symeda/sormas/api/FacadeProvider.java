package de.symeda.sormas.api;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.user.UserFacade;

public class FacadeProvider {
	
	private static final String JNDI_PREFIX = "java:global/sormas-ear/sormas-backend/";

	private final InitialContext ic;

	private static FacadeProvider instance;

	private FacadeProvider() {
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
	
	public static PersonFacade getPersonFacade() {
		return get().lookupEjbRemote(PersonFacade.class);
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

	@SuppressWarnings("unchecked")
	public <P> P lookupEjbRemote(Class<P> clazz) {
		try {
			return (P)get().ic.lookup(buildJndiLookupName(clazz));
		} catch (NamingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String buildJndiLookupName(Class<?> clazz) {
		return JNDI_PREFIX + clazz.getSimpleName();
	}
}