/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.immunization;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.VaccinationStatus;

/**
 * Value Object encapsulating derived vaccination status data computed from immunization records.
 * <p>
 * This class represents an immutable domain entity designed to hold the aggregated and evaluated
 * vaccination information for a specific individual. It is derived from raw immunization events
 * and provides a normalized view of vaccination history and current status.
 * </p>
 * <p>
 * Key characteristics:
 * </p>
 * <ul>
 * <li>
 * <strong>Immutability:</strong> All fields are final and initialized in the constructor.</li>
 * <li><strong>Serializable:</strong> Designed for safe
 * transmission across network boundaries and persistence.</li>
 * <li><strong>Value Object Pattern:</strong> Equality and hash code are based on content, not reference.</li>
 * </ul>
 * <p>
 * Typical instantiation scenarios:
 * </p>
 * <ul>
 * <li>After aggregating immunization events from a case or contact record</li>
 * <li>When computing vaccination coverage statistics</li>
 * <li>During data export to external health reporting systems</li>
 * </ul>
 */
@AuditedClass
public class VaccinationStatusData implements Serializable {

	/**
	 * Unique identifier for serialization of this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The derived vaccination status as an enum value (e.g., IMMUNIZED, NOT_IMMUNIZED, UNKNOWN).
	 * This is the primary classification resulting from analysis of immunization records.
	 */
	private final VaccinationStatus vaccinationStatus;

	/**
	 * Total number of vaccine doses administered to the subject, as recorded in immunization events.
	 * This aggregate count supports verification against national immunization schedules.
	 */
	private final Integer numberOfDoses;

	/**
	 * Assessment of the reliability of vaccination information sources.
	 * Used for quality control when evaluating data completeness and source credibility.
	 */
	private final InformationReliability informationReliability;

	/**
	 * Optional free-text
	 * 
	 * field containing additional context or notes about the vaccination status.
	 * This field is typically populated only when {@code vaccinationStatus == VaccinationStatus.OTHER}.
	 * <p>
	 * Contains supplementary information such as:
	 * </p>
	 * <ul>
	 * <li>Exceptions to standard vaccination procedures</li>
	 * <li>Alternative vaccination protocols applied</li>
	 * <li>Clinical notes requiring attention</li>
	 * </ul>
	 */
	private final String vaccinationStatusDetails;

	/**
	 * The date of
	 * 
	 * the most recent vaccine dose administered, or null if no vaccination history exists.
	 */
	private final Date dateOfLastDose;

	/**
	 * Start date of the reference period for vaccination data analysis.
	 * Used for time-windowed queries and epidemiological studies.
	 */
	private final Date referencePeriodFrom;

	/**
	 * End date of the reference period for vaccination data analysis.
	 * Used for time-windowed queries and epidemiological studies.
	 */
	private final Date referencePeriodTo;

	/**
	 * Timestamp of the last automatic vaccination status update on the associated entity (Case, Contact, or EventParticipant).
	 * Null if the vaccination status was never automatically updated by the system.
	 */
	private final Date vaccinationStatusLastUpdated;

	public static boolean isComparableVaccinationStatusDataEqual(VaccinationStatusData caseData, VaccinationStatusData immunizationData) {
		// Both parameters must be non-null to proceed with comparison
		if (caseData == null || immunizationData == null) {
			return false;
		}
		return Objects.equals(caseData.getVaccinationStatus(), immunizationData.getVaccinationStatus())
			&& Objects.equals(caseData.getVaccinationStatusDetails(), immunizationData.getVaccinationStatusDetails())
			&& Objects.equals(caseData.getNumberOfDoses(), immunizationData.getNumberOfDoses())
			&& Objects.equals(caseData.getInformationReliability(), immunizationData.getInformationReliability());
	}

	/**
	 * Constructs a new VaccinationStatusData instance with the specified vaccination status information.
	 *
	 * @param vaccinationStatus
	 *            The derived vaccination status from immunization records.
	 * @param numberOfDoses
	 *            Total number of vaccine doses administered.
	 * @param informationReliability
	 *            Assessment of information source reliability.
	 * @param vaccinationStatusDetails
	 *            Free-text details (populatedonly for OTHER status).
	 * @param dateOfLastDose
	 *            Date of the most recent vaccination dose.
	 * @param referencePeriodFrom
	 *            Start of the reference analysis period.
	 * @param referencePeriodTo
	 *            End of the reference analysis period.
	 * @param vaccinationStatusLastUpdated
	 *            Timestamp of the last automatic vaccination status update on the entity.
	 */
	public VaccinationStatusData(
		VaccinationStatus vaccinationStatus,
		Integer numberOfDoses,
		InformationReliability informationReliability,
		String vaccinationStatusDetails,
		Date dateOfLastDose,
		Date referencePeriodFrom,
		Date referencePeriodTo,
		Date vaccinationStatusLastUpdated) {

		this.vaccinationStatus = vaccinationStatus;
		this.numberOfDoses = numberOfDoses;
		this.informationReliability = informationReliability;
		this.vaccinationStatusDetails = vaccinationStatusDetails;
		this.dateOfLastDose = dateOfLastDose == null ? null : new Date(dateOfLastDose.getTime());
		this.referencePeriodFrom = referencePeriodFrom == null ? null : new Date(referencePeriodFrom.getTime());
		this.referencePeriodTo = referencePeriodTo == null ? null : new Date(referencePeriodTo.getTime());
		this.vaccinationStatusLastUpdated = vaccinationStatusLastUpdated == null ? null : new Date(vaccinationStatusLastUpdated.getTime());
	}

	/**
	 * Returns the derived vaccination status.
	 *
	 * @return The VaccinationStatus enum value.
	 */
	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	/**
	 * Returns the total number of vaccine doses administered.
	 *
	 * @return The number of doses, or null if not available.
	 */
	public Integer getNumberOfDoses() {
		return numberOfDoses;
	}

	/**
	 * Returns the reliability assessment of vaccination information sources.
	 *
	 * @return The InformationReliability value.
	 */
	public InformationReliability getInformationReliability() {
		return informationReliability;
	}

	/**
	 * Returns optional free-text details about the vaccination status.
	 *
	 * @return Free-text details string, or null if not populated.
	 */
	public String getVaccinationStatusDetails() {
		return vaccinationStatusDetails;
	}

	/**
	 * Returns the date of the most recent vaccination dose.
	 *
	 * @return The date of last dose, or null if no vaccination
	 * 
	 *         recorded.
	 */
	public Date getDateOfLastDose() {
		return dateOfLastDose;
	}

	/**
	 * Returns the start date of the reference analysis period.
	 * 
	 * @return The reference period start date, or null if not defined.
	 */
	public Date getReferencePeriodFrom() {
		return referencePeriodFrom;
	}

	/**
	 * Returns the end date of the reference analysis period.
	 *
	 * @return The reference period end date, or null if not defined.
	 */
	public Date getReferencePeriodTo() {
		return referencePeriodTo;
	}

	/**
	 * Returns the timestamp of the last automatic vaccination status update on the associated entity.
	 *
	 * @return The last auto-update timestamp, or null if never automatically updated.
	 */
	public Date getVaccinationStatusLastUpdated() {
		return vaccinationStatusLastUpdated;
	}

	/**
	 * Checks if this object equals another object based on all field values.
	 *
	 * @param o
	 *            The object to compare against, or null.
	 * @return true if equal, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		VaccinationStatusData that = (VaccinationStatusData) o;
		return vaccinationStatus == that.vaccinationStatus
			&& Objects.equals(numberOfDoses, that.numberOfDoses)
			&& Objects.equals(informationReliability, that.informationReliability)
			&& Objects.equals(vaccinationStatusDetails, that.vaccinationStatusDetails)
			&& Objects.equals(dateOfLastDose, that.dateOfLastDose)
			&& Objects.equals(referencePeriodFrom, that.referencePeriodFrom)
			&& Objects.equals(referencePeriodTo, that.referencePeriodTo)
			&& Objects.equals(vaccinationStatusLastUpdated, that.vaccinationStatusLastUpdated);
	}

	/**
	 * Generates a hash code based on all field values for use in collections.
	 *
	 * @return A hash code value.
	 */
	@Override
	public int hashCode() {

		return Objects.hash(
			vaccinationStatus,
			numberOfDoses,
			informationReliability,
			vaccinationStatusDetails,
			dateOfLastDose,
			referencePeriodFrom,
			referencePeriodTo,
			vaccinationStatusLastUpdated);
	}

	/**
	 * Builder class for constructing VaccinationStatusData instances using the Fluent API pattern.
	 * <p>
	 * The builder provides a type-safe, readable construction process with method chaining.
	 * All fields can be set individually, and optional parameters may be omitted.
	 * <p>
	 * Typical usage pattern :
	 * 
	 * <pre>
	 * 
	 * VaccinationStatusData data = new VaccinationStatusData.Builder().vaccinationStatus(VaccinationStatus.IMMUNIZED)
	 * 	.numberOfDoses(3)
	 * 	.informationReliability(InformationReliability.COMPLETE)
	 * 	.build();
	 * </pre>
	 */
	public static class Builder {

		private VaccinationStatus vaccinationStatus;
		private Integer numberOfDoses;
		private InformationReliability informationReliability;
		private String vaccinationStatusDetails;
		private Date dateOfLastDose;
		private Date referencePeriodFrom;
		private Date referencePeriodTo;
		private Date vaccinationStatusLastUpdated;

		/**
		 * Sets the vaccination status and returns this builderfor method chaining.
		 *
		 * @param vaccinationStatus
		 *            The VaccinationStatus enum value.
		 * @return This builder instance.
		 */
		public Builder vaccinationStatus(VaccinationStatus vaccinationStatus) {
			this.vaccinationStatus = vaccinationStatus;
			return this;
		}

		/**
		 * Sets the number of administered vaccine doses and returns this builder formethod chaining.
		 *
		 * @param numberOfDoses
		 *            Total number of doses administered.
		 * @return This builder instance.
		 */
		public Builder numberOfDoses(Integer numberOfDoses) {
			this.numberOfDoses = numberOfDoses;
			return this;
		}

		/**
		 * Sets the information reliability assessment and returns this builder for method chaining.
		 *
		 * @param informationReliability
		 *            The reliability level.
		 * @return This builder instance.
		 */
		public Builder informationReliability(InformationReliability informationReliability) {

			this.informationReliability = informationReliability;
			return this;
		}

		/**
		 * Sets the free-text vaccination status details and returns this builder for method chaining.
		 *
		 * @param vaccinationStatusDetails
		 *            Additional context or notes.
		 * @return This builder instance.
		 */
		public Builder vaccinationStatusDetails(String vaccinationStatusDetails) {
			this.vaccinationStatusDetails = vaccinationStatusDetails;
			return this;
		}

		/**
		 * Sets the date of the last administered dose and returns this builder for method chaining.
		 *
		 * @param dateOfLastDose
		 *            The date of the most recent vaccination.
		 * @return This builder instance.
		 */
		public Builder dateOfLastDose(Date dateOfLastDose) {
			this.dateOfLastDose = dateOfLastDose == null ? null : new Date(dateOfLastDose.getTime());
			return this;
		}

		/**
		 * Sets the start date of the reference analysis period and returns this builder for method chaining.
		 *
		 * @param referencePeriodFrom
		 *            Start of the reference period.
		 * @return This builder instance.
		 */

		public Builder referencePeriodFrom(Date referencePeriodFrom) {
			this.referencePeriodFrom = referencePeriodFrom == null ? null : new Date(referencePeriodFrom.getTime());
			return this;
		}

		/**
		 * Sets the end date of the reference analysis period and returns this builder for method chaining.
		 *
		 * @param referencePeriodTo
		 *            End of the reference period.
		 * @return This builder instance.
		 */

		public Builder referencePeriodTo(Date referencePeriodTo) {
			this.referencePeriodTo = referencePeriodTo == null ? null : new Date(referencePeriodTo.getTime());
			return this;
		}

		public Builder vaccinationStatusLastUpdated(Date vaccinationStatusLastUpdated) {
			this.vaccinationStatusLastUpdated = vaccinationStatusLastUpdated;
			return this;
		}

		/**
		 * Constructs a new VaccinationStatus the builder's current state.
		 *
		 * @return A new VaccinationStatusData instance, or null if fields were never set.
		 */
		public VaccinationStatusData build() {
			return new VaccinationStatusData(
				vaccinationStatus,
				numberOfDoses,
				informationReliability,
				vaccinationStatusDetails,
				dateOfLastDose,
				referencePeriodFrom,
				referencePeriodTo,
				vaccinationStatusLastUpdated);
		}

		/**
		 * Creates a builder initialized from an existing VaccinationStatusData instance.
		 * This enables copy-and-modify patterns while preserving immutability guarantees.
		 *
		 * @param data
		 *            The VaccinationStatusData instance to copy from.
		 * @return A new Builder populated with the source data's field values.
		 */
		public static Builder createFrom(VaccinationStatusData data) {
			Builder builder = new Builder();
			builder.vaccinationStatus = data.vaccinationStatus;
			builder.numberOfDoses = data.numberOfDoses;
			builder.informationReliability = data.informationReliability;
			builder.vaccinationStatusDetails = data.vaccinationStatusDetails;
			builder.dateOfLastDose = data.dateOfLastDose;
			builder.referencePeriodFrom = data.referencePeriodFrom;
			builder.referencePeriodTo = data.referencePeriodTo;
			builder.vaccinationStatusLastUpdated = data.vaccinationStatusLastUpdated;
			return builder;
		}

		/**
		 * Creates a new empty Builder instance with no field values.
		 * 
		 * @return A new Builder ready for field configuration.
		 */
		public static Builder createBlank() {
			return new Builder();
		}
	}
}
