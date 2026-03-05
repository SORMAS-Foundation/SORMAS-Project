package de.symeda.sormas.backend.patch.mapping.impl.groupedfieldmapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.SinglePatchResult;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsMapper;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsRequest;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsResponse;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.patch.PatchFieldHelper;
import de.symeda.sormas.backend.patch.mapping.ValueMapperRegistry;

@ApplicationScoped
public class ImmunizationGroupedFieldMapper implements GroupedFieldsMapper<ImmunizationDto> {

	private static final Set<String> SUPPORTED_PREFIXES = Stream.of(ImmunizationDto.I18N_PREFIX, VaccinationDto.I18N_PREFIX)
		.map(prefix -> prefix + PatchFieldHelper.PATH_SEPARATOR)
		.collect(Collectors.toSet());

	@Inject
	private ValueMapperRegistry valueMapperRegistry;

	@Override
	public GroupedFieldsResponse<ImmunizationDto> aggregatedPatch(GroupedFieldsRequest request) {
		// TODO: use a field as reference to trigger one or another logic: Immunization.immunizationStatus

		Map<String, Object> originalPatchDictionary = request.getPartialPatchDictionary();

		ImmunizationDto build = ImmunizationDto.build(request.getPerson());
		build.setDisease(request.getDisease());

		Object immunizationStatus = originalPatchDictionary.get(ImmunizationDto.IMMUNIZATION_STATUS);
		if (immunizationStatus == null) {
			return new GroupedFieldsResponse<ImmunizationDto>().setPatchingResults(
				originalPatchDictionary.entrySet()
					.stream()
					.map(
						entry -> new SinglePatchResult().setFieldName(entry.getKey())
							.setFailure(
								new DataPatchFailure().setProvidedFieldValue(entry.getValue())
									.setDataPatchFailureCause(DataPatchFailureCause.MISSING_MANDATORY_FIELD_FOR_GROUP)))
					.collect(Collectors.toList()));
		}

		String immunizationStatusString = immunizationStatus.toString();

		valueMapperRegistry.map(new ValuePatchRequest<Boolean>().setValue(immunizationStatus).setTargetType(Boolean.class));

		// TODO: use (Field ID) Vaccination.vaccineType: to determine if it is a vaccine for the mother.

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

		GroupedFieldsResponse<ImmunizationDto> groupedFieldsResponse =
			new GroupedFieldsResponse<ImmunizationDto>().setEntityDto(build).setPatchingResults(null);
		return groupedFieldsResponse;
	}

	@Override
	public Set<String> aggregatedPrefixes() {
		return SUPPORTED_PREFIXES;
	}
}
