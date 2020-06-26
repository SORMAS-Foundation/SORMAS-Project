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

package de.symeda.sormas.app.caze.edit;

import java.util.Arrays;
import java.util.List;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableArrayList;

import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.FieldHelper;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class CaseEditEpidemiologicalDataFragment extends BaseEditFragment<FragmentCaseEditEpidLayoutBinding, EpiData, Case> {

	public static final String TAG = CaseEditEpidemiologicalDataFragment.class.getSimpleName();

	private EpiData record;
	private Disease disease;

	private IEntryItemOnClickListener onGatheringItemClickListener;
	private IEntryItemOnClickListener onTravelItemClickListener;
	private IEntryItemOnClickListener onBurialItemClickListener;

	private List<Item> drinkingWaterSourceList;
	private List<Item> animalConditionList;

	// Static methods

	public static CaseEditEpidemiologicalDataFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditEpidemiologicalDataFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			null);
	}

	// Instance methods

	private void setUpControlListeners(final FragmentCaseEditEpidLayoutBinding contentBinding) {
		onGatheringItemClickListener = (v, item) -> {
			final EpiDataGathering gathering = (EpiDataGathering) item;
			final EpiDataGathering gatheringClone = (EpiDataGathering) gathering.clone();
			final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gatheringClone);

			dialog.setPositiveCallback(() -> {
				record.getGatherings().set(record.getGatherings().indexOf(gathering), gatheringClone);
				updateGatherings();
			});

			dialog.setDeleteCallback(() -> removeGathering(gathering));

			dialog.show();
		};

		onTravelItemClickListener = (v, item) -> {
			final EpiDataTravel travel = (EpiDataTravel) item;
			final EpiDataTravel travelClone = (EpiDataTravel) travel.clone();
			final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travelClone);

			dialog.setPositiveCallback(() -> {
				record.getTravels().set(record.getTravels().indexOf(travel), travelClone);
				updateTravels();
			});

			dialog.setDeleteCallback(() -> removeTravel(travel));

			dialog.show();
		};

		onBurialItemClickListener = (v, item) -> {
			final EpiDataBurial burial = (EpiDataBurial) item;
			final EpiDataBurial burialClone = (EpiDataBurial) burial.clone();
			final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burialClone);

			dialog.setPositiveCallback(() -> {
				record.getBurials().set(record.getBurials().indexOf(burial), burialClone);
				updateBurials();
			});

			dialog.setDeleteCallback(() -> removeBurial(burial));

			dialog.show();
		};

		contentBinding.btnAddGathering.setOnClickListener(v -> {
			final EpiDataGathering gathering = DatabaseHelper.getEpiDataGatheringDao().build();
			final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);

			dialog.setPositiveCallback(() -> addGathering(gathering));

			dialog.setDeleteCallback(() -> removeGathering(gathering));

			dialog.show();
		});

		contentBinding.btnAddTravel.setOnClickListener(v -> {
			final EpiDataTravel travel = DatabaseHelper.getEpiDataTravelDao().build();
			final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);

			dialog.setPositiveCallback(() -> addTravel(travel));

			dialog.setDeleteCallback(() -> removeTravel(travel));

			dialog.show();
		});

		contentBinding.btnAddBurial.setOnClickListener(v -> {
			final EpiDataBurial burial = DatabaseHelper.getEpiDataBurialDao().build();
			final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);

			dialog.setPositiveCallback(() -> addBurial(burial));

			dialog.setDeleteCallback(() -> removeBurial(burial));

			dialog.show();
		});
	}

	private ObservableArrayList<EpiDataGathering> getGatherings() {
		ObservableArrayList<EpiDataGathering> newGatherings = new ObservableArrayList<>();
		newGatherings.addAll(record.getGatherings());
		return newGatherings;
	}

	private void clearGatherings() {
		record.getGatherings().clear();
		updateGatherings();
	}

	private void removeGathering(EpiDataGathering item) {
		record.getGatherings().remove(item);
		updateGatherings();
	}

	private void updateGatherings() {
		getContentBinding().setGatheringList(getGatherings());
		verifyGatheringStatus();
	}

	private void addGathering(EpiDataGathering item) {
		record.getGatherings().add(0, item);
		updateGatherings();
	}

	private void verifyGatheringStatus() {
		YesNoUnknown gatheringAttended = record.getGatheringAttended();
		if (gatheringAttended == YesNoUnknown.YES && getGatherings().size() <= 0) {
			getContentBinding().epiDataGatheringAttended.enableWarningState(R.string.validation_soft_add_list_entry);
		} else {
			getContentBinding().epiDataGatheringAttended.disableWarningState();
		}

		getContentBinding().epiDataGatheringAttended.setEnabled(getGatherings().size() == 0);
	}

	private ObservableArrayList<EpiDataBurial> getBurials() {
		ObservableArrayList<EpiDataBurial> newBurials = new ObservableArrayList<>();
		newBurials.addAll(record.getBurials());
		return newBurials;
	}

	private void clearBurials() {
		record.getBurials().clear();
		updateBurials();
	}

	private void removeBurial(EpiDataBurial item) {
		record.getBurials().remove(item);
		updateBurials();
	}

	private void updateBurials() {
		getContentBinding().setBurialList(getBurials());
		verifyBurialStatus();
	}

	private void addBurial(EpiDataBurial item) {
		record.getBurials().add(0, item);
		updateBurials();
	}

	private void verifyBurialStatus() {
		YesNoUnknown burialAttended = record.getBurialAttended();
		if (burialAttended == YesNoUnknown.YES && getBurials().size() <= 0) {
			getContentBinding().epiDataBurialAttended.enableWarningState(R.string.validation_soft_add_list_entry);
		} else {
			getContentBinding().epiDataBurialAttended.disableWarningState();
		}

		getContentBinding().epiDataBurialAttended.setEnabled(getBurials().size() == 0);
	}

	private ObservableArrayList<EpiDataTravel> getTravels() {
		ObservableArrayList<EpiDataTravel> newTravels = new ObservableArrayList<>();
		newTravels.addAll(record.getTravels());
		return newTravels;
	}

	private void clearTravels() {
		record.getTravels().clear();
		updateTravels();
	}

	private void removeTravel(EpiDataTravel item) {
		record.getTravels().remove(item);
		updateTravels();
	}

	private void updateTravels() {
		getContentBinding().setTravelList(getTravels());
		verifyTravelStatus();
	}

	private void addTravel(EpiDataTravel item) {
		record.getTravels().add(0, item);
		updateTravels();
	}

	private void verifyTravelStatus() {
		YesNoUnknown traveled = record.getTraveled();
		if (traveled == YesNoUnknown.YES && getTravels().size() <= 0) {
			getContentBinding().epiDataTraveled.enableWarningState(R.string.validation_soft_add_list_entry);
		} else {
			getContentBinding().epiDataTraveled.disableWarningState();
		}

		getContentBinding().epiDataTraveled.setEnabled(getTravels().size() == 0);
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_epidemiological_data);
	}

	@Override
	public EpiData getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		Case caze = getActivityRootData();
		disease = caze.getDisease();
		record = caze.getEpiData();

		drinkingWaterSourceList = DataUtils.getEnumItems(WaterSource.class, true);
		animalConditionList = DataUtils.getEnumItems(AnimalCondition.class, true);
	}

	@Override
	public void onLayoutBinding(final FragmentCaseEditEpidLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		setDefaultValues(record);

		contentBinding.setData(record);
		contentBinding.setWaterSourceClass(WaterSource.class);
		contentBinding.setVaccinationClass(Vaccination.class);
		contentBinding.setGatheringList(getGatherings());
		contentBinding.setTravelList(getTravels());
		contentBinding.setBurialList(getBurials());
		contentBinding.setGatheringItemClickCallback(onGatheringItemClickListener);
		contentBinding.setTravelItemClickCallback(onTravelItemClickListener);
		contentBinding.setBurialItemClickCallback(onBurialItemClickListener);

		contentBinding.epiDataBurialAttended.addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				YesNoUnknown value = (YesNoUnknown) field.getValue();
				contentBinding.burialsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
				if (value != YesNoUnknown.YES) {
					clearBurials();
				}

				verifyBurialStatus();
			}
		});

		contentBinding.epiDataGatheringAttended.addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				YesNoUnknown value = (YesNoUnknown) field.getValue();
				contentBinding.gatheringsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
				if (value != YesNoUnknown.YES) {
					clearGatherings();
				}

				verifyGatheringStatus();
			}
		});

		contentBinding.epiDataTraveled.addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				YesNoUnknown value = (YesNoUnknown) field.getValue();
				contentBinding.travelsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
				if (value != YesNoUnknown.YES) {
					clearTravels();
				}

				verifyTravelStatus();
			}
		});

		// iterate through all epi data animal fields and add listener
		ValueChangeListener updateHadAnimalExposureListener = field -> updateHadAnimalExposure();
		List<String> animalExposureProperties = Arrays.asList(EpiDataDto.ANIMAL_EXPOSURE_PROPERTIES);
		FieldHelper.iteratePropertyFields((ViewGroup) contentBinding.getRoot(), field -> {
			if (animalExposureProperties.contains(field.getSubPropertyId())) {
				field.addValueChangedListener(updateHadAnimalExposureListener);
			}
			return true;
		});

		List<String> environmentalExposureProperties = Arrays.asList(EpiData.ENVIRONMENTAL_EXPOSURE_PROPERTIES);
		int environmentalExposureHeadingVisibiliy = View.GONE;
		for (String property : environmentalExposureProperties) {
			if (Diseases.DiseasesConfiguration.isDefinedOrMissing(EpiDataDto.class, property, disease)) {
				environmentalExposureHeadingVisibiliy = View.VISIBLE;
				break;
			}
		}
		contentBinding.headingEnvironmentalExposure.setVisibility(environmentalExposureHeadingVisibiliy);
	}

	public void setDefaultValues(EpiData epiDataDto) {
		if (epiDataDto == null) {
			return;
		}
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(EpiData.class, AbstractDomainObject.class).getPropertyDescriptors()) {
				if (pd.getWriteMethod() != null && (pd.getReadMethod().getReturnType().equals(YesNoUnknown.class))) {
					try {
						if (pd.getReadMethod().invoke(epiDataDto) == null)
							pd.getWriteMethod().invoke(epiDataDto, YesNoUnknown.NO);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void updateHadAnimalExposure() {
		// iterate through all epi data animal fields to get value
		List<String> animalExposureProperties = Arrays.asList(EpiDataDto.ANIMAL_EXPOSURE_PROPERTIES);
		boolean iterationCancelled = !FieldHelper.iteratePropertyFields((ViewGroup) getContentBinding().getRoot(), field -> {
			if (animalExposureProperties.contains(field.getSubPropertyId())) {
				YesNoUnknown value = (YesNoUnknown) field.getValue();
				if (YesNoUnknown.YES.equals(value)) {
					return false;
				}
			}
			return true;
		});
		boolean hadAnimalExposure = iterationCancelled;
		getContentBinding().setAnimalExposureDependentVisibility(hadAnimalExposure ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onAfterLayoutBinding(FragmentCaseEditEpidLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(EpiDataDto.class, contentBinding.mainContent);

		// Initialize ControlSpinnerFields
		contentBinding.epiDataWaterSource.initializeSpinner(drinkingWaterSourceList);
		contentBinding.epiDataAnimalCondition.initializeSpinner(animalConditionList);

		// Initialize ControlDateFields
		contentBinding.epiDataDateOfLastExposure.initializeDateField(getFragmentManager());
		contentBinding.epiDataSickDeadAnimalsDate.initializeDateField(getFragmentManager());
		contentBinding.epiDataDateOfProphylaxis.initializeDateField(getFragmentManager());

		verifyBurialStatus();
		verifyGatheringStatus();
		verifyTravelStatus();

		if (DiseaseConfigurationCache.getInstance().getFollowUpDuration(getActivityRootData().getDisease()) > 0) {
			contentBinding.epiDataTraveled.setCaption(
				String.format(
					I18nProperties.getCaption(Captions.epiDataTraveledIncubationPeriod),
					DiseaseConfigurationCache.getInstance().getFollowUpDuration(getActivityRootData().getDisease())));
		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_epid_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}
}
