package de.symeda.sormas.api.immunization;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public final class ImmunizationSimilarityCriteria extends BaseCriteria implements Cloneable {

	private final String immunizationUuid;
	private final Disease disease;
	private final Date startDate;
	private final Date endDate;
	private final String personUuid;
	private final MeansOfImmunization meansOfImmunization;

	public static class Builder {

		private String immunizationUuid;
		private Disease disease;
		private Date startDate;
		private Date endDate;
		private String personUuid;
		private MeansOfImmunization meansOfImmunization;

		public Builder withImmunization(String immunizationUuid) {
			this.immunizationUuid = immunizationUuid;
			return this;
		}

		public Builder withDisease(Disease disease) {
			this.disease = disease;
			return this;
		}

		public Builder withStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder withEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder withPerson(String personUuid) {
			this.personUuid = personUuid;
			return this;
		}

		public Builder withMeansOfImmunization(MeansOfImmunization meansOfImmunization) {
			this.meansOfImmunization = meansOfImmunization;
			return this;
		}

		public ImmunizationSimilarityCriteria build() {
			return new ImmunizationSimilarityCriteria(this);
		}
	}

	private ImmunizationSimilarityCriteria(Builder builder) {
		this.immunizationUuid = builder.immunizationUuid;
		this.disease = builder.disease;
		this.startDate = builder.startDate;
		this.endDate = builder.endDate;
		this.personUuid = builder.personUuid;
		this.meansOfImmunization = builder.meansOfImmunization;
	}

	@IgnoreForUrl
	public MeansOfImmunization getMeansOfImmunization() {
		return meansOfImmunization;
	}

	@IgnoreForUrl
	public String getImmunizationUuid() {
		return immunizationUuid;
	}

	@IgnoreForUrl
	public Disease getDisease() {
		return disease;
	}

	@IgnoreForUrl
	public Date getStartDate() {
		return startDate;
	}

	@IgnoreForUrl
	public Date getEndDate() {
		return endDate;
	}

	@IgnoreForUrl
	public String getPersonUuid() {
		return personUuid;
	}
}
