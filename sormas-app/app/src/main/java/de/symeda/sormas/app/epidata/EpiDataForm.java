package de.symeda.sormas.app.epidata;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.ListField;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.CaseEpidataFragmentLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.EpiDataValidator;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataForm extends FormTab {

    public static final String KEY_CASE_UUID = "caseUuid";

    private CaseEpidataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_epidata_fragment_layout, container, false);

        View view = binding.getRoot();
        editOrCreateUserRight = (UserRight) getArguments().get(EDIT_OR_CREATE_USER_RIGHT);

        final Disease disease = (Disease) getArguments().getSerializable(Case.DISEASE);
        final String epiDataUuid = getArguments().getString(EpiData.UUID);

        if (epiDataUuid != null) {
            final EpiData epiData = DatabaseHelper.getEpiDataDao().queryUuid(epiDataUuid);
            binding.setEpiData(epiData);
        } else {
            binding.setEpiData(new EpiData());
        }

        binding.epiDataBurials.initialize(
                new EpiDataBurialsListArrayAdapter(
                        this.getActivity(),
                        R.layout.epidata_burials_list_item),
                new Consumer() {
                    @Override
                    public void accept(Object burial) {
                        editBurial((EpiDataBurial)burial);
                    }
                }
        );

        binding.epiDataGatherings.initialize(
                new EpiDataGatheringsListArrayAdapter(
                        this.getActivity(),
                        R.layout.epidata_gatherings_list_item),
                new Consumer() {
                    @Override
                    public void accept(Object gathering) {
                        editGathering((EpiDataGathering)gathering);
                    }
                }
        );

        binding.epiDataTravels.initialize(
                new EpiDataTravelsListArrayAdapter(
                        this.getActivity(),
                        R.layout.epidata_travels_list_item),
                new Consumer() {
                    @Override
                    public void accept(Object travel) {
                        editTravel((EpiDataTravel)travel);
                    }
                }
        );

        binding.epiDataSickDeadAnimalsDate.initialize(this);
        FieldHelper.initSpinnerField(binding.epiDataWaterSource, WaterSource.class);

        binding.epiDataBurialAttended.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataBurials.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                updateBurialsHint();
            }
        });

        binding.epiDataGatheringAttended.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataGatherings.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                updateGatheringsHint();
            }
        });

        binding.epiDataTraveled.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataTravels.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                updateTravelsHint();
            }
        });

        binding.epiDataEatingRawAnimals.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataOtherAnimalsDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
            }
        });

        binding.epiDataSickDeadAnimals.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataSickDeadAnimalsDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                if (Diseases.DiseasesConfiguration.isDefinedOrMissing(EpiDataDto.class, EpiDataDto.SICK_DEAD_ANIMALS_DATE, disease)) {
                    binding.epiDataSickDeadAnimalsDate.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
                if (Diseases.DiseasesConfiguration.isDefinedOrMissing(EpiDataDto.class, EpiDataDto.SICK_DEAD_ANIMALS_LOCATION, disease)) {
                    binding.epiDataSickDeadAnimalsLocation.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            }
        });

        binding.epiDataOtherAnimals.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataOtherAnimalsDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
            }
        });

        binding.epiDataWaterSource.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataWaterSourceOther.setVisibility(field.getValue() == WaterSource.OTHER ? View.VISIBLE : View.GONE);
            }
        });

        binding.epiDataWaterBody.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.epiDataWaterBodyDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
            }
        });

        setVisibilityByDisease(EpiDataDto.class, disease, (ViewGroup)binding.getRoot());
        showOrHideHeadlines();

        FieldHelper.initSpinnerField(binding.epiDataAnimalCondition, AnimalCondition.class);
        binding.epiDataDateOfLastExposure.initialize(this);

        return view;
    }

    private void editTravel(EpiDataTravel travel) {
        if (travel == null) {
            travel = DatabaseHelper.getEpiDataTravelDao().build();
        }
        final EpiDataTravelForm travelDialog = new EpiDataTravelForm();
        travelDialog.initialize(
                travel,
                new Consumer() {
                    @Override
                    public void accept(Object travel) {
                        if (EpiDataValidator.validateTravelData(travelDialog.getBinding())) {
                            binding.epiDataTravels.setValue(
                                    ListField.updateList(
                                            binding.epiDataTravels.getValue(),
                                            (EpiDataTravel) travel
                                    )
                            );
                            travelDialog.dismiss();
                            updateTravelsHint();
                        }
                    }
                }, new Consumer() {
                    @Override
                    public void accept(Object travel) {
                        binding.epiDataTravels.setValue(
                                ListField.removeFromList(
                                        binding.epiDataTravels.getValue(),
                                        (EpiDataTravel) travel
                                )
                        );
                        updateTravelsHint();
                    }
                },
                getActivity().getResources().getString(R.string.headline_travel)
        );
        travelDialog.show(getFragmentManager(), "epidata_travel_edit_fragment");
    }

    private void editGathering(EpiDataGathering gathering) {
        if (gathering == null) {
            gathering = DatabaseHelper.getEpiDataGatheringDao().build();
        }
        final EpiDataGatheringForm gatheringDialog = new EpiDataGatheringForm();
        gatheringDialog.initialize(
                gathering,
                new Consumer() {
                    @Override
                    public void accept(Object gathering) {
                        binding.epiDataGatherings.setValue(
                                ListField.updateList(
                                        binding.epiDataGatherings.getValue(),
                                        (EpiDataGathering) gathering
                                )
                        );
                        gatheringDialog.dismiss();
                        updateGatheringsHint();
                    }
                }, new Consumer() {
                    @Override
                    public void accept(Object gathering) {
                        binding.epiDataGatherings.setValue(
                                ListField.removeFromList(
                                        binding.epiDataGatherings.getValue(),
                                        (EpiDataGathering) gathering
                                )
                        );
                        updateGatheringsHint();
                    }
                },
                getActivity().getResources().getString(R.string.headline_gathering)
        );
        gatheringDialog.show(getFragmentManager(), "epidata_gathering_edit_fragment");
    }

    private void editBurial(EpiDataBurial burial) {
        if (burial == null) {
            burial = DatabaseHelper.getEpiDataBurialDao().build();
        }
        final EpiDataBurialForm burialDialog = new EpiDataBurialForm();
        burialDialog.initialize(
                burial,
                new Consumer() {
                    @Override
                    public void accept(Object burial) {
                        if (EpiDataValidator.validateBurialData(burialDialog.getBinding())) {
                            binding.epiDataBurials.setValue(
                                    ListField.updateList(
                                            binding.epiDataBurials.getValue(),
                                            (EpiDataBurial) burial
                                    )
                            );
                            burialDialog.dismiss();
                            updateBurialsHint();
                        }
                    }
                }, new Consumer() {
                    @Override
                    public void accept(Object burialDialog) {
                        binding.epiDataBurials.setValue(
                                ListField.removeFromList(
                                        binding.epiDataBurials.getValue(),
                                        (EpiDataBurial) burialDialog
                                )
                        );
                        updateBurialsHint();
                    }
                },
                getActivity().getResources().getString(R.string.headline_burial)
        );
        burialDialog.show(getChildFragmentManager(), "epidata_burial_edit_fragment");
    }

    private void showOrHideHeadlines() {
        if (!(binding.epiDataBurialAttended.getVisibility() == View.VISIBLE || binding.epiDataGatheringAttended.getVisibility() == View.VISIBLE ||
                binding.epiDataTraveled.getVisibility() == View.VISIBLE)) {
            binding.epiDataEpiData.setVisibility(View.GONE);
            binding.epiDataEpiDataInfoText.setVisibility(View.GONE);
        }

        if (!(binding.epiDataRodents.getVisibility() == View.VISIBLE || binding.epiDataBats.getVisibility() == View.VISIBLE ||
                binding.epiDataPrimates.getVisibility() == View.VISIBLE || binding.epiDataSwine.getVisibility() == View.VISIBLE ||
                binding.epiDataBirds.getVisibility() == View.VISIBLE || binding.epiDataCattle.getVisibility() == View.VISIBLE ||
                binding.epiDataOtherAnimals.getVisibility() == View.VISIBLE ||
                binding.epiDataEatingRawAnimals.getVisibility() == View.VISIBLE || binding.epiDataEatingRawAnimalsInInfectedArea.getVisibility() == View.VISIBLE ||
                binding.epiDataSickDeadAnimals.getVisibility() == View.VISIBLE
        )) {
            binding.epiDataAnimalContacts.setVisibility(View.GONE);
            binding.epiDataAnimalContactsInfoText.setVisibility(View.GONE);
        }

        if (!(binding.epiDataWaterSource.getVisibility() == View.VISIBLE || binding.epiDataWaterBody.getVisibility() == View.VISIBLE ||
                binding.epiDataTickBite.getVisibility() == View.VISIBLE || binding.epiDataFleaBite.getVisibility() == View.VISIBLE)) {
            binding.epiDataEnvironmentalExposure.setVisibility(View.GONE);
        }
    }

    /**
     * helper method to remove all numbers from a property String (which are needed because of the
     * 10 character limitation for property names Android currently has
     */
    private String cropNumbers(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!isInteger(String.valueOf(s.charAt(i)))) {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateBurialsHint() {
        YesNoUnknown burialAttended = binding.epiDataBurialAttended.getValue();
        if (burialAttended == YesNoUnknown.YES && binding.epiDataBurials.getValue().size() == 0) {
            binding.epiDataBurialAttended.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_add_list_entry));
        } else {
            binding.epiDataBurialAttended.clearError();
        }
    }

    private void updateGatheringsHint() {
        YesNoUnknown gatheringAttended = binding.epiDataGatheringAttended.getValue();
        if (gatheringAttended == YesNoUnknown.YES && binding.epiDataGatherings.getValue().size() == 0) {
            binding.epiDataGatheringAttended.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_add_list_entry));
        } else {
            binding.epiDataGatheringAttended.clearError();
        }
    }

    private void updateTravelsHint() {
        YesNoUnknown traveled = binding.epiDataTraveled.getValue();
        if (traveled == YesNoUnknown.YES && binding.epiDataTravels.getValue().size() == 0) {
            binding.epiDataTraveled.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_add_list_entry));
        } else {
            binding.epiDataTraveled.clearError();
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return binding == null ? null : binding.getEpiData();
    }
}
