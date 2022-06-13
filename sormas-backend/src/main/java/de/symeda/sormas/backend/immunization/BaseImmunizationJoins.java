package de.symeda.sormas.backend.immunization;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.immunization.entity.BaseImmunization;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonJoins;

public class BaseImmunizationJoins<S extends BaseImmunization> extends QueryJoins<S> {

	private Join<S, Person> person;
	private Join<S, Region> responsibleRegion;
	private Join<S, District> responsibleDistrict;
	private Join<S, Community> responsibleCommunity;
	private Join<S, Facility> healthFacility;

	private PersonJoins personJoins;

	public BaseImmunizationJoins(From<?, S> root) {
		super(root);
	}

	public Join<S, Person> getPerson() {
		return getOrCreate(person, Immunization.PERSON, JoinType.LEFT, this::setPerson);
	}

	public void setPerson(Join<S, Person> person) {
		this.person = person;
	}

	public PersonJoins getPersonJoins() {
		return getOrCreate(personJoins, () -> new PersonJoins(getPerson()), this::setPersonJoins);
	}

	private void setPersonJoins(PersonJoins personJoins) {
		this.personJoins = personJoins;
	}

	public Join<S, Region> getResponsibleRegion() {
		return getOrCreate(responsibleRegion, Immunization.RESPONSIBLE_REGION, JoinType.LEFT, this::setResponsibleRegion);
	}

	public void setResponsibleRegion(Join<S, Region> responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public Join<S, District> getResponsibleDistrict() {
		return getOrCreate(responsibleDistrict, Immunization.RESPONSIBLE_DISTRICT, JoinType.LEFT, this::setResponsibleDistrict);
	}

	public void setResponsibleDistrict(Join<S, District> responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Join<S, Community> getResponsibleCommunity() {
		return getOrCreate(responsibleCommunity, Immunization.RESPONSIBLE_COMMUNITY, JoinType.LEFT, this::setResponsibleCommunity);
	}

	public void setResponsibleCommunity(Join<S, Community> responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public Join<S, Facility> getHealthFacility() {
		return getOrCreate(healthFacility, Immunization.HEALTH_FACILITY, JoinType.LEFT, this::setHealthFacility);
	}

	public void setHealthFacility(Join<S, Facility> healthFacility) {
		this.healthFacility = healthFacility;
	}
}
