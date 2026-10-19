package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CaseServiceEditPermissionTypesTest extends AbstractBeanTest {

	@Test
	public void answersForManyCasesAsEachCaseAnswersAlone() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		TestDataCreator.RDCF elsewhere = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2");
		UserDto admin = useNationalAdminLogin();
		List<CaseDataDto> created = List.of(
			creator.createCase(admin.toReference(), creator.createPerson().toReference(), rdcf),
			creator.createCase(admin.toReference(), creator.createPerson().toReference(), elsewhere),
			creator.createCase(admin.toReference(), creator.createPerson().toReference(), rdcf),
			creator.createSharedCase(admin.toReference(), creator.createPerson().toReference(), rdcf, true),
			creator.createSharedCase(admin.toReference(), creator.createPerson().toReference(), rdcf, false),
			creator.createReceivedCase(admin.toReference(), creator.createPerson().toReference(), rdcf, false),
			creator.createReceivedCase(admin.toReference(), creator.createPerson().toReference(), rdcf, true));
		getCaseFacade().archive(created.get(2).getUuid(), null);
		loginWith(creator.createUser(rdcf, "District", "Officer", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER)));
		CaseService service = getBean(CaseService.class);
		List<Case> cases = service.getByUuids(created.stream().map(CaseDataDto::getUuid).collect(Collectors.toList()));

		Map<String, EditPermissionType> one = cases.stream().collect(Collectors.toMap(Case::getUuid, service::getEditPermissionType));
		Map<String, EditPermissionType> many =
			service.getEditPermissionTypes(cases).entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getUuid(), Map.Entry::getValue));

		assertEquals(EditPermissionType.ALLOWED, one.get(created.get(0).getUuid()));
		assertEquals(EditPermissionType.OUTSIDE_JURISDICTION, one.get(created.get(1).getUuid()));
		assertTrue(cases.stream().filter(caze -> caze.getUuid().equals(created.get(2).getUuid())).findFirst().orElseThrow().isArchived());
		assertEquals(EditPermissionType.WITHOUT_OWNERSHIP, one.get(created.get(3).getUuid()));
		assertEquals(EditPermissionType.WITHOUT_OWNERSHIP, one.get(created.get(5).getUuid()));
		assertEquals(one, many);
	}

	@Test
	public void answersNothingForNoCases() {
		assertEquals(Map.of(), getBean(CaseService.class).getEditPermissionTypes(List.of()));
	}
}
