package de.symeda.sormas.backend.news;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

public class NewsJoin extends QueryJoins<News> {

	private Join<News, Region> region;
	private Join<News, District> district;
	private Join<News, Community> community;

	public NewsJoin(From<?, News> root) {
		super(root);
	}

	public Join<News, Region> getRegion() {
		return getOrCreate(region, News.REGION, JoinType.LEFT, this::setRegion);
	}

	public void setRegion(Join<News, Region> region) {
		this.region = region;
	}

	public Join<News, District> getDistrict() {
		return getOrCreate(district, News.DISTRICT, JoinType.LEFT, this::setDistrict);
	}

	public void setDistrict(Join<News, District> district) {
		this.district = district;
	}

	public Join<News, Community> getCommunity() {
		return getOrCreate(community, News.COMMUNITY, JoinType.LEFT, this::setCommunity);
	}

	public void setCommunity(Join<News, Community> community) {
		this.community = community;
	}
}
