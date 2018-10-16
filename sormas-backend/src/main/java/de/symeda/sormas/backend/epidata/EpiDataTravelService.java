package de.symeda.sormas.backend.epidata;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EpiDataTravelService extends AbstractAdoService<EpiDataTravel> {

	public EpiDataTravelService() {
		super(EpiDataTravel.class);
	}

	@SuppressWarnings("unchecked")
	public List<EpiDataTravel> getAllByEpiDataId(long epiDataId) {
		return (List<EpiDataTravel>) em.createNativeQuery("SELECT * FROM " + EpiDataTravel.TABLE_NAME + " WHERE " 
				+ EpiDataTravel.EPI_DATA + "_id = " + epiDataId, EpiDataTravel.class).getResultList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<EpiDataTravel, EpiDataTravel> from, User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
}
