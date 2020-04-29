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
import com.vaadin.v7.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.*;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.GridButtonRenderer;
import de.symeda.sormas.ui.utils.V7AbstractGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class PrescriptionGrid extends Grid implements V7AbstractGrid<PrescriptionCriteria> {

	private static final String EDIT_BTN_ID = "edit";
	private static final String DOCUMENT_TREATMENT_BTN_ID = "documentTreatment";
	
	private PrescriptionCriteria prescriptionCriteria = new PrescriptionCriteria();
	
	public PrescriptionGrid(TherapyView parentView) {		
		setSizeFull();
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
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
		
		setColumns(EDIT_BTN_ID, PrescriptionIndexDto.PRESCRIPTION_TYPE, PrescriptionIndexDto.PRESCRIPTION_DATE, 
				PrescriptionIndexDto.PRESCRIPTION_PERIOD, PrescriptionIndexDto.FREQUENCY, PrescriptionIndexDto.DOSE, 
				PrescriptionIndexDto.ROUTE, PrescriptionIndexDto.PRESCRIBING_CLINICIAN, DOCUMENT_TREATMENT_BTN_ID);
		
		getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
		getColumn(EDIT_BTN_ID).setWidth(20);
		getColumn(EDIT_BTN_ID).setHeaderCaption("");
		getColumn(DOCUMENT_TREATMENT_BTN_ID).setRenderer(new GridButtonRenderer());
		getColumn(DOCUMENT_TREATMENT_BTN_ID).setHeaderCaption("");
		getColumn(PrescriptionIndexDto.PRESCRIPTION_DATE).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		getColumn(PrescriptionIndexDto.PRESCRIPTION_PERIOD).setRenderer(new PeriodRenderer());

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
					PrescriptionIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		addItemClickListener(e -> {
			if (e.getPropertyId() == null) {
				return;
			}
			
			if (DOCUMENT_TREATMENT_BTN_ID.equals(e.getPropertyId())) {
				PrescriptionDto prescription = FacadeProvider.getPrescriptionFacade().getPrescriptionByUuid(((PrescriptionIndexDto) e.getItemId()).getUuid());
				ControllerProvider.getTherapyController().openTreatmentCreateForm(prescription, new Runnable() {
					@Override
					public void run() {
						parentView.reloadTreatmentGrid();
					}
				});
			} else if (EDIT_BTN_ID.equals(e.getPropertyId()) || e.isDoubleClick()) {
				ControllerProvider.getTherapyController().openPrescriptionEditForm((PrescriptionIndexDto) e.getItemId(), this::reload, false);
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
