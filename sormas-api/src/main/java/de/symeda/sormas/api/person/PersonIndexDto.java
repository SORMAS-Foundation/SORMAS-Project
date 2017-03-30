package de.symeda.sormas.api.person;

public class PersonIndexDto extends PersonReferenceDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "CasePerson";

	public static final String SEX = "sex";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String APPROXIMATE_AGE_TYPE = "approximateAgeType";
	
	
	private Sex sex;
	private PresentCondition presentCondition;
	private Integer approximateAge;
	private ApproximateAgeType approximateAgeType;

	public Integer getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(Integer approximateAge) {
		this.approximateAge = approximateAge;
	}
	
	public ApproximateAgeType getApproximateAgeType() {
		return approximateAgeType;
	}

	public void setApproximateAgeType(ApproximateAgeType approximateAgeType) {
		this.approximateAgeType = approximateAgeType;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}
	
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}
	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonIndexDto other = (PersonIndexDto) obj;
		if (approximateAge == null) {
			if (other.approximateAge != null)
				return false;
		} else if (!approximateAge.equals(other.approximateAge))
			return false;
		if (approximateAgeType != other.approximateAgeType)
			return false;
		if (presentCondition != other.presentCondition)
			return false;
		if (sex != other.sex)
			return false;
		return true;
	}

}
