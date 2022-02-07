package de.symeda.sormas.backend.common;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.crypto.Data;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;

@LocalBean
@Stateless
public class DefaultEntitiesCreator {

	public Continent createDefaultContinent(boolean randomUuid) {
		Continent continent = new Continent();
		continent.setUuid(createUuid(randomUuid, DefaultEntityHelper.DefaultInfrastructureUuidSeed.CONTINENT));
		continent.setDefaultName(I18nProperties.getCaption(Captions.continent, "Default Continent"));
		continent.setExternalId("CONT");
		return continent;
	}

	private String createUuid(boolean randomUuid, DefaultEntityHelper.DefaultInfrastructureUuidSeed seed) {
		if (randomUuid) {
			return DataHelper.createUuid();
		} else {
			return DefaultEntityHelper.getConstantUuidFor(seed);
		}
	}

	public Subcontinent createDefaultSubcontinent(Continent continent, boolean randomUuid) {
		Subcontinent subcontinent = new Subcontinent();
		subcontinent.setUuid(createUuid(randomUuid, DefaultEntityHelper.DefaultInfrastructureUuidSeed.SUBCONTINENT));
		subcontinent.setDefaultName(I18nProperties.getCaption(Captions.subcontinent, "Default Subcontinent"));
		subcontinent.setExternalId("SUB-CNT");
		subcontinent.setContinent(continent);
		return subcontinent;
	}

	public Country createDefaultCountry(Subcontinent subcontinent, boolean randomUuid) {
		Country country = new Country();
		country.setUuid(createUuid(randomUuid, DefaultEntityHelper.DefaultInfrastructureUuidSeed.COUNTRY));
		country.setDefaultName(I18nProperties.getCaption(Captions.country, "Default Country"));
		country.setExternalId("CNT");
		country.setSubcontinent(subcontinent);
		return country;
	}

	public Region createDefaultRegion(boolean randomUuid) {
		Region region = new Region();
		region.setUuid(createUuid(randomUuid, DefaultEntityHelper.DefaultInfrastructureUuidSeed.REGION));
		region.setName(I18nProperties.getCaption(Captions.defaultRegion, "Default Region"));
		region.setEpidCode("DEF-REG");
		region.setDistricts(new ArrayList<>());
		return region;
	}

	public District createDefaultDistrict(Region region, boolean randomUuid) {
		District district = new District();
		district.setUuid(createUuid(randomUuid, DefaultEntityHelper.DefaultInfrastructureUuidSeed.DISTRICT));
		district.setName(I18nProperties.getCaption(Captions.defaultDistrict, "Default District"));
		district.setRegion(region);
		district.setEpidCode("DIS");
		district.setCommunities(new ArrayList<>());
		return district;
	}

	public Community createDefaultCommunity(District district, boolean randomUuid) {
		Community community = new Community();
		community.setUuid(createUuid(randomUuid, DefaultEntityHelper.DefaultInfrastructureUuidSeed.COMMUNITY));
		community.setName(I18nProperties.getCaption(Captions.defaultCommunity, "Default Community"));
		community.setDistrict(district);
		return community;
	}

	public PointOfEntry createDefaultPointOfEntry(Region region, District district) {
		PointOfEntry pointOfEntry = new PointOfEntry();
		pointOfEntry.setUuid(DataHelper.createUuid());
		pointOfEntry.setName(I18nProperties.getCaption(Captions.defaultPointOfEntry, "Default Point Of Entry"));
		pointOfEntry.setDistrict(district);
		pointOfEntry.setRegion(region);
		pointOfEntry.setPointOfEntryType(PointOfEntryType.AIRPORT);
		return pointOfEntry;
	}

	public Facility createDefaultLaboratory(Region region, District district, Community community) {
		Facility laboratory = new Facility();
		laboratory.setUuid(DataHelper.createUuid());
		laboratory.setName(I18nProperties.getCaption(Captions.defaultLaboratory, "Default Laboratory"));
		laboratory.setCommunity(community);
		laboratory.setDistrict(district);
		laboratory.setRegion(region);
		laboratory.setType(FacilityType.LABORATORY);
		return laboratory;
	}

	public Facility createDefaultFacility(Region region, District district, Community community) {
		Facility facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setType(FacilityType.HOSPITAL);
		facility.setName(I18nProperties.getCaption(Captions.defaultFacility, "Default Health Facility"));
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		return facility;
	}

}
