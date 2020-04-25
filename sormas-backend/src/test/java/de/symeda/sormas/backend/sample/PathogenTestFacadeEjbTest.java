package de.symeda.sormas.backend.sample;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class PathogenTestFacadeEjbTest extends AbstractBeanTest {

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
		
		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getBySampleUuids(Arrays.asList(sample.getUuid(), sample2.getUuid()));
		
		assertThat(pathogenTests, hasSize(3));
		assertThat(pathogenTests, contains(pathogenTest, pathogenTest2, pathogenTest3));
	}

}
