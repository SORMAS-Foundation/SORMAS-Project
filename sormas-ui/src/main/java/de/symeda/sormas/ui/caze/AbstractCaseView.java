package de.symeda.sormas.ui.caze;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.epidata.EpiDataView;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractSubNavigationView {

	public static final String VIEW_MODE_URL_PREFIX = "v";
	
	private CaseReferenceDto caseRef = null;

	private ViewMode viewMode;
    private final OptionGroup viewModeToggle;
    private final Property.ValueChangeListener viewModeToggleListener;

	protected AbstractCaseView(String viewName) {
		super(viewName);        
		
		viewModeToggle = new OptionGroup();
        CssStyles.style(viewModeToggle, 
        		ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY,
        		CssStyles.VSPACE_TOP_3);
        viewModeToggle.addItems((Object[]) ViewMode.values());
        viewModeToggle.setItemCaption(ViewMode.SIMPLE, I18nProperties.getEnumCaption(ViewMode.SIMPLE));
        viewModeToggle.setItemCaption(ViewMode.FULL, I18nProperties.getEnumCaption(ViewMode.FULL));
        viewModeToggle.setValue(ViewMode.SIMPLE);
        // View mode toggle is hidden by default
        viewModeToggle.setVisible(false);
        addHeaderComponent(viewModeToggle);     
        
        viewModeToggleListener = new ValueChangeListener() {
    		
    		@Override
    		public void valueChange(ValueChangeEvent event) {
    	   		viewMode = (ViewMode)event.getProperty().getValue();
   	   			reloadView();
    		}
    	};        
        viewModeToggle.addValueChangeListener(viewModeToggleListener);
	}
	
	public void reloadView() {
   		String navigationState = AbstractCaseView.this.viewName + "/" + getCaseRef().getUuid();
   		if (viewMode == ViewMode.FULL) {
   			// pass full view mode as param so it's also used for other views when switching
   			navigationState	+= "/" + VIEW_MODE_URL_PREFIX + "=" + viewMode.toString();
   		}
   		SormasUI.get().getNavigator().navigateTo(navigationState);	
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		
		String[] passedParams = params.split("/");
		if (passedParams.length > 0) {
			caseRef = FacadeProvider.getCaseFacade().getReferenceByUuid(passedParams[0]);
		}
		
		if (caseRef == null) {
			ControllerProvider.getCaseController().navigateToIndex();
			return;
		}
		
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		
		// outbreak?
		if (FacadeProvider.getOutbreakFacade().hasOutbreak(caze.getDistrict(), caze.getDisease())) {
			viewMode = ViewMode.SIMPLE;
			// param might change this
			if (passedParams.length > 1 && passedParams[1].startsWith(VIEW_MODE_URL_PREFIX + "=")) {
				String viewModeString = passedParams[1].substring(2);
				try {
					viewMode = ViewMode.valueOf(viewModeString.toUpperCase());
				} catch (IllegalArgumentException ex) { } // just ignore
			}
			
			viewModeToggle.removeValueChangeListener(viewModeToggleListener);
			viewModeToggle.setValue(viewMode);
	        viewModeToggle.addValueChangeListener(viewModeToggleListener);
			viewModeToggle.setVisible(true);
		} else {
			viewMode = ViewMode.FULL;
			viewModeToggle.setVisible(false);
		}
		
		menu.removeAllViews();
		menu.addView(CasesView.VIEW_NAME, "Cases list");
		menu.addView(CaseDataView.VIEW_NAME, I18nProperties.getFieldCaption(CaseDataDto.I18N_PREFIX), params);
		if (viewMode != ViewMode.SIMPLE) {
			menu.addView(CasePersonView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON), params);
			menu.addView(CaseHospitalizationView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HOSPITALIZATION), params);
			menu.addView(CaseSymptomsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SYMPTOMS), params);
			menu.addView(EpiDataView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EPI_DATA), params);
		}
		if (DiseaseHelper.hasContactFollowUp(caze.getDisease(), caze.getPlagueType())) {
			menu.addView(CaseContactsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, "contacts"), params);
		}
		infoLabel.setValue(caseRef.getCaption());
		
		infoLabelSub.setValue(caze.getDisease() != Disease.OTHER
				? DataHelper.toStringNullable(caze.getDisease())
				: DataHelper.toStringNullable(caze.getDiseaseDetails()));
    }

	public CaseReferenceDto getCaseRef() {
		return caseRef;
	}
	

	public ViewMode getViewMode() {
		return viewMode;
	}
}
