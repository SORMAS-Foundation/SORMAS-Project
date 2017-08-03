package de.symeda.sormas.backend.region;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class Region extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2958216667876104358L;

	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String DISTRICTS = "districts";
	
	private String name;
	private String epidCode;
	private List<District> districts;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEpidCode() {
		return epidCode;
	}
	public void setEpidCode(String epidCode) {
		this.epidCode = epidCode;
	}
	
	@OneToMany(mappedBy = District.REGION, cascade = {}, fetch = FetchType.LAZY)
	@OrderBy(District.NAME)
	public List<District> getDistricts() {
		return districts;
	}
	public void setDistricts(List<District> districts) {
		this.districts = districts;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
