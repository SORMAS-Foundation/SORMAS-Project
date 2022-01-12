package de.symeda.sormas.backend.importexport;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.backend.auditlog.AuditLogEntry;
import de.symeda.sormas.backend.common.messaging.ManualMessageLog;
import de.symeda.sormas.backend.immunization.entity.DirectoryImmunization;
import de.symeda.sormas.backend.systemevent.SystemEvent;
import de.symeda.sormas.backend.user.UserReference;
import de.symeda.sormas.backend.user.UserRoleConfig;
import de.symeda.sormas.backend.vaccination.FirstVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccineType;

/**
 * @see DatabaseExportService
 * @author Stefan Kock
 */
public class DatabaseExportServiceTest {

	/**
	 * Assure, that every {@link DatabaseTable} has an export configuration defined.
	 */
	@Test
	public void testGetConfigFullyDefined() {

		for (DatabaseTable databaseTable : DatabaseTable.values()) {
			DatabaseExportConfiguration config = DatabaseExportService.getConfig(databaseTable);
			assertNotNull(
				String.format("No export configuration defined for %s.%s", DatabaseTable.class.getSimpleName(), databaseTable.name()),
				config);

			assertThat(config.getTableName(), not(isEmptyString()));

			if (config.isUseJoinTable()) {
				assertThat(config.getJoinTableName(), not(isEmptyString()));
				assertThat(config.getColumnName(), not(isEmptyString()));
				assertThat(config.getJoinColumnName(), not(isEmptyString()));
			}
		}
	}

	private static final List<Class<?>> NOT_EXPORTED_ENTITIES = Arrays.asList(
		DirectoryImmunization.class,
		LastVaccinationDate.class,
		ManualMessageLog.class,
		UserReference.class,
		LastVaccineType.class,
		SystemEvent.class,
		FirstVaccinationDate.class,
		AuditLogEntry.class,
		UserRoleConfig.class);

	@Test
	public void test_all_entities_have_export_configuration() {
		Set<String> exportableTables =
			DatabaseExportService.EXPORT_CONFIGS.values().stream().map(DatabaseExportConfiguration::getTableName).collect(Collectors.toSet());
		Set<String> missingEntities = new HashSet<>();
		Set<String> exportedButNotWanted = new HashSet<>();

		JavaClasses classes = new ClassFileImporter().importPackages("de.symeda.sormas.backend");

		for (JavaClass clazz : classes) {
			if (clazz.isAnnotatedWith(Entity.class)) {
				Entity entityAnnotation = clazz.getAnnotationOfType(Entity.class);
				String tableName = entityAnnotation.name();
				if (StringUtils.isBlank(tableName)) {
					tableName = clazz.getSimpleName().toLowerCase();
				}

				if (!exportableTables.contains(tableName)) {
					missingEntities.add(clazz.getSimpleName());
				} else if (NOT_EXPORTED_ENTITIES.contains(clazz.reflect())) {
					exportedButNotWanted.add(clazz.getSimpleName());
				}
			}
		}

		// remove not exported entities from the list of missing ones
		NOT_EXPORTED_ENTITIES.forEach(e -> missingEntities.remove(e.getSimpleName()));

		assertThat("Missing export configuration for entities [" + String.join(", ", missingEntities) + "]", missingEntities, hasSize(0));
		assertThat(
			"Export configuration not wanted for entities [" + String.join(", ", exportedButNotWanted) + "]",
			exportedButNotWanted,
			hasSize(0));
	}
}
