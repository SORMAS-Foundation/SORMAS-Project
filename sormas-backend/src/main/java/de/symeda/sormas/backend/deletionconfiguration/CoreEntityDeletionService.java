package de.symeda.sormas.backend.deletionconfiguration;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.symptoms.SymptomsService;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.util.IterableHelper;

@LocalBean
@Singleton
public class CoreEntityDeletionService {

	private static final int DELETE_BATCH_SIZE = 200;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<EntityTypeFacadePair> coreEntityFacades = new ArrayList<>();

	@EJB
	private DeletionConfigurationService deletionConfigurationService;
	@EJB
	private PersonService personService;
	@EJB
	private SormasToSormasShareRequestService sormasToSormasShareRequestService;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private ShareRequestInfoService shareRequestInfoService;
	@EJB
	private SymptomsService symptomsService;
	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private ImmunizationService immunizationService;

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

	@SuppressWarnings("unchecked")
	public void executeAutomaticDeletion() {

		long startTime = DateHelper.startTime();

		// Delete CoreEntities by type
		coreEntityFacades.forEach(entityTypeFacadePair -> {
			List<DeletionConfiguration> coreEntityTypeConfigs =
				deletionConfigurationService.getCoreEntityTypeConfigs(entityTypeFacadePair.coreEntityType);

			coreEntityTypeConfigs.stream().filter(c -> c.getDeletionReference() != null && c.getDeletionPeriod() != null).forEach(c -> {

				List<String> deleteUuids = entityTypeFacadePair.entityFacade.getUuidsForAutomaticDeletion(c);
				logger.debug("executeAutomaticDeletion(): Detected deletable entities of type {}: n={}", c.getEntityType(), deleteUuids.size());
				IterableHelper.executeBatched(
					deleteUuids,
					DELETE_BATCH_SIZE,
					batchedUuids -> entityTypeFacadePair.entityFacade
						.doAutomaticDeletion(batchedUuids, supportsPermanentDeletion(entityTypeFacadePair.coreEntityType)));
			});
		});

		deleteOrphanEntities();

		logger.debug("executeAutomaticDeletion() finished. {}s", DateHelper.durationSeconds(startTime));
	}

	private void deleteOrphanEntities() {

		List<String> nonReferencedSymptoms = symptomsService.getOrphanSymptoms();
		logger.debug("executeAutomaticDeletion(): Detected non referenced symptoms: n={}", nonReferencedSymptoms.size());
		IterableHelper.executeBatched(nonReferencedSymptoms, DELETE_BATCH_SIZE, batchedUuids -> symptomsService.deletePermanentByUuids(batchedUuids));

		if (featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			List<String> orphanImmunizations = immunizationService.getOrphanImmunizations();
			logger.debug("executeAutomaticDeletion(): Detected non referenced immunizations: n={}", orphanImmunizations.size());
			IterableHelper
				.executeBatched(orphanImmunizations, DELETE_BATCH_SIZE, batchedUuids -> immunizationService.deletePermanentByUuids(batchedUuids));
		}

		// Delete non referenced Persons
		List<String> nonReferencedPersonUuids = personService.getAllNonReferencedPersonUuids();
		logger.debug("executeAutomaticDeletion(): Detected non referenced persons: n={}", nonReferencedPersonUuids.size());
		IterableHelper
			.executeBatched(nonReferencedPersonUuids, DELETE_BATCH_SIZE, batchedUuids -> personService.deletePermanentByUuids(batchedUuids));

		List<String> nonReferencedS2SShareRequestsUuids = sormasToSormasShareRequestService.getAllNonRefferencedSormasToSormasShareRequest();
		logger.debug(
			"executeAutomaticDeletion(): Detected non referenced sormasToSormasShareRequests: n={}",
			nonReferencedS2SShareRequestsUuids.size());
		IterableHelper.executeBatched(
			nonReferencedS2SShareRequestsUuids,
			DELETE_BATCH_SIZE,
			batchedUuids -> sormasToSormasShareRequestService.deletePermanentByUuids(batchedUuids));

		List<String> nonReferencedShareRequestInfoUuids = shareRequestInfoService.getAllNonReferencedShareRequestInfo();
		logger.debug("executeAutomaticDeletion(): Detected orphan ShareRequestInfo: n={}", nonReferencedShareRequestInfoUuids.size());
		IterableHelper.executeBatched(
			nonReferencedShareRequestInfoUuids,
			DELETE_BATCH_SIZE,
			batchedUuids -> shareRequestInfoService.deletePermanentByUuids(batchedUuids));
	}

	private boolean supportsPermanentDeletion(CoreEntityType coreEntityType) {
		return coreEntityType == CoreEntityType.IMMUNIZATION
			|| coreEntityType == CoreEntityType.TRAVEL_ENTRY
			|| coreEntityType == CoreEntityType.CASE
			|| coreEntityType == CoreEntityType.CONTACT
			|| coreEntityType == CoreEntityType.EVENT
			|| coreEntityType == CoreEntityType.EVENT_PARTICIPANT;
	}

	@SuppressWarnings("rawtypes")
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
}
