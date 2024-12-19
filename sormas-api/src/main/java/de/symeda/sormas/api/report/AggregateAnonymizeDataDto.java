package de.symeda.sormas.api.report;

import java.util.Objects;

import de.symeda.sormas.api.EntityDto;

@SuppressWarnings("serial")
public class AggregateAnonymizeDataDto extends EntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2839807582334824493L;
	
	public Long valueSum;
	public String disease;
	public String sex;
	public String districtuuid;
	public String agegroup;
	public Long deaths;
	public Long cases;
	public AggregateAnonymizeDataDto(Long valueSum, String disease, String sex, String districtuuid, String agegroup,
			Long deaths, Long cases) {
		super();
		this.valueSum = valueSum;
		this.disease = disease;
		this.sex = sex;
		this.districtuuid = districtuuid;
		this.agegroup = agegroup;
		this.deaths = deaths;
		this.cases = cases;
	}
	public Long getValueSum() {
		return valueSum;
	}
	public void setValueSum(Long valueSum) {
		this.valueSum = valueSum;
	}
	public String getDisease() {
		return disease;
	}
	public void setDisease(String disease) {
		this.disease = disease;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getDistrictuuid() {
		return districtuuid;
	}
	public void setDistrictuuid(String districtuuid) {
		this.districtuuid = districtuuid;
	}
	public String getAgegroup() {
		return agegroup;
	}
	public void setAgegroup(String agegroup) {
		this.agegroup = agegroup;
	}
	public Long getDeaths() {
		return deaths;
	}
	public void setDeaths(Long deaths) {
		this.deaths = deaths;
	}
	public Long getCases() {
		return cases;
	}
	public void setCases(Long cases) {
		this.cases = cases;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(agegroup, cases, deaths, disease, districtuuid, sex, valueSum);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregateAnonymizeDataDto other = (AggregateAnonymizeDataDto) obj;
		return Objects.equals(agegroup, other.agegroup) && Objects.equals(cases, other.cases)
				&& Objects.equals(deaths, other.deaths) && Objects.equals(disease, other.disease)
				&& Objects.equals(districtuuid, other.districtuuid) && Objects.equals(sex, other.sex)
				&& Objects.equals(valueSum, other.valueSum);
	}
	
	
	
}