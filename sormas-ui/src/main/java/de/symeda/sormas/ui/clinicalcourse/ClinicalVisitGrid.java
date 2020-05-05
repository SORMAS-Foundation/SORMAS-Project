package de.symeda.sormas.ui.clinicalcourse;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionModel.HasUserSelectionAllowed;
import com.vaadin.v7.ui.renderers.DateRenderer;
import com.vaadin.v7.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.V7AbstractGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class ClinicalVisitGrid extends Grid implements V7AbstractGrid<ClinicalVisitCriteria> {

	private static final String EDIT_BTN_ID = "edit";

	private ClinicalVisitCriteria clinicalVisitCriteria = new ClinicalVisitCriteria();

	public ClinicalVisitGrid(CaseReferenceDto caseRef) {
		setSizeFull();

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		BeanItemContainer<ClinicalVisitIndexDto> container = new BeanItemContainer<>(ClinicalVisitIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addIconColumn(generatedContainer, EDIT_BTN_ID, VaadinIcons.EDIT);
		setContainerDataSource(generatedContainer);

		setColumns(EDIT_BTN_ID, ClinicalVisitIndexDto.VISIT_DATE_TIME, ClinicalVisitIndexDto.VISITING_PERSON, ClinicalVisitIndexDto.TEMPERATURE,
				ClinicalVisitIndexDto.BLOOD_PRESSURE, ClinicalVisitIndexDto.HEART_RATE, ClinicalVisitIndexDto.VISIT_REMARKS);
		getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
		getColumn(EDIT_BTN_ID).setWidth(20);
		getColumn(EDIT_BTN_ID).setHeaderCaption("");

		Language userLanguage = I18nProperties.getUserLanguage();
		getColumn(ClinicalVisitIndexDto.VISIT_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
					ClinicalVisitIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		addItemClickListener(e -> {
			if (EDIT_BTN_ID.equals(e.getPropertyId()) || e.isDoubleClick()) {
				ControllerProvider.getClinicalCourseController().openClinicalVisitEditForm((ClinicalVisitIndexDto) e.getItemId(), caseRef.getUuid(), this::reload);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public BeanItemContainer<ClinicalVisitIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<ClinicalVisitIndexDto>) container.getWrappedContainer();
	}

	public void reload() {
		if (getSelectionModel() instanceof HasUserSelectionAllowed) {
			deselectAll();
		}

		List<ClinicalVisitIndexDto> entries = FacadeProvider.getClinicalVisitFacade().getIndexList(clinicalVisitCriteria);

		getContainer().removeAllItems();
		getContainer().addAll(entries);
	}

	@Override
	public void setCriteria(ClinicalVisitCriteria clinicalVisitCriteria) {
		this.clinicalVisitCriteria = clinicalVisitCriteria;
	}

	@Override
	public ClinicalVisitCriteria getCriteria() {
		return clinicalVisitCriteria;
	}

}
