package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name=District.TABLE_NAME)
@DatabaseTable(tableName = District.TABLE_NAME)
public class District extends AbstractDomainObject {
	
	private static final long serialVersionUID = -6057113756091470463L;

	public static final String TABLE_NAME = "district";

	public static final String NAME = "name";
	public static final String REGION = "region";
	public static final String COMMUNITIES = "communities";
	
	private String name;
	private Region region;
	private List<Community> communities;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(nullable = false)
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}

	@OneToMany(mappedBy = Community.DISTRICT, cascade = {}, fetch = FetchType.LAZY)
	@OrderBy(District.NAME)
	public List<Community> getCommunities() {
		return communities;
	}
	public void setCommunities(List<Community> communities) {
		this.communities = communities;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
