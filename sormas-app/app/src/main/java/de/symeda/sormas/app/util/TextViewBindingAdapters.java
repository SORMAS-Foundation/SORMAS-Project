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

package de.symeda.sormas.app.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableList;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.therapy.TypeOfDrug;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.core.TimeAgo;
import de.symeda.sormas.app.core.enumeration.PathogenTestResultTypeElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

public class TextViewBindingAdapters {

    @BindingAdapter(value = {"value", "shortUuid", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setUuidValue(TextView textField, String stringValue, boolean shortUuid, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (stringValue == null) {
            textField.setText(val);
        } else {
            if (shortUuid) {
                val = DataHelper.getShortUuid(stringValue);
            } else {
                val = stringValue;
            }

            //textField.setText(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, stringValue));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"value", "prependValue", "valueFormat", "defaultValue"})
    public static void setValueWithPrepend(TextView textField, String stringValue, String prependValue, String valueFormat, String defaultValue) {
        if (StringUtils.isEmpty(stringValue)) {
            textField.setText(defaultValue);
        } else {
            textField.setText(String.format(valueFormat, prependValue, stringValue));
        }
    }

    @BindingAdapter(value = {"value", "prependValue", "valueFormat", "defaultValue"}, requireAll = true)
    public static void setValueWithPrepend(TextView textField, Enum enumValue, String prependValue, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (enumValue == null) {
            textField.setText(prependValue + ": " + val);
        } else {
            val = enumValue.toString();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, prependValue, val));
            } else {
                textField.setText(prependValue + ": " + val);
            }
        }
    }

    @BindingAdapter(value = {"value", "valueCaption", "defaultValue"})
    public static void setValueWithCaption(TextView view, String value, String valueCaption, String defaultValue) {
        StringBuilder valBuilder = new StringBuilder();
        valBuilder.append("<b>").append(valueCaption).append(": </b>");
        if (StringUtils.isEmpty(value)) {
            view.setText(Html.fromHtml(valBuilder.append(defaultValue).toString()));
        } else {
            view.setText(Html.fromHtml(valBuilder.append(value).toString()));
        }
    }

    @BindingAdapter(value = {"dateValue", "valueCaption", "defaultValue"})
    public static void setDateValueWithCaption(TextView view, Date date, String valueCaption, String defaultValue) {
        setValueWithCaption(view, DateFormatHelper.formatLocalDate(date), valueCaption, defaultValue);
    }

    @BindingAdapter(value = {"facilityValue", "facilityDetailsValue", "valueCaption", "defaultValue"})
    public static void setFacilityValueWithCaption(TextView view, Facility facility, String facilityDetailsValue, String valueCaption, String defaultValue) {
        if (facility != null) {
            setValueWithCaption(view, FacilityHelper.buildFacilityString(facility.getUuid(), facility.getName(), facilityDetailsValue), valueCaption, defaultValue);
        } else {
            setValueWithCaption(view, null, valueCaption, defaultValue);
        }
    }

    @BindingAdapter(value = {"enumValue", "valueCaption", "defaultValue"})
    public static void setEnumValueWithCaption(TextView view, Enum enumValue, String valueCaption, String defaultValue) {
        setValueWithCaption(view, enumValue != null ? enumValue.toString() : null, valueCaption, defaultValue);
    }

    @BindingAdapter(value = {"ageDateValue", "valueCaption", "defaultValue"})
    public static void setAgeDateValueWithCaption(TextView view, Person person, String valueCaption, String defaultValue) {
        setValueWithCaption(view,
                DateFormatHelper.getAgeAndBirthdateString(person.getApproximateAge(), person.getApproximateAgeType(), person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY()),
                valueCaption, defaultValue);
    }

    @BindingAdapter(value = {"value", "prependValue", "appendValue", "valueFormat", "defaultValue"}, requireAll = true)
    public static void setValueWithPrepend(TextView textField, Integer integerValue, String prependValue, String appendValue, String valueFormat, String defaultValue) {
        String val = defaultValue;
        String stringValue = (integerValue != null) ? integerValue.toString() : "";

        if (integerValue == null) {
            textField.setText(prependValue + ": " + val);
        } else {
            val = stringValue;
            //textField.setText(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, prependValue, stringValue, appendValue));
            } else {
                textField.setText(prependValue + ": " + val);
            }
        }
    }

    @BindingAdapter(value = {"locationValue", "prependValue", "valueFormat", "defaultValue"}, requireAll = true)
    public static void setLocationValueWithPrepend(TextView textField, Location location, String prependValue, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (location == null || location.toString().isEmpty()) {
            textField.setText(prependValue + ": " + val);
        } else {
            val = location.getCompleteString();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, prependValue, val));
            } else {
                textField.setText(prependValue + ": " + val);
            }


            textField.setText(location.toString());
        }
    }

    @BindingAdapter(value = {"ageWithDateValue", "prependValue", "valueFormat", "defaultValue"}, requireAll = true)
    public static void setAgeWithDateValueAndPrepend(TextView textField, Person person, String prependValue, String valueFormat, String defaultValue) {
        if (person == null || person.getApproximateAge() == null) {
            textField.setText(prependValue + ": " + defaultValue);
        } else {
            String age = person.getApproximateAge().toString();
            ApproximateAgeType ageType = person.getApproximateAgeType();
            String dateOfBirth = DateFormatHelper.formatBirthdate(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY());

            StringBuilder ageWithDateBuilder = new StringBuilder()
                    .append(age).append(" ").append(ageType != null ? ageType.toString() : "")
                    .append(" (").append(dateOfBirth).append(")");

            textField.setText(prependValue + ": " + ageWithDateBuilder.toString());
        }
    }

    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(TextView textField, String stringValue, String appendValue, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (stringValue == null) {
            textField.setText(val);
        } else {
            val = stringValue;
            //textField.setText(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, stringValue, appendValue));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"dateRangeValue", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDateRangeValue(TextView textField, Date fromValue, Date appendValue, String valueFormat, String defaultValue) {
        String from = "";
        String to = "";

        if (fromValue == null && appendValue == null) {
            textField.setText(defaultValue);
            return;
        }

        if (fromValue == null) {
            from = " ? ";
        } else {
            from = DateFormatHelper.formatLocalDate(fromValue);
        }

        if (appendValue == null) {
            to = " ? ";
        } else {
            to = DateFormatHelper.formatLocalDate(appendValue);
        }

        if (valueFormat != null && valueFormat.trim() != "") {
            textField.setText(String.format(valueFormat, from, to));
        } else {
            textField.setText(from + " - " + to);
        }
    }

    @BindingAdapter(value = {"value", "append1", "append2", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(TextView textField, String stringValue, String append1, String append2, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (stringValue == null) {
            textField.setText(val);
        } else {
            val = stringValue;
            //textField.setText(val);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, stringValue, append1, append2));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"temperatureValue", "temperatureSource", "defaultValue"})
    public static void setTemperatureValue(TextView textField, Float temperature, TemperatureSource temperatureSource, String defaultValue) {
        if (temperature == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(TemperatureSource.formatTemperatureSource(temperature, temperatureSource));
        }
    }

    @BindingAdapter(value = {"bloodPressureSystolicValue", "bloodPressureDiastolicValue", "defaultValue"})
    public static void setBloodPressureValue(TextView textField, Integer bloodPressureSystolic, Integer bloodPressureDiastolic, String defaultValue) {
        if (bloodPressureSystolic == null && bloodPressureDiastolic == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(SymptomsHelper.getBloodPressureString(bloodPressureSystolic, bloodPressureDiastolic));
        }
    }

    @BindingAdapter(value = {"heartRateValue", "defaultValue"})
    public static void setHeartRateValue(TextView textField, Integer heartRate, String defaultValue) {
        if (heartRate == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(SymptomsHelper.getHeartRateString(heartRate));
        }
    }

    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(TextView textField, Integer integerValue, String appendValue, String valueFormat, String defaultValue) {
        setValue(textField, (integerValue != null) ? integerValue.toString() : "", appendValue, valueFormat, defaultValue);
    }

    @BindingAdapter(value = {"diseaseValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDiseaseValue(TextView textField, Task task, String valueFormat, String defaultValue) {
        String val = defaultValue;
        String diseaseString = getDisease(task);

        if (task == null || diseaseString == null || diseaseString.isEmpty()) {
            textField.setText(val);
        } else {
            val = diseaseString;

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"diseaseValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDiseaseValue(TextView textField, Sample sample, String valueFormat, String defaultValue) {
        String val = defaultValue;
        String diseaseString = getDisease(sample);

        if (sample == null || diseaseString == null || diseaseString.isEmpty()) {
            textField.setText(val);
        } else {
            val = diseaseString;

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"diseaseValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDiseaseValue(TextView textField, Event eventRecord, String valueFormat, String defaultValue) {
        String val = defaultValue;
        String diseaseString = getDisease(eventRecord);

        if (eventRecord == null || diseaseString == null || diseaseString.isEmpty()) {
            textField.setText(val);
        } else {
            val = diseaseString;

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"diseaseValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDiseaseValue(TextView textField, Case caseRecord, String valueFormat, String defaultValue) {
        if (caseRecord == null || caseRecord.getDisease() == null) {
            textField.setText(defaultValue);
        } else {
            if (caseRecord.getDisease() == Disease.OTHER) {
                textField.setText(caseRecord.getDiseaseDetails());
            } else {
                textField.setText(caseRecord.getDisease().toShortString());
            }
        }
    }

    @BindingAdapter(value = {"diseaseValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDiseaseValue(TextView textField, Disease disease, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (disease == null) {
            textField.setText(val);
        } else {
            val = disease.toString();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"value", "defaultValue"}, requireAll = true)
    public static void setValue(TextView textField, Enum enumValue, String defaultValue) {
        setValue(textField, enumValue, defaultValue, null);
    }

    @BindingAdapter(value = {"value", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setValue(TextView textField, Enum enumValue, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (enumValue == null) {
            textField.setText(val);
        } else {
            val = enumValue.toString();

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"treatmentType", "valueDetails", "typeOfDrug"})
    public static void setTreatmentTypeValue(TextView textField, TreatmentType value, String valueDetails, TypeOfDrug typeOfDrug) {
        if (value == TreatmentType.DRUG_INTAKE) {
            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(valueDetails)) {
                sb.append(value.toString()).append(" (").append(valueDetails);
            }
            if (typeOfDrug != null) {
                if (!StringUtils.isEmpty(valueDetails)) {
                    sb.append(" - ").append(typeOfDrug.toString());
                } else {
                    sb.append(" (").append(typeOfDrug.toString());
                }
            }
            sb.append(")");
            textField.setText(sb.toString());
        } else if (value == TreatmentType.OTHER) {
            textField.setText(valueDetails);
        } else {
            textField.setText(value.toString());
        }
    }

    @BindingAdapter(value = {"doseValue", "routeValue", "routeDetailsValue"})
    public static void setDoseAndRoute(TextView textField, String dose, TreatmentRoute route, String routeDetails) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(dose)) {
            sb.append(dose);
        }
        if (route != null) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            if (route == TreatmentRoute.OTHER && !StringUtils.isEmpty(routeDetails)) {
                sb.append(routeDetails);
            } else {
                sb.append(route.toString());
            }
        }
        textField.setText(sb.toString());
    }

    @BindingAdapter(value = {"yesNoUnknown"}, requireAll = false)
    public static void setYesNoUnknown(TextView textField, YesNoUnknown yesNoUnknown) {
        if (yesNoUnknown == null) {
            textField.setText("");
        } else {
            String val = yesNoUnknown.toString();
            String fieldId = textField.getResources().getResourceName(textField.getId());
            String caption = I18nProperties.getCaption(ControlPropertyField.getPropertyIdPrefix(fieldId), ControlPropertyField.getSubPropertyId(fieldId));

            if (DataHelper.isNullOrEmpty(caption)) {
                textField.setText(val);
            } else {
                textField.setText(caption + ": " + val);
            }
        }
    }

    @BindingAdapter(value = {"value", "appendValue", "valueFormat", "defaultValue"}, requireAll = true)
    public static void setValue(TextView textField, Enum enumValue, Date dateValue, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (enumValue == null) {
            textField.setText(val);
        } else {
            val = enumValue.toString();
            String _dateValue = DateFormatHelper.formatLocalDate(dateValue);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val + "(" + _dateValue + ")");
            }
        }
    }

    @BindingAdapter(value = {"investigationStatusValue", "defaultValue"}, requireAll = false)
    public static void setInvestigationStatusValue(TextView textField, InvestigationStatus status, String defaultValue) {
        if (status == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(status.toString());
        }
    }

    @BindingAdapter(value = {"userValue", "defaultValue"}, requireAll = false)
    public static void setUserValue(TextView textField, User user, String defaultValue) {
        if (user == null) {
            textField.setText(defaultValue);
        } else {
            String valueFormat = textField.getContext().getResources().getString(R.string.person_name_format);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, user.getFirstName(), user.getLastName().toUpperCase()));
            } else {
                textField.setText(user.toString());
            }
        }
    }

    @BindingAdapter(value = {"personValue", "defaultValue"}, requireAll = false)
    public static void setPersonValue(TextView textField, Person person, String defaultValue) {
        if (person == null) {
            textField.setText(defaultValue);
        } else {
            String valueFormat = textField.getContext().getResources().getString(R.string.person_name_format);

            if (valueFormat != null && !valueFormat.trim().equals("")) {
                textField.setText(String.format(valueFormat, person.getFirstName(), person.getLastName().toUpperCase()));
            } else {
                textField.setText(person.toString());
            }
        }
    }

    @BindingAdapter(value = {"personValue", "defaultValue"}, requireAll = false)
    public static void setPersonValue(TextView textField, Sample sample, String defaultValue) {
        if (sample == null) {
            textField.setText(defaultValue);
        } else {
            String result = "";
            String valueFormat = textField.getContext().getResources().getString(R.string.person_name_format);
            Case assocCase = sample.getAssociatedCase();

            if (assocCase == null) {
                textField.setText(defaultValue);
                return;
            }

            Person person = assocCase.getPerson();

            if (person == null) {
                textField.setText(defaultValue);
                return;
            }

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, person.getFirstName(), person.getLastName().toUpperCase()));
            } else {
                textField.setText(person.toString());
            }
        }
    }

    @BindingAdapter(value = {"patientValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setPatientValue(TextView textField, Task task, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (task == null) {
            textField.setText(val);
        } else {
            val = getPersonInfo(task);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"dueDateValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDueDateValue(TextView textField, Task task, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (task == null || task.getDueDate() == null) {
            textField.setText(val);
        } else {
            val = DateFormatHelper.formatLocalDate(task.getDueDate());

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }


            Integer dueDateColor = null;
            if (task.getDueDate().compareTo(new Date()) <= 0 && !TaskStatus.DONE.equals(task.getTaskStatus())) {
                dueDateColor = textField.getContext().getResources().getColor(R.color.watchOut);
                textField.setTypeface(textField.getTypeface(), Typeface.BOLD);
                textField.setTextColor(dueDateColor);
            }
        }
    }

    @BindingAdapter(value = {"dueTimeAgoValue", "textColor", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDueTimeAgoValue(TextView textField, Task task, int textColor, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (task == null || task.getDueDate() == null) {
            textField.setText(val);
        } else {
            //val = DateHelper.formatLocalShortDate(task.getDueDate());
            val = TimeAgo.using(textField.getContext()).with(task.getDueDate());

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }


            Integer dueDateColor = null;
            TaskStatus kkk = task.getTaskStatus();
            if (task.getDueDate().compareTo(new Date()) <= 0 && !TaskStatus.DONE.equals(task.getTaskStatus())) {
                dueDateColor = textField.getContext().getResources().getColor(R.color.watchOut);
                //textField.setTypeface(textField.getTypeface(), Typeface.BOLD);
                textField.setTextColor(dueDateColor);
            } else {
                //dueDateColor = textField.getContext().getResources().getColor();
                //textField.setTypeface(textField.getTypeface(), Typeface.NORMAL);
                textField.setTextColor(textColor);
            }
        }
    }

    @BindingAdapter(value = {"ageWithDateValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setAgeWithDateValue(TextView textField, Person person, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (person == null || person.getApproximateAge() == null) {
            textField.setText(val);
        } else {
            val = person.getApproximateAge().toString();

            if (valueFormat != null && valueFormat.trim() != "") {
                //Age

                //Year or Month
                String ageType = person.getApproximateAgeType().toString();

                //Dob
                String dob = DateFormatHelper.formatBirthdate(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY());

                textField.setText(String.format(valueFormat, val, ageType, dob));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"ageWithUnit", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setAgeWithUnit(TextView textField, Person person, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (person == null || person.getApproximateAge() == null) {
            textField.setText(val);
        } else {
            val = person.getApproximateAge().toString();

            if (val == null || val == "")
                return;

            //Year or Month
            String ageType = person.getApproximateAgeType() != null ? person.getApproximateAgeType().toString() : null;

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val, ageType));
            } else {
                if (ageType != null) {
                    textField.setText(val + " " + ageType);
                } else {
                    textField.setText(val);
                }
            }
        }
    }

    @BindingAdapter(value = {"resultingCaseStatus", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setResultingCaseStatus(TextView textField, String resultingCaseUuid, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (StringUtils.isEmpty(resultingCaseUuid)) {
            textField.setText(val);
        } else {
            Case caze = DatabaseHelper.getCaseDao().queryUuidBasic(resultingCaseUuid);
            if (caze != null) {
                val = DatabaseHelper.getString(R.string.caption_resulting_case_status) + ": " + caze.getInvestigationStatus().toString();
            } else {
                val = DatabaseHelper.getString(R.string.caption_resulting_case_uuid) + ": " + DataHelper.getShortUuid(resultingCaseUuid);
            }

            if (valueFormat != null && !valueFormat.trim().equals("")) {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"dateValue", "prependValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setDateValue(TextView textField, Date dateValue, String prependValue, String valueFormat, String defaultValue) {
        if (dateValue == null) {
            textField.setText(defaultValue);
        } else {
            String val = DateFormatHelper.formatLocalDate(dateValue);

            if (valueFormat != null && !valueFormat.trim().equals("")) {
                val = String.format(valueFormat, val);
            }

            textField.setText(prependValue != null ? prependValue + " " + val : val);
        }
    }

    @BindingAdapter(value = {"timeValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setTimeValue(TextView textField, Date dateValue, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (dateValue == null) {
            textField.setText(val);
        } else {
            val = DateHelper.formatTime(dateValue);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"dateTimeValue", "defaultValue"})
    public static void setDateTimeValue(TextView textField, Date dateValue, String defaultValue) {
        if (dateValue == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(DateFormatHelper.formatLocalDateTime(dateValue));
        }
    }

    @BindingAdapter(value = {"timeAgoValue", "valueFormat", "defaultValue"}, requireAll = false)
    public static void setTimeAgoValue(TextView textField, Date dateValue, String valueFormat, String defaultValue) {
        String val = defaultValue;

        if (dateValue == null) {
            textField.setText(val);
        } else {
            val = TimeAgo.using(textField.getContext()).with(dateValue);

            if (valueFormat != null && valueFormat.trim() != "") {
                textField.setText(String.format(valueFormat, val));
            } else {
                textField.setText(val);
            }
        }
    }

    @BindingAdapter(value = {"shortLocationValue", "defaultValue"}, requireAll = false)
    public static void setShortLocationValue(TextView textField, Location location, String defaultValue) {
        if (location == null || location.toString().isEmpty()) {
            textField.setText(defaultValue);
        } else {
            textField.setText(location.getRegion() + ", " + location.getDistrict());
        }
    }


    @BindingAdapter(value = {"locationValue", "defaultValue"}, requireAll = false)
    public static void setLocationValue(TextView textField, Location location, String defaultValue) {
        if (location == null || location.toString().isEmpty()) {
            textField.setText(defaultValue);
        } else {
            textField.setText(location.toString());
        }
    }

    @BindingAdapter(value = {"testResultValue", "defaultValue"}, requireAll = false)
    public static void setTestResultValue(TextView textField, Sample sample, String defaultValue) {
        if (sample == null) {
            textField.setText(defaultValue);
        } else {
            Resources resources = textField.getContext().getResources();

            String result = "";
            SpecimenCondition condition = sample.getSpecimenCondition();
            //TODO: Orson - Replace
            //PathogenTest mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(sample);
            PathogenTest mostRecentTest = null;

            if (condition == null) {
                textField.setText(defaultValue);
                return;
            }

            if (condition == SpecimenCondition.NOT_ADEQUATE) {
                result = resources.getString(R.string.value_inadequate_specimen_condition);
            } else if (mostRecentTest != null) {
                result = mostRecentTest.getTestResult().toString();
            } else {
                result = resources.getString(R.string.info_no_sample_test);
            }

            textField.setText(result);
        }
    }

    @BindingAdapter(value = {"facilityValue", "defaultValue"}, requireAll = false)
    public static void setFacilityValue(TextView textField, Facility facility, String defaultValue) {
        if (facility == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(facility.toString());
        }
    }

    @BindingAdapter(value = {"facilityValue", "facilityDetailsValue", "prependValue", "valueFormat"})
    public static void setFacilityValue(TextView textField, Facility facility, String facilityDetails, String prependValue, String valueFormat) {
        String value;
        if (FacilityHelper.isOtherOrNoneHealthFacility(facility.getUuid())) {
            value = facility.getName() + " (" + facilityDetails + ")";
        } else {
            value = facility.getName();
        }

        if (valueFormat != null && !valueFormat.trim().equals("")) {
            textField.setText(String.format(valueFormat, prependValue, value));
        } else {
            textField.setText(prependValue + ": " + value);
        }
    }

    @BindingAdapter(value = {"samplePurposeValue", "defaultValue"}, requireAll = false)
    public static void setPurposeValue(TextView textField, SamplePurpose samplePurpose, String defaultValue) {
        if (samplePurpose == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(samplePurpose.toString());
        }
    }

    @BindingAdapter(value = {"pointOfEntryValue", "defaultValue"}, requireAll = false)
    public static void setPointOfEntryValue(TextView textField, PointOfEntry pointOfEntry, String defaultValue) {
        if (pointOfEntry == null) {
            textField.setText(defaultValue);
        } else {
            textField.setText(pointOfEntry.toString());
        }
    }

    @BindingAdapter(value = {"adoValue", "defaultValue"}, requireAll = false)
    public static void setAdoValue(TextView textField, AbstractDomainObject ado, String defaultValue) {
        if (ado == null || StringUtils.isEmpty(ado.toString())) {
            textField.setText(defaultValue);
        } else {
            textField.setText(ado.toString());
        }
    }

    //TODO: Orson - remove
    @BindingAdapter(value = {"removeBottomMarginForBurialIfEmpty", "bottomMargin"})
    public static void setRemoveBottomMarginForBurialIfEmpty(LinearLayout viewGroup, ObservableList<EpiDataBurial> list, float bottomMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();

        if (list == null || list.size() <= 0) {
            params.bottomMargin = 0;
        } else {
            params.bottomMargin = (int) bottomMargin;
        }

        viewGroup.setLayoutParams(params);
    }

    //TODO: Orson - remove
    @BindingAdapter(value = {"removeBottomMarginForGatheringIfEmpty", "bottomMargin"})
    public static void setRemoveBottomMarginForGatheringIfEmpty(LinearLayout viewGroup, ObservableList<EpiDataGathering> list, float bottomMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();

        if (list == null || list.size() <= 0) {
            params.bottomMargin = 0;
        } else {
            params.bottomMargin = (int) bottomMargin;
        }

        viewGroup.setLayoutParams(params);
    }

    //TODO: Orson - remove
    @BindingAdapter(value = {"removeBottomMarginForTravelIfEmpty", "bottomMargin"})
    public static void setRemoveBottomMarginForTravelIfEmpty(LinearLayout viewGroup, ObservableList<EpiDataTravel> list, float bottomMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();

        if (list == null || list.size() <= 0) {
            params.bottomMargin = 0;
        } else {
            params.bottomMargin = (int) bottomMargin;
        }

        viewGroup.setLayoutParams(params);
    }

    @BindingAdapter(value = {"removeBottomMarginIfEmpty", "bottomMargin"})
    public static void setRemoveBottomMarginIfEmpty(LinearLayout viewGroup, ObservableList list, float bottomMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();

        if (list == null || list.size() <= 0) {
            params.bottomMargin = 0;
        } else {
            params.bottomMargin = (int) bottomMargin;
        }

        viewGroup.setLayoutParams(params);
    }

    @BindingAdapter("hasValue")
    public static void setHasValue(TextView textView, ObservableList list) {
        Resources resources = textView.getContext().getResources();

        String yes = resources.getString(R.string.yes);
        String no = resources.getString(R.string.no);

        if (list != null && list.size() > 0) {
            textView.setText(yes);
        } else {
            textView.setText(no);
        }
    }

    @BindingAdapter("resultType")
    public static void setResultType(TextView textView, PathogenTestResultType resultType) {
        if (resultType != null) {
            Context context = textView.getContext();
            PathogenTestResultTypeElaborator elaborator =
                    (PathogenTestResultTypeElaborator) StatusElaboratorFactory.getElaborator(resultType);
            textView.setText(resultType.name());
            textView.setTextColor(context.getResources().getColor(elaborator.getColorIndicatorResource()));
        }
    }

    private static String getDisease(Task record) {
        String result = null;

//        if (record.getCaze() != null && record.getCaze().getDisease() != null) {
//            result = record.getCaze().getDisease().toShortString();
//        } else if (record.getContact() != null && record.getContact().getCaze() != null && record.getContact().getCaze().getDisease() != null) {
//            result = record.getContact().getCaze().getDisease().toShortString();
//        } else if (record.getEvent() != null && record.getEvent().getDisease() != null){
//            result = record.getEvent().getDisease().toShortString();
//        } else {
//            result = "";
//        }

        return result;
    }

    private static String getDisease(Event record) {
        if (record == null)
            return "";

        if (record.getDisease() == null)
            return "";

        return record.getDisease().toShortString();
    }

    private static String getDisease(Case record) {
        if (record == null)
            return "";

        if (record.getDisease() == null)
            return "";

        return record.getDisease().toShortString();
    }

    private static String getDisease(Sample sample) {
        String result = "";
        Case assocCase = sample.getAssociatedCase();

        if (assocCase == null)
            return result;

        Disease disease = assocCase.getDisease();

        if (disease == null)
            return result;


        if (disease == Disease.OTHER) {
            result = disease.toShortString() + " (" + sample.getAssociatedCase().getDiseaseDetails() + ")";
        } else {
            result = disease.toShortString();
        }

        return result;
    }

    private static String getPersonInfo(Sample sample) {
        String result = "";
        Case assocCase = sample.getAssociatedCase();

        if (assocCase == null)
            return result;

        Person person = assocCase.getPerson();

        if (person == null)
            return result;

        return person.toString();
    }

    private static String getPersonInfo(Task record) {
        String result = null;

        if (record.getCaze() != null) {
            result = record.getCaze().getPerson().getFirstName() + " " + record.getCaze().getPerson().getLastName().toUpperCase();
        } else if (record.getContact() != null) {
            result = record.getContact().getPerson().getFirstName() + " " + record.getContact().getPerson().getLastName().toUpperCase();
        } else if (record.getEvent() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(record.getEvent().getEventStatus());
            sb.append(", " + DateFormatHelper.formatLocalDate(record.getEvent().getEventDate()));
            if (record.getEvent().getEventLocation().getCity() != null && !record.getEvent().getEventLocation().getCity().isEmpty()) {
                sb.append(", " + record.getEvent().getEventLocation().getCity());
            }
            sb.append(", " + StringUtils.substring(record.getEvent().getEventDesc(), 0, 15) + (record.getEvent().getEventDesc().length() > 15 ? "..." : ""));
            result = sb.toString();
        }

        return result;
    }


}
