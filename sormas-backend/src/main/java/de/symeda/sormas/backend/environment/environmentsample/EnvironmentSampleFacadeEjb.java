/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment.environmentsample;

import static java.util.Objects.isNull;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleFacade;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "EnvironmentSampleFacade")
@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_VIEW)
public class EnvironmentSampleFacadeEjb
	extends
	AbstractBaseEjb<EnvironmentSample, EnvironmentSampleDto, EnvironmentSampleIndexDto, EnvironmentSampleReferenceDto, EnvironmentSampleService, EnvironmentSampleCriteria>
	implements EnvironmentSampleFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private EnvironmentService environmentService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;

	public EnvironmentSampleFacadeEjb() {
	}

	@Inject
	public EnvironmentSampleFacadeEjb(EnvironmentSampleService service) {
		super(EnvironmentSample.class, EnvironmentSampleDto.class, service);
	}

	@Override
	@RightsAllowed({
		UserRight._ENVIRONMENT_SAMPLE_CREATE,
		UserRight._ENVIRONMENT_SAMPLE_EDIT })
	public EnvironmentSampleDto save(@Valid @NotNull EnvironmentSampleDto dto) {
		EnvironmentSample existingSample = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

		validateUserRights(dto, existingSample);

		EnvironmentSampleDto existingDto = toDto(existingSample);
		Pseudonymizer pseudonymizer = createPseudonymizer();
		restorePseudonymizedDto(dto, existingDto, existingSample, pseudonymizer);

		validate(dto);

		EnvironmentSample sample = fillOrBuildEntity(dto, existingSample, true);
		service.ensurePersisted(sample);

		return toPseudonymizedDto(sample, pseudonymizer);
	}

	@Override
	public long count(EnvironmentSampleCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EnvironmentSample> from = cq.from(EnvironmentSample.class);
		EnvironmentSampleQueryContext queryContext = new EnvironmentSampleQueryContext(cb, cq, from, new EnvironmentSampleJoins(from));

		Predicate filter = CriteriaBuilderHelper.and(cb, service.createUserFilter(cb, cq, from), service.createDefaultFilter(cb, from));
		if (criteria != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, service.buildCriteriaFilter(criteria, queryContext));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(from));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<EnvironmentSampleIndexDto> getIndexList(
		EnvironmentSampleCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void validate(EnvironmentSampleDto dto) throws ValidationRuntimeException {
		Facility laboratory = facilityService.getByReferenceDto(dto.getLaboratory());

		if (laboratory == null || laboratory.getType() != FacilityType.LABORATORY) {
			throw new ValidationRuntimeException(I18nProperties.getString(Validations.validLaboratory));
		}
	}

	private void validateUserRights(EnvironmentSampleDto sample, EnvironmentSample existingSample) throws ValidationRuntimeException {
		FacadeHelper.checkCreateAndEditRights(existingSample, userService, UserRight.ENVIRONMENT_SAMPLE_CREATE, UserRight.ENVIRONMENT_SAMPLE_EDIT);

		if (existingSample != null) {
			if (!isEditAllowed(existingSample)) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorEnvironmentSampleNotEditable));
			}

			if (!userService.hasRight(UserRight.ENVIRONMENT_SAMPLE_EDIT_DISPATCH)
				&& (existingSample.isDispatched() != sample.isDispatched()
					|| !DataHelper.equal(existingSample.getDispatchDate(), sample.getDispatchDate())
					|| !DataHelper.equal(existingSample.getDispatchDetails(), sample.getDispatchDetails()))) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorEnvironmentSampleNoDispatchRight));
			}

			if (!userService.hasRight(UserRight.ENVIRONMENT_SAMPLE_EDIT_RECEIVAL)
				&& (existingSample.isReceived() != sample.isReceived()
					|| !DataHelper.equal(existingSample.getReceivalDate(), sample.getReceivalDate()))) {

				throw new AccessDeniedException(I18nProperties.getString(Strings.errorEnvironmentSampleNoReceivalRight));

			}
		}
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public void delete(String uuid, DeletionDetails deletionDetails) {
		EnvironmentSample sample = service.getByUuid(uuid);
		delete(sample, deletionDetails);
	}

	private void delete(EnvironmentSample sample, DeletionDetails deletionDetails) {
		if (!service.inJurisdictionOrOwned(sample)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageEnvironmentSampleOutsideJurisdictionDeletionDenied));
		}

		service.delete(sample, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public List<String> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<String> deletedUuids = new ArrayList<>();
		List<EnvironmentSample> samplesToDelete = service.getByUuids(uuids);

		if (samplesToDelete != null) {
			samplesToDelete.stream().filter(not(EnvironmentSample::isDeleted)).forEach(sample -> {
				try {
					delete(sample.getUuid(), deletionDetails);
					deletedUuids.add(sample.getUuid());
				} catch (Exception e) {
					logger.error("The environment sample with uuid: {} could not be deleted", sample.getUuid(), e);
				}
			});
		}
		return deletedUuids;
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public void restore(String uuid) {
		EnvironmentSample sample = service.getByUuid(uuid);
		restore(sample);
	}

	private void restore(EnvironmentSample sample) {
		service.restore(sample);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public List<String> restore(List<String> uuids) {
		List<String> restoredUuids = new ArrayList<>();
		List<EnvironmentSample> samplesToRestore = service.getByUuids(uuids);

		if (samplesToRestore != null) {
			samplesToRestore.stream().filter(EnvironmentSample::isDeleted).forEach(sample -> {
				try {
					restore(sample);
					restoredUuids.add(sample.getUuid());
				} catch (Exception e) {
					logger.error("The sample with uuid: {} could not be restored", sample.getUuid(), e);
				}
			});
		}
		return restoredUuids;
	}

	@Override
	public boolean isDeleted(String uuid) {
		return service.isDeleted(uuid);
	}

	@Override
	protected EnvironmentSample fillOrBuildEntity(EnvironmentSampleDto source, EnvironmentSample target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);
		target = DtoHelper.fillOrBuildEntity(source, target, EnvironmentSample::new, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getLocation(), source.getLocation());
		}

		target.setEnvironment(environmentService.getByReferenceDto(source.getEnvironment()));
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setOtherSampleMaterial(source.getOtherSampleMaterial());
		target.setSampleVolume(source.getSampleVolume());
		target.setFieldSampleId(source.getFieldSampleId());
		target.setTurbidity(source.getTurbidity());
		target.setPhValue(source.getPhValue());
		target.setSampleTemperature(source.getSampleTemperature());
		target.setChlorineResiduals(source.getChlorineResiduals());
		target.setLaboratory(facilityService.getByReferenceDto(source.getLaboratory()));
		target.setLaboratoryDetails(source.getLaboratoryDetails());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setOtherRequestedPathogenTests(source.getOtherRequestedPathogenTests());
		target.setWeatherConditions(source.getWeatherConditions());
		target.setHeavyRain(source.getHeavyRain());
		target.setDispatched(source.isDispatched());
		target.setDispatchDate(source.getDispatchDate());
		target.setDispatchDetails(source.getDispatchDetails());
		target.setReceived(source.isReceived());
		target.setReceivalDate(source.getReceivalDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setLocation(locationFacade.fillOrBuildEntity(source.getLocation(), target.getLocation(), checkChangeDate));
		target.setGeneralComment(source.getGeneralComment());

		return target;
	}

	@Override
	protected EnvironmentSampleDto toDto(EnvironmentSample source) {
		if (source == null) {
			return null;
		}
		EnvironmentSampleDto target = new EnvironmentSampleDto();
		DtoHelper.fillDto(target, source);

		target.setEnvironment(EnvironmentFacadeEjb.toReferenceDto(source.getEnvironment()));
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setOtherSampleMaterial(source.getOtherSampleMaterial());
		target.setSampleVolume(source.getSampleVolume());
		target.setFieldSampleId(source.getFieldSampleId());
		target.setTurbidity(source.getTurbidity());
		target.setPhValue(source.getPhValue());
		target.setSampleTemperature(source.getSampleTemperature());
		target.setChlorineResiduals(source.getChlorineResiduals());
		target.setLaboratory(FacilityFacadeEjb.toReferenceDto(source.getLaboratory()));
		target.setLaboratoryDetails(source.getLaboratoryDetails());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setOtherRequestedPathogenTests(source.getOtherRequestedPathogenTests());
		target.setWeatherConditions(source.getWeatherConditions());
		target.setHeavyRain(source.getHeavyRain());
		target.setDispatched(source.isDispatched());
		target.setDispatchDate(source.getDispatchDate());
		target.setDispatchDetails(source.getDispatchDetails());
		target.setReceived(source.isReceived());
		target.setReceivalDate(source.getReceivalDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setLocation(LocationFacadeEjb.toDto(source.getLocation()));
		target.setGeneralComment(source.getGeneralComment());

		return target;
	}

	@Override
	protected EnvironmentSampleReferenceDto toRefDto(EnvironmentSample environmentSample) {
		return new EnvironmentSampleReferenceDto(
			environmentSample.getUuid(),
			environmentSample.getSampleMaterial(),
			environmentSample.getEnvironment().getUuid());
	}

	@Override
	protected void pseudonymizeDto(EnvironmentSample source, EnvironmentSampleDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {
		if (dto != null) {
			pseudonymizer.pseudonymizeDto(EnvironmentSampleDto.class, dto, inJurisdiction, e -> {
				pseudonymizer.pseudonymizeDto(EnvironmentReferenceDto.class, e.getEnvironment(), inJurisdiction, null);
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), userService.getCurrentUser(), dto::setReportingUser);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(
		EnvironmentSampleDto dto,
		EnvironmentSampleDto existingDto,
		EnvironmentSample entity,
		Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			pseudonymizer.restorePseudonymizedValues(EnvironmentSampleDto.class, dto, existingDto, inJurisdiction);
			pseudonymizer.restoreUser(entity.getReportingUser(), userService.getCurrentUser(), dto, dto::setReportingUser);
		}

	}

	private boolean isEditAllowed(EnvironmentSample sample) {
		return service.isEditAllowed(sample);
	}

	@LocalBean
	@Stateless
	public static class EnvironmentSampleFacadeEjbLocal extends EnvironmentSampleFacadeEjb {

		public EnvironmentSampleFacadeEjbLocal() {
			super();
		}

		@Inject
		public EnvironmentSampleFacadeEjbLocal(EnvironmentSampleService service) {
			super(service);
		}
	}
}
