package de.symeda.sormas.app.epidata;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.YesNoUnknown;
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
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataTab extends FormTab {

    public static final String KEY_CASE_UUID = "caseUuid";

    private CaseEpidataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_epidata_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {

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
                            if (burial == null) {
                                try {
                                    burial = DataUtils.createNew(EpiDataBurial.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            EpiDataBurialTab burialTab = new EpiDataBurialTab();
                            burialTab.initialize(
                                    (EpiDataBurial) burial,
                                    new Consumer() {
                                        @Override
                                        public void accept(Object burialDialog) {
                                            binding.epiDataBurials.setValue(
                                                    ListField.updateList(
                                                            binding.epiDataBurials.getValue(),
                                                            (EpiDataBurial) burialDialog
                                                    )
                                            );
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
                                        }
                                    },
                                    getActivity().getResources().getString(R.string.headline_burial)
                            );
                            burialTab.show(getFragmentManager(), "epidata_burial_edit_fragment");
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
                            if (gathering == null) {
                                try {
                                    gathering = DataUtils.createNew(EpiDataGathering.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            EpiDataGatheringTab gatheringTab = new EpiDataGatheringTab();
                            gatheringTab.initialize(
                                    (EpiDataGathering) gathering,
                                    new Consumer() {
                                        @Override
                                        public void accept(Object gatheringDialog) {
                                            binding.epiDataGatherings.setValue(
                                                    ListField.updateList(
                                                            binding.epiDataGatherings.getValue(),
                                                            (EpiDataGathering) gatheringDialog
                                                    )
                                            );
                                        }
                                    }, new Consumer() {
                                        @Override
                                        public void accept(Object gatheringDialog) {
                                            binding.epiDataGatherings.setValue(
                                                    ListField.removeFromList(
                                                            binding.epiDataGatherings.getValue(),
                                                            (EpiDataGathering) gatheringDialog
                                                    )
                                            );
                                        }
                                    },
                                    getActivity().getResources().getString(R.string.headline_gathering)
                            );
                            gatheringTab.show(getFragmentManager(), "epidata_gathering_edit_fragment");
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
                            if (travel == null) {
                                try {
                                    travel = DataUtils.createNew(EpiDataTravel.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            EpiDataTravelTab travelTab = new EpiDataTravelTab();
                            travelTab.initialize(
                                    (EpiDataTravel) travel,
                                    new Consumer() {
                                        @Override
                                        public void accept(Object travelDialog) {
                                            binding.epiDataTravels.setValue(
                                                    ListField.updateList(
                                                            binding.epiDataTravels.getValue(),
                                                            (EpiDataTravel) travelDialog
                                                    )
                                            );
                                        }
                                    }, new Consumer() {
                                        @Override
                                        public void accept(Object travelDialog) {
                                            binding.epiDataTravels.setValue(
                                                    ListField.removeFromList(
                                                            binding.epiDataTravels.getValue(),
                                                            (EpiDataTravel) travelDialog
                                                    )
                                            );
                                        }
                                    },
                                    getActivity().getResources().getString(R.string.headline_travel)
                            );
                            travelTab.show(getFragmentManager(), "epidata_travel_edit_fragment");
                        }
                    }
            );

            binding.epiData4poultryDate.initialize(this);
            binding.epiData3wildbirdsDate.initialize(this);
            FieldHelper.initSpinnerField(binding.epiDataWaterSource, WaterSource.class);

            binding.epiDataBurialAttended.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiDataBurials.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiDataGatheringAttended.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiDataGatherings.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiDataTraveled.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiDataTravels.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiDataPoultry.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiData1poultryDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiData2poultrySick.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiData3poultrySickDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                    binding.epiData4poultryDate.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                    binding.epiData5poultryLocation.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiDataOtherAnimals.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiData1otherAnimalsDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiDataWildbirds.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiData2wildbirdsDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                    binding.epiData3wildbirdsDate.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                    binding.epiData4wildbirdsLocation.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiDataWaterSource.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiData5waterSourceOther.setVisibility(field.getValue() == WaterSource.OTHER ? View.VISIBLE : View.GONE);
                }
            });

            binding.epiDataWaterBody.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    binding.epiData6waterBodyDetails.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                }
            });

            visibilityDisease(disease);

        } catch(Exception e) {
            e.printStackTrace();;
        }
    }

    private void visibilityDisease(Disease disease) {
        for (int i=0; i<binding.caseEpidataForm.getChildCount(); i++){
            View child = binding.caseEpidataForm.getChildAt(i);
            if (child instanceof PropertyField) {
                String propertyId = cropNumbers(((PropertyField)child).getPropertyId());
                boolean definedOrMissing = Diseases.DiseasesConfiguration.isDefinedOrMissing(EpiDataDto.class, propertyId, disease);
                child.setVisibility(definedOrMissing ? View.VISIBLE : View.GONE);
            }
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

    @Override
    public AbstractDomainObject getData() {
        return binding.getEpiData();
    }
}
