/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name= AdditionalTest.TABLE_NAME)
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

    @Column
    private Integer arterialVenousGasPH;

    @Column
    private Integer arterialVenousGasPco2;

    @Column
    private Integer arterialVenousGasPao2;

    @Column
    private Integer arterialVenousGasHco3;

    @Column
    private Integer gasOxygenTherapy;

    @Column
    private Integer altSgpt;

    @Column
    private Integer astSgot;

    @Column
    private Integer creatinine;

    @Column
    private Integer potassium;

    @Column
    private Integer urea;

    @Column
    private Integer haemoglobin;

    @Column
    private Integer totalBilirubin;

    @Column
    private Integer conjBilirubin;

    @Column
    private Integer wbcCount;

    @Column
    private Integer platelets;

    @Column
    private Integer prothrombinTime;

    @Column(length = 512)
    private String otherTestResults;

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

    public Integer getArterialVenousGasPH() {
        return arterialVenousGasPH;
    }

    public void setArterialVenousGasPH(Integer arterialVenousGasPH) {
        this.arterialVenousGasPH = arterialVenousGasPH;
    }

    public Integer getArterialVenousGasPco2() {
        return arterialVenousGasPco2;
    }

    public void setArterialVenousGasPco2(Integer arterialVenousGasPco2) {
        this.arterialVenousGasPco2 = arterialVenousGasPco2;
    }

    public Integer getArterialVenousGasPao2() {
        return arterialVenousGasPao2;
    }

    public void setArterialVenousGasPao2(Integer arterialVenousGasPao2) {
        this.arterialVenousGasPao2 = arterialVenousGasPao2;
    }

    public Integer getArterialVenousGasHco3() {
        return arterialVenousGasHco3;
    }

    public void setArterialVenousGasHco3(Integer arterialVenousGasHco3) {
        this.arterialVenousGasHco3 = arterialVenousGasHco3;
    }

    public Integer getGasOxygenTherapy() {
        return gasOxygenTherapy;
    }

    public void setGasOxygenTherapy(Integer gasOxygenTherapy) {
        this.gasOxygenTherapy = gasOxygenTherapy;
    }

    public Integer getAltSgpt() {
        return altSgpt;
    }

    public void setAltSgpt(Integer altSgpt) {
        this.altSgpt = altSgpt;
    }

    public Integer getAstSgot() {
        return astSgot;
    }

    public void setAstSgot(Integer astSgot) {
        this.astSgot = astSgot;
    }

    public Integer getCreatinine() {
        return creatinine;
    }

    public void setCreatinine(Integer creatinine) {
        this.creatinine = creatinine;
    }

    public Integer getPotassium() {
        return potassium;
    }

    public void setPotassium(Integer potassium) {
        this.potassium = potassium;
    }

    public Integer getUrea() {
        return urea;
    }

    public void setUrea(Integer urea) {
        this.urea = urea;
    }

    public Integer getHaemoglobin() {
        return haemoglobin;
    }

    public void setHaemoglobin(Integer haemoglobin) {
        this.haemoglobin = haemoglobin;
    }

    public Integer getTotalBilirubin() {
        return totalBilirubin;
    }

    public void setTotalBilirubin(Integer totalBilirubin) {
        this.totalBilirubin = totalBilirubin;
    }

    public Integer getConjBilirubin() {
        return conjBilirubin;
    }

    public void setConjBilirubin(Integer conjBilirubin) {
        this.conjBilirubin = conjBilirubin;
    }

    public Integer getWbcCount() {
        return wbcCount;
    }

    public void setWbcCount(Integer wbcCount) {
        this.wbcCount = wbcCount;
    }

    public Integer getPlatelets() {
        return platelets;
    }

    public void setPlatelets(Integer platelets) {
        this.platelets = platelets;
    }

    public Integer getProthrombinTime() {
        return prothrombinTime;
    }

    public void setProthrombinTime(Integer prothrombinTime) {
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