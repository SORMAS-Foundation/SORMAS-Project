package de.symeda.sormas.api.person;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PersonHelperTest {

	@Test
	public void nameSimilarityExceedsThreshold() {
		String firstName = "Thomas Miller";
		String secondName = "Tomas Miller";
		
		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));
		
		firstName = "Thomas Miller";
		secondName = "Miller Thomas";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));
		
		firstName = "Thomas Jake Miller";
		secondName = "Thomas Miller";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));
		
		firstName = "Thomas Jake Miller";
		secondName = "Thomas Jacob Miller";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));
		
		firstName = "Dan Brown";
		secondName = "Dan Browning";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));
		
		firstName = "Dan Van";
		secondName = "Gan Zan";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));
	}
	
	@Test
	public void nameSimilarityDeceedsThreshold() {
		String firstName = "Thomas Miller";
		String secondName = "Tomislav Millerton";

		assertFalse(PersonHelper.areNamesSimilar(firstName, secondName));
		
		firstName = "Jonathan Lee Sterling";
		secondName = "Jonathan Lee Langston";

		assertFalse(PersonHelper.areNamesSimilar(firstName, secondName));
		
		firstName = "Gan Zan";
		secondName = "Don Van";

		assertFalse(PersonHelper.areNamesSimilar(firstName, secondName));
	}
	
}
