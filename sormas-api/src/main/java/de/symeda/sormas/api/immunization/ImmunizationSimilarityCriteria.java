package de.symeda.sormas.api.immunization;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public final class ImmunizationSimilarityCriteria extends BaseCriteria implements Cloneable {

	private final String immunizationUuid;
	private final Disease disease;
	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	private final Date startDate;
	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	private final Date endDate;
	private final String personUuid;
	private final MeansOfImmunization meansOfImmunization;

	public static class Builder {

		private String immunizationUuid;
		private Disease disease;
		/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
		@Deprecated
		private Date startDate;
		/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
		@Deprecated
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

		/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
		@Deprecated
		public Builder withStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}

		/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
		@Deprecated
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

	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	@IgnoreForUrl
	public Date getStartDate() {
		return startDate;
	}

	/** @deprecated Superseded by the validity period ({@link ImmunizationDto#VALID_FROM}, {@link ImmunizationDto#VALID_UNTIL}). */
	@Deprecated
	@IgnoreForUrl
	public Date getEndDate() {
		return endDate;
	}

	@IgnoreForUrl
	public String getPersonUuid() {
		return personUuid;
	}
}
