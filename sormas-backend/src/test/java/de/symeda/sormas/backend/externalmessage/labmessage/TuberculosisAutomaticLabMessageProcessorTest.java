package de.symeda.sormas.backend.externalmessage.labmessage;

import static de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus.DONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;

public class TuberculosisAutomaticLabMessageProcessorTest extends AbstractBeanTest {

    private static final String TEST_HEALTH_ID_1 = "2010010100774";
    private static final String TEST_HEALTH_ID_2 = "1010010100774";

    private AutomaticLabMessageProcessor flow;

    private TestDataCreator.RDCF rdcf;
    private UserDto reportingUser;
    private FacilityDto lab;

    @Override
    public void init() {
        super.init();
        flow = getAutomaticLabMessageProcessingFlow();
        rdcf = creator.createRDCF();
        reportingUser = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER);
        lab = creator.createFacility("Lab", rdcf.region, rdcf.district, f -> {
            f.setType(FacilityType.LABORATORY);
            f.setExternalID("test-facility-ext-id-1");
        });
    }

    @Test
    public void testNegativeLatentTuberculosisNoExistingCase() throws ExecutionException, InterruptedException {

        MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");

        final ExternalMessageDto externalMessage = createLatentTuberculosisIgraNegativeExternalMessage(e -> {
            e.getSampleReports().get(0).setSampleDateTime(DateHelper.subtractDays(new Date(), 10));
        });

        // set the threshold
        creator.updateDiseaseConfiguration(Disease.LATENT_TUBERCULOSIS, true, true, true, true, null, 30);
        getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

        ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
        assertThat(result.getStatus(), is(DONE));
        assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

        assertThat("A new case should result from a negative IGRA Latent TB.", result.getData().getSelectedCase().isNew(), equalTo(true));

        assertThat("Resulting case classification be NO_CASE", result.getData().getCase().getCaseClassification(), is(CaseClassification.NO_CASE));

        assertThat("Case count should increase after processing a NEG IGRA Latent TB.", getCaseFacade().getAllActiveUuids().size(), is(1));

        assertThat("Sample count should have increased when adding a Latent Tuberculosis case.", getSampleFacade().getAllActiveUuids().size(), is(1));
    }

    @Test
    public void testPositiveLatentTuberculosisNoExistingCase() throws ExecutionException, InterruptedException {

        MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");

        final ExternalMessageDto externalMessage = createLatentTuberculosisIgraPositiveExternalMessage(e -> {
            e.getSampleReports().get(0).setSampleDateTime(DateHelper.subtractDays(new Date(), 10));
        });

        // set the threshold
        creator.updateDiseaseConfiguration(Disease.LATENT_TUBERCULOSIS, true, true, true, true, null, 30);
        getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

        ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
        assertThat(result.getStatus(), is(DONE));
        assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

        assertThat("A new case should result from a positive IGRA Latent TB.", result.getData().getSelectedCase().isNew(), equalTo(true));

        assertThat(
            "Resulting case classification be CONFIRMED",
            result.getData().getCase().getCaseClassification(),
            is(CaseClassification.CONFIRMED));

        assertThat("Case count should increase after processing a POS IGRA Latent TB.", getCaseFacade().getAllActiveUuids().size(), is(1));

        assertThat("Sample count should have increased when adding a Latent Tuberculosis case.", getSampleFacade().getAllActiveUuids().size(), is(1));
    }

    /**
     * Test the processing scenario for a Latent Tuberculosis external message with a positive IGRA test.
     * This test is for the case of having an already similar case with a positive IGRA test.
     * <p>
     * The existing Latent Tuberculosis case be updated.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testNegativeLatentTuberculosisExistingNegativeCase() throws ExecutionException, InterruptedException {

        MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");

        final ExternalMessageDto externalMessage = createLatentTuberculosisIgraNegativeExternalMessage(e -> {
            e.getSampleReports().get(0).setSampleDateTime(DateHelper.subtractDays(new Date(), 10));
        });

        // Create an existing Latent Tuberculosis case with negative IGRA test
        final PersonDto person =
            creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
                p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
            });

        final CaseDataDto caze = creator.createCase(reportingUser.toReference(), person.toReference(), rdcf, c -> {
            c.setCaseClassification(CaseClassification.NO_CASE); // NOT A CASE - this is the default for NEG LATENT TB
            c.setDisease(Disease.LATENT_TUBERCULOSIS);
            c.setReportDate(DateHelper.subtractDays(new Date(), 15));
        });

        final SampleDto sample = creator.createSample(caze.toReference(), reportingUser.toReference(), rdcf.facility, s -> {
            s.setSampleMaterial(SampleMaterial.BLOOD);
            s.setSampleDateTime(DateHelper.subtractDays(new Date(), 15));
            s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
        });

        final PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);

        pathogenTest.setTestType(PathogenTestType.IGRA);
        pathogenTest.setTestResult(PathogenTestResultType.NEGATIVE);

        // set the threshold
        creator.updateDiseaseConfiguration(Disease.LATENT_TUBERCULOSIS, true, true, true, true, null, 30);
        getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

        ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
        assertThat(result.getStatus(), is(DONE));
        assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

        assertThat(
            "For an existing Latent Tuberculosis case that is negative IGRA, a new case should not be created for a negative IGRA external message.",
            result.getData().getSelectedCase().isNew(),
            equalTo(false));

        assertThat(
            "Resulting case classification should remain NO_CASE",
            result.getData().getCase().getCaseClassification(),
            is(CaseClassification.NO_CASE));

        assertThat(
            "Case count should not increase after processing a NEG->NEG IGRA Latent TB.",
            getCaseFacade().count(new CaseCriteria().person(caze.getPerson())),
            is(1L));

        assertThat(
            "Sample count should have increased when adding to an existing Latent Tuberculosis case.",
            getSampleFacade().count(new SampleCriteria().caze(caze.toReference())),
            is(2L));

    }

    /**
     * Test the processing scenario for a Latent Tuberculosis external message with a positive IGRA test.
     * This test is for the case of having an already similar case with a positive IGRA test.
     * <p>
     * The existing Latent Tuberculosis case be updated.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testPositiveLatentTuberculosisExistingPositiveCase() throws ExecutionException, InterruptedException {

        MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");

        final ExternalMessageDto externalMessage = createLatentTuberculosisIgraPositiveExternalMessage(e -> {
            e.getSampleReports().get(0).setSampleDateTime(DateHelper.subtractDays(new Date(), 10));
        });

        // Create an existing Latent Tuberculosis case with negative IGRA test
        final PersonDto person =
            creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
                p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
            });

        final CaseDataDto caze = creator.createCase(reportingUser.toReference(), person.toReference(), rdcf, c -> {
            c.setCaseClassification(CaseClassification.CONFIRMED); // CONFIRMED - this is the default for POS LATENT TB
            c.setDisease(Disease.LATENT_TUBERCULOSIS);
            c.setReportDate(DateHelper.subtractDays(new Date(), 15));
        });

        final SampleDto sample = creator.createSample(caze.toReference(), reportingUser.toReference(), rdcf.facility, s -> {
            s.setSampleMaterial(SampleMaterial.BLOOD);
            s.setSampleDateTime(DateHelper.subtractDays(new Date(), 15));
            s.setPathogenTestResult(PathogenTestResultType.POSITIVE);
        });

        final PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);

        pathogenTest.setTestType(PathogenTestType.IGRA);
        pathogenTest.setTestResult(PathogenTestResultType.POSITIVE);

        // set the threshold
        creator.updateDiseaseConfiguration(Disease.LATENT_TUBERCULOSIS, true, true, true, true, null, 30);
        getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

        ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
        assertThat(result.getStatus(), is(DONE));
        assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

        assertThat(
            "For an existing Latent Tuberculosis case that is positive IGRA, a new case should not be created.",
            result.getData().getSelectedCase().isNew(),
            equalTo(false));

        assertThat(
            "Resulting case classification should remain CONFIRMED",
            result.getData().getCase().getCaseClassification(),
            is(CaseClassification.CONFIRMED));

        assertThat(
            "Case count should not increase after processing a POS->ANY IGRA Latent TB.",
            getCaseFacade().count(new CaseCriteria().person(caze.getPerson())),
            is(1L));

        assertThat(
            "Sample count should have increased when adding to an existing Latent Tuberculosis case.",
            getSampleFacade().count(new SampleCriteria().caze(caze.toReference())),
            is(2L));

    }

    /**
     * Test the processing scenario for a Latent Tuberculosis external message with a positive IGRA test.
     * This test is for the case of having an already similar case with a negative IGRA test.
     * <ul>
     * <li>The existing Latent Tuberculosis case should be kept</li>
     * <li>A new case should be created with classification = CONFIRMED</li>
     * </ul>
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testPositiveLatentTuberculosisExistingNegativeCase() throws ExecutionException, InterruptedException {

        MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");

        final ExternalMessageDto externalMessage = createLatentTuberculosisIgraPositiveExternalMessage(e -> {
            e.getSampleReports().get(0).setSampleDateTime(DateHelper.subtractDays(new Date(), 10));
        });

        // Create an existing Latent Tuberculosis case with negative IGRA test
        final PersonDto person =
            creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
                p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
            });

        final CaseDataDto caze = creator.createCase(reportingUser.toReference(), person.toReference(), rdcf, c -> {
            c.setDisease(Disease.LATENT_TUBERCULOSIS);
            c.setReportDate(DateHelper.subtractDays(new Date(), 15));
        });

        final SampleDto sample = creator.createSample(caze.toReference(), reportingUser.toReference(), rdcf.facility, s -> {
            s.setSampleMaterial(SampleMaterial.BLOOD);
            s.setSampleDateTime(DateHelper.subtractDays(new Date(), 15));
            s.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
        });

        final PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);

        pathogenTest.setTestType(PathogenTestType.IGRA);
        pathogenTest.setTestResult(PathogenTestResultType.NEGATIVE);

        // set the threshold
        creator.updateDiseaseConfiguration(Disease.LATENT_TUBERCULOSIS, true, true, true, true, null, 30);
        getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

        ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
        assertThat(result.getStatus(), is(DONE));
        assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

        assertThat(
            "For an existing Latent Tuberculosis case that is negative IGRA, a new case should be created.",
            result.getData().getSelectedCase().isNew(),
            equalTo(true));

        assertThat(
            "Resulting case classification should be CONFIRMED",
            result.getData().getCase().getCaseClassification(),
            is(CaseClassification.CONFIRMED));

        assertThat(
            "Case count should increase after processing a NEG->POS IGRA Latent TB.",
            getCaseFacade().count(new CaseCriteria().person(caze.getPerson())),
            is(2L));
    }

    @Test
    public void testPositiveLatentTuberculosisExistingTuberculosisCase() throws ExecutionException, InterruptedException {

        MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "lu");

        final ExternalMessageDto externalMessage = createLatentTuberculosisIgraPositiveExternalMessage(e -> {
            e.getSampleReports().get(0).setSampleDateTime(DateHelper.subtractDays(new Date(), 10));
        });

        // Create an existing Latent Tuberculosis case with negative IGRA test
        final PersonDto person =
            creator.createPerson(externalMessage.getPersonFirstName(), externalMessage.getPersonLastName(), externalMessage.getPersonSex(), p -> {
                p.setNationalHealthId(externalMessage.getPersonNationalHealthId());
            });

        final CaseDataDto caze = creator.createCase(reportingUser.toReference(), person.toReference(), rdcf, c -> {
            c.setCaseClassification(CaseClassification.CONFIRMED);
            c.setDisease(Disease.TUBERCULOSIS);
            c.setReportDate(DateHelper.subtractDays(new Date(), 15));
        });

        final SampleDto sample = creator.createSample(caze.toReference(), reportingUser.toReference(), rdcf.facility, s -> {
            s.setSampleMaterial(SampleMaterial.TISSUE);
            s.setSampleDateTime(DateHelper.subtractDays(new Date(), 15));
            s.setPathogenTestResult(PathogenTestResultType.POSITIVE);
        });

        final PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);

        pathogenTest.setTestType(PathogenTestType.CULTURE);
        pathogenTest.setTestResult(PathogenTestResultType.POSITIVE);

        // set the threshold
        creator.updateDiseaseConfiguration(Disease.LATENT_TUBERCULOSIS, true, true, true, true, null, 30);

        // We need to set the threshold to 30 days because the LT samples should be added to the TUBE case, so both need to have the same threshold
        creator.updateDiseaseConfiguration(Disease.TUBERCULOSIS, true, true, true, true, null, 30);
        getBean(DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal.class).loadData();

        ProcessingResult<ExternalMessageProcessingResult> result = runFlow(externalMessage);
        assertThat(result.getStatus(), is(DONE));
        assertThat(externalMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

        assertThat("For an existing Tuberculosis, a new case should not be created.", result.getData().getSelectedCase().isNew(), equalTo(false));

        assertThat(
            "Resulting case classification should remain CONFIRMED",
            result.getData().getCase().getCaseClassification(),
            is(CaseClassification.CONFIRMED));

        assertThat(
            "Case count should not increase after processing a Latent TB POS->ANY_TB.",
            getCaseFacade().count(new CaseCriteria().person(caze.getPerson())),
            is(1L));

        assertThat(
            "Sample count should have increased when adding to an existing Tuberculosis case.",
            getSampleFacade().count(new SampleCriteria().caze(caze.toReference())),
            is(2L));

    }

    private ProcessingResult<ExternalMessageProcessingResult> runFlow(ExternalMessageDto labMessage) throws ExecutionException, InterruptedException {
        return flow.processLabMessage(labMessage);
    }

    private ExternalMessageDto createLatentTuberculosisIgraPositiveExternalMessage(Consumer<ExternalMessageDto> extraConfig) {
        return creator.createExternalMessage(externalMessage -> {
            externalMessage.setType(ExternalMessageType.LAB_MESSAGE);
            externalMessage.setMessageDateTime(new Date());
            externalMessage.setDisease(Disease.TUBERCULOSIS);
            externalMessage.setPersonFirstName("John");
            externalMessage.setPersonLastName("Doe");
            externalMessage.setPersonSex(Sex.MALE);
            externalMessage.setPersonNationalHealthId(TEST_HEALTH_ID_1);
            externalMessage.setPersonFacility(rdcf.facility);
            externalMessage.setReporterExternalIds(Collections.singletonList(lab.getExternalID()));

            SampleReportDto sampleReport = new SampleReportDto();
            sampleReport.setSampleDateTime(new Date());
            sampleReport.setSpecimenCondition(SpecimenCondition.ADEQUATE);
            sampleReport.setSampleMaterial(SampleMaterial.BLOOD);

            TestReportDto testReport = new TestReportDto();
            testReport.setTestResult(PathogenTestResultType.POSITIVE);
            testReport.setTestDateTime(new Date());
            testReport.setTestType(PathogenTestType.IGRA);

            sampleReport.setTestReports(Collections.singletonList(testReport));
            externalMessage.setSampleReports(Collections.singletonList(sampleReport));

            if (extraConfig != null) {
                extraConfig.accept(externalMessage);
            }
        });
    }

    private ExternalMessageDto createLatentTuberculosisIgraNegativeExternalMessage(Consumer<ExternalMessageDto> extraConfig) {
        return creator.createExternalMessage(externalMessage -> {
            externalMessage.setType(ExternalMessageType.LAB_MESSAGE);
            externalMessage.setMessageDateTime(new Date());
            externalMessage.setDisease(Disease.TUBERCULOSIS);
            externalMessage.setPersonFirstName("John");
            externalMessage.setPersonLastName("Doe");
            externalMessage.setPersonSex(Sex.MALE);
            externalMessage.setPersonNationalHealthId(TEST_HEALTH_ID_1);
            externalMessage.setPersonFacility(rdcf.facility);
            externalMessage.setReporterExternalIds(Collections.singletonList(lab.getExternalID()));

            SampleReportDto sampleReport = new SampleReportDto();
            sampleReport.setSampleDateTime(new Date());
            sampleReport.setSpecimenCondition(SpecimenCondition.ADEQUATE);
            sampleReport.setSampleMaterial(SampleMaterial.BLOOD);

            TestReportDto testReport = new TestReportDto();
            testReport.setTestResult(PathogenTestResultType.NEGATIVE);
            testReport.setTestDateTime(new Date());
            testReport.setTestType(PathogenTestType.IGRA);

            sampleReport.setTestReports(Collections.singletonList(testReport));
            externalMessage.setSampleReports(Collections.singletonList(sampleReport));

            if (extraConfig != null) {
                extraConfig.accept(externalMessage);
            }
        });
    }

    private ExternalMessageDto createExternalMessage(Consumer<ExternalMessageDto> extraConfig) {
        return creator.createExternalMessage(externalMessage -> {
            externalMessage.setType(ExternalMessageType.LAB_MESSAGE);
            externalMessage.setMessageDateTime(new Date());
            externalMessage.setDisease(Disease.CORONAVIRUS);
            externalMessage.setPersonFirstName("John");
            externalMessage.setPersonLastName("Doe");
            externalMessage.setPersonSex(Sex.MALE);
            externalMessage.setPersonNationalHealthId("1234567890");
            externalMessage.setPersonFacility(rdcf.facility);
            externalMessage.setReporterExternalIds(Collections.singletonList(lab.getExternalID()));

            SampleReportDto sampleReport = new SampleReportDto();
            sampleReport.setSampleDateTime(new Date());
            sampleReport.setSpecimenCondition(SpecimenCondition.ADEQUATE);
            sampleReport.setSampleMaterial(SampleMaterial.CRUST);

            TestReportDto testReport = new TestReportDto();
            testReport.setTestResult(PathogenTestResultType.PENDING);
            testReport.setTestDateTime(new Date());
            testReport.setTestType(PathogenTestType.PCR_RT_PCR);

            sampleReport.setTestReports(Collections.singletonList(testReport));
            externalMessage.setSampleReports(Collections.singletonList(sampleReport));

            if (extraConfig != null) {
                extraConfig.accept(externalMessage);
            }
        });
    }

}
