/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.docgeneration;

import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_CASE;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_CONTACT;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_EVENT_PARTICIPANT;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_PATHOGEN_TEST;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_PERSON;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_SAMPLE;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_TRAVEL_ENTRY;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_USER;
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_VACCINATION;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.google.common.base.Functions;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationReferenceDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb.VaccinationFacadeEjbLocal;

@Stateless
@LocalBean
public class DocumentTemplateEntitiesBuilder {

	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;
	@EJB
	private TravelEntryFacadeEjbLocal travelEntryFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacadeEjb;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	private final Map<RootEntityType, Function<HasUuid, RootEntityResolver>> resolverFactories;

	public DocumentTemplateEntitiesBuilder() {
		resolverFactories = buildResolverFactories();
	}

	public DocumentTemplateEntities getQuarantineOrderEntities(
		RootEntityType rootEntityType,
		ReferenceDto rootEntityRef,
		SampleReferenceDto sampleRef,
		PathogenTestReferenceDto pathogenTestRef,
		VaccinationReferenceDto vaccinationRef) {

		return resolveEntities(
			new RootEntities().addReference(rootEntityType, rootEntityRef)
				.addReference(ROOT_SAMPLE, sampleRef)
				.addReference(ROOT_PATHOGEN_TEST, pathogenTestRef)
				.addReference(ROOT_VACCINATION, vaccinationRef)
				.addEntity(RootEntityType.ROOT_USER, userFacade.getCurrentUser()));
	}

	public DocumentTemplateEntities resolveEntities(RootEntities entities) {
		Map<RootEntityType, RootEntityResolver> resolvers = entities.getEntities().entrySet().stream().map(ref -> {
			RootEntities.Entity entity = ref.getValue();
			RootEntityResolver resolver =
				entity.isReference() ? resolverFactories.get(ref.getKey()).apply(entity.getValue()) : createNoopResolver(entity.getValue());
			return new DataHelper.Pair<>(ref.getKey(), resolver);
		}).collect(Collectors.toMap(DataHelper.Pair::getElement0, DataHelper.Pair::getElement1));

		DocumentTemplateEntities resolved = new DocumentTemplateEntities();
		resolvers.forEach((entityType, resolver) -> {
			Object entity = resolver.resolve();
			if (entity != null && RootEntityAndPersonRef.class.isAssignableFrom(entity.getClass()) && !entities.hasReference(ROOT_PERSON)) {
				RootEntityAndPersonRef<?> entityAndPersonRef = (RootEntityAndPersonRef<?>) entity;
				resolved.addEntity(entityType, entityAndPersonRef.rootEntity);
				resolved.addEntity(ROOT_PERSON, entityAndPersonRef.personRef);
			} else {
				resolved.addEntity(entityType, entity);
			}
		});

		return resolved;
	}

	private Map<RootEntityType, Function<HasUuid, RootEntityResolver>> buildResolverFactories() {
		return Map.ofEntries(
			Map.entry(ROOT_CASE, this::createCaseResolver),
			Map.entry(ROOT_CONTACT, this::createContactResolver),
			Map.entry(ROOT_EVENT_PARTICIPANT, this::createEventParticipantResolver),
			Map.entry(ROOT_TRAVEL_ENTRY, this::createTravelEntryResolver),
			Map.entry(ROOT_PERSON, this::createPersonResolver),
			Map.entry(ROOT_SAMPLE, this::createSampleResolver),
			Map.entry(ROOT_PATHOGEN_TEST, this::createPathogenTestResolver),
			Map.entry(ROOT_VACCINATION, this::createVaccinationResolver));
	}

	private static RootEntityResolver createNoopResolver(HasUuid entity) {
		return RootEntityResolver.createResolver(entity, Functions.identity());
	}

	public RootEntityResolver createCaseResolver(HasUuid caseRef) {
		validateReference(caseRef, CaseReferenceDto.class);
		return RootEntityResolver.createResolver(caseRef, r -> {
			CaseDataDto caze = caseFacade.getByUuid(r.getUuid());
			return new RootEntityAndPersonRef<>(caze, caze.getPerson());
		});
	}

	public RootEntityResolver createContactResolver(HasUuid contactRef) {
		validateReference(contactRef, ContactReferenceDto.class);

		return RootEntityResolver.createResolver(contactRef, r -> {
			ContactDto contact = contactFacade.getByUuid(r.getUuid());
			return new RootEntityAndPersonRef<>(contact, contact.getPerson());
		});
	}

	public RootEntityResolver createEventParticipantResolver(HasUuid eventParticipantRef) {
		validateReference(eventParticipantRef, EventParticipantReferenceDto.class);

		return RootEntityResolver.createResolver(eventParticipantRef, r -> {
			EventParticipantDto eventParticipant = eventParticipantFacade.getByUuid(r.getUuid());
			return new RootEntityAndPersonRef<>(eventParticipant, eventParticipant.getPerson().toReference());
		});
	}

	public RootEntityResolver createTravelEntryResolver(HasUuid travelEntryRef) {
		validateReference(travelEntryRef, TravelEntryReferenceDto.class);

		return RootEntityResolver.createResolver(travelEntryRef, r -> {
			TravelEntryDto travelEntry = travelEntryFacade.getByUuid(r.getUuid());
			return new RootEntityAndPersonRef<>(travelEntry, travelEntry.getPerson());
		});
	}

	public RootEntityResolver createPersonResolver(HasUuid personRef) {
		validateReference(personRef, PersonReferenceDto.class);

		return RootEntityResolver.createResolver(personRef, r -> personFacade.getByUuid(personRef.getUuid()));
	}

	public RootEntityResolver createSampleResolver(HasUuid sampleRef) {
		validateReference(sampleRef, SampleReferenceDto.class);

		return RootEntityResolver.createResolver(sampleRef, r -> sampleFacadeEjb.getSampleByUuid(r.getUuid()));
	}

	public RootEntityResolver createPathogenTestResolver(HasUuid pathogenTestRef) {
		validateReference(pathogenTestRef, PathogenTestReferenceDto.class);

		return RootEntityResolver.createResolver(pathogenTestRef, r -> pathogenTestFacade.getByUuid(r.getUuid()));
	}

	public RootEntityResolver createVaccinationResolver(HasUuid vaccinationTestRef) {
		validateReference(vaccinationTestRef, VaccinationReferenceDto.class);

		return RootEntityResolver.createResolver(vaccinationTestRef, r -> vaccinationFacade.getByUuid(r.getUuid()));
	}

	private static <R> void validateReference(HasUuid referenceDto, Class<R> referenceType) {
		if (referenceDto != null && !referenceType.isAssignableFrom(referenceDto.getClass())) {
			throw new IllegalArgumentException("reference must be of type " + referenceType.getSimpleName());
		}
	}

	public Map<ReferenceDto, DocumentTemplateEntities> getQuarantineOrderEntities(DocumentWorkflow workflow, List<ReferenceDto> referenceDtos)
		throws DocumentTemplateException {
		List<String> entityUuids = referenceDtos.stream().map(ReferenceDto::getUuid).collect(Collectors.toList());

		final BulkEntitiesBuilder<? extends EntityDto> builder;
		switch (workflow) {
		case QUARANTINE_ORDER_CASE:
			builder = createBulkCaseEntitiesBuilder(entityUuids);

			break;
		case QUARANTINE_ORDER_CONTACT:
			builder = createBulkContactEntitiesBuilder(entityUuids);

			break;
		case QUARANTINE_ORDER_EVENT_PARTICIPANT:
			throw new RuntimeException(
				"getQuarantineOrderEntities should not be called for event participants; @see getEventParticipantQuarantineOrderEntities");
		default:
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorQuarantineBulkOnlySupportedEntities));
		}

		return builder.build();
	}

	public Map<ReferenceDto, DocumentTemplateEntities> getEventParticipantQuarantineOrderEntities(
		List<EventParticipantReferenceDto> referenceDtos,
		Disease eventDisease)
		throws DocumentTemplateException {
		List<String> entityUuids = referenceDtos.stream().map(ReferenceDto::getUuid).collect(Collectors.toList());

		return createBulkEventParticipantEntitiesBuilder(entityUuids, eventDisease).build();
	}

	private BulkEntitiesBuilder<CaseDataDto> createBulkCaseEntitiesBuilder(List<String> caseUuids) {

		return new BulkEntitiesBuilder<>(
			ROOT_CASE,
			caseFacade.getByUuids(caseUuids),
			CaseDataDto::toReference,
			CaseDataDto::getPerson,
			new SampleCriteria().caseUuids(caseUuids),
			Sample::getAssociatedCase,
			SampleDto::getAssociatedCase,
			CaseDataDto::getDisease);
	}

	private BulkEntitiesBuilder<ContactDto> createBulkContactEntitiesBuilder(List<String> contactUuids) {

		return new BulkEntitiesBuilder<>(
			ROOT_CONTACT,
			contactFacade.getByUuids(contactUuids),
			ContactDto::toReference,
			ContactDto::getPerson,
			new SampleCriteria().contactUuids(contactUuids),
			Sample::getAssociatedContact,
			SampleDto::getAssociatedContact,
			ContactDto::getDisease);
	}

	private BulkEntitiesBuilder<EventParticipantDto> createBulkEventParticipantEntitiesBuilder(List<String> eventParticipantUuids, Disease disease) {

		return new BulkEntitiesBuilder<>(
			ROOT_EVENT_PARTICIPANT,
			eventParticipantFacade.getByUuids(eventParticipantUuids),
			EventParticipantDto::toReference,
			(EventParticipantDto e) -> e.getPerson().toReference(),
			new SampleCriteria().eventParticipantUuids(eventParticipantUuids),
			Sample::getAssociatedEventParticipant,
			SampleDto::getAssociatedEventParticipant,
			(ep) -> disease);
	}

	final class BulkEntitiesBuilder<T extends EntityDto> {

		private final RootEntityType mainRootEntityType;
		private final List<T> mainRootEntities;
		private final Function<T, ReferenceDto> createReference;
		private final Function<T, PersonReferenceDto> getEntityPerson;
		private final SampleCriteria sampleCriteria;
		private final Function<Sample, AbstractDomainObject> sampleAssociatedObjectFn;
		private final Function<SampleDto, ReferenceDto> sampleDtoAssociatedObjectFn;
		private final Function<T, Disease> getEntityDisease;

		private BulkEntitiesBuilder(
			RootEntityType mainRootEntityType,
			List<T> mainRootEntities,
			Function<T, ReferenceDto> createReference,
			Function<T, PersonReferenceDto> getEntityPerson,
			SampleCriteria sampleCriteria,
			Function<Sample, AbstractDomainObject> sampleAssociatedObjectFn,
			Function<SampleDto, ReferenceDto> sampleDtoAssociatedObjectFn,
			Function<T, Disease> getEntityDisease) {
			this.mainRootEntityType = mainRootEntityType;
			this.mainRootEntities = mainRootEntities;
			this.createReference = createReference;
			this.getEntityPerson = getEntityPerson;
			this.sampleCriteria = sampleCriteria;
			this.sampleAssociatedObjectFn = sampleAssociatedObjectFn;
			this.sampleDtoAssociatedObjectFn = sampleDtoAssociatedObjectFn;
			this.getEntityDisease = getEntityDisease;
		}

		Map<ReferenceDto, DocumentTemplateEntities> build() throws DocumentTemplateException {
			Map<String, SampleDto> samples = sampleFacadeEjb.getPositiveOrLatest(sampleCriteria, sampleAssociatedObjectFn)
				.stream()
				.collect(Collectors.toMap(s -> sampleDtoAssociatedObjectFn.apply(s).getUuid(), s -> s));

			Map<String, PathogenTestDto> pathogenTests = !samples.isEmpty()
				? pathogenTestFacade.getPositiveOrLatest(samples.values().stream().map(SampleDto::getUuid).collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(t -> t.getSample().getUuid(), s -> s))
				: new HashMap<>(0);

			Set<Disease> diseases = mainRootEntities.stream().map(getEntityDisease).collect(Collectors.toSet());
			final Map<String, VaccinationDto> vaccinations;
			if (featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				if (diseases.size() > 1) {
					throw new DocumentTemplateException(I18nProperties.getString(Strings.errorDocumentGenerationMultipleDiseasses));
				} else if (!diseases.isEmpty()) {
					vaccinations = vaccinationFacade
						.getLatestByPersons(mainRootEntities.stream().map(getEntityPerson).collect(Collectors.toList()), diseases.iterator().next());
				} else {
					vaccinations = Collections.emptyMap();
				}
			} else {
				vaccinations = Collections.emptyMap();
			}

			return mainRootEntities.stream().map(entity -> {
				SampleDto sample = samples.get(entity.getUuid());
				PathogenTestDto pathogenTest = sample != null ? pathogenTests.get(sample.getUuid()) : null;
				PersonReferenceDto person = getEntityPerson.apply(entity);
				VaccinationDto vaccination = vaccinations.get(person.getUuid());

				return new AbstractMap.SimpleEntry<>(
					createReference.apply(entity),
					resolveEntities(
						new RootEntities().addEntity(mainRootEntityType, entity)
							.addEntity(ROOT_PERSON, person)
							.addEntity(ROOT_SAMPLE, sample)
							.addEntity(ROOT_PATHOGEN_TEST, pathogenTest)
							.addEntity(ROOT_VACCINATION, vaccination)
							.addEntity(ROOT_USER, userFacade.getCurrentUser())));
			}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
		}
	}

	public interface RootEntityResolver {

		Object resolve();

		static <T extends HasUuid, R> RootEntityResolver createResolver(T reference, Function<T, R> resolve) {
			return () -> reference != null ? resolve.apply(reference) : null;
		}
	}

	private class RootEntityAndPersonRef<T> {

		private final T rootEntity;
		private PersonReferenceDto personRef;

		public RootEntityAndPersonRef(T rootEntity, PersonReferenceDto personRef) {
			this.rootEntity = rootEntity;
			this.personRef = personRef;
		}
	}
}
