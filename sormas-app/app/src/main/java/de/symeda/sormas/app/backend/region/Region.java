package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name=Region.TABLE_NAME)
@DatabaseTable(tableName = Region.TABLE_NAME)
public class Region extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2958216667876104358L;

	public static final String TABLE_NAME = "region";

	public static final String NAME = "name";
	public static final String DISTRICTS = "districts";

	@Column
	private String name;

	@OneToMany(mappedBy = District.REGION, cascade = {}, fetch = FetchType.LAZY)
	@OrderBy(District.NAME)
	private List<District> districts;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
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
