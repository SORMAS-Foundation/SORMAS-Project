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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.visualization.VisualizationFacade;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "VisualizationFacade")
public class VisualizationFacadeEjb implements VisualizationFacade {

//	private static final String TRANSMISSION_CHAIN_SCRIPT = "transmission_chain.r";
	private static final String TRANSMISSION_CHAIN_SCRIPT = "transform_contact.R";
	private static final String[] REQUIRED_SCRIPTS = {"encodeGraphic.R", "networkFunction.R"};

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private static final Logger logger = LoggerFactory.getLogger(VisualizationFacadeEjb.class);

	@Override
	public String buildTransmissionChainJson(LocalDate fromDate, LocalDate toDate, Collection<Disease> diseases) {
		
		String rExecutable = configFacade.getRScriptExecutable();
		if (StringUtils.isBlank(rExecutable)) {
			return null;
		}
		Path tempBasePath = new File(configFacade.getTempFilesPath()).toPath();
		
		Collection<Disease> d;
		if (CollectionUtils.isEmpty(diseases)) {
			d = EnumSet.allOf(Disease.class);
		} else {
			d = diseases;
		}

		LocalDate from = Optional.ofNullable(fromDate).orElse(LocalDate.MIN);
		LocalDate to = Optional.ofNullable(toDate).orElse(LocalDate.MAX);
		
		//working dir is the config directory of the domain
		Path domainXmlPath = Paths.get("domain.xml");
		
		return buildTransmissionChainJson(rExecutable, tempBasePath, domainXmlPath, from, to, d);
	}

	enum EnvParam {
		DB_USER("user"),
		DB_PASS("password"),
		DB_NAME("databaseName"),
		DB_HOST("serverName"), 
		DB_PORT("portNumber"), 
		DATE_FROM,
		DATE_TO,
		/**
		 * The Diseases, concatenated with ','
		 */
		DISEASES,
		OUTFILE;
		private final String propertyName;

		EnvParam() {
			this(null);
		}
		EnvParam(String propertyName) {
			this.propertyName = propertyName;
			
		}
		String toEnv(String value) {
			return  value == null ? null : (this.name() + '=' + value);
		}

		void put(Map<String, String> env, String value) {
			if (value == null) {
				env.remove(this.name());
			} else {
				env.put(this.name(), value);
			}
		}
		
		void putFrom(Map<String, String> env, Map<String, String> properties) {
			env.put(this.name(), properties.get(propertyName));
		}
	}
	
	static String buildTransmissionChainJson(String rScriptExecutable, Path tempBasePath, Path domainXmlPath, LocalDate from, LocalDate to, Collection<Disease> diseases) {


		// TODO vaadin component

		// TODO correct script
		// TODO ENVIRONMENT
		
		Path tempDir;
		try {
			tempDir = Files.createTempDirectory(tempBasePath, "vis_");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		
		try {
			copyVisualisationResourceFile(tempDir, TRANSMISSION_CHAIN_SCRIPT);

			Arrays.stream(REQUIRED_SCRIPTS)
				.forEach(s -> copyVisualisationResourceFile(tempDir, s));
			
			Path scriptFile = tempDir.resolve(TRANSMISSION_CHAIN_SCRIPT);
			
			Path outputFile = tempDir.resolve("result.html");
			
			try {
				ProcessBuilder pb;
				if (rScriptExecutable.toUpperCase().contains("RSCRIPT")) {
					pb = new ProcessBuilder(new String[] {rScriptExecutable, scriptFile.toString()});
				} else {
					pb = new ProcessBuilder(new String[] {rScriptExecutable, "-f", scriptFile.toString()});
				}
				pb.directory(scriptFile.getParent().toFile());
					
				Map<String, String> poolProperties = getConnectionPoolProperties(domainXmlPath, "sormasDataPool");
				Map<String, String> env = pb.environment();
	
				EnvParam.DB_USER.putFrom(env, poolProperties);
				EnvParam.DB_PASS.putFrom(env, poolProperties);
				EnvParam.DB_NAME.putFrom(env, poolProperties);
				EnvParam.DB_HOST.putFrom(env, poolProperties); 
				EnvParam.DB_PORT.putFrom(env, poolProperties);

				EnvParam.DATE_FROM.put(env, from.toString());
				EnvParam.DATE_TO.put(env, to.toString());
				EnvParam.DISEASES.put(env, diseases.stream().map(Enum::name).collect(Collectors.joining(",")));
				EnvParam.OUTFILE.put(env, outputFile.toString());
				
				
				Process pr = pb.start();
				int exitCode = pr.waitFor();
				
				if (exitCode == 0) {
					String html = new String(Files.readAllBytes(outputFile));
					return extractJson(html);
				} else {
					logger.warn("R failed with code " + exitCode + ": " + pb.command().stream().collect(Collectors.joining(" ")));
					return null;
				}
				
			} catch (IOException e) {
				throw new UncheckedIOException(e);
				
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
		} finally {
			try {
				FileUtils.deleteDirectory(tempDir.toFile());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	private static void copyVisualisationResourceFile(Path dir, String resourceName) {
		
		Path outFile = dir.resolve(resourceName);
		try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("visualisation/" + resourceName)) {
			if (in == null) {
				throw new IllegalArgumentException("Could not find " + resourceName);
			}
			Files.copy(in, outFile);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	static String extractJson(String html) {
		
		final Parser parser = Parser.htmlParser();
		final Document doc = parser.parseInput(html, "");
		
		Element jsonScripElement = doc.select("script[type='application/json']").first();
		String json = jsonScripElement.html();
		return json;
	}

	static Map<String, String> getConnectionPoolProperties(Path domPath, String poolName) throws IOException {
		final Parser parser = Parser.xmlParser();
		final org.jsoup.nodes.Document doc;
		try ( Reader rd = Files.newBufferedReader(domPath)) {
			doc = parser.parseInput(rd, "");
		}
		
		Map<String, String> dbProperties = doc.select("jdbc-connection-pool[name=" + poolName + "] > property").stream()
		.collect(Collectors.toMap(e -> e.attr("name"), e -> e.attr("value")));
		return dbProperties;
	}

	@LocalBean
	@Stateless
	public static class VisualizationFacadeEjbLocal extends VisualizationFacadeEjb {
	}	
}
