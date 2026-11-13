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

package de.symeda.sormas.backend.epipulse.export;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.epipulse.AbstractEpipulseExportTest;
import de.symeda.sormas.backend.epipulse.EpipulseExport;
import de.symeda.sormas.backend.epipulse.disease.EpipulseFieldModels;

/**
 * Tests that {@link EpipulseCsvExportOrchestrator} stores a reason the user understands for a failed export, which the
 * export detail dialog shows: the message of a {@link ValidationRuntimeException} (an incomplete configuration), else a
 * general message that points to the server log.
 *
 * <p>
 * Every test creates one pending export of {@code DIPH} for the last 30 days on the Luxembourg server of
 * {@link AbstractEpipulseExportTest}, runs it and reads {@code status} and {@code status_reason} from
 * {@code epipulse_export}.
 */
public class EpipulseCsvExportOrchestratorTest extends AbstractEpipulseExportTest {

	private EpipulseCsvExportOrchestrator orchestrator;

	@BeforeEach
	public void setUpOrchestrator() {

		// the country only: DIPHTHERIA gets neither subject code nor data source
		configureLuxembourgFor();
		MockProducer.mockProperty(ConfigFacadeEjb.GENERATED_FILES_PATH, System.getProperty("java.io.tmpdir"));
		orchestrator = getBean(EpipulseCsvExportOrchestrator.class);
	}

	/**
	 * An incomplete configuration fails the export, and the message of the lookup becomes its reason.
	 *
	 * <p>
	 * Example: DIPHTHERIA without subject code → status FAILED, reason "No EpiPulse subject code is configured for
	 * Diphtheria. Enter it in the disease configuration of Diphtheria…".
	 */
	@Test
	@DisplayName("a missing subject code is stored as reason of the failed export")
	public void missingSubjectCodeIsStoredAsReason() {

		String uuid = createExport(EpipulseExportStatus.PENDING);

		orchestrator.orchestrateExport(
			uuid,
			() -> EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.DIPH),
			getBean(EpipulseCaseBasedExportStrategy.class)::export);

		String disease = String.valueOf(Disease.DIPHTHERIA);
		assertThat(statusOf(uuid), is(EpipulseExportStatus.FAILED.name()));
		assertThat(reasonOf(uuid), is(I18nProperties.getValidationError(Validations.epipulseSubjectCodeMissing, disease, disease)));
	}

	/**
	 * A {@link ValidationRuntimeException} wrapped in other exceptions, e.g. by the EJB container, is still found.
	 *
	 * <p>
	 * Example: an {@link IllegalStateException} caused by "An EpiPulse data source is required for Diphtheria…" → that
	 * text is the reason.
	 */
	@Test
	@DisplayName("a wrapped validation message is still used as reason")
	public void wrappedValidationMessageIsUsedAsReason() {

		String uuid = createExport(EpipulseExportStatus.PENDING);
		String message = "An EpiPulse data source is required for Diphtheria.";

		orchestrator.orchestrateExport(uuid, () -> EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.DIPH), (fieldModel, dto, countryName) -> {
			throw new IllegalStateException(new ValidationRuntimeException(message));
		});

		assertThat(statusOf(uuid), is(EpipulseExportStatus.FAILED.name()));
		assertThat(reasonOf(uuid), is(message));
	}

	/**
	 * A technical error is not shown to the user; the reason is a general message and the details stay in the log.
	 *
	 * <p>
	 * Example: an {@link SQLException} "relation epipulse_x does not exist" → reason "The export failed because of an
	 * unexpected error…", without the SQL text.
	 */
	@Test
	@DisplayName("a technical error gets the general message as reason")
	public void technicalErrorGetsGeneralReason() {

		String uuid = createExport(EpipulseExportStatus.PENDING);

		orchestrator.orchestrateExport(uuid, () -> EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.DIPH), (fieldModel, dto, countryName) -> {
			throw new SQLException("relation epipulse_x does not exist");
		});

		assertThat(statusOf(uuid), is(EpipulseExportStatus.FAILED.name()));
		assertThat(reasonOf(uuid), is(I18nProperties.getString(Strings.messageEpipulseExportFailedUnexpected)));
		assertThat(reasonOf(uuid), not(containsString("epipulse_x")));
	}

	/**
	 * An export that is not pending is not run again, and its status and reason stay as they are.
	 *
	 * <p>
	 * Example: a COMPLETED export → the export function is not called, status COMPLETED, no reason.
	 */
	@Test
	@DisplayName("an export that is not pending is not run and keeps its status")
	public void exportNotPendingIsNotRun() {

		String uuid = createExport(EpipulseExportStatus.COMPLETED);
		AtomicBoolean exportRun = new AtomicBoolean(false);

		orchestrator.orchestrateExport(uuid, () -> EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.DIPH), (fieldModel, dto, countryName) -> {
			exportRun.set(true);
			throw new SQLException("must not be called");
		});

		assertThat(exportRun.get(), is(false));
		assertThat(statusOf(uuid), is(EpipulseExportStatus.COMPLETED.name()));
		assertThat(reasonOf(uuid), is(nullValue()));
	}

	/**
	 * Creates an export of {@code DIPH} for the last 30 days with {@code status}, as the export dialog does, and returns its
	 * uuid.
	 */
	private String createExport(EpipulseExportStatus status) {

		String uuid = DataHelper.createUuid();
		Date endDate = new Date();

		executeInTransaction(em -> {
			EpipulseExport export = new EpipulseExport();
			export.setUuid(uuid);
			export.setSubjectCode(EpipulseSubjectCode.DIPH);
			export.setStartDate(DateHelper.subtractDays(endDate, 30));
			export.setEndDate(endDate);
			export.setStatus(status);
			// both required by epipulse_export
			export.setStatusChangeDate(endDate);
			export.setCreationUser(getUserService().getByUuid(nationalAdmin.getUuid()));
			em.persist(export);
		});

		return uuid;
	}

	/** The status of the export as stored, e.g. {@code "FAILED"}. */
	private String statusOf(String uuid) {
		return readColumn(uuid, "status");
	}

	/** The reason of the export as stored, e.g. "No EpiPulse subject code is configured for Diphtheria…". */
	private String reasonOf(String uuid) {
		return readColumn(uuid, "status_reason");
	}

	private String readColumn(String uuid, String column) {

		Object value = getBean(EntityManager.class).createNativeQuery("SELECT " + column + " FROM epipulse_export WHERE uuid = :uuid")
			.setParameter("uuid", uuid)
			.getSingleResult();
		return value != null ? value.toString() : null;
	}
}
