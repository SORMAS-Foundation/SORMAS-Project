/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.infrastructure.formfield;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.infrastructure.fields.FormFieldFacade;
import de.symeda.sormas.api.infrastructure.fields.FormFieldIndexDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsCriteria;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "FormFieldFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class FormFieldFacadeEjb
	extends AbstractInfrastructureFacadeEjb<FormField, FormFieldsDto, FormFieldIndexDto, FormFieldReferenceDto, FormFieldService, FormFieldsCriteria>
	implements FormFieldFacade {

	public FormFieldFacadeEjb() {
	}

	@Inject
	protected FormFieldFacadeEjb(
		FormFieldService service,
		FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			FormField.class,
			FormFieldsDto.class,
			service,
			featureConfiguration,
			null,
			null,
			null);
	}

	@Override
	protected FormFieldsDto toDto(FormField source) {
		if (source == null) {
			return null;
		}

		FormFieldsDto dto = new FormFieldsDto();
		DtoHelper.fillDto(dto, source);

		dto.setFormType(source.getFormType());
		dto.setFieldName(source.getFieldName());
		dto.setDescription(source.getDescription());
		dto.setActive(source.getActive());

		return dto;
	}

	protected FormFieldIndexDto toIndexDto(FormField source) {
		if (source == null) {
			return null;
		}

		return new FormFieldIndexDto(
			source.getUuid(),
			source.getFormType(),
			source.getFieldName(),
			source.getDescription(),
			source.getActive());
	}

	@Override
	protected FormFieldReferenceDto toRefDto(FormField source) {
		if (source == null) {
			return null;
		}
		return new FormFieldReferenceDto(source.getUuid(), source.getDescription(), null);
	}

	@Override
	protected FormField fillOrBuildEntity(FormFieldsDto source, FormField target, boolean checkChangeDate, boolean allowUuidOverwrite) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, FormField::new, checkChangeDate, allowUuidOverwrite);

		target.setFormType(source.getFormType());
		target.setFieldName(source.getFieldName());
		target.setDescription(source.getDescription());
		target.setActive(source.getActive());

		return target;
	}

	@Override
	public List<FormFieldIndexDto> getIndexList(FormFieldsCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FormField> cq = cb.createQuery(FormField.class);
		Root<FormField> formFieldRoot = cq.from(FormField.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, formFieldRoot);
		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Order orderBy;
				switch (sortProperty.propertyName) {
				case FormField.FORM_TYPE:
					orderBy = sortProperty.ascending
						? cb.asc(formFieldRoot.get(sortProperty.propertyName))
						: cb.desc(formFieldRoot.get(sortProperty.propertyName));
					break;
				case FormField.FIELD_NAME:
					orderBy = sortProperty.ascending
						? cb.asc(cb.lower(formFieldRoot.get(sortProperty.propertyName)))
						: cb.desc(cb.lower(formFieldRoot.get(sortProperty.propertyName)));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(orderBy);
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(
				cb.asc(formFieldRoot.get(FormField.FORM_TYPE)),
				cb.asc(cb.lower(formFieldRoot.get(FormField.FIELD_NAME))));
		}

		cq.select(formFieldRoot);

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	@Override
	@PermitAll
	public List<FormFieldReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {
		// FormField doesn't have externalId, so return empty list
		return new ArrayList<>();
	}

	@Override
	protected List<FormField> findDuplicates(FormFieldsDto dto, boolean includeArchived) {
		// Find duplicates based on formType and fieldName combination
		// Query all fields with same formType, then filter by fieldName in memory
		FormFieldsCriteria criteria = new FormFieldsCriteria().formType(dto.getFormType());
		if (!includeArchived) {
			criteria.relevanceStatus(de.symeda.sormas.api.EntityRelevanceStatus.ACTIVE);
		}
		
		// Use CriteriaBuilder to query directly
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FormField> cq = cb.createQuery(FormField.class);
		Root<FormField> root = cq.from(FormField.class);
		
		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		if (dto.getFieldName() != null) {
			Predicate fieldNameFilter = cb.equal(cb.lower(root.get(FormField.FIELD_NAME)), dto.getFieldName().toLowerCase());
			filter = CriteriaBuilderHelper.and(cb, filter, fieldNameFilter);
		}
		
		// Exclude current entity if updating
		if (dto.getUuid() != null) {
			Predicate uuidFilter = cb.notEqual(root.get(FormField.UUID), dto.getUuid());
			filter = CriteriaBuilderHelper.and(cb, filter, uuidFilter);
		}
		
		if (filter != null) {
			cq.where(filter);
		}
		
		return em.createQuery(cq).getResultList();
	}

	@LocalBean
	@Stateless
	public static class FormFieldFacadeEjbLocal extends FormFieldFacadeEjb {

	}
}

