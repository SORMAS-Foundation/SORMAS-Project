package de.symeda.sormas.app.backend.facility;

import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.location.Location;

@Entity(name=Facility.TABLE_NAME)
@DatabaseTable(tableName = Facility.TABLE_NAME)
public class Facility extends AbstractDomainObject {
	
	private static final long serialVersionUID = 8572137127616417072L;

	public static final String TABLE_NAME = "facility";


	public static final String NAME = "name";
	public static final String LOCATION = "location";
	
	private String name;
	private Location location;
	private FacilityType type;
	private boolean publicOwnership;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Enumerated(EnumType.STRING)
	public FacilityType getType() {
		return type;
	}
	public void setType(FacilityType type) {
		this.type = type;
	}
	
	public boolean isPublicOwnership() {
		return publicOwnership;
	}
	public void setPublicOwnership(boolean publicOwnership) {
		this.publicOwnership = publicOwnership;
	}

	@Override
	public String toString() {
		StringBuilder caption = new StringBuilder();
		caption.append(name);
		if (location != null) {
			if (location.getCommunity() != null) {
				caption.append(" (").append(location.getCommunity().getName()).append(")");
			}
		}
		return caption.toString();
	}
}
