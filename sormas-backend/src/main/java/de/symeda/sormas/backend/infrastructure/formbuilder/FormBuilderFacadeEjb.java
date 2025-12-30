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

package de.symeda.sormas.backend.infrastructure.formbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderCriteria;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderFacade;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderReferenceDto;
import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.formfield.FormField;
import de.symeda.sormas.backend.infrastructure.formfield.FormFieldService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "FormBuilderFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class FormBuilderFacadeEjb
	extends AbstractInfrastructureFacadeEjb<FormBuilder, FormBuilderDto, FormBuilderDto, FormBuilderReferenceDto, FormBuilderService, FormBuilderCriteria>
	implements FormBuilderFacade {

	@EJB
	private FormFieldService formFieldService;

	public FormBuilderFacadeEjb() {
	}

	@Inject
	protected FormBuilderFacadeEjb(
		FormBuilderService service,
		FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			FormBuilder.class,
			FormBuilderDto.class,
			service,
			featureConfiguration,
			null,
			null,
			null);
	}

	@Override
	protected FormBuilderDto toDto(FormBuilder source) {
		if (source == null) {
			return null;
		}

		FormBuilderDto dto = new FormBuilderDto();
		DtoHelper.fillDto(dto, source);

		dto.setFormType(source.getFormType());
		dto.setDisease(source.getDisease());
		dto.setActive(source.getActive());

		// Map formFields with displayOrder
		List<FormFieldReferenceDto> formFieldRefs = new ArrayList<>();
		List<FormField> formFields = source.getFormFields();

		if (formFields != null) {
			for (int i = 0; i < formFields.size(); i++) {
				FormField field = formFields.get(i);
				FormFieldReferenceDto refDto = toFormFieldReferenceDto(field);
				// Set displayOrder from list index (JPA OrderColumn maintains order)
				refDto.setDisplayOrder(i);
				formFieldRefs.add(refDto);
			}
		}

		dto.setFormFields(formFieldRefs);
		return dto;
	}

	@Override
	protected FormBuilderReferenceDto toRefDto(FormBuilder source) {
		if (source == null) {
			return null;
		}
		return new FormBuilderReferenceDto(source.getUuid(), buildCaption(source), null);
	}

	public static FormBuilderReferenceDto toReferenceDto(FormBuilder source) {
		if (source == null) {
			return null;
		}
		return new FormBuilderReferenceDto(source.getUuid(), buildCaption(source), null);
	}

	private static String buildCaption(FormBuilder source) {
		StringBuilder caption = new StringBuilder();
		if (source.getFormType() != null) {
			caption.append(source.getFormType().toString());
		}
		if (source.getDisease() != null) {
			if (caption.length() > 0) {
				caption.append(" - ");
			}
			caption.append(source.getDisease().toString());
		}
		return caption.length() > 0 ? caption.toString() : source.getUuid();
	}

	private FormFieldReferenceDto toFormFieldReferenceDto(FormField source) {
		if (source == null) {
			return null;
		}
		FormFieldReferenceDto dto = new FormFieldReferenceDto(source.getUuid(), source.getDescription(), null);
		dto.setFieldName(source.getFieldName());
		return dto;
	}

	@Override
	protected FormBuilder fillOrBuildEntity(@NotNull FormBuilderDto source, FormBuilder target, boolean checkChangeDate, boolean allowUuidOverwrite) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, FormBuilder::new, checkChangeDate, allowUuidOverwrite);

		target.setFormType(source.getFormType());
		target.setDisease(source.getDisease());
		target.setActive(source.getActive());

		// Map formFields from DTOs
		if (source.getFormFields() != null) {
			List<FormField> formFields = new ArrayList<>();
			for (FormFieldReferenceDto fieldRef : source.getFormFields()) {
				FormField field = formFieldService.getByUuid(fieldRef.getUuid());
				if (field != null) {
					formFields.add(field);
				}
			}
			target.setFormFields(formFields);
		}

		return target;
	}

	@Override
	@PermitAll
	public FormBuilderDto getByFormTypeAndDisease(FormType formType, Disease disease, boolean active) {
		FormBuilderCriteria criteria = new FormBuilderCriteria().formType(formType).disease(disease).active(active);
		List<FormBuilder> results = service.getByFormTypeAndDisease(criteria);
		if (results.isEmpty()) {
			return null;
		}
		return toDto(results.get(0));
	}

	@Override
	@PermitAll
	public List<FormBuilderDto> getAllActive() {
		FormBuilderCriteria criteria = new FormBuilderCriteria().active(true);
		return service.getByFormTypeAndDisease(criteria).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<FormBuilderDto> getByFormType(FormType formType, boolean active) {
		FormBuilderCriteria criteria = new FormBuilderCriteria().formType(formType).active(active);
		return service.getByFormTypeAndDisease(criteria).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	protected List<FormBuilder> findDuplicates(FormBuilderDto dto, boolean includeArchived) {
		// Find duplicates based on formType and disease combination
		FormBuilderCriteria criteria = new FormBuilderCriteria()
			.formType(dto.getFormType())
			.disease(dto.getDisease());
		if (!includeArchived) {
			criteria.active(true);
		}
		return service.getByFormTypeAndDisease(criteria);
	}

	@Override
	public List<FormBuilderDto> getIndexList(FormBuilderCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FormBuilder> cq = cb.createQuery(FormBuilder.class);
		Root<FormBuilder> formBuilderRoot = cq.from(FormBuilder.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, formBuilderRoot);
		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Order orderBy;
				switch (sortProperty.propertyName) {
				case FormBuilder.FORM_TYPE:
				case FormBuilder.DISEASE:
					orderBy = sortProperty.ascending
						? cb.asc(formBuilderRoot.get(sortProperty.propertyName))
						: cb.desc(formBuilderRoot.get(sortProperty.propertyName));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(orderBy);
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(
				cb.asc(formBuilderRoot.get(FormBuilder.FORM_TYPE)),
				cb.asc(formBuilderRoot.get(FormBuilder.DISEASE)));
		}

		cq.select(formBuilderRoot);

		return QueryHelper.getResultList(em, cq, first, max, this::toDto);
	}

	@Override
	@PermitAll
	public List<FormBuilderReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {
		// FormBuilder doesn't have externalId, so return empty list
		return new ArrayList<>();
	}

	@LocalBean
	@Stateless
	public static class FormBuilderFacadeEjbLocal extends FormBuilderFacadeEjb {

	}
}

