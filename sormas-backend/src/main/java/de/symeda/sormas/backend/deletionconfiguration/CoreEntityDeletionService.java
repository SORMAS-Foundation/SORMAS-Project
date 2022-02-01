package de.symeda.sormas.backend.deletionconfiguration;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;

import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;

@LocalBean
@Singleton
public class CoreEntityDeletionService {

	private final List<EntityTypeFacadePair> coreEntityFacades = new ArrayList<>();

	@EJB
	private DeletionConfigurationService deletionConfigurationService;

	public CoreEntityDeletionService() {
	}

	@Inject
	public CoreEntityDeletionService(
		CaseFacadeEjb.CaseFacadeEjbLocal caseFacadeEjb,
		ContactFacadeEjb.ContactFacadeEjbLocal contactFacadeEjb,
		EventFacadeEjb.EventFacadeEjbLocal eventFacadeEjb,
		EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal eventParticipantFacadeEjb,
		ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal immunizationFacadeEjb,
		TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal travelEntryFacadeEjb) {
		coreEntityFacades.add(EntityTypeFacadePair.of(CoreEntityType.CASE, caseFacadeEjb));
		coreEntityFacades.add(EntityTypeFacadePair.of(CoreEntityType.CONTACT, contactFacadeEjb));
		coreEntityFacades.add(EntityTypeFacadePair.of(CoreEntityType.EVENT, eventFacadeEjb));
		coreEntityFacades.add(EntityTypeFacadePair.of(CoreEntityType.EVENT_PARTICIPANT, eventParticipantFacadeEjb));
		coreEntityFacades.add(EntityTypeFacadePair.of(CoreEntityType.IMMUNIZATION, immunizationFacadeEjb));
		coreEntityFacades.add(EntityTypeFacadePair.of(CoreEntityType.TRAVEL_ENTRY, travelEntryFacadeEjb));
	}

	public void executeAutomaticDeletion() {

		coreEntityFacades.forEach(coreEntityType -> {
			DeletionConfiguration coreEntityTypeConfig = deletionConfigurationService.getCoreEntityTypeConfig(coreEntityType.coreEntityType);

			if (coreEntityTypeConfig.deletionReference != null && coreEntityTypeConfig.deletionPeriod != null) {
				coreEntityType.entityFacade.executeAutomaticDeletion(coreEntityTypeConfig);
			}
		});

	}

	private static final class EntityTypeFacadePair {

		private final CoreEntityType coreEntityType;
		private final AbstractCoreEntityFacade<?> entityFacade;

		private EntityTypeFacadePair(CoreEntityType coreEntityType, AbstractCoreEntityFacade<?> entityFacade) {
			this.coreEntityType = coreEntityType;
			this.entityFacade = entityFacade;
		}

		public static EntityTypeFacadePair of(CoreEntityType coreEntityType, AbstractCoreEntityFacade<?> entityFacade) {
			return new EntityTypeFacadePair(coreEntityType, entityFacade);
		}
	}
}
