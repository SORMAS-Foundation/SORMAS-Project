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

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
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
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseEditDataForm extends FormTab {

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

        boolean definedOrMissing = Diseases.DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, binding.caseDataMeaslesVaccination.getPropertyId(), binding.getCaze().getDisease());
        binding.caseDataMeaslesVaccination.setVisibility(definedOrMissing ? View.VISIBLE : View.GONE);

        if (binding.getCaze().getPerson().getSex() != Sex.FEMALE) {
            binding.caseDataPregnant.setVisibility(View.GONE);
        }

        toggleMeaslesFields();

        binding.caseDataMeaslesVaccination.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                toggleMeaslesFields();
            }
        });

        if (binding.caseDataPregnant.getVisibility() == View.GONE && binding.caseDataMeaslesVaccination.getVisibility() == View.GONE) {
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
                        binding.caseDataFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
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

        if (ConfigProvider.getUser().getUserRole() == UserRole.CASE_OFFICER || ConfigProvider.getUser().getUserRole() == UserRole.SURVEILLANCE_OFFICER) {
            binding.caseDataMove.setVisibility(View.VISIBLE);
            binding.caseDataMove.setPaintFlags(binding.caseDataMove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            binding.caseDataMove.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            binding.caseDataMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        binding.caseDataMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display warning popup that moving the case will discard all unsaved changes
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setPositiveButton(view.getContext().getResources().getText(R.string.action_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Consumer positiveCallback = new Consumer() {
                                    @Override
                                    public void accept(Object success) {
                                        Case updatedCase = DatabaseHelper.getCaseDao().queryUuid(binding.getCaze().getUuid());
                                        binding.setCaze(updatedCase);

                                        if ((boolean) success) {
                                            Snackbar.make(CaseEditDataForm.this.getView().findViewById(R.id.base_layout), getResources().getString(R.string.snackbar_case_moved), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Snackbar.make(CaseEditDataForm.this.getView().findViewById(R.id.base_layout), getResources().getString(R.string.snackbar_case_moved_error), Snackbar.LENGTH_LONG).show();
                                        }

                                        ((CaseEditActivity) CaseEditDataForm.this.getActivity()).setAdapter();
                                    }
                                };

                                final FacilityChangeDialogBuilder dialogBuilder = new FacilityChangeDialogBuilder(getActivity(), binding.getCaze(), positiveCallback);
                                AlertDialog facilityChangeDialog = dialogBuilder.create();
                                facilityChangeDialog.show();
                                dialogBuilder.setButtonListeners(facilityChangeDialog, CaseEditDataForm.this.getActivity());
                            }
                        }
                );
                builder.setNegativeButton(view.getContext().getResources().getText(R.string.action_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );

                AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.setTitle(view.getContext().getResources().getText(R.string.headline_reset_PIN).toString());
                dialog.setMessage(view.getContext().getResources().getText(R.string.infoText_move_case_discard_changes).toString());
                dialog.show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getCaze();
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

    public CaseDataFragmentLayoutBinding getBinding() {
        return binding;
    }
}