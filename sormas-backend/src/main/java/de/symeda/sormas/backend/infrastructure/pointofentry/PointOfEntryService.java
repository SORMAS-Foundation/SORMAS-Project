package de.symeda.sormas.backend.infrastructure.pointofentry;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class PointOfEntryService extends AbstractInfrastructureAdoService<PointOfEntry, PointOfEntryCriteria> {

	@EJB
	private CountryFacadeEjbLocal countryFacade;

	public PointOfEntryService() {
		super(PointOfEntry.class);
	}

	public List<PointOfEntry> getAllByDistrict(District district, boolean includeOthers) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(getElementClass());
		Root<PointOfEntry> pointOfEntry = cq.from(getElementClass());

		Predicate filter = cb.and(cb.equal(pointOfEntry.get(PointOfEntry.DISTRICT), district), cb.equal(pointOfEntry.get(PointOfEntry.ACTIVE), true));

		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(pointOfEntry.get(PointOfEntry.NAME)));

		List<PointOfEntry> pointsOfEntry = em.createQuery(cq).getResultList();

		if (includeOthers) {
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_AIRPORT_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_SEAPORT_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_POE_UUID));
		}

		return pointsOfEntry;
	}

	public List<PointOfEntry> getByName(String name, District district, boolean includeArchivedEntities) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(getElementClass());
		Root<PointOfEntry> from = cq.from(getElementClass());

		Predicate filter = cb.or(
			cb.equal(cb.trim(from.get(PointOfEntry.NAME)), name.trim()),
			cb.equal(cb.lower(cb.trim(from.get(PointOfEntry.NAME))), name.trim().toLowerCase()));
		if (district != null && !PointOfEntryDto.isNameOtherPointOfEntry(name.trim())) {
			filter = cb.and(filter, cb.equal(from.get(PointOfEntry.DISTRICT), district));
		}
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);
		return em.createQuery(cq).getResultList();
	}

	public List<PointOfEntry> getByExternalId(String externalId, boolean includeArchivedEntities) {
		return getByExternalId(externalId, PointOfEntry.EXTERNAL_ID, includeArchivedEntities);
	}

	public PointOfEntry getByCaseUuid(String caseUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(PointOfEntry.class);
		Root<Case> root = cq.from(Case.class);
		CaseJoins caseCaseJoins = new CaseJoins(root);
		Join<Case, PointOfEntry> pointOfEntryJoin = caseCaseJoins.getPointOfEntry();

		cq.select(pointOfEntryJoin);
		cq.where(cb.equal(root.get(Case.UUID), caseUuid));
		return QueryHelper.getSingleResult(em, cq);
	}

	public boolean existsForCase(String caseUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<Case> root = cq.from(Case.class);
		CaseJoins caseCaseJoins = new CaseJoins(root);
		Join<Case, PointOfEntry> pointOfEntryJoin = caseCaseJoins.getPointOfEntry();

		cq.select(cb.literal(true));
		cq.where(cb.and(cb.equal(root.get(Case.UUID), caseUuid), cb.isNotNull(pointOfEntryJoin.get(PointOfEntry.ID))));

		return Boolean.TRUE.equals(QueryHelper.getSingleResult(em, cq));
	}

	@Override
	public Predicate buildCriteriaFilter(PointOfEntryCriteria criteria, CriteriaBuilder cb, Root<PointOfEntry> root) {

		// ignore Poe created at startup through createConstantPointsOfEntry
		Predicate excludeConstantPoe = cb.and(
			cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_AIRPORT_UUID),
			cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_SEAPORT_UUID),
			cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_GROUND_CROSSING_UUID),
			cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_POE_UUID));

		if (criteria == null) {
			return excludeConstantPoe;
		}

		Predicate filter = null;

		CountryReferenceDto country = criteria.getCountry();
		if (country != null) {
			CountryReferenceDto serverCountry = countryFacade.getServerCountry();

			Path<Object> countryUuid = root.join(PointOfEntry.REGION, JoinType.LEFT).join(Region.COUNTRY, JoinType.LEFT).get(Country.UUID);
			Predicate countryFilter = cb.equal(countryUuid, country.getUuid());

			if (country.equals(serverCountry)) {
				filter = CriteriaBuilderHelper.and(cb, filter, CriteriaBuilderHelper.or(cb, countryFilter, countryUuid.isNull()));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, countryFilter);
			}
		}

		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(root.join(PointOfEntry.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(root.join(PointOfEntry.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(PointOfEntry.POINT_OF_ENTRY_TYPE), criteria.getType()));
		}
		if (criteria.getActive() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(PointOfEntry.ACTIVE), criteria.getActive()));
		}
		if (criteria.getNameLike() != null) {
			String[] textFilters = criteria.getNameLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = CriteriaBuilderHelper.unaccentedIlike(cb, root.get(PointOfEntry.NAME), textFilter);
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(root.get(PointOfEntry.ARCHIVED), false), cb.isNull(root.get(PointOfEntry.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(PointOfEntry.ARCHIVED), true));
			}
		}

		return CriteriaBuilderHelper.and(cb, filter, excludeConstantPoe);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, PointOfEntry> from) {
		return null;
	}

	public void createConstantPointsOfEntry() {
		if (getByUuid(PointOfEntryDto.OTHER_AIRPORT_UUID) == null) {
			PointOfEntry otherAirport = new PointOfEntry();
			otherAirport.setName("OTHER_AIRPORT");
			otherAirport.setUuid(PointOfEntryDto.OTHER_AIRPORT_UUID);
			otherAirport.setActive(true);
			otherAirport.setPointOfEntryType(PointOfEntryType.AIRPORT);
			persist(otherAirport);
		}
		if (getByUuid(PointOfEntryDto.OTHER_SEAPORT_UUID) == null) {
			PointOfEntry otherSeaport = new PointOfEntry();
			otherSeaport.setName("OTHER_SEAPORT");
			otherSeaport.setUuid(PointOfEntryDto.OTHER_SEAPORT_UUID);
			otherSeaport.setActive(true);
			otherSeaport.setPointOfEntryType(PointOfEntryType.SEAPORT);
			persist(otherSeaport);
		}
		if (getByUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID) == null) {
			PointOfEntry otherGC = new PointOfEntry();
			otherGC.setName("OTHER_GROUND_CROSSING");
			otherGC.setUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID);
			otherGC.setActive(true);
			otherGC.setPointOfEntryType(PointOfEntryType.GROUND_CROSSING);
			persist(otherGC);
		}
		if (getByUuid(PointOfEntryDto.OTHER_POE_UUID) == null) {
			PointOfEntry otherPoe = new PointOfEntry();
			otherPoe.setName("OTHER_POE");
			otherPoe.setUuid(PointOfEntryDto.OTHER_POE_UUID);
			otherPoe.setActive(true);
			otherPoe.setPointOfEntryType(PointOfEntryType.OTHER);
			persist(otherPoe);
		}
	}
}
