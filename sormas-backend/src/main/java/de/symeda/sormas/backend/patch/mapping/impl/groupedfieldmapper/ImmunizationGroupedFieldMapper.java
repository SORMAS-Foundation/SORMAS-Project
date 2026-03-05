package de.symeda.sormas.backend.patch.mapping.impl.groupedfieldmapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsMapper;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsRequest;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.patch.PatchFieldHelper;

@ApplicationScoped
public class ImmunizationGroupedFieldMapper implements GroupedFieldsMapper {

	private static final Set<String> SUPPORTED_PREFIXES = Stream.of(ImmunizationDto.I18N_PREFIX, VaccinationDto.I18N_PREFIX)
		.map(prefix -> prefix + PatchFieldHelper.PATH_SEPARATOR)
		.collect(Collectors.toSet());

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
		return SUPPORTED_PREFIXES;
	}
}
