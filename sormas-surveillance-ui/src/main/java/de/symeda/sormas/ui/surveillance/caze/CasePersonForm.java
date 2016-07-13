package de.symeda.sormas.ui.surveillance.caze;

import java.util.Date;

import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class CasePersonForm extends AbstractEditForm<CasePersonDto> {

	private static final long serialVersionUID = -1L;
    
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Patient information")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.FIRST_NAME)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.LAST_NAME)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.SEX))
					),
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.BIRTH_DATE)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.DEATH_DATE)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.APPROXIMATE_AGE))
					))+
    		LayoutUtil.h3(CssStyles.VSPACE3, "Permanent Residence")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.PHONE))
					)
				)+
    		LayoutUtil.h3(CssStyles.VSPACE3, "Occupation")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.OCCUPATION_TYPE)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.OCCUPATION_DETAILS)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CasePersonDto.OCCUPATION_FACILITY))
					)
    			);

    public CasePersonForm() {
    	super(CasePersonDto.class, CasePersonDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(CasePersonDto.FIRST_NAME, TextField.class);
    	addField(CasePersonDto.LAST_NAME, TextField.class);
    	addField(CasePersonDto.SEX, NativeSelect.class);

    	addField(CasePersonDto.BIRTH_DATE, DateField.class);
    	addField(CasePersonDto.DEATH_DATE, DateField.class);
    	addField(CasePersonDto.APPROXIMATE_AGE, TextField.class);
    	
    	addField(CasePersonDto.PHONE, TextField.class);

    	addField(CasePersonDto.OCCUPATION_TYPE, NativeSelect.class);
    	addField(CasePersonDto.OCCUPATION_DETAILS, TextField.class);
    	addField(CasePersonDto.OCCUPATION_FACILITY, TextField.class);
    	
    	
    	setRequired(true, 
    			CasePersonDto.FIRST_NAME, 
    			CasePersonDto.LAST_NAME);
    	setReadOnly(true, 
    			CasePersonDto.APPROXIMATE_AGE);
    	setVisible(false, 
    			CasePersonDto.OCCUPATION_DETAILS,
    			CasePersonDto.OCCUPATION_FACILITY);
    	
    	// add some listeners 
    	addFieldListener(CasePersonDto.BIRTH_DATE, e -> updateApproximateAge());
    	addFieldListener(CasePersonDto.DEATH_DATE, e -> updateApproximateAge());
    	addFieldListener(CasePersonDto.OCCUPATION_TYPE, e -> toogleOccupationMetaFields());
    }



	@Override
	protected void setLayout() {
		setTemplateContents(HTML_LAYOUT);
	}
    
	private void updateApproximateAge() {
		TextField textField = (TextField)getFieldGroup().getField(CasePersonDto.APPROXIMATE_AGE);
		// textfield must be writeable for update
		textField.setReadOnly(false);
		textField.setValue(DateHelper.getApproximateAge(
				(Date) getFieldGroup().getField(CasePersonDto.BIRTH_DATE).getValue(),
				(Date) getFieldGroup().getField(CasePersonDto.DEATH_DATE).getValue())
				);
		textField.setReadOnly(true);
	}
	
	private Object toogleOccupationMetaFields() {
		OccupationType type = (OccupationType) ((NativeSelect)getFieldGroup().getField(CasePersonDto.OCCUPATION_TYPE)).getValue();
		switch(type) {
			case BUSINESSMAN_WOMAN:
			case TRANSPORTER:
			case OTHER:
				setVisible(false, 
						CasePersonDto.OCCUPATION_FACILITY);
				setVisible(true, 
		    			CasePersonDto.OCCUPATION_DETAILS);
				break;
			case HEALTHCARE_WORKER:
				setVisible(true, 
						CasePersonDto.OCCUPATION_DETAILS,
						CasePersonDto.OCCUPATION_FACILITY);
				break;
			default:
				setVisible(false, 
		    			CasePersonDto.OCCUPATION_DETAILS,
		    			CasePersonDto.OCCUPATION_FACILITY);
				break;
		
		}
		return null;
	}
}
