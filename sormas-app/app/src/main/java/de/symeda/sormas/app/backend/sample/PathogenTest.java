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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DateFormatHelper;

@Entity(name = PathogenTest.TABLE_NAME)
@DatabaseTable(tableName = PathogenTest.TABLE_NAME)
public class PathogenTest extends PseudonymizableAdo {

	private static final long serialVersionUID = 2290351143518627813L;

	public static final String TABLE_NAME = "pathogenTest";
	public static final String I18N_PREFIX = "PathogenTest";

	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String SAMPLE = "sample";
	public static final String ENVIRONMENT_SAMPLE = "environmentSample";

	public static final String TEST_RESULT = "testResult";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Sample sample;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private EnvironmentSample environmentSample;

	@Enumerated(EnumType.STRING)
	private PathogenTestType testType;

	@Enumerated(EnumType.STRING)
	private PCRTestSpecification pcrTestSpecification;

	@Column
	private String testTypeText;

	@Enumerated(EnumType.STRING)
	private Disease testedDisease;

	@Column(name = "testedDiseaseVariant")
	private String testedDiseaseVariantString;
	private DiseaseVariant testedDiseaseVariant;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String testedDiseaseDetails;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String testedDiseaseVariantDetails;

	@Column(name = "testedPathogen", length = CHARACTER_LIMIT_DEFAULT)
	private String testedPathogenString;

	private Pathogen testedPathogen;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String testedPathogenDetails;

	@Column
	private String typingId;

	@Enumerated(EnumType.STRING)
	@Column
	private PathogenTestResultType testResult;

	@Column
	private Boolean testResultVerified;

	@Column(length = CHARACTER_LIMIT_BIG)
	private String testResultText;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date testDateTime;

	@Column
	private boolean fourFoldIncreaseAntibodyTiter;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String serotype;

	@DatabaseField
	private Float cqValue;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date reportDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility lab;

	@Column
	private String labDetails;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User labUser;

	@Column
	private boolean viaLims;

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public EnvironmentSample getEnvironmentSample() {
		return environmentSample;
	}

	public void setEnvironmentSample(EnvironmentSample environmentSample) {
		this.environmentSample = environmentSample;
	}

	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	public PCRTestSpecification getPcrTestSpecification() {
		return pcrTestSpecification;
	}

	public void setPcrTestSpecification(PCRTestSpecification pcrTestSpecification) {
		this.pcrTestSpecification = pcrTestSpecification;
	}

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public String getTestedDiseaseVariantString() {
		return testedDiseaseVariantString;
	}

	public void setTestedDiseaseVariantString(String testedDiseaseVariantString) {
		this.testedDiseaseVariantString = testedDiseaseVariantString;
	}

	@Transient
	public DiseaseVariant getTestedDiseaseVariant() {
		if (StringUtils.isBlank(testedDiseaseVariantString)) {
			return null;
		} else {
			return DatabaseHelper.getCustomizableEnumValueDao().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, testedDiseaseVariantString);
		}
	}

	public void setTestedDiseaseVariant(DiseaseVariant testedDiseaseVariant) {
		this.testedDiseaseVariant = testedDiseaseVariant;
		if (testedDiseaseVariant == null) {
			testedDiseaseVariantString = null;
		} else {
			testedDiseaseVariantString = testedDiseaseVariant.getValue();
		}
	}

	public String getTestedDiseaseDetails() {
		return testedDiseaseDetails;
	}

	public void setTestedDiseaseDetails(String testedDiseaseDetails) {
		this.testedDiseaseDetails = testedDiseaseDetails;
	}

	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String testedDiseaseVariantDetails) {
		this.testedDiseaseVariantDetails = testedDiseaseVariantDetails;
	}

	public String getTestedPathogenString() {
		return testedPathogenString;
	}

	public void setTestedPathogenString(String testedPathogenString) {
		this.testedPathogenString = testedPathogenString;
	}

	@Transient
	public Pathogen getTestedPathogen() {
		if (StringUtils.isBlank(testedPathogenString)) {
			return null;
		} else {
			return DatabaseHelper.getCustomizableEnumValueDao().getEnumValue(CustomizableEnumType.PATHOGEN, testedPathogenString);
		}
	}

	public void setTestedPathogen(Pathogen testedPathogen) {
		this.testedPathogen = testedPathogen;
		if (testedPathogen == null) {
			testedPathogenString = null;
		} else {
			testedPathogenString = testedPathogen.getValue();
		}
	}

	public String getTestedPathogenDetails() {
		return testedPathogenDetails;
	}

	public void setTestedPathogenDetails(String testedPathogenDetails) {
		this.testedPathogenDetails = testedPathogenDetails;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public Boolean getTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public Facility getLab() {
		return lab;
	}

	public void setLab(Facility lab) {
		this.lab = lab;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public User getLabUser() {
		return labUser;
	}

	public void setLabUser(User labUser) {
		this.labUser = labUser;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
	}

	public boolean isFourFoldIncreaseAntibodyTiter() {
		return fourFoldIncreaseAntibodyTiter;
	}

	public void setFourFoldIncreaseAntibodyTiter(boolean fourFoldIncreaseAntibodyTiter) {
		this.fourFoldIncreaseAntibodyTiter = fourFoldIncreaseAntibodyTiter;
	}

	public String getTestTypeText() {
		return testTypeText;
	}

	public void setTestTypeText(String testTypeText) {
		this.testTypeText = testTypeText;
	}

	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	public Float getCqValue() {
		return cqValue;
	}

	public void setCqValue(Float cqValue) {
		this.cqValue = cqValue;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public boolean isViaLims() {
		return viaLims;
	}

	public void setViaLims(boolean viaLims) {
		this.viaLims = viaLims;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String buildCaption() {
		return super.buildCaption() + DateFormatHelper.formatLocalDate(getTestDateTime());
	}
}
