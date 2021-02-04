package de.symeda.sormas.app.backend.vaccinationinfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;

@Entity(name = VaccinationInfo.TABLE_NAME)
@DatabaseTable(tableName = VaccinationInfo.TABLE_NAME)
@EmbeddedAdo
public class VaccinationInfo extends AbstractDomainObject {

	public static final String TABLE_NAME = "vaccinationInfo";
	public static final String I18N_PREFIX = "VaccinationInfo";

	@Enumerated(EnumType.STRING)
	private Vaccination vaccination;

	@Column(columnDefinition = "text")
	private String vaccinationDoses;

	@Enumerated(EnumType.STRING)
	private VaccinationInfoSource vaccinationInfoSource;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date firstVaccinationDate;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date lastVaccinationDate;

	@Enumerated(EnumType.STRING)
	private Vaccine vaccineName;

	@Column(columnDefinition = "text")
	private String otherVaccineName;

	@Enumerated(EnumType.STRING)
	private VaccineManufacturer vaccineManufacturer;

	@Column(columnDefinition = "text")
	private String otherVaccineManufacturer;

	@Column(columnDefinition = "text")
	private String vaccineInn;

	@Column(columnDefinition = "text")
	private String vaccineBatchNumber;

	@Column(columnDefinition = "text")
	private String vaccineUniiCode;

	@Column(columnDefinition = "text")
	private String vaccineAtcCode;

	public Vaccination getVaccination() {
		return vaccination;
	}

	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	public void setFirstVaccinationDate(Date firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}

	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	public void setLastVaccinationDate(Date lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public Vaccine getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(Vaccine vaccineName) {
		this.vaccineName = vaccineName;
	}

	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	public void setOtherVaccineName(String otherVaccineName) {
		this.otherVaccineName = otherVaccineName;
	}

	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	public void setVaccineManufacturer(VaccineManufacturer vaccineManufacturer) {
		this.vaccineManufacturer = vaccineManufacturer;
	}

	public String getOtherVaccineManufacturer() {
		return otherVaccineManufacturer;
	}

	public void setOtherVaccineManufacturer(String otherVaccineManufacturer) {
		this.otherVaccineManufacturer = otherVaccineManufacturer;
	}

	public String getVaccineInn() {
		return vaccineInn;
	}

	public void setVaccineInn(String vaccineInn) {
		this.vaccineInn = vaccineInn;
	}

	public String getVaccineBatchNumber() {
		return vaccineBatchNumber;
	}

	public void setVaccineBatchNumber(String vaccineBatchNumber) {
		this.vaccineBatchNumber = vaccineBatchNumber;
	}

	public String getVaccineUniiCode() {
		return vaccineUniiCode;
	}

	public void setVaccineUniiCode(String vaccineUniiCode) {
		this.vaccineUniiCode = vaccineUniiCode;
	}

	public String getVaccineAtcCode() {
		return vaccineAtcCode;
	}

	public void setVaccineAtcCode(String vaccineAtcCode) {
		this.vaccineAtcCode = vaccineAtcCode;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
