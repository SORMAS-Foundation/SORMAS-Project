package de.symeda.sormas.backend.therapy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class Therapy extends AbstractDomainObject {

	private static final long serialVersionUID = -1467303502817738376L;
	
	public static final String TABLE_NAME = "therapy";
	
	private List<Prescription> prescriptions = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = Prescription.THERAPY)
	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}
	
	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

}
