package de.symeda.sormas.backend.importexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinTable;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.backend.common.messaging.ManualMessageLog;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.backend.immunization.entity.DirectoryImmunization;
import de.symeda.sormas.backend.systemevent.SystemEvent;
import de.symeda.sormas.backend.user.UserReference;
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
			String tableName = DatabaseExportService.getTableName(databaseTable);
			assertNotNull(
				String.format("No export configuration defined for %s.%s", DatabaseTable.class.getSimpleName(), databaseTable.name()),
				tableName);
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
		Environment.class,
		EnvironmentSample.class);

	@Test
	public void test_all_entities_have_export_configuration() {
		Collection<String> exportableTables = DatabaseExportService.EXPORT_CONFIGS.values();

		Set<String> missingEntities = new HashSet<>();
		Set<String> missingJoinTables = new HashSet<>();
		Set<String> exportedButNotWantedEntity = new HashSet<>();

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
					exportedButNotWantedEntity.add(clazz.getSimpleName());
				}

				for (JavaMethod method : clazz.getMethods()) {
					if (method.isAnnotatedWith(JoinTable.class)) {
						JoinTable joinTableAnnotation = method.getAnnotationOfType(JoinTable.class);
						String joinTableName = joinTableAnnotation.name();
						assertFalse(StringUtils.isBlank(joinTableName));

						if (!exportableTables.contains(joinTableName)) {
							missingJoinTables.add(joinTableName);
						}
					}
				}
			}
		}

		// remove not exported entities from the list of missing ones
		NOT_EXPORTED_ENTITIES.forEach(e -> missingEntities.remove(e.getSimpleName()));

		assertThat("Missing export configuration for entities [" + String.join(", ", missingEntities) + "]", missingEntities, hasSize(0));
		assertThat(
			"Export configuration not wanted for entities [" + String.join(", ", exportedButNotWantedEntity) + "]",
			exportedButNotWantedEntity,
			hasSize(0));
		assertThat("Missing export configuration for join tables [" + String.join(", ", missingJoinTables) + "]", missingJoinTables, hasSize(0));
	}
}
