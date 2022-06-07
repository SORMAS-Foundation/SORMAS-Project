package de.symeda.sormas.backend.user;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;

public class UserJoins extends QueryJoins<User> {

	private Join<User, Region> region;
	private Join<User, District> district;
	private Join<User, Community> community;
	private Join<User, Facility> healthFacility;
	private Join<User, Facility> laboratory;
	private Join<User, PointOfEntry> pointOfEntry;

	public UserJoins(From<?, User> root) {
		super(root);
	}

	public Join<User, Region> getRegion() {
		return getOrCreate(region, User.REGION, JoinType.LEFT, this::setRegion);
	}

	public void setRegion(Join<User, Region> region) {
		this.region = region;
	}

	public Join<User, District> getDistrict() {
		return getOrCreate(district, User.DISTRICT, JoinType.LEFT, this::setDistrict);
	}

	public void setDistrict(Join<User, District> district) {
		this.district = district;
	}

	public Join<User, Community> getCommunity() {
		return getOrCreate(community, User.COMMUNITY, JoinType.LEFT, this::setCommunity);
	}

	public void setCommunity(Join<User, Community> community) {
		this.community = community;
	}

	public Join<User, Facility> getHealthFacility() {
		return getOrCreate(healthFacility, User.HEALTH_FACILITY, JoinType.LEFT, this::setHealthFacility);
	}

	public void setHealthFacility(Join<User, Facility> healthFacility) {
		this.healthFacility = healthFacility;
	}

	public Join<User, Facility> getLaboratory() {
		return getOrCreate(laboratory, User.LABORATORY, JoinType.LEFT, this::setLaboratory);
	}

	public void setLaboratory(Join<User, Facility> laboratory) {
		this.laboratory = laboratory;
	}

	public Join<User, PointOfEntry> getPointOfEntry() {
		return getOrCreate(pointOfEntry, User.POINT_OF_ENTRY, JoinType.LEFT, this::setPointOfEntry);
	}

	public void setPointOfEntry(Join<User, PointOfEntry> pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}
}
