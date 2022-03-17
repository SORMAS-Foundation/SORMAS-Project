package de.symeda.sormas.backend.sample;

public class SampleDashboardCount {

	private Long PathogenTestResultNegative;
	private Long PathogenTestResultPositive;
	private Long PathogenTestResultPending;
	private Long PathogenTestResultIndeterminate;
	private Long SampleShipped;
	private Long SampleNotShipped;
	private Long SampleReceived;
	private Long SampleNotReceived;
	private Long SpecimenConditionAdequate;
	private Long SpecimenConditionInadequate;

	public SampleDashboardCount() {
	}

	public SampleDashboardCount(
		Long pathogenTestResultNegative,
		Long pathogenTestResultPositive,
		Long pathogenTestResultPending,
		Long pathogenTestResultIndeterminate,
		Long sampleShipped,
		Long sampleNotShipped,
		Long specimenAdequate,
		Long specimenInadequate,
		Long sampleReceived,
		Long sampleNotReceived) {
		PathogenTestResultNegative = pathogenTestResultNegative;
		PathogenTestResultPositive = pathogenTestResultPositive;
		PathogenTestResultPending = pathogenTestResultPending;
		PathogenTestResultIndeterminate = pathogenTestResultIndeterminate;
		SampleShipped = sampleShipped;
		SampleNotShipped = sampleNotShipped;
		SpecimenConditionAdequate = specimenAdequate;
		SpecimenConditionInadequate = specimenInadequate;
		SampleReceived = sampleReceived;
		SampleNotReceived = sampleNotReceived;
	}

	public Long getPathogenTestResultNegative() {
		return PathogenTestResultNegative;
	}

	public void setPathogenTestResultNegative(Long pathogenTestResultNegative) {
		PathogenTestResultNegative = pathogenTestResultNegative;
	}

	public Long getPathogenTestResultPositive() {
		return PathogenTestResultPositive;
	}

	public void setPathogenTestResultPositive(Long pathogenTestResultPositive) {
		PathogenTestResultPositive = pathogenTestResultPositive;
	}

	public Long getPathogenTestResultPending() {
		return PathogenTestResultPending;
	}

	public void setPathogenTestResultPending(Long pathogenTestResultPending) {
		PathogenTestResultPending = pathogenTestResultPending;
	}

	public Long getPathogenTestResultIndeterminate() {
		return PathogenTestResultIndeterminate;
	}

	public void setPathogenTestResultIndeterminate(Long pathogenTestResultIndeterminate) {
		PathogenTestResultIndeterminate = pathogenTestResultIndeterminate;
	}

	public Long getSampleShipped() {
		return SampleShipped;
	}

	public void setSampleShipped(Long sampleShipped) {
		SampleShipped = sampleShipped;
	}

	public Long getSampleNotShipped() {
		return SampleNotShipped;
	}

	public void setSampleNotShipped(Long sampleNotShipped) {
		SampleNotShipped = sampleNotShipped;
	}

	public Long getSampleReceived() {
		return SampleReceived;
	}

	public void setSampleReceived(Long sampleReceived) {
		SampleReceived = sampleReceived;
	}

	public Long getSampleNotReceived() {
		return SampleNotReceived;
	}

	public void setSampleNotReceived(Long sampleNotReceived) {
		SampleNotReceived = sampleNotReceived;
	}

	public Long getSpecimenConditionAdequate() {
		return SpecimenConditionAdequate;
	}

	public void setSpecimenConditionAdequate(Long specimenConditionAdequate) {
		SpecimenConditionAdequate = specimenConditionAdequate;
	}

	public Long getSpecimenConditionInadequate() {
		return SpecimenConditionInadequate;
	}

	public void setSpecimenConditionInadequate(Long specimenConditionInadequate) {
		SpecimenConditionInadequate = specimenConditionInadequate;
	}

}
