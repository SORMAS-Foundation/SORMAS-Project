package de.symeda.sormas.ui.caze;

import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.epidata.EpiDataView;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractSubNavigationView {

	private CaseReferenceDto caseRef;
	private ViewMode viewMode;
	private OptionGroup viewModeToggle;
	
	protected AbstractCaseView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, OptionGroup viewModeToggle, String params) {
		
		if (this.viewModeToggle == null) {
			this.viewModeToggle = viewModeToggle;
		}
		
		caseRef = FacadeProvider.getCaseFacade().getReferenceByUuid(params);
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		
		menu.removeAllViews();
		menu.addView(CasesView.VIEW_NAME, "Cases list");
		menu.addView(CaseDataView.VIEW_NAME, I18nProperties.getFieldCaption(CaseDataDto.I18N_PREFIX), params);
		menu.addView(CasePersonView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON), params);
		menu.addView(CaseHospitalizationView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, "hospitalization"), params);
		menu.addView(CaseSymptomsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SYMPTOMS), params);
		menu.addView(EpiDataView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, "epiData"), params);
		if (DiseaseHelper.hasContactFollowUp(caze)) {
			menu.addView(CaseContactsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, "contacts"), params);
		}
		infoLabel.setValue(caseRef.getCaption());
		
		if (FacadeProvider.getOutbreakFacade().hasOutbreak(caze.getDistrict(), caze.getDisease())) {
			viewModeToggle.setVisible(true);
			viewMode = (ViewMode) viewModeToggle.getValue();
		}
		
		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(params);
		infoLabelSub.setValue(caseDto.getDisease() != Disease.OTHER
				? DataHelper.toStringNullable(caseDto.getDisease())
				: DataHelper.toStringNullable(caseDto.getDiseaseDetails()));
    }

	public CaseReferenceDto getCaseRef() {
		return caseRef;
	}
	
	public ViewMode getViewMode() {
		return viewMode;
	}
	
	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}
	
	public OptionGroup getViewModeToggle() {
		return viewModeToggle;
	}

}
