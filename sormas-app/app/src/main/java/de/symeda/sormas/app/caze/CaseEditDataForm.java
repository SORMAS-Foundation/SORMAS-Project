package de.symeda.sormas.app.caze;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.FacilityChangeDialogBuilder;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.CaseDataFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.PersonValidator;
import de.symeda.sormas.app.validation.SymptomsValidator;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseEditDataForm extends FormTab {

    public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";

    private CaseDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_data_fragment_layout, container, false);

        final String caseUuid = (String) getArguments().getString(Case.UUID);
        final CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseUuid);
        binding.setCaze(caze);

        // case classification can only be edited by officers, not informants; additionally,
        // the "Not yet classified" classification is hidden from informants
        FieldHelper.initSpinnerField(binding.caseDataCaseOfficerClassification, CaseClassification.class);
        User user = ConfigProvider.getUser();
        if (user.getUserRole() == UserRole.INFORMANT) {
            binding.caseDataCaseOfficerClassification.setVisibility(View.GONE);
            if (binding.getCaze().getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
                binding.caseDataCaseClassification.setVisibility(View.GONE);
            }
        } else {
            binding.caseDataCaseClassification.setVisibility(View.GONE);
        }

        FieldHelper.initSpinnerField(binding.caseDataMeaslesVaccination, Vaccination.class);
        FieldHelper.initSpinnerField(binding.caseDataVaccinationInfoSource, VaccinationInfoSource.class);
        FieldHelper.initSpinnerField(binding.caseDataYellowFeverVaccination, Vaccination.class);
        FieldHelper.initSpinnerField(binding.caseDataYellowFeverVaccinationInfoSource, VaccinationInfoSource.class);
        FieldHelper.initSpinnerField(binding.caseDataPlagueType, PlagueType.class);
        binding.caseDataSmallpoxVaccinationDate.initialize(this);

        boolean showMeaslesVaccination = Diseases.DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, binding.caseDataMeaslesVaccination.getPropertyId(), binding.getCaze().getDisease());
        binding.caseDataMeaslesVaccination.setVisibility(showMeaslesVaccination ? View.VISIBLE : View.GONE);

        boolean showYellowFeverVaccination = Diseases.DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, binding.caseDataYellowFeverVaccination.getPropertyId(), binding.getCaze().getDisease());
        binding.caseDataYellowFeverVaccination.setVisibility(showYellowFeverVaccination ? View.VISIBLE : View.GONE);

        boolean showSmallpoxVaccination = Diseases.DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, binding.caseDataSmallpoxVaccinationReceived.getPropertyId(), binding.getCaze().getDisease());
        binding.caseDataSmallpoxVaccinationReceived.setVisibility(showSmallpoxVaccination ? View.VISIBLE : View.GONE);

        binding.caseDataSmallpoxVaccinationReceived.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                toggleSmallpoxVaccinationFields();
            }
        });

        if (binding.getCaze().getPerson().getSex() != Sex.FEMALE) {
            binding.caseDataPregnant.setVisibility(View.GONE);
        }

        toggleMeaslesFields();
        toggleYellowFeverFields();

        binding.caseDataMeaslesVaccination.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                toggleMeaslesFields();
            }
        });

        binding.caseDataYellowFeverVaccination.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                toggleYellowFeverFields();
            }
        });

        if (binding.caseDataPregnant.getVisibility() == View.GONE && binding.caseDataMeaslesVaccination.getVisibility() == View.GONE &&
                binding.caseDataYellowFeverVaccination.getVisibility() == View.GONE && binding.caseDataSmallpoxVaccinationScar.getVisibility() == View.GONE) {
            binding.caseMedicalInformationHeadline.setVisibility(View.GONE);
        }

        binding.caseDataHealthFacility.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                Facility selectedFacility = binding.getCaze().getHealthFacility();
                if (selectedFacility != null) {
                    boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
                    boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

                    if (otherHealthFacility) {
                        binding.caseDataFacilityDetails.setVisibility(View.VISIBLE);
                        binding.caseDataFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
                    } else if (noneHealthFacility) {
                        binding.caseDataFacilityDetails.setVisibility(View.VISIBLE);
                        binding.caseDataFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
                    } else {
                        binding.caseDataFacilityDetails.setVisibility(View.GONE);
                    }
                } else {
                    binding.caseDataFacilityDetails.setVisibility(View.GONE);
                }
            }
        });

        if (ConfigProvider.getUser().getUserRole() != UserRole.INFORMANT) {
            binding.caseDataEpidNumber.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    String value = (String) field.getValue();
                    if (value.trim().isEmpty()) {
                        field.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_case_epid_number_empty));
                    } else if (value.matches(DataHelper.getEpidNumberRegexp())) {
                        field.clearError();
                    } else {
                        field.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_case_epid_number));
                    }
                }
            });
        } else {
            binding.caseDataEpidNumber.setEnabled(false);
        }

        if (ConfigProvider.getUser().getUserRole() == UserRole.SURVEILLANCE_OFFICER) {
            binding.caseDataCaseOfficerClassification.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    CaseClassification caseClassification = (CaseClassification) field.getValue();
                    if (caseClassification == CaseClassification.NOT_CLASSIFIED) {
                        field.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_case_classification));
                    } else {
                        field.clearError();
                    }
                }
            });
        }

        if (ConfigProvider.getUser().getUserRole() == UserRole.CASE_OFFICER || ConfigProvider.getUser().getUserRole() == UserRole.SURVEILLANCE_OFFICER) {
            binding.caseDataMove.setVisibility(View.VISIBLE);
            binding.caseDataMove.setPaintFlags(binding.caseDataMove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            binding.caseDataMove.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }

        if (binding.getCaze().getDisease() != Disease.OTHER) {
            binding.caseDataDiseaseDetails.setVisibility(View.GONE);
        }

        if (binding.getCaze().getDisease() != Disease.PLAGUE) {
            binding.caseDataPlagueType.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding == null ? null : binding.getCaze();
    }

    private void toggleMeaslesFields() {
        if (binding.caseDataMeaslesVaccination.getVisibility() != View.VISIBLE || binding.caseDataMeaslesVaccination.getValue() != Vaccination.VACCINATED) {
            binding.caseDataDoses.setVisibility(View.GONE);
            binding.caseDataVaccinationInfoSource.setVisibility(View.GONE);
        } else {
            binding.caseDataDoses.setVisibility(View.VISIBLE);
            binding.caseDataVaccinationInfoSource.setVisibility(View.VISIBLE);
        }
    }

    private void toggleYellowFeverFields() {
        if (binding.caseDataYellowFeverVaccination.getVisibility() != View.VISIBLE || binding.caseDataYellowFeverVaccination.getValue() != Vaccination.VACCINATED) {
            binding.caseDataYellowFeverVaccinationInfoSource.setVisibility(View.GONE);
        } else {
            binding.caseDataYellowFeverVaccinationInfoSource.setVisibility(View.VISIBLE);
        }
    }

    private void toggleSmallpoxVaccinationFields() {
        if (binding.caseDataSmallpoxVaccinationReceived.getVisibility() == View.VISIBLE
                && binding.caseDataSmallpoxVaccinationReceived.getValue() == YesNoUnknown.YES) {
            binding.caseDataSmallpoxVaccinationDate.setVisibility(View.VISIBLE);
            binding.caseDataSmallpoxVaccinationScar.setVisibility(View.VISIBLE);
            binding.smallpoxVaccinationScarImg.setVisibility(View.VISIBLE);
        } else {
            binding.caseDataSmallpoxVaccinationDate.setVisibility(View.GONE);
            binding.caseDataSmallpoxVaccinationScar.setVisibility(View.GONE);
            binding.smallpoxVaccinationScarImg.setVisibility(View.GONE);
        }
    }

    public CaseDataFragmentLayoutBinding getBinding() {
        return binding;
    }
}