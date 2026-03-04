package de.symeda.sormas.backend.patch.mapping.impl.groupedfieldmapper;

import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.patch.mapping.GroupedFieldsMapper;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsRequest;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;

@ApplicationScoped
public class ImmunizationGroupedFieldMapper implements GroupedFieldsMapper {

	@Override
	public Map<String, ValueMappingResult<Object>> aggregatedPatch(GroupedFieldsRequest request) {
		// TODO: use a field as reference to trigger one or another logic: Immunization.immunizationStatus

		/*
		 * Implementation steps:
		 * - Retrieve value for: 'Immunization.immunizationStatus'
		 * - Try to detect what is it:
		 * - Yes: must be a vaccine that will be specified in the rest of the object
		 * - No OR don't know: create "dummy-object" that says:
		 * result.setImmunizationStatus(ImmunizationStatus.NOT_ACQUIRED).setMeansOfImmunization(MeansOfImmunization.OTHER);
		 * - no: result.setMeansOfImmunizationDetails("NOT_VACCINATED")
		 * - don't know: result.setMeansOfImmunizationDetails("DON'T KNOW")
		 * YES detailed explanation:
		 * - create ImmunizationDto
		 * - create VaccineDto
		 * - Patch the remaining values as usual ?
		 * - Can use the DataPatcher again to be able to set all single field values: values or exact fields. (focus on values now)
		 */

		return Map.of();
	}

	@Override
	public Set<String> aggregatedPrefixes() {
		if (true) {
			// TODO: specifiy adequate fields.
			throw new IllegalStateException();
		}
		return Set.of();
	}
}
