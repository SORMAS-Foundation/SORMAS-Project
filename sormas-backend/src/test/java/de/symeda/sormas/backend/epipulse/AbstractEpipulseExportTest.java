/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.epipulse;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.junit.jupiter.api.Tag;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.epipulse.disease.EpipulseFieldModels;
import de.symeda.sormas.backend.epipulse.export.EpipulseCaseBasedExportStrategy;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.ExportLayout;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.infrastructure.country.Country;

/**
 * Base class for PostgreSQL-backed EpiPulse export integration tests.
 *
 * <p>
 * Tests build fixtures via facades (not raw inserts) so service-side derivations match production
 * behavior.
 *
 * @see EpipulseTestTags#POSTGRES
 */
@Tag(EpipulseTestTags.POSTGRES)
public abstract class AbstractEpipulseExportTest extends AbstractBeanTest {

	/** Server country used by export integration tests. */
	protected static final String SERVER_COUNTRY_NAME = "Luxembourg";
	protected static final String SERVER_COUNTRY_LOCALE = "de-LU";
	protected static final String SERVER_COUNTRY_ISO3 = "LUX";
	protected static final String SERVER_COUNTRY_LOCATION_CODE = "LU";

	/** Shared test data source used for configured subject codes. */
	protected static final String SERVER_DATA_SOURCE = "LU-SURV";

	/**
	 * Creates required EpiPulse configuration rows for the provided subject codes.
	 */
	protected void configureLuxembourgFor(EpipulseSubjectCode... subjectCodes) {

		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_NAME, SERVER_COUNTRY_NAME);
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, SERVER_COUNTRY_LOCALE);

		executeInTransaction(em -> {

			// the EpiPulse location code is the EpiPulse reporting country of the server country
			Country country = new Country();
			country.setUuid(DataHelper.createUuid());
			country.setDefaultName(SERVER_COUNTRY_NAME);
			country.setIsoCode(SERVER_COUNTRY_ISO3);
			country.setEpipulseLocationCode(SERVER_COUNTRY_LOCATION_CODE);
			em.persist(country);

			for (EpipulseSubjectCode subjectCode : subjectCodes) {
				configureDiseaseSubjectCode(em, subjectCode);
			}
		});
	}

	/**
	 * Enters the subject code in the disease configuration of its disease, creating the configuration when it is missing.
	 * An already entered code is kept, so SYPH and CONSYPH configured together leave SYPHILIS with the first of them; the
	 * export accepts the other one as a sibling code of the same disease.
	 */
	private void configureDiseaseSubjectCode(EntityManager em, EpipulseSubjectCode subjectCode) {

		Disease disease = subjectCode.getDisease();
		if (disease == null) {
			return;
		}

		DiseaseConfiguration configuration =
			em.createQuery("select d from " + DiseaseConfiguration.TABLE_NAME + " d where d.disease = :disease", DiseaseConfiguration.class)
				.setParameter("disease", disease)
				.getResultStream()
				.findFirst()
				.orElse(null);

		if (configuration == null) {
			configuration = DiseaseConfiguration.build(disease);
			configuration.setUuid(DataHelper.createUuid());
			em.persist(configuration);
		}

		// the disease's own data source; the system configuration value EPIPULSE_DATASOURCE, created empty by schema version
		// 687, is removed by the truncation before every test, see setSystemDatasource(String)
		if (configuration.getEpipulseDatasource() == null || configuration.getEpipulseDatasource().trim().isEmpty()) {
			configuration.setEpipulseDatasource(SERVER_DATA_SOURCE);
		}

		if (configuration.getEpipulseSubjectCode() == null || configuration.getEpipulseSubjectCode().trim().isEmpty()) {
			configuration.setEpipulseSubjectCode(subjectCode.name());
		}
	}

	/**
	 * Creates the system configuration value {@code EPIPULSE_DATASOURCE} with {@code value}, as schema version 687 creates it
	 * (category {@code EPIPULSE}, optional), and as an admin then fills it in. The test database is truncated before every
	 * test, so the row from the migration is gone and every test that needs it creates it here.
	 *
	 * <p>
	 * Example: {@code setSystemDatasource("LU-SYSTEM1")} → a disease without its own data source is exported with
	 * {@code "LU-SYSTEM1"}; {@code setSystemDatasource("")} → the state of a new installation, the key exists but is empty.
	 */
	protected void setSystemDatasource(String value) {

		executeInTransaction(em -> {
			//@formatter:off
			em.createNativeQuery(
				"INSERT INTO systemconfigurationcategory(id, uuid, changedate, creationdate, name, caption, description) " +
				"SELECT nextval('entity_seq'), generate_base32_uuid(), now(), now(), 'EPIPULSE', " +
				"       'i18n/EpiPulse/categoryEpipulse', 'i18n/EpiPulse/categoryEpipulse' " +
				"WHERE NOT EXISTS (SELECT 1 FROM systemconfigurationcategory WHERE name = 'EPIPULSE')")
				.executeUpdate();
			em.createNativeQuery("DELETE FROM systemconfigurationvalue WHERE config_key = 'EPIPULSE_DATASOURCE'").executeUpdate();
			em.createNativeQuery(
				"INSERT INTO systemconfigurationvalue(config_key, config_value, category_id, value_optional, value_encrypt, " +
				"                                     changedate, creationdate, id, uuid) " +
				"SELECT 'EPIPULSE_DATASOURCE', :value, c.id, true, false, now(), now(), nextval('entity_seq'), generate_base32_uuid() " +
				"FROM systemconfigurationcategory c WHERE c.name = 'EPIPULSE'")
				.setParameter("value", value)
				.executeUpdate();
			//@formatter:on
		});
	}

	/**
	 * Sets one EpiPulse column of the disease configuration of {@code disease} directly in the database, e.g.
	 * {@code setDiseaseConfigurationValue(Disease.DIPHTHERIA, "epipulsesubjectcode", "MEAS")} for a wrong subject code, or
	 * {@code setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null)} to remove its own data source.
	 *
	 * @param column
	 *            {@code epipulsesubjectcode} or {@code epipulsedatasource}
	 */
	protected void setDiseaseConfigurationValue(Disease disease, String column, String value) {

		executeInTransaction(em -> {
			// unwrapped like EpipulseCaseBasedExportStrategy does, so a null value is bound with its SQL type
			em.createNativeQuery("UPDATE diseaseconfiguration SET " + column + " = :value WHERE disease = :disease")
				.unwrap(NativeQuery.class)
				.setParameter("value", value, StandardBasicTypes.STRING)
				.setParameter("disease", disease.name())
				.executeUpdate();
		});
	}

	/**
	 * Sets the EpiPulse location code of the server country (default name {@link #SERVER_COUNTRY_NAME}) directly in the
	 * database, e.g. {@code setServerCountryLocationCode(null)} for a server country without one, or
	 * {@code setServerCountryLocationCode("lu")} for one entered in lower case.
	 */
	protected void setServerCountryLocationCode(String locationCode) {

		executeInTransaction(em -> {
			em.createNativeQuery("UPDATE country SET epipulselocationcode = :code WHERE defaultname = :name")
				.unwrap(NativeQuery.class)
				.setParameter("code", locationCode, StandardBasicTypes.STRING)
				.setParameter("name", SERVER_COUNTRY_NAME)
				.executeUpdate();
		});
	}

	/**
	 * Archives the server country (default name {@link #SERVER_COUNTRY_NAME}) directly in the database, e.g. to check that
	 * an archived country is never used as reporting country.
	 */
	protected void archiveServerCountry() {

		executeInTransaction(em -> {
			em.createNativeQuery("UPDATE country SET archived = true WHERE defaultname = :name")
				.setParameter("name", SERVER_COUNTRY_NAME)
				.executeUpdate();
		});
	}

	/**
	 * Assigns test NUTS codes for region/district/community in one jurisdiction.
	 */
	protected void assignNutsCodes(TestDataCreator.RDCF rdcf, String regionNutsCode, String districtNutsCode, String communityNutsCode) {

		executeInTransaction(em -> {
			setNutsCode(em, "region", rdcf.region.getUuid(), regionNutsCode);
			setNutsCode(em, "district", rdcf.district.getUuid(), districtNutsCode);
			setNutsCode(em, "community", rdcf.community.getUuid(), communityNutsCode);
		});
	}

	private static void setNutsCode(EntityManager em, String table, String uuid, String nutsCode) {

		em.createNativeQuery("UPDATE " + table + " SET nutscode = :nutsCode WHERE uuid = :uuid")
			.setParameter("nutsCode", nutsCode)
			.setParameter("uuid", uuid)
			.executeUpdate();
	}

	/**
	 * Runs one export and returns in-memory entries/layout helpers.
	 */
	protected ExportedEntries runExport(EpipulseSubjectCode subjectCode, Date startDate, Date endDate) {

		EpipulseExportDto exportDto = new EpipulseExportDto();
		exportDto.setSubjectCode(subjectCode);
		exportDto.setStartDate(startDate);
		exportDto.setEndDate(endDate);

		FieldModel fieldModel = EpipulseFieldModels.forSubjectCode(subjectCode);

		try {
			List<EpipulseDiseaseExportEntryDto> entries = getBean(EpipulseCaseBasedExportStrategy.class)
				.export(fieldModel, exportDto, getConfigFacade().getCountryName())
				.getExportEntryList();

			return new ExportedEntries(fieldModel, entries);
		} catch (SQLException e) {
			throw new IllegalStateException("Export of " + subjectCode + " failed", e);
		}
	}

	/**
	 * Fixture builder for one disease/jurisdiction/reporting user setup.
	 */
	protected final class CaseFixture {

		private final Disease disease;
		private final TestDataCreator.RDCF rdcf;
		private final UserReferenceDto reportingUser;

		private CaseFixture(Disease disease, TestDataCreator.RDCF rdcf, UserDto reportingUser) {
			this.disease = disease;
			this.rdcf = rdcf;
			this.reportingUser = reportingUser.toReference();
		}

		/**
		 * @param reportAndOnsetDate
		 *            written to both the report date and the symptom onset date, as
		 *            {@code TestDataCreator} does -- the case selection reads the first and most of
		 *            the date columns read the second
		 */
		public CaseDataDto caze(PersonReferenceDto person, Date reportAndOnsetDate) {
			return caze(person, reportAndOnsetDate, null);
		}

		public CaseDataDto caze(PersonReferenceDto person, Date reportAndOnsetDate, Consumer<CaseDataDto> extraConfig) {

			return creator.createCase(
				reportingUser,
				person,
				disease,
				CaseClassification.CONFIRMED,
				InvestigationStatus.DONE,
				reportAndOnsetDate,
				rdcf,
				extraConfig);
		}

		/**
		 * Creates a vaccination course and resaves it so service logic derives {@code ACQUIRED} state.
		 */
		public ImmunizationDto completedCourse(PersonReferenceDto person, Integer numberOfDoses, Date validFrom, Date validUntil, Date... doseDates) {

			ImmunizationDto immunization = creator.createImmunization(disease, person, reportingUser, rdcf, i -> {
				i.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
				i.setNumberOfDoses(numberOfDoses);
				i.setValidFrom(validFrom);
				i.setValidUntil(validUntil);
			});

			for (Date doseDate : doseDates) {
				creator.createVaccination(reportingUser, immunization.toReference(), new HealthConditionsDto(), doseDate, null, null);
			}

			return getImmunizationFacade().save(getImmunizationFacade().getByUuid(immunization.getUuid()));
		}

		/** A test on a sample of its own, so several can sit on one case. */
		public void pathogenTest(CaseDataDto caze, PathogenTestType testType, PathogenTestResultType result, boolean verified, Date testDate) {
			pathogenTest(caze, testType, result, verified, testDate, null);
		}

		/**
		 * As {@link #pathogenTest(CaseDataDto, PathogenTestType, PathogenTestResultType, boolean, Date)},
		 * with the laboratory detail a disease reports on top -- a serotyping method, a
		 * susceptibility panel, the reference-laboratory flag.
		 */
		public void pathogenTest(
			CaseDataDto caze,
			PathogenTestType testType,
			PathogenTestResultType result,
			boolean verified,
			Date testDate,
			Consumer<PathogenTestDto> extraConfig) {

			SampleDto sample = creator.createSample(caze.toReference(), reportingUser, rdcf.facility, null);

			creator.createPathogenTest(
				sample.toReference(),
				testType,
				disease,
				testDate,
				rdcf.facility,
				reportingUser,
				result,
				"",
				verified,
				extraConfig);
		}

		/**
		 * A specimen with nothing done to it yet, for a disease that reports the specimen itself.
		 *
		 * <p>
		 * {@link #pathogenTest} gives every test a specimen of its own, which is all a disease
		 * reporting only test results needs. MEAS reports what the specimens were made of and when
		 * the first was taken, and tells "collected" from "collected for serology" by which tests
		 * sit on one -- so it needs to say which specimen a test goes on, and needs a specimen with
		 * no test at all to be expressible.
		 *
		 * @param sampleDate
		 *            the collection date, rather than {@code TestDataCreator}'s default of now -- a
		 *            fixture with fixed dates cannot assert a date column against today
		 */
		public SampleDto specimen(CaseDataDto caze, SampleMaterial material, Date sampleDate) {

			return creator.createSample(caze.toReference(), reportingUser, rdcf.facility, sample -> {
				sample.setSampleMaterial(material);
				sample.setSampleDateTime(sampleDate);
			});
		}

		/** A test on an existing {@link #specimen}, so several can share one. */
		public PathogenTestDto testOn(SampleDto specimen, PathogenTestType testType, PathogenTestResultType result, boolean verified, Date testDate) {
			return testOn(specimen, testType, result, verified, testDate, null);
		}

		public PathogenTestDto testOn(
			SampleDto specimen,
			PathogenTestType testType,
			PathogenTestResultType result,
			boolean verified,
			Date testDate,
			Consumer<PathogenTestDto> extraConfig) {

			return creator.createPathogenTest(
				specimen.toReference(),
				testType,
				disease,
				testDate,
				rdcf.facility,
				reportingUser,
				result,
				"",
				verified,
				extraConfig);
		}
	}

	protected CaseFixture fixtureFor(Disease disease, TestDataCreator.RDCF rdcf, UserDto reportingUser) {
		return new CaseFixture(disease, rdcf, reportingUser);
	}

	/**
	 * The case configuration {@code Hospitalisation} reports {@code true} for.
	 *
	 * <p>
	 * Both halves are needed -- an admission for any other reason reports {@code false} -- which is
	 * the part of the rule a fixture is most likely to get wrong, so it is written once here.
	 */
	protected static Consumer<CaseDataDto> hospitalisedForTheReportedDisease() {

		return caze -> {
			caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
			caze.getHospitalization().setHospitalizationReason(HospitalizationReasonType.REPORTED_DISEASE);
		};
	}

	/**
	 * One export's entries, with the field model that produced them, so a value can be asked for by
	 * EpiPulse variable and the written shape can be asked for separately.
	 */
	protected static final class ExportedEntries {

		private final List<EpipulseDiseaseExportEntryDto> entries;
		private final EpipulseValues values;
		private final ExportLayout layout;

		private ExportedEntries(FieldModel fieldModel, List<EpipulseDiseaseExportEntryDto> entries) {
			this.entries = entries;
			this.values = new EpipulseValues(fieldModel);
			this.layout = fieldModel.layout(entries);
		}

		public List<EpipulseDiseaseExportEntryDto> all() {
			return entries;
		}

		public int size() {
			return entries.size();
		}

		/**
		 * The one entry this export produced.
		 *
		 * @throws AssertionError
		 *             if the export produced any other number, which is a failure of the fixture or
		 *             of the case selection rather than of whatever the caller meant to assert
		 */
		public EpipulseDiseaseExportEntryDto only() {

			if (entries.size() != 1) {
				throw new AssertionError("Expected exactly one exported case, got " + entries.size());
			}

			return entries.get(0);
		}

		/**
		 * The entry for one particular case, so that a single export can carry a case per scenario.
		 *
		 * <p>
		 * Several cases in one export rather than one case per test is what keeps a branch-heavy
		 * suite affordable: the database is truncated after every test method, so each method costs
		 * a couple of seconds whatever it asserts, while another case inside one costs almost
		 * nothing.
		 *
		 * @param nationalRecordId
		 *            the case UUID, which is what {@code NationalRecordId} reports
		 * @throws AssertionError
		 *             if the export has no entry for that case
		 */
		public EpipulseDiseaseExportEntryDto entryFor(String nationalRecordId) {

			return entries.stream()
				.filter(entry -> nationalRecordId.equals(entry.getNationalRecordId()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("No exported entry for case " + nationalRecordId));
		}

		public String value(EpipulseDiseaseExportEntryDto entry, EpipulseVariable variable) {
			return values.value(entry, variable);
		}

		public List<String> values(EpipulseDiseaseExportEntryDto entry, EpipulseVariable variable) {
			return values.values(entry, variable);
		}

		public boolean emits(EpipulseVariable variable) {
			return values.emits(variable);
		}

		/**
		 * The header this export would write -- repeatable groups already measured against every
		 * entry, so a group no case filled is absent entirely.
		 */
		public List<String> header() {
			return layout.columnNames();
		}

		public String[] row(EpipulseDiseaseExportEntryDto entry) {
			return layout.row(entry);
		}
	}
}
