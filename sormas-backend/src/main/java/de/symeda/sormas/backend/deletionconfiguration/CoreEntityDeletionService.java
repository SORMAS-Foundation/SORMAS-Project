package de.symeda.sormas.backend.deletionconfiguration;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;

import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;

@LocalBean
@Singleton
public class CoreEntityDeletionService {

	private static final int DELETE_BATCH_SIZE = 200;

	private final List<EntityTypeFacadePair> coreEntityFacades = new ArrayList<>();

	@EJB
	private DeletionConfigurationService deletionConfigurationService;
	@EJB
	private PersonService personService;

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

		coreEntityFacades.forEach(entityTypeFacadePair -> {
			List<DeletionConfiguration> coreEntityTypeConfigs =
				deletionConfigurationService.getCoreEntityTypeConfigs(entityTypeFacadePair.coreEntityType);

			coreEntityTypeConfigs.stream().filter(c -> c.getDeletionReference() != null && c.getDeletionPeriod() != null).forEach(c -> {
				entityTypeFacadePair.entityFacade
					.executeAutomaticDeletion(c, supportsPermanentDeletion(entityTypeFacadePair.coreEntityType), DELETE_BATCH_SIZE);
			});
		});

		personService.deleteUnreferencedPersons(DELETE_BATCH_SIZE);
	}

	private static final class EntityTypeFacadePair {

		private final CoreEntityType coreEntityType;
		private final AbstractCoreFacadeEjb entityFacade;

		private EntityTypeFacadePair(CoreEntityType coreEntityType, AbstractCoreFacadeEjb entityFacade) {
			this.coreEntityType = coreEntityType;
			this.entityFacade = entityFacade;
		}

		public static EntityTypeFacadePair of(CoreEntityType coreEntityType, AbstractCoreFacadeEjb entityFacade) {
			return new EntityTypeFacadePair(coreEntityType, entityFacade);
		}
	}

	private boolean supportsPermanentDeletion(CoreEntityType coreEntityType) {
		return coreEntityType == CoreEntityType.IMMUNIZATION
			|| coreEntityType == CoreEntityType.TRAVEL_ENTRY
			|| coreEntityType == CoreEntityType.CASE;
	}
}
