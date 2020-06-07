package de.symeda.sormas.api.infrastructure;

import java.util.Date;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class PopulationDataDto extends EntityDto {

	private static final long serialVersionUID = -4254008000534611519L;

	public static final String I18N_PREFIX = "PopulationData";

	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String SEX = "sex";
	public static final String AGE_GROUP = "ageGroup";
	public static final String POPULATION = "population";
	public static final String COLLECTION_DATE = "collectionDate";

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Sex sex;
	private AgeGroup ageGroup;
	private Integer population;
	private Date collectionDate;

	public static PopulationDataDto build(Date collectionDate) {

		PopulationDataDto dto = new PopulationDataDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setCollectionDate(collectionDate);
		return dto;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public AgeGroup getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(AgeGroup ageGroup) {
		this.ageGroup = ageGroup;
	}

	public Integer getPopulation() {
		return population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}
}
