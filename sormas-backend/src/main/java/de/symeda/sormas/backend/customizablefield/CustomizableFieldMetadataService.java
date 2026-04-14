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

package de.symeda.sormas.backend.customizablefield;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataCriteria;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;

/**
 * Service class for customizable field metadata.
 */
@Stateless
@LocalBean
public class CustomizableFieldMetadataService extends AdoServiceWithUserFilterAndJurisdiction<CustomizableFieldMetadata> {

	public CustomizableFieldMetadataService() {
		super(CustomizableFieldMetadata.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CustomizableFieldMetadata> from) {
		// No jurisdiction filtering for customizable fields
		return null;
	}

	public List<CustomizableFieldMetadata> getActiveFieldsForContext(CustomizableFieldContext contextClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableFieldMetadata> cq = cb.createQuery(CustomizableFieldMetadata.class);
		Root<CustomizableFieldMetadata> root = cq.from(CustomizableFieldMetadata.class);

		Predicate predicate = cb.and(
			cb.equal(root.get(CustomizableFieldMetadata.CONTEXT_CLASS), contextClass),
			cb.equal(root.get(CustomizableFieldMetadata.ACTIVE), true));

		cq.where(predicate);
		cq.orderBy(cb.asc(root.get(CustomizableFieldMetadata.UI_LINE_POSITION)));

		return em.createQuery(cq).getResultList();
	}

	public List<CustomizableFieldMetadata> getFieldsForUIGroup(CustomizableFieldGroup uiGroup) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableFieldMetadata> cq = cb.createQuery(CustomizableFieldMetadata.class);
		Root<CustomizableFieldMetadata> root = cq.from(CustomizableFieldMetadata.class);

		Predicate predicate = cb.equal(root.get(CustomizableFieldMetadata.UI_GROUP), uiGroup);

		cq.where(predicate);
		cq.orderBy(cb.asc(root.get(CustomizableFieldMetadata.UI_LINE_POSITION)));

		return em.createQuery(cq).getResultList();
	}

	public CustomizableFieldMetadata getByNameAndContext(String name, CustomizableFieldContext contextClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableFieldMetadata> cq = cb.createQuery(CustomizableFieldMetadata.class);
		Root<CustomizableFieldMetadata> root = cq.from(CustomizableFieldMetadata.class);

		Predicate predicate = cb
			.and(cb.equal(root.get(CustomizableFieldMetadata.NAME), name), cb.equal(root.get(CustomizableFieldMetadata.CONTEXT_CLASS), contextClass));

		cq.where(predicate);

		return em.createQuery(cq).getResultStream().findFirst().orElse(null);
	}

	public CustomizableFieldMetadata cloneField(String sourceUuid, String newName) {
		CustomizableFieldMetadata source = getByUuid(sourceUuid);
		if (source == null) {
			throw new IllegalArgumentException("Source field not found: " + sourceUuid);
		}

		// Validate new name is unique within context
		if (getByNameAndContext(newName, source.getContextClass()) != null) {
			throw new IllegalArgumentException("Field name already exists in this context: " + newName);
		}

		CustomizableFieldMetadata clone = new CustomizableFieldMetadata();
		clone.setName(newName);
		clone.setDescription(source.getDescription());
		clone.setFieldType(source.getFieldType());
		clone.setContextClass(source.getContextClass());
		clone.setUiGroup(source.getUiGroup());
		clone.setUiLinePosition(source.getUiLinePosition());
		clone.setUiLineWeight(source.getUiLineWeight());
		clone.setActive(source.isActive());
		clone.setMandatory(source.isMandatory());
		clone.setReadOnly(source.isReadOnly());
		clone.setDefaultValue(source.getDefaultValue());
		clone.setVisibilityRestrictions(source.getVisibilityRestrictions());
		clone.setCustomProperties(source.getCustomProperties());
		clone.setTranslations(source.getTranslations());

		ensurePersisted(clone);
		return clone;
	}

	public List<CustomizableFieldMetadata> getIndexList(
		CustomizableFieldMetadataCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableFieldMetadata> cq = cb.createQuery(CustomizableFieldMetadata.class);
		Root<CustomizableFieldMetadata> root = cq.from(CustomizableFieldMetadata.class);

		Predicate filter = buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> orders = new ArrayList<>();
			for (SortProperty sortProperty : sortProperties) {
				orders.add(sortProperty.ascending ? cb.asc(root.get(sortProperty.propertyName)) : cb.desc(root.get(sortProperty.propertyName)));
			}
			cq.orderBy(orders);
		} else {
			cq.orderBy(cb.asc(root.get(CustomizableFieldMetadata.NAME)));
		}

		TypedQuery<CustomizableFieldMetadata> query = em.createQuery(cq);
		if (first != null) {
			query.setFirstResult(first);
		}
		if (max != null) {
			query.setMaxResults(max);
		}
		return query.getResultList();
	}

	public long count(CustomizableFieldMetadataCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<CustomizableFieldMetadata> root = cq.from(CustomizableFieldMetadata.class);
		cq.select(cb.count(root));

		Predicate filter = buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getSingleResult();
	}

	private Predicate buildCriteriaFilter(CustomizableFieldMetadataCriteria criteria, CriteriaBuilder cb, Root<CustomizableFieldMetadata> root) {

		if (criteria == null) {
			return null;
		}

		List<Predicate> predicates = new ArrayList<>();

		if (criteria.getContextClass() != null) {
			predicates.add(cb.equal(root.get(CustomizableFieldMetadata.CONTEXT_CLASS), criteria.getContextClass()));
		}

		if (criteria.getFieldType() != null) {
			predicates.add(cb.equal(root.get(CustomizableFieldMetadata.FIELD_TYPE), criteria.getFieldType()));
		}

		if (criteria.getActive() != null) {
			predicates.add(cb.equal(root.get(CustomizableFieldMetadata.ACTIVE), criteria.getActive()));
		}

		if (!StringUtils.isBlank(criteria.getFreeTextFilter())) {
			String likePattern = "%" + criteria.getFreeTextFilter().toLowerCase() + "%";
			predicates.add(
				cb.or(
					cb.like(cb.lower(root.get(CustomizableFieldMetadata.NAME)), likePattern),
					cb.like(cb.lower(root.get(CustomizableFieldMetadata.DESCRIPTION)), likePattern),
					cb.like(cb.lower(root.get(CustomizableFieldMetadata.UI_GROUP)), likePattern)));
		}

		return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
	}
}
