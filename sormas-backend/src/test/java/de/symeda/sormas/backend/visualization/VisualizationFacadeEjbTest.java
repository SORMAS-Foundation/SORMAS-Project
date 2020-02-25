package de.symeda.sormas.backend.visualization;

import org.junit.Test;

import de.symeda.sormas.api.visualization.VisualizationFacade;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.visualization.VisualizationFacadeEjb.VisualizationFacadeEjbLocal;

public class VisualizationFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testBuildTransmissionChainJson() throws Exception {
		
		// TODO find a better solution
		MockProducer.properties.setProperty(ConfigFacadeEjb.R_EXECUTABLE,"C:\\Program Files\\R\\R-3.6.2\\bin\\R");

		VisualizationFacade visualizationFacade = getBean(VisualizationFacadeEjbLocal.class);
		visualizationFacade.buildTransmissionChainJson();
	}

}
