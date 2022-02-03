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
import static de.symeda.sormas.api.docgeneneration.RootEntityType.ROOT_TRAVEL_ENTRY;

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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
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
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationReferenceDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb;
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
	private UserFacadeEjb.UserFacadeEjbLocal userFacade;
	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacadeEjb;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	public DocumentTemplateEntities getQuarantineOrderEntities(
		DocumentWorkflow workflow,
		ReferenceDto rootEntityRef,
		SampleReferenceDto sampleRef,
		PathogenTestReferenceDto pathogenTestRef,
		VaccinationReferenceDto vaccinationRef)
		throws DocumentTemplateException {

		SampleDto sample = null;
		if (sampleRef != null) {
			sample = sampleFacadeEjb.getSampleByUuid(sampleRef.getUuid());
		}

		PathogenTestDto pathogenTest = null;
		if (pathogenTestRef != null) {
			pathogenTest = pathogenTestFacade.getByUuid(pathogenTestRef.getUuid());
		}

		VaccinationDto vaccination = null;
		if (vaccinationRef != null) {
			vaccination = vaccinationFacade.getByUuid(vaccinationRef.getUuid());
		}

		String rootEntityUuid = rootEntityRef.getUuid();

		switch (workflow) {
		case QUARANTINE_ORDER_CASE:
			CaseDataDto caseDataDto = caseFacade.getCaseDataByUuid(rootEntityUuid);

			return buildEntities(RootEntityType.ROOT_CASE, caseDataDto, caseDataDto.getPerson(), sample, pathogenTest, vaccination);

		case QUARANTINE_ORDER_CONTACT:
			ContactDto contactDto = contactFacade.getContactByUuid(rootEntityUuid);

			return buildEntities(ROOT_CONTACT, contactDto, contactDto.getPerson(), sample, pathogenTest, vaccination);

		case QUARANTINE_ORDER_EVENT_PARTICIPANT:
			EventParticipantDto eventParticipantDto = eventParticipantFacade.getByUuid(rootEntityUuid);

			return buildEntities(
				ROOT_EVENT_PARTICIPANT,
				eventParticipantDto,
				eventParticipantDto.getPerson().toReference(),
				sample,
				pathogenTest,
				vaccination);

		case QUARANTINE_ORDER_TRAVEL_ENTRY:
			TravelEntryDto travelEntryDto = travelEntryFacade.getByUuid(rootEntityUuid);

			return buildEntities(ROOT_TRAVEL_ENTRY, travelEntryDto, travelEntryDto.getPerson(), null, null, null);

		default:
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorQuarantineOnlySupportedEntities));
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

			Map<String, PathogenTestDto> pathogenTests = samples.size() > 0
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
					vaccinations = vaccinationFacade.getLatestByPersons(
						mainRootEntities.stream().map(getEntityPerson).collect(Collectors.toList()),
						diseases.iterator().next());
				} else {
					vaccinations = Collections.emptyMap();
				}
			} else {
				vaccinations = Collections.emptyMap();
			}

			return mainRootEntities.stream().map(e -> {
				SampleDto sample = samples.get(e.getUuid());
				PathogenTestDto pathogenTest = sample != null ? pathogenTests.get(sample.getUuid()) : null;
				VaccinationDto vaccination = vaccinations.get(getEntityPerson.apply(e).getUuid());

				return new AbstractMap.SimpleEntry<>(
					createReference.apply(e),
					buildEntities(mainRootEntityType, e, getEntityPerson.apply(e), sample, pathogenTest, vaccination));
			}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
		}

	}

	private DocumentTemplateEntities buildEntities(
		RootEntityType mainRootEntityType,
		EntityDto mainRootEntity,
		PersonReferenceDto person,
		SampleDto sample,
		PathogenTestDto pathogenTest,
		VaccinationDto vaccination) {
		DocumentTemplateEntities entities = new DocumentTemplateEntities();

		entities.addEntity(mainRootEntityType, mainRootEntity);
		entities.addEntity(RootEntityType.ROOT_PERSON, person);

		if (sample != null) {
			entities.addEntity(RootEntityType.ROOT_SAMPLE, sample);
		}

		if (pathogenTest != null) {
			entities.addEntity(RootEntityType.ROOT_PATHOGEN_TEST, pathogenTest);
		}

		if (vaccination != null) {
			entities.addEntity(RootEntityType.ROOT_VACCINATION, vaccination);
		}

		entities.addEntity(RootEntityType.ROOT_USER, userFacade.getCurrentUser());

		return entities;
	}
}
