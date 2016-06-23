package de.symeda.sormas.ui.surveillance.caze;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseDataForm extends AbstractEditForm<CaseDataDto> {

    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Case data")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDataDto.UUID)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDataDto.CASE_STATUS))
					),
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDataDto.DISEASE))
					)
				);

    public CaseDataForm() {
        super(CaseDataDto.class);
    }

    @Override
	protected void addFields() {
		Map<String, Class<? extends Field<?>>> formProperties = new HashMap<String, Class<? extends Field<?>>>();
		formProperties.put(CaseDataDto.UUID, TextField.class);
		formProperties.put(CaseDataDto.CASE_STATUS, NativeSelect.class);
		formProperties.put(CaseDataDto.DISEASE, NativeSelect.class);
		
		// @TODO: put this in i18n properties
		Map<String, String> captions = new HashMap<String, String>();
		captions.put(CaseDataDto.UUID, "ID");
		captions.put(CaseDataDto.CASE_STATUS, "Status");
		captions.put(CaseDataDto.DISEASE, "Disease");
		
		for (String propertyId : formProperties.keySet()) {
			Field<?> field = getFieldGroup().buildAndBind(captions.get(propertyId), propertyId, formProperties.get(propertyId));
//			field.setReadOnly(true);
			field.setSizeFull();
	        addComponent(field, propertyId);
		}
	}

	@Override
	protected void setLayout() {
		 setTemplateContents(HTML_LAYOUT);
	}

}
