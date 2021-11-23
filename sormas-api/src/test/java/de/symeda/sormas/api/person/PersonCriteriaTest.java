package de.symeda.sormas.api.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PersonCriteriaTest {

	@Test
	public void testSetPersonAssociation() {

		PersonCriteria cut = new PersonCriteria();
		assertNotNull(cut.getPersonAssociation());
		assertThat(cut.getPersonAssociation(), equalTo(PersonCriteria.DEFAULT_ASSOCIATION));

		cut.setPersonAssociation(PersonAssociation.CASE);
		assertThat(cut.getPersonAssociation(), equalTo(PersonAssociation.CASE));

		cut.personAssociation(PersonAssociation.EVENT_PARTICIPANT);
		assertThat(cut.getPersonAssociation(), equalTo(PersonAssociation.EVENT_PARTICIPANT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetPersonAssociationNullInvalid() {

		PersonCriteria cut = new PersonCriteria();
		cut.setPersonAssociation(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPersonAssociationNullInvalid() {

		PersonCriteria cut = new PersonCriteria();
		cut.personAssociation(null);
	}
}
