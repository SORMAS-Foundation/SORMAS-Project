/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.epidata;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EpiDataBurialDto extends PseudonymizableDto {

	private static final long serialVersionUID = -6274798743525238569L;

	public static final String I18N_PREFIX = "EpiDataBurial";

	public static final String BURIAL_PERSON_NAME = "burialPersonName";
	public static final String BURIAL_RELATION = "burialRelation";
	public static final String BURIAL_DATE_FROM = "burialDateFrom";
	public static final String BURIAL_DATE_TO = "burialDateTo";
	public static final String BURIAL_ADDRESS = "burialAddress";
	public static final String BURIAL_ILL = "burialIll";
	public static final String BURIAL_TOUCHING = "burialTouching";

	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	@SensitiveData
	private String burialPersonName;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	@SensitiveData
	private String burialRelation;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private Date burialDateFrom;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private Date burialDateTo;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private LocationDto burialAddress;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown burialIll;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown burialTouching;

	public String getBurialPersonName() {
		return burialPersonName;
	}

	public void setBurialPersonName(String burialPersonName) {
		this.burialPersonName = burialPersonName;
	}

	public String getBurialRelation() {
		return burialRelation;
	}

	public void setBurialRelation(String burialRelation) {
		this.burialRelation = burialRelation;
	}

	public Date getBurialDateFrom() {
		return burialDateFrom;
	}

	public void setBurialDateFrom(Date burialDateFrom) {
		this.burialDateFrom = burialDateFrom;
	}

	public Date getBurialDateTo() {
		return burialDateTo;
	}

	public void setBurialDateTo(Date burialDateTo) {
		this.burialDateTo = burialDateTo;
	}

	public LocationDto getBurialAddress() {
		return burialAddress;
	}

	public void setBurialAddress(LocationDto burialAddress) {
		this.burialAddress = burialAddress;
	}

	public YesNoUnknown getBurialIll() {
		return burialIll;
	}

	public void setBurialIll(YesNoUnknown burialIll) {
		this.burialIll = burialIll;
	}

	public YesNoUnknown getBurialTouching() {
		return burialTouching;
	}

	public void setBurialTouching(YesNoUnknown burialTouching) {
		this.burialTouching = burialTouching;
	}

	public static EpiDataBurialDto build() {

		EpiDataBurialDto dto = new EpiDataBurialDto();
		dto.setUuid(DataHelper.createUuid());
		LocationDto location = LocationDto.build();
		dto.setBurialAddress(location);
		return dto;
	}
}
