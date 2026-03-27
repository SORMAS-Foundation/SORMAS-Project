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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.components.DeletionComponent;
import de.symeda.sormas.ui.samples.components.DiseaseSelectionComponent;
import de.symeda.sormas.ui.samples.components.DiseaseVariantComponent;
import de.symeda.sormas.ui.samples.components.FourFoldCtCqComponent;
import de.symeda.sormas.ui.samples.components.PrescriberComponent;
import de.symeda.sormas.ui.samples.components.ResultTextComponent;
import de.symeda.sormas.ui.samples.components.TestIdentificationComponent;
import de.symeda.sormas.ui.samples.components.TestMethodComponent;
import de.symeda.sormas.ui.samples.components.TestResultComponent;
import de.symeda.sormas.ui.samples.diseasesection.AbstractDiseaseSectionComponent;
import de.symeda.sormas.ui.samples.diseasesection.DiseaseSectionFactory;
import de.symeda.sormas.ui.samples.diseasesection.PathogenTestFormConfig;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Component-based PathogenTestForm using Vaadin 8 components.
 * <p>
 * Extends {@link AbstractEditForm} for compatibility with
 * {@link de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent} and
 * {@link de.symeda.sormas.ui.externalmessage.CorrectionPanel}.
 * <p>
 * Each component owns its own Vaadin 8 Binder and manages its own field
 * visibility via value change listeners (no FieldHelper.setVisibleWhen).
 * The FieldGroup from AbstractEditForm is kept hollow — only DrugSusceptibilityForm
 * (in disease sections) binds to it for legacy compatibility.
 */
public class PathogenTestForm extends AbstractEditForm<PathogenTestDto> {

	private static final long serialVersionUID = -1218707278398543154L;

	private static final String COMPONENT_CONTAINER_LOC = "componentContainerLoc";
	private static final String HTML_LAYOUT = loc(COMPONENT_CONTAINER_LOC);

	private SampleDto sample;
	private EnvironmentSampleDto environmentSample;
	private AbstractSampleForm sampleForm;
	private final int caseSampleCount;
	private final boolean create;
	private Disease disease;
	private PathogenTestFormConfig formConfig;

	private final FormEventBus eventBus = new FormEventBus();
	private final List<FormComponent<PathogenTestDto>> formComponents = new ArrayList<>();
	private final List<Registration> eventRegistrations = new ArrayList<>();

	private Label headingLabel;
	private TestMethodComponent testMethodComponent;
	private TestResultComponent testResultComponent;
	private DiseaseVariantComponent diseaseVariantComponent;

	private AbstractDiseaseSectionComponent activeSection;
	private VerticalLayout diseaseSectionSlot;

	public PathogenTestForm(
		AbstractSampleForm sampleForm,
		boolean create,
		int caseSampleCount,
		boolean isPseudonymized,
		boolean inJurisdiction,
		Disease disease) {

		this(create, caseSampleCount, isPseudonymized, inJurisdiction, disease);
		this.sampleForm = sampleForm;
		this.disease = disease;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(SampleDto sample, boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {
		this(create, caseSampleCount, isPseudonymized, inJurisdiction, disease);
		this.sample = sample;
		this.disease = disease;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	public PathogenTestForm(EnvironmentSampleDto sample, boolean create, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {
		this(create, 0, isPseudonymized, inJurisdiction, disease);
		this.environmentSample = sample;
		addFields();
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	private PathogenTestForm(boolean create, int caseSampleCount, boolean isPseudonymized, boolean inJurisdiction, Disease disease) {
		super(
			PathogenTestDto.class,
			PathogenTestDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(create || inJurisdiction, !create && isPseudonymized));

		this.caseSampleCount = caseSampleCount;
		this.create = create;
		this.disease = disease;
		setWidth(900, Unit.PIXELS);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		formConfig = PathogenTestFormConfig.fromCurrentConfig();

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(false);
		container.setSpacing(true);
		getContent().addComponent(container, COMPONENT_CONTAINER_LOC);

		headingLabel = new Label();
		headingLabel.addStyleName(H3);
		container.addComponent(headingLabel);

		TestIdentificationComponent identificationComponent = new TestIdentificationComponent(eventBus);
		formComponents.add(identificationComponent);
		container.addComponent(identificationComponent);

		DiseaseSelectionComponent diseaseSelectionComponent = new DiseaseSelectionComponent(eventBus, disease, create, environmentSample != null);
		formComponents.add(diseaseSelectionComponent);
		container.addComponent(diseaseSelectionComponent);

		testMethodComponent = new TestMethodComponent(eventBus, this::getSampleDate, disease);
		formComponents.add(testMethodComponent);
		container.addComponent(testMethodComponent);

		testResultComponent = new TestResultComponent(eventBus, formConfig.isLuxembourg, disease);
		formComponents.add(testResultComponent);
		container.addComponent(testResultComponent);

		diseaseVariantComponent = new DiseaseVariantComponent(eventBus, disease);
		formComponents.add(diseaseVariantComponent);
		container.addComponent(diseaseVariantComponent);

		FourFoldCtCqComponent fourFoldCtCqComponent = new FourFoldCtCqComponent(eventBus, caseSampleCount, formConfig.isLuxembourg, disease);
		formComponents.add(fourFoldCtCqComponent);
		container.addComponent(fourFoldCtCqComponent);

		diseaseSectionSlot = new VerticalLayout();
		diseaseSectionSlot.setWidth(100, Unit.PERCENTAGE);
		diseaseSectionSlot.setMargin(false);
		diseaseSectionSlot.setSpacing(false);
		container.addComponent(diseaseSectionSlot);

		activeSection = DiseaseSectionFactory.forDisease(disease);
		activeSection.initialize(getFieldGroup(), eventBus, formConfig, disease);
		activeSection.setVisibilityCallback(visible -> diseaseSectionSlot.setVisible(visible));
		diseaseSectionSlot.addComponent(activeSection);

		ResultTextComponent resultTextComponent = new ResultTextComponent();
		formComponents.add(resultTextComponent);
		container.addComponent(resultTextComponent);

		PrescriberComponent prescriberComponent = new PrescriberComponent();
		formComponents.add(prescriberComponent);
		container.addComponent(prescriberComponent);

		DeletionComponent deletionComponent = new DeletionComponent();
		formComponents.add(deletionComponent);
		container.addComponent(deletionComponent);

		wireEvents();

		finalizeForm();
	}

	private void wireEvents() {
		// Disease change → swap section + clear test type
		eventRegistrations.add(eventBus.on(DiseaseChangedEvent.class, event -> {
			Disease newDisease = event.getDisease();
			if (newDisease != disease) {
				testMethodComponent.getTestTypeField().clear();
			}
			disease = newDisease;
			swapDiseaseSection(newDisease);
		}));

		// Disease variant → auto-set test result
		eventRegistrations.add(diseaseVariantComponent.getDiseaseVariantField().addValueChangeListener(e -> {
			DiseaseVariant variant = e.getValue();
			if (variant != null) {
				testResultComponent.getTestResultField().setValue(PathogenTestResultType.POSITIVE);
			} else {
				testResultComponent.getTestResultField().clear();
			}
		}));
	}

	private void finalizeForm() {
		testMethodComponent.setLabRequired(SamplePurpose.INTERNAL.equals(getSamplePurpose()));

		initializeAccessAndAllowedAccesses();
		initializeVisibilitiesAndAllowedVisibilities();

		for (FormComponent<PathogenTestDto> comp : formComponents) {
			if (fieldVisibilityCheckers != null) {
				comp.applyVisibility(fieldVisibilityCheckers, PathogenTestDto.class);
			}
			if (fieldAccessCheckers != null) {
				comp.applyAccess(fieldAccessCheckers, PathogenTestDto.class);
			}
		}

		if (fieldVisibilityCheckers != null) {
			activeSection.applyVisibility(fieldVisibilityCheckers, PathogenTestDto.class);
		}

		if (fieldAccessCheckers != null) {
			activeSection.applyAccess(fieldAccessCheckers, PathogenTestDto.class);
		}

	}

	private void swapDiseaseSection(Disease newDisease) {
		AbstractDiseaseSectionComponent newSection = DiseaseSectionFactory.forDisease(newDisease);
		if (newSection.getClass() == activeSection.getClass()) {
			return;
		}

		activeSection.cleanup();
		diseaseSectionSlot.removeAllComponents();

		activeSection = newSection;
		activeSection.initialize(getFieldGroup(), eventBus, formConfig, newDisease);
		activeSection.setVisibilityCallback(visible -> diseaseSectionSlot.setVisible(visible));
		diseaseSectionSlot.addComponent(activeSection);

		PathogenTestDto dto = getValue();
		if (dto != null) {
			activeSection.setDto(dto);
		}

		if (fieldVisibilityCheckers != null) {
			activeSection.applyVisibility(fieldVisibilityCheckers, PathogenTestDto.class);
		}

		if (fieldAccessCheckers != null) {
			activeSection.applyAccess(fieldAccessCheckers, PathogenTestDto.class);
		}
	}

	private Date getSampleDate() {
		if (sample != null) {
			return sample.getSampleDateTime();
		}
		if (sampleForm != null) {
			return (Date) sampleForm.getField(SampleDto.SAMPLE_DATE_TIME).getValue();
		}
		if (environmentSample != null) {
			return environmentSample.getSampleDateTime();
		}
		return null;
	}

	private SamplePurpose getSamplePurpose() {
		if (sample != null) {
			return sample.getSamplePurpose();
		}
		if (sampleForm != null) {
			return (SamplePurpose) sampleForm.getField(SampleDto.SAMPLE_PURPOSE).getValue();
		}
		return null;
	}

	@Override
	public void preCommit(CommitEvent commitEvent) throws CommitException {
		super.preCommit(commitEvent);

		List<InvalidValueException> allCauses = new ArrayList<>();

		for (FormComponent<PathogenTestDto> comp : formComponents) {
			try {
				comp.validate();
			} catch (InvalidValueException e) {
				collectCauses(e, allCauses);
			}
		}

		if (activeSection != null) {
			try {
				activeSection.validate();
			} catch (InvalidValueException e) {
				collectCauses(e, allCauses);
			}
		}

		if (!allCauses.isEmpty()) {
			String joinedCaptions = allCauses.stream().map(InvalidValueException::getMessage).collect(Collectors.joining(", "));
			throw new InvalidValueException(joinedCaptions, allCauses.toArray(new InvalidValueException[0]));
		}
	}

	private static void collectCauses(InvalidValueException e, List<InvalidValueException> target) {
		if (e.getCauses().length > 0) {
			Collections.addAll(target, e.getCauses());
		} else {
			target.add(e);
		}
	}

	@Override
	public void setHeading(String heading) {
		headingLabel.setValue(heading);
	}

	@Override
	public void setValue(PathogenTestDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		for (FormComponent<PathogenTestDto> comp : formComponents) {
			comp.setDto(newFieldValue);
		}

		if (activeSection != null) {
			activeSection.setDto(newFieldValue);
		}

		markAsDirty();
	}

	@Override
	public void detach() {
		for (Registration reg : eventRegistrations) {
			reg.remove();
		}
		eventRegistrations.clear();

		super.detach();
	}

	@Override
	public void forEachComponent(java.util.function.Consumer<Component> componentConsumer) {
		Component c = getContent().getComponent(COMPONENT_CONTAINER_LOC);
		if (c instanceof VerticalLayout) {
			VerticalLayout vl = (VerticalLayout) c;
			for (int i = 0; i < vl.getComponentCount(); i++) {
				componentConsumer.accept(vl.getComponent(i));
			}
		}
	}
}
