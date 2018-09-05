package de.symeda.sormas.backend.hospitalization;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PreviousHospitalizationService extends AbstractAdoService<PreviousHospitalization> {
	
	public PreviousHospitalizationService() {
		super(PreviousHospitalization.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, 
			From<PreviousHospitalization, PreviousHospitalization> from, User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
	
	public PreviousHospitalization buildPreviousHospitalizationFromHospitalization(Case caze) {
		PreviousHospitalization previousHospitalization = new PreviousHospitalization();
		previousHospitalization.setUuid(DataHelper.createUuid());
		
		Hospitalization hospitalization = caze.getHospitalization();
		
		if (hospitalization.getAdmissionDate() != null) {
			previousHospitalization.setAdmissionDate(hospitalization.getAdmissionDate());
		} else {
			previousHospitalization.setAdmissionDate(caze.getReportDate());
		}
		
		if (hospitalization.getDischargeDate() != null) {
			previousHospitalization.setDischargeDate(hospitalization.getDischargeDate());
		} else {
			previousHospitalization.setDischargeDate(new Date());
		}
		
		previousHospitalization.setRegion(caze.getRegion());
		previousHospitalization.setDistrict(caze.getDistrict());
		previousHospitalization.setCommunity(caze.getCommunity());
		previousHospitalization.setHealthFacility(caze.getHealthFacility());
		previousHospitalization.setHospitalization(caze.getHospitalization());
		previousHospitalization.setIsolated(hospitalization.getIsolated());
		
		return previousHospitalization;
	}
}
