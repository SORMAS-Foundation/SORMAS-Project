package de.symeda.sormas.ui.configuration.dto;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

public class BaseGenerationConfig {

	protected String entityCount;
	protected LocalDate startDate;
	protected LocalDate endDate;
	protected Disease disease;
	protected RegionReferenceDto region;
	protected DistrictReferenceDto district;

	public int getEntityCountAsNumber() {
		return StringUtils.isBlank(getEntityCount()) ? 0 : Integer.parseInt(getEntityCount());
	}

	public String getEntityCount() {
		return entityCount;
	}

	public void setEntityCount(String entityCount) {
		this.entityCount = entityCount;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
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
}
