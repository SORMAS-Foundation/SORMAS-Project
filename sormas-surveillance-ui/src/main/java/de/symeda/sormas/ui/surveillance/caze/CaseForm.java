package de.symeda.sormas.ui.surveillance.caze;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

/**
 * A form for editing a single product.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
public class CaseForm extends CustomLayout {

	private static final long serialVersionUID = 8049619475023157401L;
	private CaseController viewLogic;
    private BeanFieldGroup<CaseDto> fieldGroup;
    
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Case data")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDto.UUID)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDto.CASE_STATUS))
//						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDto.DESCRIPTION))
					),
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDto.DISEASE))
					)
				);

    public CaseForm(CaseController caseController) {
        super();
        setSizeFull();
        setTemplateContents(HTML_LAYOUT);
        
        viewLogic = caseController;

        addFields();


//        save.addClickListener(new ClickListener() {
//            @Override
//            public void buttonClick(ClickEvent event) {
//                try {
//                    fieldGroup.commit();
//
//                    // only if validation succeeds
//                    Product product = fieldGroup.getItemDataSource().getBean();
//                    viewLogic.saveProduct(product);
//                } catch (CommitException e) {
//                    Notification n = new Notification(
//                            "Please re-check the fields", Type.ERROR_MESSAGE);
//                    n.setDelayMsec(500);
//                    n.show(getUI().getPage());
//                }
//            }
//        });
//
//        cancel.addClickListener(new ClickListener() {
//            @Override
//            public void buttonClick(ClickEvent event) {
//                viewLogic.cancelProduct();
//            }
//        });
//
//        delete.addClickListener(new ClickListener() {
//            @Override
//            public void buttonClick(ClickEvent event) {
//                Product product = fieldGroup.getItemDataSource().getBean();
//                viewLogic.deleteProduct(product);
//            }
//        });
    }

	private void addFields() {
		Map<String, Class<? extends Field<?>>> formProperties = new HashMap<String, Class<? extends Field<?>>>();
		formProperties.put(CaseDto.UUID, TextField.class);
		formProperties.put(CaseDto.CASE_STATUS, NativeSelect.class);
		formProperties.put(CaseDto.DISEASE, NativeSelect.class);
		
		// @TODO: put this in i18n properties
		Map<String, String> captions = new HashMap<String, String>();
		captions.put(CaseDto.UUID, "ID");
		captions.put(CaseDto.CASE_STATUS, "Status");
		captions.put(CaseDto.DISEASE, "Disease");
		
		fieldGroup = new BeanFieldGroup<CaseDto>(CaseDto.class);

		for (String propertyId : formProperties.keySet()) {
			Field<?> field = fieldGroup.buildAndBind(captions.get(propertyId), propertyId, formProperties.get(propertyId));
			field.setReadOnly(true);
			field.setSizeFull();
	        addComponent(field, propertyId);
		}
		
//        id = fieldGroup.buildAndBind("ID", CaseDto.UUID, TextField.class);
//        id.setReadOnly(true);
//        addComponent(id, CaseDto.UUID);
//        
//        caseStatus = fieldGroup.buildAndBind("Status", CaseDto.CASE_STATUS, NativeSelect.class);
//        addComponent(caseStatus, CaseDto.CASE_STATUS);
//
//        description = fieldGroup.buildAndBind("Description", CaseDto.DESCRIPTION, TextArea.class);
//        addComponent(description, CaseDto.DESCRIPTION);

        // perform validation and enable/disable buttons while editing
        ValueChangeListener valueListener = e -> formHasChanged();
        
        for (Field<?> f : fieldGroup.getFields()) {
            f.addValueChangeListener(valueListener);
        }

        fieldGroup.addCommitHandler(new CommitHandler() {

			private static final long serialVersionUID = -8160135868709228006L;

			@Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            }

            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
            	viewLogic.updateCase(fieldGroup.getItemDataSource().getBean());
            }
        });
	}

    public void editCase(CaseDto caze) {
        if (caze == null) {
            caze = new CaseDto();
        }
        fieldGroup.setItemDataSource(new BeanItem<CaseDto>(caze));

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String id2 = getId();
        if (id2 != null) {
			String scrollScript = "window.document.getElementById('" + id2
	                + "').scrollTop = 0;";
	        Page.getCurrent().getJavaScript().execute(scrollScript);
        }
    }

    private void formHasChanged() {
        // only products that have been saved should be removable
        boolean canRemoveProduct = false;
        BeanItem<CaseDto> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	CaseDto caze = item.getBean();
            canRemoveProduct = caze.getUuid() != null;
        }
        //delete.setEnabled(canRemoveProduct);
    }
    
}
