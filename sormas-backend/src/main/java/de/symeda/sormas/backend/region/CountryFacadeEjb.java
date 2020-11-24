package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.CountryCriteria;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.region.CountryFacade;
import de.symeda.sormas.api.region.CountryIndexDto;
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.utils.EmptyValueException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Stateless(name = "CountryFacade")
public class CountryFacadeEjb implements CountryFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CountryService countryService;

	@Override
	public CountryDto getCountryByUuid(String uuid) {
		return toDto(countryService.getByUuid(uuid));
	}

	@Override
	public List<CountryReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities) {
		return countryService.getByDefaultName(name, includeArchivedEntities).stream().map(r -> toReferenceDto(r)).collect(Collectors.toList());
	}

	@Override
	public List<CountryIndexDto> getIndexList(CountryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> cq = cb.createQuery(Country.class);
		Root<Country> country = cq.from(Country.class);

		Predicate filter = countryService.buildCriteriaFilter(criteria, cb, country);

		if (filter != null) {
			cq.where(filter).distinct(true);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Country.DEFAULT_NAME:
				case Country.EXTERNAL_ID:
				case Country.ISO_CODE:
				case Country.UNO_CODE:
					expression = country.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(country.get(Country.ISO_CODE)));
		}

		cq.select(country);

		if (first != null && max != null) {
			return em.createQuery(cq)
				.setFirstResult(first)
				.setMaxResults(max)
				.getResultList()
				.stream()
				.map(f -> toIndexDto(f))
				.collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(f -> toIndexDto(f)).collect(Collectors.toList());
		}
	}

	@Override
	public long count(CountryCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Country> root = cq.from(Country.class);

		Predicate filter = countryService.buildCriteriaFilter(criteria, cb, root);

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public String saveCountry(CountryDto dto) throws ValidationRuntimeException {
		if (StringUtils.isBlank(dto.getIsoCode())) {
			throw new EmptyValueException(I18nProperties.getValidationError(Validations.importCountryEmptyIso));
		}

		Country country = countryService.getByUuid(dto.getUuid());

		if (country == null
			&& (countryService.getByIsoCode(dto.getIsoCode(), true).isPresent()
			|| countryService.getByUnoCode(dto.getUnoCode(), true).isPresent())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importCountryAlreadyExists));
		}

		country = fillOrBuildEntity(dto, country);
		countryService.ensurePersisted(country);
		return country.getUuid();
	}

	@Override
	public void archive(String countryUuid) {
		Country country = countryService.getByUuid(countryUuid);
		if (country != null) {
			country.setArchived(true);
			countryService.ensurePersisted(country);
		}
	}

	@Override
	public void dearchive(String countryUuid) {
		Country country = countryService.getByUuid(countryUuid);
		if (country != null) {
			country.setArchived(false);
			countryService.ensurePersisted(country);
		}
	}

	public static CountryReferenceDto toReferenceDto(Country entity) {
		if (entity == null) {
			return null;
		}
		CountryReferenceDto dto = new CountryReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public CountryDto toDto(Country entity) {
		if (entity == null) {
			return null;
		}
		CountryDto dto = new CountryDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setIsoCode(entity.getIsoCode());
		dto.setUnoCode(entity.getUnoCode());
		dto.setUuid(entity.getUuid());

		return dto;
	}

	public CountryIndexDto toIndexDto(Country entity) {
		if (entity == null) {
			return null;
		}
		CountryIndexDto dto = new CountryIndexDto();
		DtoHelper.fillDto(dto, entity);

		String isoCode = entity.getIsoCode();
		String displayName = I18nProperties.getCountryName(isoCode, entity.getDefaultName());
		dto.setDisplayName(displayName);
		dto.setDefaultName(entity.getDefaultName());
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setIsoCode(isoCode);
		dto.setUnoCode(entity.getUnoCode());
		dto.setUuid(entity.getUuid());

		return dto;
	}

	private Country fillOrBuildEntity(@NotNull CountryDto source, Country target) {

		if (target == null) {
			target = new Country();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setDefaultName(source.getDefaultName());
		target.setArchived(source.isArchived());
		target.setExternalId(source.getExternalId());
		target.setIsoCode(source.getIsoCode());
		target.setUnoCode(source.getUnoCode());

		return target;
	}

	@LocalBean
	@Stateless
	public static class CountryFacadeEjbLocal extends CountryFacadeEjb {

	}
}
