package de.symeda.sormas.backend.region;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.region.CountryCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class CountryService extends AbstractInfrastructureAdoService<Country> {

    public CountryService() {
        super(Country.class);
    }

    public List<Country> getByDefaultName(String name, boolean includeArchivedEntities) {
        if (name == null) {
            return Collections.emptyList();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Country> cq = cb.createQuery(getElementClass());
        Root<Country> from = cq.from(getElementClass());

        Predicate filter = cb
                .or(cb.equal(cb.trim(from.get(Country.DEFAULT_NAME)), name.trim()), cb.equal(cb.lower(cb.trim(from.get(Country.DEFAULT_NAME))), name.trim().toLowerCase()));
        if (!includeArchivedEntities) {
            filter = cb.and(filter, createBasicFilter(cb, from));
        }

        cq.where(filter);

        return em.createQuery(cq).getResultList();
    }

    public Optional<Country> getByIsoCode(String isoCode, boolean includeArchivedEntities) {
        if (isoCode == null) {
            return Optional.empty();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Country> cq = cb.createQuery(getElementClass());
        Root<Country> from = cq.from(getElementClass());

        Predicate filter = cb
                .or(cb.equal(cb.trim(from.get(Country.ISO_CODE)), isoCode.trim()), cb.equal(cb.lower(cb.trim(from.get(Country.ISO_CODE))), isoCode.trim().toLowerCase()));
        if (!includeArchivedEntities) {
            filter = cb.and(filter, createBasicFilter(cb, from));
        }

        cq.where(filter);

        return em.createQuery(cq).getResultList().stream().findFirst();
    }

    public Optional<Country> getByUnoCode(String unoCode, boolean includeArchivedEntities) {
        if (unoCode == null) {
            return Optional.empty();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Country> cq = cb.createQuery(getElementClass());
        Root<Country> from = cq.from(getElementClass());

        Predicate filter = cb
                .or(cb.equal(cb.trim(from.get(Country.UNO_CODE)), unoCode.trim()), cb.equal(cb.lower(cb.trim(from.get(Country.UNO_CODE))), unoCode.trim().toLowerCase()));
        if (!includeArchivedEntities) {
            filter = cb.and(filter, createBasicFilter(cb, from));
        }

        cq.where(filter);

        return em.createQuery(cq).getResultList().stream().findFirst();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Country> from) {
        // no filter by user needed
        return null;
    }

    public Predicate buildCriteriaFilter(CountryCriteria criteria, CriteriaBuilder cb, Root<Country> from) {

        Predicate filter = null;
        if (criteria.getNameCodeLike() != null) {
            String[] textFilters = criteria.getNameCodeLike().split("\\s+");
            for (int i = 0; i < textFilters.length; i++) {
                String textFilter = "%" + textFilters[i].toLowerCase() + "%";
                if (!DataHelper.isNullOrEmpty(textFilter)) {
                    Predicate likeFilters =
                            cb.or(cb.like(cb.lower(from.get(Country.ISO_CODE)), textFilter), cb.like(cb.lower(from.get(Country.UNO_CODE)), textFilter));
                    filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
                }
            }
        }
        if (criteria.getRelevanceStatus() != null) {
            if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
                filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Country.ARCHIVED), false), cb.isNull(from.get(Country.ARCHIVED))));
            } else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
                filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Country.ARCHIVED), true));
            }
        }
        return filter;
    }
}
