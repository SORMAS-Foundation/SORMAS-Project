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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.hospitalization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class HospitalizationDto extends EntityDto {

	private static final long serialVersionUID = 4846215199480684369L;
	
	public static final String I18N_PREFIX = "CaseHospitalization";
	
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String ISOLATED = "isolated";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String HOSPITALIZED_PREVIOUSLY = "hospitalizedPreviously";
	public static final String PREVIOUS_HOSPITALIZATIONS = "previousHospitalizations";
	public static final String ADMITTED_TO_HEALTH_FACILITY = "admittedToHealthFacility";

	// Fields are declared in the order they should appear in the import template
	
	@Outbreaks
	private YesNoUnknown admittedToHealthFacility;
	private Date admissionDate;
	private Date dischargeDate;
	private YesNoUnknown isolated;
	private Date isolationDate;
	private YesNoUnknown hospitalizedPreviously;
	
	private List<PreviousHospitalizationDto> previousHospitalizations = new ArrayList<>();
	
	public static HospitalizationDto build() {
		HospitalizationDto hospitalization = new HospitalizationDto();
		hospitalization.setUuid(DataHelper.createUuid());
		return hospitalization;
	}
	
	public Date getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	
	public Date getDischargeDate() {
		return dischargeDate;
	}
	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
	
	public YesNoUnknown getIsolated() {
		return isolated;
	}
	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}
	
	public Date getIsolationDate() {
		return isolationDate;
	}
	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}
	
	public YesNoUnknown getHospitalizedPreviously() {
		return hospitalizedPreviously;
	}
	public void setHospitalizedPreviously(YesNoUnknown hospitalizedPreviously) {
		this.hospitalizedPreviously = hospitalizedPreviously;
	}
	
	public List<PreviousHospitalizationDto> getPreviousHospitalizations() {
		return previousHospitalizations;
	}
	public void setPreviousHospitalizations(List<PreviousHospitalizationDto> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}
	
	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}
	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}
	
}
