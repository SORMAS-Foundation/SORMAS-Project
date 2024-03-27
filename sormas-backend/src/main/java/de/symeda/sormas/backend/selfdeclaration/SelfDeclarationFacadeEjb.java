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

package de.symeda.sormas.backend.selfdeclaration;

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
import de.symeda.sormas.api.selfdeclaration.SelfDeclarationCriteria;
import de.symeda.sormas.api.selfdeclaration.SelfDeclarationDto;
import de.symeda.sormas.api.selfdeclaration.SelfDeclarationFacade;
import de.symeda.sormas.api.selfdeclaration.SelfDeclarationIndexDto;
import de.symeda.sormas.api.selfdeclaration.SelfDeclarationReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SelfDeclarationFacade")
@RightsAllowed(UserRight._SELF_DECLARATION_VIEW)
public class SelfDeclarationFacadeEjb
	extends
	AbstractCoreFacadeEjb<SelfDeclaration, SelfDeclarationDto, SelfDeclarationIndexDto, SelfDeclarationReferenceDto, SelfDeclarationService, SelfDeclarationCriteria>
	implements SelfDeclarationFacade {

	public SelfDeclarationFacadeEjb() {
	}

	@Inject
	public SelfDeclarationFacadeEjb(SelfDeclarationService service) {
		super(SelfDeclaration.class, SelfDeclarationDto.class, service);
	}

	@Override
	@RightsAllowed({
		UserRight._SELF_DECLARATION_CREATE,
		UserRight._SELF_DECLARATION_EDIT })
	public SelfDeclarationDto save(@Valid SelfDeclarationDto dto) {
		SelfDeclaration existingSelfDeclaration = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

		FacadeHelper
			.checkCreateAndEditRights(existingSelfDeclaration, userService, UserRight.SELF_DECLARATION_CREATE, UserRight.SELF_DECLARATION_EDIT);

		SelfDeclarationDto existingDto = toDto(existingSelfDeclaration);
		Pseudonymizer<SelfDeclarationDto> pseudonymizer = createPseudonymizer(existingSelfDeclaration);
		restorePseudonymizedDto(dto, existingDto, existingSelfDeclaration, pseudonymizer);

		validate(dto);

		SelfDeclaration selfDeclaration = fillOrBuildEntity(dto, existingSelfDeclaration, true);
		service.ensurePersisted(selfDeclaration);

		return toPseudonymizedDto(selfDeclaration, pseudonymizer);
	}

	@Override
	public long count(SelfDeclarationCriteria criteria) {
		return 0;
	}

	@Override
	public List<SelfDeclarationIndexDto> getIndexList(
		SelfDeclarationCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		return null;
	}

	@Override
	public void validate(SelfDeclarationDto dto) throws ValidationRuntimeException {

	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._SELF_DECLARATION_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		return null;
	}

	@Override
	@RightsAllowed(UserRight._SELF_DECLARATION_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		return null;
	}

	@Override
	protected SelfDeclaration fillOrBuildEntity(SelfDeclarationDto source, SelfDeclaration target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, SelfDeclaration::new, checkChangeDate);

		target.setReportDate(source.getReportDate());
		target.setResponsibleUser(userService.getByReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		return target;
	}

	@Override
	protected SelfDeclarationDto toDto(SelfDeclaration source) {
		if (source == null) {
			return null;
		}
		SelfDeclarationDto target = new SelfDeclarationDto();

		DtoHelper.fillDto(target, source);

		target.setReportDate(source.getReportDate());
		target.setResponsibleUser(UserFacadeEjb.toReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		return target;
	}

	@Override
	protected SelfDeclarationReferenceDto toRefDto(SelfDeclaration selfDeclaration) {
		return new SelfDeclarationReferenceDto(selfDeclaration.getUuid());
	}

	@Override
	protected void pseudonymizeDto(
		SelfDeclaration source,
		SelfDeclarationDto dto,
		Pseudonymizer<SelfDeclarationDto> pseudonymizer,
		boolean inJurisdiction) {

	}

	@Override
	protected void restorePseudonymizedDto(
		SelfDeclarationDto dto,
		SelfDeclarationDto existingDto,
		SelfDeclaration entity,
		Pseudonymizer<SelfDeclarationDto> pseudonymizer) {

	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.SELF_DECLARATION;
	}

	@LocalBean
	@Stateless
	public static class SelfDeclarationFacadeEjbLocal extends SelfDeclarationFacadeEjb {

		@Inject
		public SelfDeclarationFacadeEjbLocal(SelfDeclarationService service) {
			super(service);
		}
	}
}
