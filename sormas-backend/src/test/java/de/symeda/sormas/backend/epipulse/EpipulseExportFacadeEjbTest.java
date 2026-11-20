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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.epipulse.export.EpipulseConfigurationLookupService;

/**
 * Tests that {@link EpipulseExportFacadeEjb} does not create an EpiPulse export for a disease without data source, and
 * tells the user where to enter one.
 *
 * <p>
 * Every test runs on the Luxembourg server of {@link AbstractEpipulseExportTest}, with MEASLES configured (subject code
 * {@code MEAS}, own data source {@code LU-SURV}), and a national user who may create EpiPulse exports.
 */
public class EpipulseExportFacadeEjbTest extends AbstractEpipulseExportTest {

	private EpipulseExportFacadeEjb.EpipulseExportFacadeEjbLocal exportFacade;
	private UserDto user;

	@BeforeEach
	public void setUpExportFacade() {

		configureLuxembourgFor(EpipulseSubjectCode.MEAS);

		// the EpiPulse rights are granted to roles by the schema, not by the default user roles of the tests
		user = creator.createNationalUser();
		executeInTransaction(em -> {
			getUserRoleService().getByUuid(user.getUserRoles().iterator().next().getUuid())
				.getUserRights()
				.addAll(Arrays.asList(UserRight.EPIPULSE_EXPORT_VIEW, UserRight.EPIPULSE_EXPORT_CREATE));
		});
		loginWith(user);

		exportFacade = getBean(EpipulseExportFacadeEjb.EpipulseExportFacadeEjbLocal.class);
	}

	/**
	 * A new export of a disease without own data source and without system value is rejected before it is stored, with the
	 * message of {@link EpipulseConfigurationLookupService#datasourceRequiredMessage}.
	 *
	 * <p>
	 * Example: MEASLES without data source, {@code EPIPULSE_DATASOURCE = ""} → "An EpiPulse data source is required for
	 * Measles. Enter it in the disease configuration of Measles, or as system configuration value EPIPULSE_DATASOURCE.";
	 * no row in {@code epipulse_export}.
	 */
	@Test
	@DisplayName("an export of a disease without data source is not created")
	public void exportWithoutDatasourceIsNotCreated() {

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);
		setSystemDatasource("");

		ValidationRuntimeException exception =
			assertThrows(ValidationRuntimeException.class, () -> exportFacade.saveEpipulseExport(newExport(EpipulseSubjectCode.MEAS)));

		assertThat(
			exception.getMessage(),
			is(getBean(EpipulseConfigurationLookupService.class).datasourceRequiredMessage(EpipulseSubjectCode.MEAS)));
		assertThat(exportCount(), is(0L));
	}

	/**
	 * A new export of a disease with its own data source is created.
	 *
	 * <p>
	 * Example: MEASLES with {@code "LU-SURV"}, no system value → the export is stored, one row in {@code epipulse_export}.
	 */
	@Test
	@DisplayName("an export of a disease with own data source is created")
	public void exportWithOwnDatasourceIsCreated() {

		EpipulseExportDto saved = exportFacade.saveEpipulseExport(newExport(EpipulseSubjectCode.MEAS));

		assertThat(saved, is(notNullValue()));
		assertThat(exportCount(), is(1L));
	}

	/**
	 * A new export of a disease without own data source is created when the system configuration value is set.
	 *
	 * <p>
	 * Example: MEASLES without data source, {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} → the export is stored, one row in
	 * {@code epipulse_export}.
	 */
	@Test
	@DisplayName("an export of a disease without own data source is created with the system value")
	public void exportWithSystemDatasourceIsCreated() {

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);
		setSystemDatasource("LU-SYSTEM1");

		EpipulseExportDto saved = exportFacade.saveEpipulseExport(newExport(EpipulseSubjectCode.MEAS));

		assertThat(saved, is(notNullValue()));
		assertThat(exportCount(), is(1L));
	}

	/**
	 * The data source is only checked for a new export: an existing export can still be saved after the data source was
	 * removed from the configuration.
	 *
	 * <p>
	 * Example: an export of MEASLES created with {@code "LU-SURV"}, then the data source of MEASLES removed and
	 * {@code EPIPULSE_DATASOURCE = ""} → saving the export again succeeds, still one row in {@code epipulse_export}.
	 */
	@Test
	@DisplayName("an existing export is saved again after its data source was removed")
	public void existingExportIsSavedWithoutDatasource() {

		EpipulseExportDto saved = exportFacade.saveEpipulseExport(newExport(EpipulseSubjectCode.MEAS));

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);
		setSystemDatasource("");

		EpipulseExportDto existing = exportFacade.getEpiPulseExportByUuid(saved.getUuid());
		EpipulseExportDto savedAgain = exportFacade.saveEpipulseExport(existing);

		assertThat(savedAgain.getUuid(), is(saved.getUuid()));
		assertThat(exportCount(), is(1L));
	}

	/**
	 * The reason that the background export stored for a failed export is returned with the export, so the export detail
	 * dialog can show it.
	 *
	 * <p>
	 * Example: {@code status_reason = "No EpiPulse subject code is configured for Diphtheria."} →
	 * {@link EpipulseExportDto#getStatusReason()} returns that text.
	 */
	@Test
	@DisplayName("the stored reason of an export is returned with it")
	public void statusReasonIsReturned() {

		String reason = "No EpiPulse subject code is configured for Diphtheria.";
		EpipulseExportDto saved = exportFacade.saveEpipulseExport(newExport(EpipulseSubjectCode.MEAS));

		executeInTransaction(em -> {
			em.createNativeQuery("UPDATE epipulse_export SET status_reason = :reason WHERE uuid = :uuid")
				.setParameter("reason", reason)
				.setParameter("uuid", saved.getUuid())
				.executeUpdate();
		});

		assertThat(exportFacade.getEpiPulseExportByUuid(saved.getUuid()).getStatusReason(), is(reason));
	}

	/**
	 * EpiPulse counts as configured as soon as one disease or the system configuration value has a data source.
	 *
	 * <p>
	 * Example: MEASLES with {@code "LU-SURV"} → {@code true}; no data source anywhere → {@code false};
	 * {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} only → {@code true}.
	 */
	@Test
	@DisplayName("EpiPulse is configured when a data source is entered")
	public void isConfiguredFollowsTheDatasource() {

		assertThat(exportFacade.isConfigured(), is(true));

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);
		setSystemDatasource("");
		assertThat(exportFacade.isConfigured(), is(false));

		setSystemDatasource("LU-SYSTEM1");
		assertThat(exportFacade.isConfigured(), is(true));
	}

	/**
	 * An export whose end date lies before its start date is still rejected by the date check, before the data source is
	 * looked at.
	 *
	 * <p>
	 * Example: MEASLES without data source, start today, end 10 days ago → "Report end date has to be after or on the same
	 * day as Report start date", not the data source message, and no {@code MissingFormatArgumentException}.
	 */
	@Test
	@DisplayName("the date check runs before the data source check")
	public void dateCheckRunsBeforeDatasourceCheck() {

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);

		EpipulseExportDto export = newExport(EpipulseSubjectCode.MEAS);
		export.setStartDate(new Date());
		export.setEndDate(DateHelper.subtractDays(new Date(), 10));

		ValidationRuntimeException exception = assertThrows(ValidationRuntimeException.class, () -> exportFacade.saveEpipulseExport(export));

		String dateMessage = I18nProperties.getValidationError(
			Validations.afterDate,
			I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.END_DATE),
			I18nProperties.getPrefixCaption(EpipulseExportDto.I18N_PREFIX, EpipulseExportDto.START_DATE));
		assertThat(exception.getMessage(), is(dateMessage));
		assertThat(exception.getMessage(), is("Report end date has to be after or on the same day as Report start date"));
		assertThat(exportCount(), is(0L));
	}

	/**
	 * A new export of {@code subjectCode} for the last 30 days, as the export dialog builds it: status
	 * {@link EpipulseExportStatus#PENDING} and a status change date, both required by {@code epipulse_export}.
	 */
	private EpipulseExportDto newExport(EpipulseSubjectCode subjectCode) {

		EpipulseExportDto export = EpipulseExportDto.build(user.toReference());
		export.setSubjectCode(subjectCode);
		export.setStartDate(DateHelper.subtractDays(new Date(), 30));
		export.setEndDate(new Date());
		export.setStatus(EpipulseExportStatus.PENDING);
		export.setStatusChangeDate(new Date());
		return export;
	}

	/** The number of rows in {@code epipulse_export}, e.g. {@code 0} when no export was stored. */
	private long exportCount() {
		return ((Number) getBean(EntityManager.class).createNativeQuery("SELECT count(*) FROM epipulse_export").getSingleResult()).longValue();
	}
}
