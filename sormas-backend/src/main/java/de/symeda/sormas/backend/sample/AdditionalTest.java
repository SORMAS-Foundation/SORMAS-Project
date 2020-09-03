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
package de.symeda.sormas.backend.sample;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class AdditionalTest extends AbstractDomainObject {

	private static final long serialVersionUID = -7306267901413644171L;

	public static final String TABLE_NAME = "additionaltest";

	public static final String SAMPLE = "sample";
	public static final String TEST_DATE_TIME = "testDateTime";

	private Sample sample;
	private Date testDateTime;
	private SimpleTestResultType haemoglobinuria;
	private SimpleTestResultType proteinuria;
	private SimpleTestResultType hematuria;
	private Float arterialVenousGasPH;
	private Float arterialVenousGasPco2;
	private Float arterialVenousGasPao2;
	private Float arterialVenousGasHco3;
	private Float gasOxygenTherapy;
	private Float altSgpt;
	private Float astSgot;
	private Float creatinine;
	private Float potassium;
	private Float urea;
	private Float haemoglobin;
	private Float totalBilirubin;
	private Float conjBilirubin;
	private Float wbcCount;
	private Float platelets;
	private Float prothrombinTime;
	private String otherTestResults;

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	@Enumerated(EnumType.STRING)
	public SimpleTestResultType getHaemoglobinuria() {
		return haemoglobinuria;
	}

	public void setHaemoglobinuria(SimpleTestResultType haemoglobinuria) {
		this.haemoglobinuria = haemoglobinuria;
	}

	@Enumerated(EnumType.STRING)
	public SimpleTestResultType getProteinuria() {
		return proteinuria;
	}

	public void setProteinuria(SimpleTestResultType proteinuria) {
		this.proteinuria = proteinuria;
	}

	@Enumerated(EnumType.STRING)
	public SimpleTestResultType getHematuria() {
		return hematuria;
	}

	public void setHematuria(SimpleTestResultType hematuria) {
		this.hematuria = hematuria;
	}

	public Float getArterialVenousGasPH() {
		return arterialVenousGasPH;
	}

	public void setArterialVenousGasPH(Float arterialVenousGasPH) {
		this.arterialVenousGasPH = arterialVenousGasPH;
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

	@Column(length = COLUMN_LENGTH_BIG)
	public String getOtherTestResults() {
		return otherTestResults;
	}

	public void setOtherTestResults(String otherTestResults) {
		this.otherTestResults = otherTestResults;
	}

}
