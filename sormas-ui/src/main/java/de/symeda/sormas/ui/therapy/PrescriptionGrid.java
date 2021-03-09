package de.symeda.sormas.ui.therapy;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionModel.HasUserSelectionAllowed;
import com.vaadin.v7.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionIndexDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessCellStyleGenerator;
import de.symeda.sormas.ui.utils.GridButtonRenderer;
import de.symeda.sormas.ui.utils.PeriodDtoConverter;
import de.symeda.sormas.ui.utils.V7AbstractGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class PrescriptionGrid extends Grid implements V7AbstractGrid<PrescriptionCriteria> {

	private static final String EDIT_BTN_ID = "edit";
	private static final String DOCUMENT_TREATMENT_BTN_ID = "documentTreatment";

	private PrescriptionCriteria prescriptionCriteria = new PrescriptionCriteria();

	public PrescriptionGrid(TherapyView parentView, boolean isPseudonymized) {

		setSizeFull();

		SormasUI ui = (SormasUI)getUI();
		if (ui.getUserProvider().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		BeanItemContainer<PrescriptionIndexDto> container = new BeanItemContainer<>(PrescriptionIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addIconColumn(generatedContainer, EDIT_BTN_ID, VaadinIcons.EDIT);
		setContainerDataSource(generatedContainer);

		generatedContainer.addGeneratedProperty(DOCUMENT_TREATMENT_BTN_ID, new PropertyValueGenerator<String>() {

			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				return I18nProperties.getPrefixCaption(TherapyDto.I18N_PREFIX, I18nProperties.getCaption(Captions.treatmentCreateTreatment));
			}

			@Override
			public Class<String> getType() {
				return String.class;
			}
		});

		setColumns(
			EDIT_BTN_ID,
			PrescriptionIndexDto.PRESCRIPTION_TYPE,
			PrescriptionIndexDto.PRESCRIPTION_DATE,
			PrescriptionIndexDto.PRESCRIPTION_PERIOD,
			PrescriptionIndexDto.FREQUENCY,
			PrescriptionIndexDto.DOSE,
			PrescriptionIndexDto.PRESCRIPTION_ROUTE,
			PrescriptionIndexDto.PRESCRIBING_CLINICIAN,
			DOCUMENT_TREATMENT_BTN_ID);

		VaadinUiUtil.setupEditColumn(getColumn(EDIT_BTN_ID));

		if (isPseudonymized) {
			getColumn(DOCUMENT_TREATMENT_BTN_ID).setRenderer(new GridButtonRenderer());
			getColumn(DOCUMENT_TREATMENT_BTN_ID).setHeaderCaption("");
		} else {
			getColumn(DOCUMENT_TREATMENT_BTN_ID).setHidden(true);
		}

		getColumn(PrescriptionIndexDto.PRESCRIPTION_DATE).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		getColumn(PrescriptionIndexDto.PRESCRIPTION_PERIOD).setConverter(new PeriodDtoConverter());

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.getPrefixCaption(PrescriptionIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));

			setCellStyleGenerator(
				FieldAccessCellStyleGenerator
					.withFieldAccessCheckers(PrescriptionIndexDto.class, UiFieldAccessCheckers.forSensitiveData(isPseudonymized)));
		}

		addItemClickListener(e -> {
			if (e.getPropertyId() == null) {
				return;
			}

			if (DOCUMENT_TREATMENT_BTN_ID.equals(e.getPropertyId())) {
				PrescriptionDto prescription =
					FacadeProvider.getPrescriptionFacade().getPrescriptionByUuid(((PrescriptionIndexDto) e.getItemId()).getUuid());
				ControllerProvider.getTherapyController().openTreatmentCreateForm(prescription,
						(Runnable) () -> parentView.reloadTreatmentGrid(),
						ui.getUserProvider().hasUserRight(UserRight.TREATMENT_CREATE));
			} else if (EDIT_BTN_ID.equals(e.getPropertyId()) || e.isDoubleClick()) {
				ControllerProvider.getTherapyController().openPrescriptionEditForm(ui, (PrescriptionIndexDto) e.getItemId(), this::reload, false);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public BeanItemContainer<PrescriptionIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<PrescriptionIndexDto>) container.getWrappedContainer();
	}

	public void reload() {
		if (getSelectionModel() instanceof HasUserSelectionAllowed) {
			deselectAll();
		}

		List<PrescriptionIndexDto> entries = FacadeProvider.getPrescriptionFacade().getIndexList(prescriptionCriteria);

		getContainer().removeAllItems();
		getContainer().addAll(entries);
	}

	@Override
	public void setCriteria(PrescriptionCriteria prescriptionCriteria) {
		this.prescriptionCriteria = prescriptionCriteria;
	}

	@Override
	public PrescriptionCriteria getCriteria() {
		return prescriptionCriteria;
	}
}
