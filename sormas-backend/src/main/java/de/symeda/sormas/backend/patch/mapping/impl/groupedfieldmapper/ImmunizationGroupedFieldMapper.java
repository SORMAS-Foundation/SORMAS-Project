package de.symeda.sormas.backend.patch.mapping.impl.groupedfieldmapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.SinglePatchResult;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsMapper;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsRequest;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsResponse;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.patch.DataPatcherImpl;
import de.symeda.sormas.backend.patch.PatchFieldHelper;
import de.symeda.sormas.backend.patch.mapping.ValueMapperRegistry;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.InstanceProvider;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class ImmunizationGroupedFieldMapper implements GroupedFieldsMapper<ImmunizationDto> {

	private final static Logger logger = LoggerFactory.getLogger(ImmunizationGroupedFieldMapper.class);

	public static final String IMMUNIZATION_STATUS_KEY = ImmunizationDto.I18N_PREFIX + "." + ImmunizationDto.IMMUNIZATION_STATUS;
	public static final String MEANS_IMMUNIZATION_KEY = ImmunizationDto.I18N_PREFIX + "." + ImmunizationDto.MEANS_OF_IMMUNIZATION;
	private static final Set<String> SUPPORTED_PREFIXES = Stream.of(ImmunizationDto.I18N_PREFIX, VaccinationDto.I18N_PREFIX)
		.map(prefix -> prefix + PatchFieldHelper.PATH_SEPARATOR)
		.collect(Collectors.toSet());

	public static final String UNKNOWN_STATE = StringNormalizer.normalize("UNKNOWN");
	public static final String YES_UNKNOWN_VACCINE_STATE = StringNormalizer.normalize("YES_UNKNOWN");
	public static final String VACCINATION_VACCINE_NAME_KEY = VaccinationDto.I18N_PREFIX + "." + VaccinationDto.VACCINE_NAME;

	@Inject
	private ValueMapperRegistry valueMapperRegistry;

	@EJB
	private UserFacadeEjb.UserFacadeEjbLocal userFacade;

	@Inject
	private DataPatcherImpl dataPatcher;

	@Override
	public GroupedFieldsResponse<ImmunizationDto> aggregatedPatch(GroupedFieldsRequest request) {
		// TODO: use a field as reference to trigger one or another logic: Immunization.immunizationStatus

		Map<String, Object> originalPatchDictionary = request.getPartialPatchDictionary();

		ImmunizationDto build = ImmunizationDto.build(request.getPerson().get());
		build.setDisease(request.getDisease());

		// TODO: mother vaccine
		// TODO: case for NO.
		// TODO: some additional hack could be added for mother vaccine: RSV: could be another ImmunizationDto

		Object immunizationStatus = originalPatchDictionary.get(IMMUNIZATION_STATUS_KEY);
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

		// TODO: add check for vaccination.
		Object meansOfImmunization = originalPatchDictionary.get(ImmunizationDto.I18N_PREFIX + "." + ImmunizationDto.MEANS_OF_IMMUNIZATION);

		// TODO: also mother
		Optional<ImmunizationDto> maternalImmunization = Optional.empty();
		if (meansOfImmunization != null) {
			ValueMappingResult<MeansOfImmunization> meansOfImmunizationValueMappingResult =
				getValueAsTarget(request, meansOfImmunization, MeansOfImmunization.class);

			if (meansOfImmunizationValueMappingResult.getData() == MeansOfImmunization.MATERNAL_VACCINATION) {
				ImmunizationDto immunizationDto = buildImmunizationFrom(request);
				immunizationDto.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
				immunizationDto.setMeansOfImmunization(MeansOfImmunization.MATERNAL_VACCINATION);

				maternalImmunization = Optional.of(immunizationDto);
			}
		}

		// TODO: remove this, use YesNoUnknow enum instead.
		ValueMappingResult<Boolean> booleanResult = getValueAsTarget(request, immunizationStatus, Boolean.class);

		if (Boolean.TRUE.equals(booleanResult.getData())) {
			ImmunizationDto immunizationDto = buildImmunizationFrom(request);
			immunizationDto.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
			immunizationDto.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
			VaccinationDto vaccinationDto = buildVaccine();
			immunizationDto.setVaccinations(List.of(vaccinationDto));

			return new GroupedFieldsResponse<ImmunizationDto>().setEntityDto(buildEntityList(immunizationDto, maternalImmunization))
				.setPatchingResults(
					Stream.concat(patch(request, immunizationDto).stream(), patch(request, vaccinationDto).stream()).collect(Collectors.toList()));
		}

		ValueMappingResult<String> stringResult = getValueAsTarget(request, immunizationStatus, String.class);

		ValueMappingResult<YesNoUnknown> enumYesNoUnknown = getValueAsTarget(request, immunizationStatus, YesNoUnknown.class);

		String dataAsString = stringResult.getData();
		if (YES_UNKNOWN_VACCINE_STATE.equals(StringNormalizer.normalize(dataAsString))) {
			ImmunizationDto immunizationDto = buildImmunizationFrom(request);
			immunizationDto.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
			immunizationDto.setMeansOfImmunization(MeansOfImmunization.VACCINATION);

			VaccinationDto vaccinationDto = buildVaccine();
			vaccinationDto.setVaccineName(Vaccine.UNKNOWN);

			immunizationDto.setVaccinations(List.of(vaccinationDto));

			return new GroupedFieldsResponse<ImmunizationDto>().setEntityDto(buildEntityList(immunizationDto, maternalImmunization))
				.setPatchingResults(
					Stream.concat(patch(request, immunizationDto).stream(), patch(request, vaccinationDto).stream()).collect(Collectors.toList()));

		} else if (UNKNOWN_STATE.equals(StringNormalizer.normalize(dataAsString)) || enumYesNoUnknown.getData() == YesNoUnknown.NO) {
			ImmunizationDto immunizationDto = buildImmunizationFrom(request);
			immunizationDto.setImmunizationStatus(ImmunizationStatus.NOT_ACQUIRED);

			return new GroupedFieldsResponse<ImmunizationDto>().setEntityDto(buildEntityList(immunizationDto, maternalImmunization))
				.setPatchingResults(patch(request, immunizationDto));
		} else {
			ImmunizationDto immunizationDto = buildImmunizationFrom(request);
			immunizationDto.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
			immunizationDto.setMeansOfImmunization(MeansOfImmunization.VACCINATION);

			VaccinationDto vaccinationDto = buildVaccine();
			vaccinationDto.setVaccineName(Vaccine.OTHER);

			vaccinationDto.setOtherVaccineName(dataAsString);

			immunizationDto.setVaccinations(List.of(vaccinationDto));

			return new GroupedFieldsResponse<ImmunizationDto>().setEntityDto(buildEntityList(immunizationDto, maternalImmunization))
				.setPatchingResults(
					Stream.concat(patch(request, immunizationDto).stream(), patch(request, vaccinationDto).stream()).collect(Collectors.toList()));
		}

		// TODO: use (Field ID) Vaccination.vaccineType: to determine if it is a vaccine for the mother.

		/*
		 * Implementation steps:
		 * - Retrieve value for: 'Immunization.immunizationStatus'
		 * - Try to detect what is it:
		 * - Yes: must be a vaccine that will be specified in the rest of the object
		 * - No OR don't know: create "dummy-object" that says:
		 * booleanResult.setImmunizationStatus(ImmunizationStatus.NOT_ACQUIRED).setMeansOfImmunization(MeansOfImmunization.OTHER);
		 * - no: booleanResult.setMeansOfImmunizationDetails("NOT_VACCINATED")
		 * - don't know: booleanResult.setMeansOfImmunizationDetails("DON'T KNOW")
		 * YES detailed explanation:
		 * - create ImmunizationDto
		 * - create VaccineDto
		 * - Patch the remaining values as usual ?
		 * - Can use the DataPatcher again to be able to set all single field values: values or exact fields. (focus on values now)
		 */

	}

	private static @NotNull List<ImmunizationDto> buildEntityList(ImmunizationDto immunizationDto, Optional<ImmunizationDto> maternalImmunization) {
		return Stream.of(Optional.of(immunizationDto), maternalImmunization).flatMap(Optional::stream).collect(Collectors.toList());
	}

	@NotNull
	private VaccinationDto buildVaccine() {
		VaccinationDto vaccinationDto = new VaccinationDto();

		vaccinationDto.setVaccineName(Vaccine.OTHER);
		vaccinationDto.setReportDate(new Date());
		vaccinationDto.setReportingUser(userFacade.getCurrentUserAsReference());

		vaccinationDto.setHealthConditions(HealthConditionsDto.build());

		return vaccinationDto;
	}

	@NotNull
	private ImmunizationDto buildImmunizationFrom(GroupedFieldsRequest request) {
		ImmunizationDto immunizationDto = new ImmunizationDto();

		immunizationDto.setRelatedCase(request.getCaseData());
		immunizationDto.setPerson(request.getPerson().get());
		immunizationDto.setReportDate(new Date());
		immunizationDto.setDisease(request.getDisease());

		// TODO: this is not correct and hardcoded.
		logger.error("User ist still harcoded for now");
		immunizationDto.setReportingUser(userFacade.getCurrentUserAsReference());

		return immunizationDto;
	}

	private @NotNull List<SinglePatchResult> patch(GroupedFieldsRequest request, ImmunizationDto immunizationDto) {
		return Stream.concat(
			request.getPartialPatchDictionary()
				.entrySet()
				.stream()
				.filter(entry -> !IMMUNIZATION_STATUS_KEY.equals(entry.getKey()))
				.filter(entry -> entry.getKey().startsWith(ImmunizationDto.I18N_PREFIX))
				// TODO: if needed put back single mapping
//					.map(
//						entry -> dataPatcher.produceSinglePatchResult(
//							new CaseDataPatchRequest().setReplacementStrategy(request.getReplacementStrategy()),
//							new Tuple<>(entry.getKey(), new Tuple<>(null, entry.getValue())),
//							request.getDisease(),
//							() -> immunizationDto)),
				.map(a -> new SinglePatchResult().setFieldName(a.getKey()).setValue(a.getValue())),
			Stream.of(new SinglePatchResult().setFieldName(IMMUNIZATION_STATUS_KEY).setValue(immunizationDto.getImmunizationStatus())))
			.collect(Collectors.toList());
	}

	private static @NotNull List<SinglePatchResult> patch(GroupedFieldsRequest request, VaccinationDto vaccineDto) {
		DataPatcherImpl dataPatcher = InstanceProvider.getInstanceFor(DataPatcherImpl.class);
		return Stream.concat(
			request.getPartialPatchDictionary()
				.entrySet()
				.stream()
				.filter(entry -> !VACCINATION_VACCINE_NAME_KEY.equals(entry.getKey()))
				.filter(entry -> entry.getKey().startsWith(VaccinationDto.I18N_PREFIX))
//					.map(
//						entry -> dataPatcher.produceSinglePatchResult(
//							new CaseDataPatchRequest().setReplacementStrategy(request.getReplacementStrategy()),
//							new Tuple<>(entry.getKey(), new Tuple<>(null, entry.getValue())),
//							request.getDisease(),
//							() -> vaccineDto)),
				.map(a -> new SinglePatchResult().setFieldName(a.getKey()).setValue(a.getValue())),

			Stream.of(new SinglePatchResult().setFieldName(VACCINATION_VACCINE_NAME_KEY).setValue(vaccineDto.getVaccineName())))
			.collect(Collectors.toList());
	}

	private <T> ValueMappingResult<T> getValueAsTarget(GroupedFieldsRequest request, Object immunizationStatus, Class<T> targetType) {
		return valueMapperRegistry.map(
			new ValuePatchRequest<T>().setValue(immunizationStatus)
				.setInputLanguages(request.getInputLanguages())
				.setAllowFallbackValues(request.isAllowFallbackValues())
				.setTargetType(targetType));
	}

	@Override
	public Set<String> aggregatedPrefixes() {
		return SUPPORTED_PREFIXES;
	}
}
