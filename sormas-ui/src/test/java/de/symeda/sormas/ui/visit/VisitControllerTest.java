package de.symeda.sormas.ui.visit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.SyphilisPresentation;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class VisitControllerTest extends AbstractUiBeanTest {

	@Test
	void returnsNullPresentationForContactWithoutSourceCase() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		ContactDto contact =
			creator.createContact(user.toReference(), creator.createPerson().toReference(), Disease.GONOCOCCAL_INFECTION, rdcf);

		assertNull(contact.getCaze());
		assertNull(VisitController.getSourceCaseSyphilisPresentation(contact));
	}

	@Test
	void returnsSourceCasePresentationForSyphilisContact() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf, c -> {
			c.setDisease(Disease.SYPHILIS);
			c.setSyphilisPresentation(SyphilisPresentation.CONGENITAL);
		});
		ContactDto contact = creator.createContact(user.toReference(), creator.createPerson().toReference(), caze, rdcf);

		assertEquals(SyphilisPresentation.CONGENITAL, VisitController.getSourceCaseSyphilisPresentation(contact));
	}

	@Test
	void ignoresSourceCaseForNonSyphilisContact() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf, c -> {
			c.setDisease(Disease.GONOCOCCAL_INFECTION);
			c.setSyphilisPresentation(SyphilisPresentation.ACQUIRED);
		});
		ContactDto contact = creator.createContact(user.toReference(), creator.createPerson().toReference(), caze, rdcf);

		assertNull(VisitController.getSourceCaseSyphilisPresentation(contact));
	}
}
