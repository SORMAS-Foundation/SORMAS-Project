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

package de.symeda.sormas.backend.selfreport;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.NotImplementedException;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportFacade;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.selfreport.SelfReportReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SelfReportFacade")
@RightsAllowed(UserRight._SELF_REPORT_VIEW)
public class SelfReportFacadeEjb
	extends
	AbstractCoreFacadeEjb<SelfReport, SelfReportDto, SelfReportIndexDto, SelfReportReferenceDto, SelfReportService, SelfReportCriteria>
	implements SelfReportFacade {

	public SelfReportFacadeEjb() {
	}

	@Inject
	public SelfReportFacadeEjb(SelfReportService service) {
		super(SelfReport.class, SelfReportDto.class, service);
	}

	@Override
	@RightsAllowed({
		UserRight._SELF_REPORT_CREATE,
		UserRight._SELF_REPORT_EDIT })
	public SelfReportDto save(@Valid SelfReportDto dto) {
		SelfReport existingSelfReport = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

		FacadeHelper
			.checkCreateAndEditRights(existingSelfReport, userService, UserRight.SELF_REPORT_CREATE, UserRight.SELF_REPORT_EDIT);

		SelfReportDto existingDto = toDto(existingSelfReport);
		Pseudonymizer<SelfReportDto> pseudonymizer = createPseudonymizer(existingSelfReport);
		restorePseudonymizedDto(dto, existingDto, existingSelfReport, pseudonymizer);

		validate(dto);

		SelfReport selfReport = fillOrBuildEntity(dto, existingSelfReport, true);
		service.ensurePersisted(selfReport);

		return toPseudonymizedDto(selfReport, pseudonymizer);
	}

	@Override
	public long count(SelfReportCriteria criteria) {
		return 0;
	}

	@Override
	public List<SelfReportIndexDto> getIndexList(SelfReportCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		return null;
	}

	@Override
	public void validate(SelfReportDto dto) throws ValidationRuntimeException {

	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		return null;
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		return null;
	}

	@Override
	protected SelfReport fillOrBuildEntity(SelfReportDto source, SelfReport target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, SelfReport::new, checkChangeDate);

		target.setReportDate(source.getReportDate());
		target.setResponsibleUser(userService.getByReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		return target;
	}

	@Override
	protected SelfReportDto toDto(SelfReport source) {
		if (source == null) {
			return null;
		}
		SelfReportDto target = new SelfReportDto();

		DtoHelper.fillDto(target, source);

		target.setReportDate(source.getReportDate());
		target.setResponsibleUser(UserFacadeEjb.toReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		return target;
	}

	@Override
	protected SelfReportReferenceDto toRefDto(SelfReport selfReport) {
		return new SelfReportReferenceDto(selfReport.getUuid());
	}

	@Override
	protected void pseudonymizeDto(
		SelfReport source,
		SelfReportDto dto,
		Pseudonymizer<SelfReportDto> pseudonymizer,
		boolean inJurisdiction) {

	}

	@Override
	protected void restorePseudonymizedDto(
		SelfReportDto dto,
		SelfReportDto existingDto,
		SelfReport entity,
		Pseudonymizer<SelfReportDto> pseudonymizer) {

	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.SELF_REPORT;
	}

	@LocalBean
	@Stateless
	public static class SelfReportFacadeEjbLocal extends SelfReportFacadeEjb {

		@Inject
		public SelfReportFacadeEjbLocal(SelfReportService service) {
			super(service);
		}
	}
}
