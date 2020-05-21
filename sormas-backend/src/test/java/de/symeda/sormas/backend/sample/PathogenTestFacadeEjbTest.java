package de.symeda.sormas.backend.sample;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PathogenTestFacadeEjbTest extends AbstractBeanTest {

    private Date testDateTime = new Date();

    @Test
    public void testGetBySampleUuids() throws Exception {
        RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
        UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
        PersonDto person = creator.createPerson();
        CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

        SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
        SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
        SampleDto sample3 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

        PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);
        PathogenTestDto pathogenTest2 = creator.createPathogenTest(sample.toReference(), caze);
        PathogenTestDto pathogenTest3 = creator.createPathogenTest(sample2.toReference(), caze);
        creator.createPathogenTest(sample3.toReference(), caze);

        List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getBySampleUuids(Arrays.asList(sample.getUuid()
                , sample2.getUuid()));

        assertThat(pathogenTests, hasSize(3));
        assertThat(pathogenTests, contains(pathogenTest, pathogenTest2, pathogenTest3));
    }

    @Test
    public void testSaveAndUpdatePathogenTestAssociatedToCase() {
        final RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
        final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
        final PersonDto person = creator.createPerson();
        final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
        final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

        final PathogenTestDto newPathogenTest = createPathogenTestDto(rdcf, user, sample, caze.getDisease());

        testSaveAndUpdatePathogenTest(newPathogenTest);
    }

    @Test
    public void testSaveAndUpdatePathogenTestAssociatedToContact() {

        final RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
        final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
        final PersonDto person = creator.createPerson();
        final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
        final ContactDto contact = creator.createContact(user.toReference(), person.toReference(), caze);
        final SampleDto sample = creator.createSample(contact.toReference(), new Date(), new Date(), user.toReference(),
                SampleMaterial.BLOOD, rdcf.facility);

        final PathogenTestDto newPathogenTest = createPathogenTestDto(rdcf, user, sample, contact.getDisease());

        testSaveAndUpdatePathogenTest(newPathogenTest);
    }

    @Test
    public void testSaveAndUpdatePathogenTestAssociatedToBothCaseAndContact() {
        final RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
        final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
        final PersonDto person = creator.createPerson();
        final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
        final ContactDto contact = creator.createContact(user.toReference(), person.toReference(), caze);
        final VisitDto visit = creator.createVisit(caze.getDisease(), person.toReference());
        final SampleDto sample = creator.createSample(contact.toReference(), new Date(), new Date(), user.toReference(),
                SampleMaterial.BLOOD, rdcf.facility);

        final CaseDataDto caseDataDto = CaseDataDto.buildFromContact(contact, visit);
        caseDataDto.setRegion(new RegionReferenceDto(rdcf.region.getUuid()));
        caseDataDto.setDistrict(new DistrictReferenceDto(rdcf.district.getUuid()));
        caseDataDto.setHealthFacility(new FacilityReferenceDto(rdcf.facility.getUuid()));
        caseDataDto.setReportingUser(user.toReference());
        final CaseDataDto caseConvertedFromContact = getCaseFacade().saveCase(caseDataDto);

        getCaseFacade().setSampleAssociations(contact.toReference(), caseConvertedFromContact.toReference());

        final PathogenTestDto newPathogenTest = createPathogenTestDto(rdcf, user, sample, caseConvertedFromContact.getDisease());

        testSaveAndUpdatePathogenTest(newPathogenTest);
    }

    private void testSaveAndUpdatePathogenTest(PathogenTestDto newPathogenTest) {
        final PathogenTestDto savedPathogenTest = getPathogenTestFacade().savePathogenTest(newPathogenTest);
        assertNotNull(savedPathogenTest);
        assertEquals(Disease.EVD, savedPathogenTest.getTestedDisease());
        assertEquals(PathogenTestType.ISOLATION, savedPathogenTest.getTestType());
        assertEquals(PathogenTestResultType.PENDING, savedPathogenTest.getTestResult());
        assertEquals("all bad!", savedPathogenTest.getTestResultText());
        assertEquals(testDateTime, savedPathogenTest.getTestDateTime());
        assertFalse(savedPathogenTest.getTestResultVerified());

        savedPathogenTest.setTestResultVerified(true);
        savedPathogenTest.setTestResultText("all good!");
        savedPathogenTest.setTestType(PathogenTestType.OTHER);
        savedPathogenTest.setTestResult(PathogenTestResultType.POSITIVE);

        final PathogenTestDto updatedPathogen = getPathogenTestFacade().savePathogenTest(savedPathogenTest);
        assertNotNull(updatedPathogen);
        assertEquals(Disease.EVD, updatedPathogen.getTestedDisease());
        assertEquals(PathogenTestType.OTHER, updatedPathogen.getTestType());
        assertEquals(PathogenTestResultType.POSITIVE, updatedPathogen.getTestResult());
        assertEquals("all good!", updatedPathogen.getTestResultText());
        assertEquals(testDateTime, updatedPathogen.getTestDateTime());
        assertTrue(updatedPathogen.getTestResultVerified());
    }

    private PathogenTestDto createPathogenTestDto(RDCFEntities rdcf, UserDto user, SampleDto sample, Disease disease) {
        final PathogenTestDto newPathogenTest = new PathogenTestDto();

        newPathogenTest.setSample(sample.toReference());
        newPathogenTest.setTestedDisease(disease);
        newPathogenTest.setTestType(PathogenTestType.ISOLATION);

        newPathogenTest.setTestDateTime(testDateTime);
        newPathogenTest.setLab(new FacilityReferenceDto(rdcf.facility.getUuid()));
        newPathogenTest.setLabUser(user.toReference());
        newPathogenTest.setTestResult(PathogenTestResultType.PENDING);
        newPathogenTest.setTestResultText("all bad!");
        newPathogenTest.setTestResultVerified(false);
        return newPathogenTest;
    }
}
