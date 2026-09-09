package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;
import de.symeda.sormas.api.utils.Diseases;

public enum SyphilisSerologyMethod {

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.NON_TREPONEMAL_TESTS })
	VDRL,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.NON_TREPONEMAL_TESTS })
	RPR,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.NON_TREPONEMAL_TESTS })
	TRUST,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.TREPONEMAL_TESTS })
	TPPA_TPHA,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.TREPONEMAL_TESTS })
	FTA_ABS,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.TREPONEMAL_TESTS })
	EIA,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.TREPONEMAL_TESTS })
	CLIA,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.TREPONEMAL_TESTS })
	RDT,

	@Diseases(value = {
		Disease.SYPHILIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.NON_TREPONEMAL_TESTS,
		PathogenTestType.TREPONEMAL_TESTS })
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
