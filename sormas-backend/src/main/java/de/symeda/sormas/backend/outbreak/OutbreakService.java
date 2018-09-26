package de.symeda.sormas.backend.outbreak;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class OutbreakService extends AbstractAdoService<Outbreak> {
	
	public OutbreakService() {
		super(Outbreak.class);
	}
	
	public List<Outbreak> queryByCriteria(OutbreakCriteria criteria, User user, String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Outbreak> cq = cb.createQuery(getElementClass());
		Root<Outbreak> from = cq.from(getElementClass());
		
		if (orderProperty != null) {
			cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));
		}

		Predicate filter = createUserFilter(cb, cq, from, user);
		filter = and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}
		
		return em.createQuery(cq).getResultList();
	}
	
	public Long countByCriteria(OutbreakCriteria criteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Outbreak> from = cq.from(getElementClass());
		
		Predicate filter = createUserFilter(cb, cq, from, user);
		filter = and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult();
	}
	
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Outbreak, Outbreak> from, User user) {
		// no filter by user needed
		return null;
	}
	
	public Predicate buildCriteriaFilter(OutbreakCriteria criteria, CriteriaBuilder cb, Root<Outbreak> from) {
		Predicate filter = null;
		if (criteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(from.get(Outbreak.DISEASE), criteria.getDisease()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(Outbreak.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(Outbreak.DISTRICT, JoinType.LEFT).join(District.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		return filter;
	}
}
