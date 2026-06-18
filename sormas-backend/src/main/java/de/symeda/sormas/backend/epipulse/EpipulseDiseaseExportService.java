/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.epipulse.strategy.IpiExportStrategy;
import de.symeda.sormas.backend.epipulse.strategy.MeaslesExportStrategy;
import de.symeda.sormas.backend.epipulse.strategy.MeniExportStrategy;
import de.symeda.sormas.backend.epipulse.strategy.PertussisExportStrategy;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class EpipulseDiseaseExportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private PertussisExportStrategy pertussisExportStrategy;

	@EJB
	private MeaslesExportStrategy measlesExportStrategy;

	@EJB
	private IpiExportStrategy ipiExportStrategy;

	@EJB
	private MeniExportStrategy meniExportStrategy;

	public EpipulseDiseaseExportResult exportPertussisCaseBased(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {
		return pertussisExportStrategy.export(exportDto, serverCountryLocale, serverCountryName);
	}

	public EpipulseDiseaseExportResult exportMeaslesCaseBased(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {
		return measlesExportStrategy.export(exportDto, serverCountryLocale, serverCountryName);
	}

	public EpipulseDiseaseExportResult exportIpiCaseBased(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {
		return ipiExportStrategy.export(exportDto, serverCountryLocale, serverCountryName);
	}

	public EpipulseDiseaseExportResult exportMeniCaseBased(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws SQLException, IllegalStateException, IllegalArgumentException {
		return meniExportStrategy.export(exportDto, serverCountryLocale, serverCountryName);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean tryClaimExportForProcessing(String exportUuid) {
		try {
			String sql = "UPDATE epipulse_export SET " +
				"status = :newStatus, " +
				"status_change_date = now(), " +
				"changedate = now() " +
				"WHERE uuid = :uuid AND status = :expectedStatus";

			int updated = em.createNativeQuery(sql)
				.setParameter("newStatus", EpipulseExportStatus.IN_PROGRESS.name())
				.setParameter("uuid", exportUuid)
				.setParameter("expectedStatus", EpipulseExportStatus.PENDING.name())
				.executeUpdate();

			em.flush();

			if (updated > 0) {
				logger.info("Successfully claimed export {} for processing", exportUuid);
				return true;
			} else {
				logger.info("Export {} already claimed by another process or not in PENDING status", exportUuid);
				return false;
			}

		} catch (Exception e) {
			logger.error("Failed to claim export {} for processing: {}", exportUuid, e.getMessage(), e);
			return false;
		} finally {
			em.clear();
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateStatusForBackgroundProcess(
		String exportUuid,
		EpipulseExportStatus newStatus,
		Integer totalRecords,
		String exportFileName,
		BigDecimal exportFileSizeBytes) {

		try {
			// If current status is CANCELLED, do not proceed
			@SuppressWarnings("unchecked")
			String currentStatusStr = (String) em.createNativeQuery("SELECT status FROM epipulse_export WHERE uuid = :uuid")
				.setParameter("uuid", exportUuid)
				.getSingleResult();

			EpipulseExportStatus currentStatus = EpipulseExportStatus.valueOf(currentStatusStr);
			if (currentStatus == EpipulseExportStatus.CANCELLED) {
				logger.info("Export {} is already cancelled, skipping status update", exportUuid);
				return;
			}

			// Validate that if status is COMPLETED, all file information must be non null
			if (newStatus == EpipulseExportStatus.COMPLETED) {
				if (totalRecords == null || exportFileName == null || exportFileSizeBytes == null) {
					throw new IllegalArgumentException(
						"When status is COMPLETED, totalRecords, exportFileName, and exportFileSizeBytes must not be null");
				}
			}

			//@formatter:off
			StringBuilder sql = new StringBuilder("UPDATE epipulse_export SET ")
                    .append("status = :status")
                    .append(", status_change_date = now()")
                    .append(", changedate = now()");
            //@formatter:on

			// Only update file metadata if status is COMPLETED
			if (newStatus == EpipulseExportStatus.COMPLETED) {
				sql.append(", total_records = :totalRecords");
				sql.append(", export_file_name = :exportFileName");
				sql.append(", export_file_size = :exportFileSize");
			}

			sql.append(" WHERE uuid = :uuid");

			Query q = em.createNativeQuery(sql.toString()).setParameter("status", newStatus.name()).setParameter("uuid", exportUuid);

			// Set parameters only if status is COMPLETED
			if (newStatus == EpipulseExportStatus.COMPLETED) {
				q.setParameter("totalRecords", totalRecords);
				q.setParameter("exportFileName", exportFileName);
				q.setParameter("exportFileSize", exportFileSizeBytes);
			}

			int updated = q.executeUpdate();

			em.flush();

			if (updated > 0) {
				logger.info("Updated export {} to status {}", exportUuid, newStatus);
			} else {
				logger.warn("No export found with uuid {} to update", exportUuid);
			}

		} catch (Exception e) {
			logger.error("CRITICAL: Failed to update export status in new transaction for uuid {}: {}", exportUuid, e.getMessage(), e);
			throw e;
		} finally {
			em.clear();
		}
	}

	public String generateDownloadFileName(EpipulseExportDto exportDto, Long exportId) {
		return exportDto.getSubjectCode().name() + "_" + StringUtils.replace(DateHelper.convertDateToDbFormat(exportDto.getStartDate()), "-", "")
			+ "_" + StringUtils.replace(DateHelper.convertDateToDbFormat(exportDto.getEndDate()), "-", "") + "_" + exportId + "_"
			+ (System.currentTimeMillis()) + ".csv";
	}
}
