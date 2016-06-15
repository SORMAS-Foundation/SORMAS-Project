package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
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
    
    private final TextField id;
    private final NativeSelect caseStatus; 
    private final TextArea description; 
    
    private static final String HTML_LAYOUT = 
		LayoutUtil.h3(CssStyles.VSPACE3, "Case")+
		LayoutUtil.locCss(CssStyles.VSPACE2, CaseDto.UUID)+
		LayoutUtil.locCss(CssStyles.VSPACE2, CaseDto.CASE_STATUS)+
		LayoutUtil.locCss(CssStyles.VSPACE2, CaseDto.DESCRIPTION);

    public CaseForm(CaseController caseController) {
        super();
        setSizeFull();
        setTemplateContents(HTML_LAYOUT);
        
        viewLogic = caseController;

        fieldGroup = new BeanFieldGroup<CaseDto>(CaseDto.class);

        id = fieldGroup.buildAndBind("ID", CaseDto.UUID, TextField.class);
        id.setReadOnly(true);
        addComponent(id, CaseDto.UUID);
        
        caseStatus = fieldGroup.buildAndBind("Status", CaseDto.CASE_STATUS, NativeSelect.class);
        addComponent(caseStatus, CaseDto.CASE_STATUS);

        description = fieldGroup.buildAndBind("Description", CaseDto.DESCRIPTION, TextArea.class);
        addComponent(description, CaseDto.DESCRIPTION);

        // perform validation and enable/disable buttons while editing
        ValueChangeListener valueListener = e -> formHasChanged();
        
        for (Field f : fieldGroup.getFields()) {
            f.addValueChangeListener(valueListener);
        }

        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            }

            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
            	viewLogic.updateCase(fieldGroup.getItemDataSource().getBean());
            }
        });


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
