/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.sample;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = AdditionalTest.TABLE_NAME)
@DatabaseTable(tableName = AdditionalTest.TABLE_NAME)
public class AdditionalTest extends AbstractDomainObject {

	private static final long serialVersionUID = -7306267901413644171L;

	public static final String TABLE_NAME = "additionaltest";
	public static final String I18N_PREFIX = "AdditionalTest";

	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String SAMPLE = "sample";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Sample sample;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date testDateTime;

	@Enumerated(EnumType.STRING)
	private SimpleTestResultType haemoglobinuria;

	@Enumerated(EnumType.STRING)
	private SimpleTestResultType proteinuria;

	@Enumerated(EnumType.STRING)
	private SimpleTestResultType hematuria;

	@DatabaseField
	private Float arterialVenousGasPh;

	@DatabaseField
	private Float arterialVenousGasPco2;

	@DatabaseField
	private Float arterialVenousGasPao2;

	@DatabaseField
	private Float arterialVenousGasHco3;

	@DatabaseField
	private Float gasOxygenTherapy;

	@DatabaseField
	private Float altSgpt;

	@DatabaseField
	private Float astSgot;

	@DatabaseField
	private Float creatinine;

	@DatabaseField
	private Float potassium;

	@DatabaseField
	private Float urea;

	@DatabaseField
	private Float haemoglobin;

	@DatabaseField
	private Float totalBilirubin;

	@DatabaseField
	private Float conjBilirubin;

	@DatabaseField
	private Float wbcCount;

	@DatabaseField
	private Float platelets;

	@DatabaseField
	private Float prothrombinTime;

	@Column(length = COLUMN_LENGTH_BIG)
	private String otherTestResults;

	public boolean hasArterialVenousGasValue() {
		return arterialVenousGasPh != null || arterialVenousGasPco2 != null || arterialVenousGasPao2 != null || arterialVenousGasHco3 != null;
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public SimpleTestResultType getHaemoglobinuria() {
		return haemoglobinuria;
	}

	public void setHaemoglobinuria(SimpleTestResultType haemoglobinuria) {
		this.haemoglobinuria = haemoglobinuria;
	}

	public SimpleTestResultType getProteinuria() {
		return proteinuria;
	}

	public void setProteinuria(SimpleTestResultType proteinuria) {
		this.proteinuria = proteinuria;
	}

	public SimpleTestResultType getHematuria() {
		return hematuria;
	}

	public void setHematuria(SimpleTestResultType hematuria) {
		this.hematuria = hematuria;
	}

	public Float getArterialVenousGasPh() {
		return arterialVenousGasPh;
	}

	public void setArterialVenousGasPh(Float arterialVenousGasPh) {
		this.arterialVenousGasPh = arterialVenousGasPh;
	}

	public Float getArterialVenousGasPco2() {
		return arterialVenousGasPco2;
	}

	public void setArterialVenousGasPco2(Float arterialVenousGasPco2) {
		this.arterialVenousGasPco2 = arterialVenousGasPco2;
	}

	public Float getArterialVenousGasPao2() {
		return arterialVenousGasPao2;
	}

	public void setArterialVenousGasPao2(Float arterialVenousGasPao2) {
		this.arterialVenousGasPao2 = arterialVenousGasPao2;
	}

	public Float getArterialVenousGasHco3() {
		return arterialVenousGasHco3;
	}

	public void setArterialVenousGasHco3(Float arterialVenousGasHco3) {
		this.arterialVenousGasHco3 = arterialVenousGasHco3;
	}

	public Float getGasOxygenTherapy() {
		return gasOxygenTherapy;
	}

	public void setGasOxygenTherapy(Float gasOxygenTherapy) {
		this.gasOxygenTherapy = gasOxygenTherapy;
	}

	public Float getAltSgpt() {
		return altSgpt;
	}

	public void setAltSgpt(Float altSgpt) {
		this.altSgpt = altSgpt;
	}

	public Float getAstSgot() {
		return astSgot;
	}

	public void setAstSgot(Float astSgot) {
		this.astSgot = astSgot;
	}

	public Float getCreatinine() {
		return creatinine;
	}

	public void setCreatinine(Float creatinine) {
		this.creatinine = creatinine;
	}

	public Float getPotassium() {
		return potassium;
	}

	public void setPotassium(Float potassium) {
		this.potassium = potassium;
	}

	public Float getUrea() {
		return urea;
	}

	public void setUrea(Float urea) {
		this.urea = urea;
	}

	public Float getHaemoglobin() {
		return haemoglobin;
	}

	public void setHaemoglobin(Float haemoglobin) {
		this.haemoglobin = haemoglobin;
	}

	public Float getTotalBilirubin() {
		return totalBilirubin;
	}

	public void setTotalBilirubin(Float totalBilirubin) {
		this.totalBilirubin = totalBilirubin;
	}

	public Float getConjBilirubin() {
		return conjBilirubin;
	}

	public void setConjBilirubin(Float conjBilirubin) {
		this.conjBilirubin = conjBilirubin;
	}

	public Float getWbcCount() {
		return wbcCount;
	}

	public void setWbcCount(Float wbcCount) {
		this.wbcCount = wbcCount;
	}

	public Float getPlatelets() {
		return platelets;
	}

	public void setPlatelets(Float platelets) {
		this.platelets = platelets;
	}

	public Float getProthrombinTime() {
		return prothrombinTime;
	}

	public void setProthrombinTime(Float prothrombinTime) {
		this.prothrombinTime = prothrombinTime;
	}

	public String getOtherTestResults() {
		return otherTestResults;
	}

	public void setOtherTestResults(String otherTestResults) {
		this.otherTestResults = otherTestResults;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
