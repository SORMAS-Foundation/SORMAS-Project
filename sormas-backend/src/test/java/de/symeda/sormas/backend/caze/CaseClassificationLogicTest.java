package de.symeda.sormas.backend.caze;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.backend.AbstractBeanTest;

public class CaseClassificationLogicTest extends AbstractBeanTest {

	@Test
	public void testGetClassificationDescription() throws Exception {

		for (Disease disease : Disease.values()) {
			System.out.println("\n# " + disease.toString());
			for (CaseClassification classification : CaseClassification.values()) {
				String desc = getCaseClassificationLogic().getClassificationDescription(disease, classification);
				if (desc != null && !desc.isEmpty()) {
					System.out.println("\n## " + classification.toString());
					System.out.println(getCaseClassificationLogic().getClassificationDescription(disease, classification));
				}
			}
			
		}
	}

}
