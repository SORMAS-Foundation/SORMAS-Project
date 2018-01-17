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

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractSubNavigationView {

	private CaseReferenceDto caseRef;
	
	protected AbstractCaseView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, OptionGroup viewModeToggle, String params) {
		
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
		
		if (FacadeProvider.getOutbreakFacade().hasOutbreak(caze.getDisease(), caze.getDistrict())) {
			viewModeToggle.setVisible(true);
		}
		
		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(params);
		infoLabelSub.setValue(caseDto.getDisease() != Disease.OTHER
				? DataHelper.toStringNullable(caseDto.getDisease())
				: DataHelper.toStringNullable(caseDto.getDiseaseDetails()));
    }

	public CaseReferenceDto getCaseRef() {
		return caseRef;
	}

}
