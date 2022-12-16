/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.ComputedForApi;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.NotExposedToApi;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.travelentry.TravelEntry;

public class EntityMappingTest {

	private static final Map<Class<? extends AbstractDomainObject>, Class<? extends EntityDto>> mappings = new HashMap<>();
	private static final List<Class<?>> mustMatchTypes = Arrays.asList(String.class, Integer.class, Long.class);

	static {
		mappings.put(Area.class, AreaDto.class);
		mappings.put(Community.class, CommunityDto.class);
		mappings.put(Continent.class, ContinentDto.class);
		mappings.put(Country.class, CountryDto.class);
		mappings.put(District.class, DistrictDto.class);
		mappings.put(Facility.class, FacilityDto.class);
		mappings.put(PointOfEntry.class, PointOfEntryDto.class);
		mappings.put(Region.class, RegionDto.class);
		mappings.put(Subcontinent.class, SubcontinentDto.class);

		mappings.put(Campaign.class, CampaignDto.class);
		mappings.put(Case.class, CaseDataDto.class);
		mappings.put(Contact.class, ContactDto.class);
		mappings.put(Event.class, EventDto.class);
		mappings.put(EventParticipant.class, EventParticipantDto.class);
		mappings.put(Immunization.class, ImmunizationDto.class);
		mappings.put(PathogenTest.class, PathogenTestDto.class);
		mappings.put(Sample.class, SampleDto.class);
		mappings.put(TravelEntry.class, TravelEntryDto.class);
	}

	@Test
	public void testDtoEntityFieldMatching() {

		mappings.forEach((entityClass, dtoClass) -> {
			final String entityName = entityClass.getSimpleName();
			final String dtoName = dtoClass.getSimpleName();

			System.out.println(entityName + " <-> " + dtoName);

			final ClassComparisonResult entityVsDto = compareClassFields(entityClass, dtoClass);
			final List<String> missingFieldsInDto = entityVsDto.getMissingFields();
			if (!missingFieldsInDto.isEmpty()) {
				System.out.println("\tMissing fields in DTO:");
				missingFieldsInDto.forEach(s -> System.out.println("\t - " + s));
			}

			final ClassComparisonResult dtoVsEntity = compareClassFields(dtoClass, entityClass);
			final List<String> missingFieldsInEntity = dtoVsEntity.getMissingFields();
			if (!missingFieldsInEntity.isEmpty()) {
				System.out.println("\tMissing fields in entity:");
				missingFieldsInEntity.forEach(s -> System.out.println("\t - " + s));
			}

			final List<String> fieldsHavingDifferentTypes = dtoVsEntity.getFieldsHavingDifferentTypes();
			if (!fieldsHavingDifferentTypes.isEmpty()) {
				System.out.println("\tFields having different types:");
				fieldsHavingDifferentTypes.forEach(s -> System.out.println("\t - " + s));
			}
		});
	}

	private ClassComparisonResult compareClassFields(Class<?> leadClass, Class<?> comparisonClass) {

		final ClassComparisonResult classComparisonResult = new ClassComparisonResult();

		final Predicate<Field> excludeConstantFields = field -> !(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()));
		final Predicate<Field> excludeFieldsNotExposedToApi = field -> !(field.isAnnotationPresent(NotExposedToApi.class));
		final Predicate<Field> excludeFieldsComputedForApi = field -> !(field.isAnnotationPresent(ComputedForApi.class));
		final List<Field> leadClassDeclaredFields = FieldUtils.getAllFieldsList(leadClass)
			.stream()
			.filter(excludeConstantFields.and(excludeFieldsComputedForApi).and(excludeFieldsNotExposedToApi))
			.collect(Collectors.toList());
		for (final Field field : leadClassDeclaredFields) {
			final String fieldName = field.getName();
			final Class<?> fieldType = field.getType();

			Field comparisonField = FieldUtils.getField(comparisonClass, fieldName, true);
			if (comparisonField == null) {
				if (!Collection.class.isAssignableFrom(fieldType)) {
					classComparisonResult.addMissingField(fieldName);
				}
			} else {
				final Class<?> comparisonFieldType = comparisonField.getType();
				if ((mustMatchTypes.contains(fieldType) || fieldType.isEnum()) && !fieldType.equals(comparisonFieldType)) {
					classComparisonResult.addDifferentTypeField(fieldName);
				}
			}
		}
		return classComparisonResult;
	}

	public static class ClassComparisonResult {

		final List<String> missingFields = new ArrayList<>();
		final List<String> fieldsHavingDifferentTypes = new ArrayList<>();

		public void addMissingField(String fieldName) {
			missingFields.add(fieldName);
		}

		public void addDifferentTypeField(String fieldName) {
			fieldsHavingDifferentTypes.add(fieldName);
		}

		public List<String> getMissingFields() {
			return missingFields;
		}

		public List<String> getFieldsHavingDifferentTypes() {
			return fieldsHavingDifferentTypes;
		}
	}

}
