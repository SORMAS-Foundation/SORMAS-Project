package de.symeda.sormas.backend.infrastructure.subcontinent;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Subcontinent extends InfrastructureAdo {

	public static final String TABLE_NAME = "subcontinent";

	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String CONTINENT = "continent";

	private String defaultName;
	private String externalId;
	private Continent continent;
	private List<Country> countries;

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@ManyToOne(cascade = CascadeType.REFRESH, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OneToMany(mappedBy = Country.SUBCONTINENT, cascade = {}, fetch = FetchType.LAZY)
	@OrderBy(Country.SUBCONTINENT)
	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
}
