package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;

public class SymptomsHelperTest {

	@Test
	public void testUpdateIsSymptomatic() {
		SymptomsDto symptoms = new SymptomsDto();
		SymptomsHelper.updateIsSymptomatic(symptoms);
		
		assertFalse(symptoms.getSymptomatic());
		
		symptoms.setBackache(SymptomState.YES);
		SymptomsHelper.updateIsSymptomatic(symptoms);
		
		assertTrue(symptoms.getSymptomatic());
	}
	
}
