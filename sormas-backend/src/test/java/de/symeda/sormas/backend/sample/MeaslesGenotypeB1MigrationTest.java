package de.symeda.sormas.backend.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.epipulse.EpipulseTestTags;

@Tag(EpipulseTestTags.POSTGRES)
public class MeaslesGenotypeB1MigrationTest extends AbstractBeanTest {

	private SampleDto sample;
	private UserDto user;
	private TestDataCreator.RDCF rdcf;

	@BeforeEach
	public void createSample() {

		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf, DefaultUserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf, c -> c.setDisease(Disease.MEASLES));
		sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
	}

	@Test
	public void retiresEveryGenotypeBPathogenTestToB1() throws IOException {

		String labB1 = pathogenTest();
		String labB1MultiLine = pathogenTest();
		String manualB = pathogenTest();
		String notB1 = pathogenTest();
		String b3 = pathogenTest();
		// seeded only after every test exists: JPA can no longer read GENOTYPE_B
		setGenotype("pathogentest", labB1, "GENOTYPE_B", "MEAS-6 Genotype: B1");
		setGenotype("pathogentest", labB1MultiLine, "GENOTYPE_B", "Lab note\nMEAS-6 Genotype: B1\nother");
		setGenotype("pathogentest", manualB, "GENOTYPE_B", null);
		setGenotype("pathogentest", notB1, "GENOTYPE_B", "MEAS-6 Genotype: B10");
		setGenotype("pathogentest", b3, "GENOTYPE_B3", "MEAS-6 Genotype: B3");

		runMigration694();

		assertThat(genotype("pathogentest", labB1), is("GENOTYPE_B1"));
		assertThat(genotype("pathogentest", labB1MultiLine), is("GENOTYPE_B1"));
		assertThat(genotype("pathogentest", manualB), is("GENOTYPE_B1"));
		assertThat(genotype("pathogentest", notB1), is("GENOTYPE_B1"));
		assertThat(genotype("pathogentest", b3), is("GENOTYPE_B3"));
	}

	@Test
	public void retagsHistoryRowsWithoutWritingNewOnes() throws IOException {

		String labB1 = pathogenTest();
		setGenotype("pathogentest", labB1, "GENOTYPE_B", "MEAS-6 Genotype: B1");
		// a second update makes the trigger copy the B1-text version into history
		executeSql("UPDATE pathogentest SET changedate = now() WHERE uuid = '" + labB1 + "'");
		long historyRowsBefore = count("SELECT count(*) FROM pathogentest_history");

		runMigration694();

		assertThat(count("SELECT count(*) FROM pathogentest_history"), equalTo(historyRowsBefore));
		assertThat(
			count("SELECT count(*) FROM pathogentest_history WHERE genotype = 'GENOTYPE_B'"),
			equalTo(0L));
		assertThat(
			count(
				"SELECT count(*) FROM pathogentest_history WHERE genotype = 'GENOTYPE_B1' AND testresulttext = 'MEAS-6 Genotype: B1'"),
			equalTo(1L));
	}

	@Test
	public void retiresGenotypeBTestReportsToB1() throws IOException {

		String b1Report = testReport("GENOTYPE_B", "MEAS-6 Genotype: B1");
		String bReport = testReport("GENOTYPE_B", "manually typed");
		String b3Report = testReport("GENOTYPE_B3", "MEAS-6 Genotype: B3");

		runMigration694();

		assertThat(genotype("testreport", b1Report), is("GENOTYPE_B1"));
		assertThat(genotype("testreport", bReport), is("GENOTYPE_B1"));
		assertThat(genotype("testreport", b3Report), is("GENOTYPE_B3"));
	}

	@Test
	public void runningTheMigrationTwiceChangesNothingMore() throws IOException {

		String labB1 = pathogenTest();
		String manualB = pathogenTest();
		setGenotype("pathogentest", labB1, "GENOTYPE_B", "MEAS-6 Genotype: B1");
		setGenotype("pathogentest", manualB, "GENOTYPE_B", null);

		runMigration694();
		runMigration694();

		assertThat(genotype("pathogentest", labB1), is("GENOTYPE_B1"));
		assertThat(genotype("pathogentest", manualB), is("GENOTYPE_B1"));
	}

	private String pathogenTest() {

		PathogenTestDto test = creator.createPathogenTest(sample.toReference(), user.toReference(), t -> {
			t.setTestedDisease(Disease.MEASLES);
			t.setLab(rdcf.facility);
		});
		return test.getUuid();
	}

	private String testReport(String genotype, String resultText) {

		ExternalMessageDto message = creator.createLabMessageWithTestReport(sample.toReference());
		String uuid = (String) singleValue(
			"SELECT tr.uuid FROM testreport tr JOIN samplereport sr ON tr.samplereport_id = sr.id JOIN externalmessage em ON sr.labmessage_id = em.id WHERE em.uuid = '"
				+ message.getUuid() + "'");
		setGenotype("testreport", uuid, genotype, resultText);
		return uuid;
	}

	private void setGenotype(String table, String uuid, String genotype, String resultText) {

		String text = resultText == null ? "NULL" : "'" + resultText.replace("'", "''") + "'";
		executeSql("UPDATE " + table + " SET genotype = '" + genotype + "', testresulttext = " + text + " WHERE uuid = '" + uuid + "'");
	}

	private String genotype(String table, String uuid) {
		return (String) singleValue("SELECT genotype FROM " + table + " WHERE uuid = '" + uuid + "'");
	}

	private long count(String sql) {
		return ((Number) singleValue(sql)).longValue();
	}

	private Object singleValue(String sql) {

		return executeInTransaction(em -> {
			List<?> rows = em.createNativeQuery(sql).getResultList();
			return rows.isEmpty() ? null : rows.get(0);
		});
	}

	private void executeSql(String sql) {
		executeInTransaction((EntityManager em) -> {
			for (String statement : sql.split(";")) {
				if (!statement.isBlank()) {
					em.createNativeQuery(statement).executeUpdate();
				}
			}
		});
	}

	private void runMigration694() throws IOException {
		executeSql(migration694());
	}

	private static String migration694() throws IOException {

		String schema = new String(Files.readAllBytes(Paths.get("src/main/resources/sql/sormas_schema.sql")), StandardCharsets.UTF_8);
		int start = schema.indexOf('\n', schema.indexOf("VALUES (693,")) + 1;
		int end = schema.lastIndexOf('\n', schema.indexOf("VALUES (694,"));
		if (start <= 0 || end <= start) {
			throw new AssertionError("migration 694 not found in sormas_schema.sql");
		}
		return schema.substring(start, end);
	}
}
