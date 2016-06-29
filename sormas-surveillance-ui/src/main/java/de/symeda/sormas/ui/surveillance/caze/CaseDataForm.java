package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseDataForm extends AbstractEditForm<CaseDataDto> {

    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Case data")+
			LayoutUtil.fluidRow(
				LayoutUtil.fluidColumn(8, 0, LayoutUtil.div(
					LayoutUtil.fluidRowLocs(CaseDataDto.UUID, CaseDataDto.CASE_STATUS),
					LayoutUtil.fluidRowLocs(CaseDataDto.REPORTER, CaseDataDto.REPORT_DATE),
					LayoutUtil.fluidRowLocs(CaseDataDto.DISEASE, CaseDataDto.HEALTH_FACILITY)
					)),
				LayoutUtil.fluidColumn(4, 0, LayoutUtil.div(
						""
					))
			);

    public CaseDataForm() {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(CaseDataDto.UUID, TextField.class);
    	addField(CaseDataDto.CASE_STATUS, NativeSelect.class);
    	TextField reporter = addField(CaseDataDto.REPORTER, TextField.class);
    	reporter.setNullRepresentation("");
    	addField(CaseDataDto.REPORT_DATE, DateField.class);
    	addField(CaseDataDto.DISEASE, NativeSelect.class);
    	
    	// TODO use only facilities from own region or district?!
    	addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class)
			.addItems(FacadeProvider.getFacilityFacade().getAllAsReference());
    	
    	setReadOnly(true, CaseDataDto.UUID, CaseDataDto.CASE_STATUS, CaseDataDto.REPORTER,
    			CaseDataDto.REPORT_DATE, CaseDataDto.DISEASE);
	}
    
	@Override
	protected void setLayout() {
		 setTemplateContents(HTML_LAYOUT);
	}

}
