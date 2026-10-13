package de.symeda.sormas.api.news.eios;

import java.util.List;

public class Location {

	private GeoData geoData;
	private List<String> areas;
	private List<String> areaFullName;
	private String areaId;
	private String iso2;
	private String countryName;
	private Long emmAreaId;
	private String trigger;
	private Boolean hasMaxEmmScore;
	private String whoRegionCode;
	private String continentCode;

	public GeoData getGeoData() {
		return geoData;
	}

	public void setGeoData(GeoData geoData) {
		this.geoData = geoData;
	}

	public List<String> getAreas() {
		return areas;
	}

	public void setAreas(List<String> areas) {
		this.areas = areas;
	}

	public List<String> getAreaFullName() {
		return areaFullName;
	}

	public void setAreaFullName(List<String> areaFullName) {
		this.areaFullName = areaFullName;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getIso2() {
		return iso2;
	}

	public void setIso2(String iso2) {
		this.iso2 = iso2;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Long getEmmAreaId() {
		return emmAreaId;
	}

	public void setEmmAreaId(Long emmAreaId) {
		this.emmAreaId = emmAreaId;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public Boolean getHasMaxEmmScore() {
		return hasMaxEmmScore;
	}

	public void setHasMaxEmmScore(Boolean hasMaxEmmScore) {
		this.hasMaxEmmScore = hasMaxEmmScore;
	}

	public String getWhoRegionCode() {
		return whoRegionCode;
	}

	public void setWhoRegionCode(String whoRegionCode) {
		this.whoRegionCode = whoRegionCode;
	}

	public String getContinentCode() {
		return continentCode;
	}

	public void setContinentCode(String continentCode) {
		this.continentCode = continentCode;
	}

}
