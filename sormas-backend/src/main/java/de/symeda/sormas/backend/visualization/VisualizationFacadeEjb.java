/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.visualization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.visualization.VisualizationFacade;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "VisualizationFacade")
public class VisualizationFacadeEjb implements VisualizationFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private static final Logger logger = LoggerFactory.getLogger(VisualizationFacadeEjb.class);

	@Override
	public String buildTransmissionChainJson() {
		
		String rExecutable = configFacade.getRExecutable();
		Path tempPath = new File(configFacade.getTempFilesPath()).toPath();
		File scriptFile = tempPath.resolve("scripttest.r").toFile();
		File outputFile = tempPath.resolve("result.html").toFile();
		
		try {
		    FileWriter writer = new FileWriter(scriptFile);
		    writer.write("library(epicontacts)\r\n" + 
		    		"library(outbreaks)\r\n" + 
		    		"merskor15 <- make_epicontacts(linelist = mers_korea_2015$linelist,\r\n" + 
		    		"                              contacts = mers_korea_2015$contacts,\r\n" + 
		    		"                              directed = FALSE)\r\n" + 
		    		"g <- plot(merskor15)\r\n" + 
		    		"htmlwidgets::saveWidget(g, \"" + outputFile.toString().replace("\\", "\\\\") + "\", selfcontained = FALSE)");
		    writer.close();
		
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(rExecutable + " -f " + scriptFile.toString());
		} catch (IOException e) {
			// TODO error handling. R should not be a "hard" requirement for test systems - e.g. we wont be able to install it on travis CI
			e.printStackTrace();
		}
		
		// TODO extract JSON
		
		return "";
	}
	
	@LocalBean
	@Stateless
	public static class VisualizationFacadeEjbLocal extends VisualizationFacadeEjb {
	}	
}
