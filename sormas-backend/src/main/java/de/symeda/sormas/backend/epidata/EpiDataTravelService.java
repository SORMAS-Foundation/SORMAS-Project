package de.symeda.sormas.backend.epidata;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EpiDataTravelService extends AbstractAdoService<EpiDataTravel> {

	public EpiDataTravelService() {
		super(EpiDataTravel.class);
	}

	public List<EpiDataTravel> getAllByEpiDataId(long epiDataId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EpiDataTravel> cq = cb.createQuery(EpiDataTravel.class);
		Root<EpiDataTravel> root = cq.from(getElementClass());
		cq.where(cb.equal(root.get(EpiDataTravel.EPI_DATA).get(EpiData.ID), epiDataId));
		cq.select(root);
		List<EpiDataTravel> result = em.createQuery(cq).getResultList();
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<EpiDataTravel, EpiDataTravel> from,
			User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
}
