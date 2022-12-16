package de.symeda.sormas.ui.configuration.generate;

import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.vaadin.data.Binder;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.configuration.generate.config.SampleGenerationConfig;

public class SampleGenerator extends EntityGenerator<SampleGenerationConfig> {

	private final String[] sampleComments = new String[] {
		"Very expensive test",
		"Urgent need to have the results",
		"This is a repeated test",
		"Repeated test after 1 day",
		"Repeated test after 1 week",
		"-" };
	private final String[] sampleShipmentDetails = new String[] {
		"Dispatch is required within 1 week",
		"Dispatch is required within 2 weeks",
		"Dispatch is required within 1 months",
		"Dispatch is required by the end of a day",
		"Dispatch is very urgent",
		"-" };
	private final String[] otherPerformedTestsAndResultsSample = new String[] {
		"Blood donation has been performed 1 week ago",
		"Blood donation has been performed 2 weeks ago",
		"Haemoglobin in urine was positive 1 week ago",
		"Protein is urine was negative 1 month ago",
		"Red blood cells in urine was indeterminate 2 weeks ago",
		"-" };

	public void generate(Binder<SampleGenerationConfig> binder) {
		SampleGenerationConfig sampleGenerationConfig = binder.getBean();
		boolean valid = true;
		StringBuilder errorMessage = new StringBuilder();

		if (sampleGenerationConfig.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated samples' in 'Generate Samples'").append("<br>");
			valid = false;
		}
		if (sampleGenerationConfig.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Sample collected start date' in 'Generate Samples'").append("<br>");
			valid = false;
		}

		if (sampleGenerationConfig.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Sample collected end date' in 'Generate Samples'").append("<br>");
			valid = false;
		}

		if (sampleGenerationConfig.getSampleMaterial() == null) {
			errorMessage.append("You must set a valid value for field 'Type of the Sample' in 'Generate Samples'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateSamples(sampleGenerationConfig);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}
	}

	public void generateSamples(SampleGenerationConfig sampleGenerationConfig) {
		initializeRandomGenerator();

		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(sampleGenerationConfig.getStartDate(), sampleGenerationConfig.getEndDate());

		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(sampleGenerationConfig.getRegion());
		facilityCriteria.district(sampleGenerationConfig.getDistrict());

		long dt = System.nanoTime();

		UserReferenceDto user = UserProvider.getCurrent().getUserReference();

		List<CaseReferenceDto> cases = FacadeProvider.getCaseFacade()
			.getRandomCaseReferences(
				new CaseCriteria().region(sampleGenerationConfig.getRegion())
					.district(sampleGenerationConfig.getDistrict())
					.disease(sampleGenerationConfig.getDisease()),
				sampleGenerationConfig.getEntityCountAsNumber() * 2,
				random());

		if (nonNull(cases)) {
			for (int i = 0; i < sampleGenerationConfig.getEntityCountAsNumber(); i++) {

				CaseReferenceDto caseReference = random(cases);

				List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
				Disease disease = sampleGenerationConfig.getDisease();
				if (disease == null) {
					disease = random(diseases);
					sampleGenerationConfig.setDisease(disease);
				}

				LocalDateTime referenceDateTime = getReferenceDateTime(
					i,
					sampleGenerationConfig.getEntityCountAsNumber(),
					baseOffset,
					sampleGenerationConfig.getDisease(),
					sampleGenerationConfig.getStartDate(),
					daysBetween);

				SampleDto sample = SampleDto.build(user, caseReference);

				sample.setSamplePurpose(sampleGenerationConfig.getSamplePurpose());

				Date date = Date.from(referenceDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				sample.setSampleDateTime(date);

				sample.setSampleMaterial(sampleGenerationConfig.getSampleMaterial());

				sample.setFieldSampleID(UUID.randomUUID().toString());

				sample.setComment(random(sampleComments));

				sample.setLab(sampleGenerationConfig.getLaboratory());

				if (sampleGenerationConfig.isRequestPathogenTestsToBePerformed()) {
					Set pathogenTestTypes = new HashSet<PathogenTestType>();
					int until = randomInt(1, PathogenTestType.values().length);
					for (int j = 0; j < until; j++) {
						pathogenTestTypes.add(PathogenTestType.values()[j]);
					}
					sample.setPathogenTestingRequested(true);
					sample.setRequestedPathogenTests(pathogenTestTypes);
				}

				if (sampleGenerationConfig.isRequestAdditionalTestsToBePerformed()) {
					Set additionalTestTypes = new HashSet<AdditionalTestType>();
					int until = randomInt(1, AdditionalTestType.values().length);
					for (int j = 0; j < until; j++) {
						additionalTestTypes.add(AdditionalTestType.values()[j]);
					}
					sample.setAdditionalTestingRequested(true);
					sample.setRequestedAdditionalTests(additionalTestTypes);
				}

				if (sampleGenerationConfig.isSendDispatch()) {
					sample.setShipped(true);
					sample.setShipmentDate(date);
					sample.setShipmentDetails(random(sampleShipmentDetails));
				}

				if (sampleGenerationConfig.isReceived()) {
					sample.setReceived(true);
					sample.setReceivedDate(date);

					sample.setSpecimenCondition(random(SpecimenCondition.values()));
				}

				SampleDto sampleDto = FacadeProvider.getSampleFacade().saveSample(sample);

				if (sampleGenerationConfig.isRequestAdditionalTestsToBePerformed()) {
					createAdditionalTest(sampleDto, date);
				}

			}

			dt = System.nanoTime() - dt;
			long perSample = dt / sampleGenerationConfig.getEntityCountAsNumber();
			String msg = String.format(
				"Generating %d samples took %.2f  s (%.1f ms per sample)",
				sampleGenerationConfig.getEntityCountAsNumber(),
				(double) dt / 1_000_000_000,
				(double) perSample / 1_000_000);
			logger.info(msg);
			Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
		} else {
			String msg = "No Sample has been generated because cases is null ";
			logger.info(msg);
			Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
		}
	}

	private void createAdditionalTest(SampleDto sample, Date date) {

		AdditionalTestDto additionalTestDto = new AdditionalTestDto();
		additionalTestDto.setUuid(UUID.randomUUID().toString());
		additionalTestDto.setTestDateTime(date);
		additionalTestDto.setHaemoglobinuria(random(SimpleTestResultType.values()));
		additionalTestDto.setProteinuria(random(SimpleTestResultType.values()));
		additionalTestDto.setHematuria(random(SimpleTestResultType.values()));

		additionalTestDto.setArterialVenousGasPH(new Random().nextFloat());
		additionalTestDto.setArterialVenousGasPco2(new Random().nextFloat());
		additionalTestDto.setArterialVenousGasPao2(new Random().nextFloat());
		additionalTestDto.setArterialVenousGasHco3(new Random().nextFloat());
		additionalTestDto.setGasOxygenTherapy(new Random().nextFloat());

		additionalTestDto.setAltSgpt(new Random().nextFloat());
		additionalTestDto.setTotalBilirubin(new Random().nextFloat());
		additionalTestDto.setAstSgot(new Random().nextFloat());
		additionalTestDto.setConjBilirubin(new Random().nextFloat());
		additionalTestDto.setCreatinine(new Random().nextFloat());
		additionalTestDto.setWbcCount(new Random().nextFloat());
		additionalTestDto.setPotassium(new Random().nextFloat());
		additionalTestDto.setPlatelets(new Random().nextFloat());
		additionalTestDto.setUrea(new Random().nextFloat());
		additionalTestDto.setProthrombinTime(new Random().nextFloat());
		additionalTestDto.setHaemoglobin(new Random().nextFloat());

		additionalTestDto.setOtherTestResults(random(otherPerformedTestsAndResultsSample));

		additionalTestDto.setSample(sample.toReference());

		FacadeProvider.getAdditionalTestFacade().saveAdditionalTest(additionalTestDto);
	}
}
