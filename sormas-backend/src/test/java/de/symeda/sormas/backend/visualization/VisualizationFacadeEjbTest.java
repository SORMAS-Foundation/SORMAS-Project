package de.symeda.sormas.backend.visualization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.symeda.sormas.api.Language;

public class VisualizationFacadeEjbTest { // extends AbstractBeanTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Test
	@Ignore
	public void testStaticBuildTransmissionChainJson() {

		// FIXME depends on local database

		List<Long> contactIds = Arrays.asList(30481L, 30478L);

		String[] rscriptExecutableLocs = {
			"C:\\Program Files\\R\\R-3.6.2\\bin\\Rscript.exe",
			"C:\\Program Files\\R\\R-3.6.3\\bin\\Rscript.exe" };

		Optional<String> rscriptExecutable = Arrays.stream(rscriptExecutableLocs).filter(p -> Files.isExecutable(Paths.get(p))).findFirst();

		rscriptExecutable.ifPresent(r -> {

			Path domainXmlPath = writeDomainXml();

			String result = VisualizationFacadeEjb.buildTransmissionChainJson(r, temp.getRoot().toPath(), domainXmlPath, contactIds, Language.EN);
			assertThat(result, startsWith("{"));
			assertThat(result, endsWith("}"));
		});
	}

	@Test
	public void testExtractJson() {
		String json = VisualizationFacadeEjb.extractJson(
			"<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<meta charset=\"utf-8\"/>\r\n" + "<style>body{background-color:white;}</style>\r\n"
				+ "<script src=\"result_files/htmlwidgets-1.5.1/htmlwidgets.js\"></script>\r\n"
				+ "<link href=\"result_files/vis-4.20.1/vis.css\" rel=\"stylesheet\" />\r\n" + "</head>\r\n" + "<body>\r\n"
				+ "<div id=\"htmlwidget_container\">\r\n"
				+ "  <div id=\"htmlwidget-b9f896960aa32dc1f3e5\" style=\"width:90%;height:700px;\" class=\"visNetwork html-widget\"></div>\r\n"
				+ "</div>\r\n" + "<script type=\"application/json\" data-for=\"htmlwidget-b9f896960aa32dc1f3e5\">{\"x\":\"a\\/b\"}</script>\r\n"
				+ "</body>\r\n" + "</html>",
			Language.EN);
		assertThat(json, is("{\"x\":\"a\\/b\"}"));
	}

	@Test
	public void testReadDbConection() throws IOException {

		Path domainXmlPath = writeDomainXml();

		String poolName = "sormasDataPool";
		Map<String, String> dbProperties = VisualizationFacadeEjb.getConnectionPoolProperties(domainXmlPath, poolName);
		assertThat(dbProperties.entrySet(), hasSize(5));
		assertThat(dbProperties, Matchers.hasEntry("user", "sormas_user"));
		assertThat(dbProperties, Matchers.hasEntry("password", "sormas_user"));
		assertThat(dbProperties, Matchers.hasEntry("portNumber", "5432"));
		assertThat(dbProperties, Matchers.hasEntry("databaseName", "sormas_db"));
		assertThat(dbProperties, Matchers.hasEntry("serverName", "localhost"));
	}

	private Path writeDomainXml() {
		try {
			Path domPath = Files.createTempFile(temp.getRoot().toPath(), "domain", ".xml");

			try (InputStream in = getClass().getResourceAsStream("/domain.xml")) {
				Files.copy(in, domPath, StandardCopyOption.REPLACE_EXISTING);
			}
			return domPath;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
