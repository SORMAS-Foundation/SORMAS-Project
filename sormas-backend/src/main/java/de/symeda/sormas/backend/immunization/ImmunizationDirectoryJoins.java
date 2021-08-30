package de.symeda.sormas.backend.immunization;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.immunization.entity.ImmunizationDirectory;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;
import de.symeda.sormas.backend.vaccination.FirstVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccineType;

public class ImmunizationDirectoryJoins<T> extends AbstractDomainObjectJoins<T, ImmunizationDirectory> {

	private Join<ImmunizationDirectory, Person> person;
	private Join<ImmunizationDirectory, Region> responsibleRegion;
	private Join<ImmunizationDirectory, District> responsibleDistrict;
	private Join<ImmunizationDirectory, Community> responsibleCommunity;
	private Join<ImmunizationDirectory, Facility> healthFacility;
	private Join<ImmunizationDirectory, User> reportingUser;
	private Join<ImmunizationDirectory, LastVaccineType> lastVaccineType;
	private Join<ImmunizationDirectory, LastVaccinationDate> lastVaccinationDate;
	private Join<ImmunizationDirectory, FirstVaccinationDate> firstVaccinationDate;

	public ImmunizationDirectoryJoins(From<T, ImmunizationDirectory> root) {
		super(root);
	}

	public Join<ImmunizationDirectory, Person> getPerson() {
		return getOrCreate(person, Immunization.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<ImmunizationDirectory, Person> person) {
		this.person = person;
	}

	public Join<ImmunizationDirectory, Region> getResponsibleRegion() {
		return getOrCreate(responsibleRegion, Immunization.RESPONSIBLE_REGION, JoinType.LEFT, this::setResponsibleRegion);
	}

	private void setResponsibleRegion(Join<ImmunizationDirectory, Region> responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public Join<ImmunizationDirectory, District> getResponsibleDistrict() {
		return getOrCreate(responsibleDistrict, Immunization.RESPONSIBLE_DISTRICT, JoinType.LEFT, this::setResponsibleDistrict);
	}

	private void setResponsibleDistrict(Join<ImmunizationDirectory, District> responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Join<ImmunizationDirectory, Community> getResponsibleCommunity() {
		return getOrCreate(responsibleCommunity, Immunization.RESPONSIBLE_COMMUNITY, JoinType.LEFT, this::setResponsibleCommunity);
	}

	private void setResponsibleCommunity(Join<ImmunizationDirectory, Community> responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public Join<ImmunizationDirectory, Facility> getHealthFacility() {
		return getOrCreate(healthFacility, Immunization.HEALTH_FACILITY, JoinType.LEFT, this::setHealthFacility);
	}

	public void setHealthFacility(Join<ImmunizationDirectory, Facility> healthFacility) {
		this.healthFacility = healthFacility;
	}

	public Join<ImmunizationDirectory, User> getReportingUser() {
		return getOrCreate(reportingUser, Immunization.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<ImmunizationDirectory, User> reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Join<ImmunizationDirectory, LastVaccineType> getLastVaccineType() {
		return getOrCreate(lastVaccineType, Immunization.LAST_VACCINE_TYPE, JoinType.LEFT, this::setLastVaccineType);
	}

	public void setLastVaccineType(Join<ImmunizationDirectory, LastVaccineType> lastVaccineType) {
		this.lastVaccineType = lastVaccineType;
	}

	public Join<ImmunizationDirectory, LastVaccinationDate> getLastVaccinationDate() {
		return getOrCreate(lastVaccinationDate, Immunization.LAST_VACCINATION_DATE, JoinType.LEFT, this::setLastVaccinationDate);
	}

	public void setLastVaccinationDate(Join<ImmunizationDirectory, LastVaccinationDate> lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public Join<ImmunizationDirectory, FirstVaccinationDate> getFirstVaccinationDate() {
		return getOrCreate(firstVaccinationDate, Immunization.FIRST_VACCINATION_DATE, JoinType.LEFT, this::setFirstVaccinationDate);
	}

	public void setFirstVaccinationDate(Join<ImmunizationDirectory, FirstVaccinationDate> firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}
}
