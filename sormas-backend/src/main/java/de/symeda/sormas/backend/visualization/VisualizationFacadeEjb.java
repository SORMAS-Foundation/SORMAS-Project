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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.visualization.VisualizationFacade;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "VisualizationFacade")
public class VisualizationFacadeEjb implements VisualizationFacade {

//	private static final String TRANSMISSION_CHAIN_SCRIPT = "transmission_chain.r";
	private static final String TRANSMISSION_CHAIN_SCRIPT = "transform_contact.R";
	private static final String[] REQUIRED_SCRIPTS = {"encodeGraphic.R", "networkFunction.R"};

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Override
	public String buildTransmissionChainJson(RegionReferenceDto region, DistrictReferenceDto district, Collection<Disease> diseases) {
		
		String rExecutable = configFacade.getRScriptExecutable();
		if (StringUtils.isBlank(rExecutable)) {
			return null;
		}
		Path tempBasePath = new File(configFacade.getTempFilesPath()).toPath();
		
		Collection<Long> contactIds = getContactIds(region, district, diseases);
		
		if (contactIds.isEmpty()) {
			return null;
		}
		
		//working dir is the config directory of the domain
		Path domainXmlPath = Paths.get("domain.xml");
		
		return buildTransmissionChainJson(rExecutable, tempBasePath, domainXmlPath, contactIds);
	}
	

	private Collection<Long> getContactIds(RegionReferenceDto region, DistrictReferenceDto district, Collection<Disease> diseases) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> root = cq.from(Contact.class);
		Join<Contact, Case> caze = root.join(Contact.CAZE, JoinType.LEFT);

		cq.where(AbstractAdoService.and(cb,
				contactService.createUserFilter(cb, cq, root),
				contactService.createActiveContactsFilter(cb, root),
				contactService.createDefaultFilter(cb, root),
				cb.notEqual(root.get(Contact.CONTACT_CLASSIFICATION), ContactClassification.NO_CONTACT),
				cb.notEqual(root.get(Contact.CONTACT_STATUS), ContactStatus.DROPPED),
				cb.or(cb.isNull(caze), caseService.createDefaultFilter(cb, caze)),
				root.get(Contact.DISEASE).in(diseases),
				region == null ? null : cb.equal(root.join(Contact.REGION).get(Region.UUID), region.getUuid()),
				district == null ? null : cb.equal(root.join(Contact.DISTRICT).get(District.UUID), district.getUuid())
		));
		
		cq.select(root.get(AbstractDomainObject.ID));
		return em.createQuery(cq).getResultList();
	}

	enum EnvParam {
		DB_USER("user"),
		DB_PASS("password"),
		DB_NAME("databaseName"),
		DB_HOST("serverName"), 
		DB_PORT("portNumber"), 
		CONTACT_IDS,
		HIERARCHICAL,
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
	
	static String buildTransmissionChainJson(String rScriptExecutable, Path tempBasePath, Path domainXmlPath, Collection<Long> contactIds) {

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
				pb.directory(tempDir.toFile());
					
				Map<String, String> poolProperties = getConnectionPoolProperties(domainXmlPath, "sormasDataPool");
				Map<String, String> env = pb.environment();
	
				EnvParam.DB_USER.putFrom(env, poolProperties);
				EnvParam.DB_PASS.putFrom(env, poolProperties);
				EnvParam.DB_NAME.putFrom(env, poolProperties);
				EnvParam.DB_HOST.putFrom(env, poolProperties); 
				EnvParam.DB_PORT.putFrom(env, poolProperties);

				String contactIdStr;
				if (contactIds.isEmpty()) {
					contactIdStr = "NULL";
				} else {
					StringBuilder sb = new StringBuilder(contactIds.size() * 6);
					contactIds.forEach(l -> {
						sb.append(l);
						sb.append(',');
					});
					contactIdStr = sb.substring(0, sb.length() - 1);
				}
				
				EnvParam.CONTACT_IDS.put(env, contactIdStr);
				EnvParam.OUTFILE.put(env, outputFile.toString());
				
//				File outFile = tempDir.resolve("console.log").toFile();
//				pb.redirectOutput(outFile );
//				pb.redirectError(outFile);
				
				Process pr = pb.start();
				int exitCode = pr.waitFor();
				
				if (exitCode == 0) {
					String html = new String(Files.readAllBytes(outputFile));
					return extractJson(html);
				} else {
					LoggerFactory
						.getLogger(VisualizationFacadeEjb.class)
						.warn("R failed with code {} : {}", exitCode, pb.command().stream().collect(Collectors.joining(" ")));
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
		json = doI18n(json);
		return json;
	}
	
	private static final Map<String, Enum<?>> supportedEnums;
	static {
		Map<String, Enum<?>> map = new HashMap<>();
		Arrays.stream(CaseClassification.values())
		.forEach(e -> map.put("Classification." + e.name(), e));
		supportedEnums = Collections.unmodifiableMap(map);
	}

	private static final Pattern ENUM_PATTERN = Pattern.compile("\"(([A-Za-z]+)\\.([A-Z_]+))\"");
	
	private static String doI18n(String json) {
		
		Matcher m = ENUM_PATTERN.matcher(json);
		
		StringBuffer sb = new StringBuffer(json.length());
		 while (m.find()) {
			String replacement = Optional.of(m.group(1))
			.map(supportedEnums::get)
			.map(I18nProperties::getEnumCaption)
			//TODO real json escaping
			.map(c -> "\"" + c.replace("\"", "\\\"") + "\"")
			.orElseGet(() -> {
				//TODO i18n of Classification.HEALTHY
				if (m.group(2).equals("Classification")) {
					String name = m.group(3);
					return "\"" + name.charAt(0) + name.substring(1).toLowerCase() + "\"";
				} else {
					return m.group();
				}
			});
			 
		     m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		 }
		 m.appendTail(sb);
		
		 return sb.toString();
		
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
