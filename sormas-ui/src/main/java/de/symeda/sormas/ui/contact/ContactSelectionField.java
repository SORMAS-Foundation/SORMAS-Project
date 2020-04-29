package de.symeda.sormas.ui.contact;

import com.vaadin.ui.*;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import java.util.function.Consumer;

public class ContactSelectionField extends CustomField<SimilarContactDto> {

    public static final String CREATE_CONTACT = "createContact";
    public static final String SELECT_CONTACT = "selectContact";

    private ContactDto referenceContact;
    private String infoText;
    private String referenceFirstName;
    private String referenceLastName;
    private VerticalLayout mainLayout;
    private ContactSelectionGrid contactSelectionGrid;
    private RadioButtonGroup<String> rbSelectContact;
    private RadioButtonGroup<String> rbCreateContact;
    private Consumer<Boolean> selectionChangeCallback;

    public ContactSelectionField(ContactDto referenceContact, String infoText, String referenceFirstName, String referenceLastName) {
        this.referenceContact = referenceContact;
        this.infoText = infoText;
        this.referenceFirstName = referenceFirstName;
        this.referenceLastName = referenceLastName;

        initializeGrid();
    }

    private void initializeGrid() {
        final ContactSimilarityCriteria criteria = new ContactSimilarityCriteria(referenceContact.getPerson(),
                referenceContact.getCaze(), referenceContact.getDisease(), referenceContact.getLastContactDate(),
                referenceContact.getReportDateTime());
        contactSelectionGrid = new ContactSelectionGrid(criteria);

        contactSelectionGrid.addSelectionListener(e -> {
            if (e.getSelected().size() > 0) {
                rbCreateContact.setValue(null);
            }

            if (selectionChangeCallback != null) {
                selectionChangeCallback.accept(!e.getSelected().isEmpty());
            }
        });
    }

    @Override
    protected Component initContent() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(false);
        mainLayout.setSizeUndefined();
        mainLayout.setWidth(100, Unit.PERCENTAGE);

        addInfoComponent();
        addContactDetailsComponent();
        addSelectContactRadioGroup();
        mainLayout.addComponent(contactSelectionGrid);
        addCreateContactRadioGroup();

        rbSelectContact.setValue(SELECT_CONTACT);

        return mainLayout;
    }

    @Override
    protected void doSetValue(SimilarContactDto similarContactDto) {
        rbSelectContact.setValue(SELECT_CONTACT);

        if (similarContactDto != null) {
            contactSelectionGrid.select(similarContactDto);
        }
    }

    @Override
    public SimilarContactDto getValue() {
        if (contactSelectionGrid != null) {
            SimilarContactDto value = (SimilarContactDto) contactSelectionGrid.getSelectedRow();
            return value;
        }

        return null;
    }

    public boolean hasMatches() {
        return contactSelectionGrid.getContainerDataSource().size() > 0;
    }

    public void selectBestMatch() {
        if (contactSelectionGrid.getContainerDataSource().size() == 1) {
            setValue((SimilarContactDto) contactSelectionGrid.getContainerDataSource().firstItemId());
        } else {
            setValue(null);
        }
    }

    /**
     * Callback is executed with 'true' when a grid entry or "Create new person" is selected.
     */
    public void setSelectionChangeCallback(Consumer<Boolean> callback) {
        this.selectionChangeCallback = callback;
    }

    private void addInfoComponent() {
        mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoText));
    }

    private void addContactDetailsComponent() {
        HorizontalLayout contactDetailsLayout = new HorizontalLayout();
        contactDetailsLayout.setSpacing(true);

        final Label lblFirstName = new Label(referenceFirstName);
        lblFirstName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
        lblFirstName.setWidthUndefined();
        contactDetailsLayout.addComponent(lblFirstName);

        final Label lblLastName = new Label(referenceLastName);
        lblLastName.setWidthUndefined();
        lblLastName.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
        contactDetailsLayout.addComponent(lblLastName);

        final Label lblCase = new Label(referenceContact.getCaze() != null ? referenceContact.getCaze().getCaption() : "");
        lblCase.setWidthUndefined();
        lblCase.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX,
                ContactDto.CAZE));
        contactDetailsLayout.addComponent(lblCase);

        final Label lblCaseIdExternalSystem = new Label(referenceContact.getCaseIdExternalSystem());
        lblCaseIdExternalSystem.setWidthUndefined();
        lblCaseIdExternalSystem.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX,
                ContactDto.CASE_ID_EXTERNAL_SYSTEM));
        contactDetailsLayout.addComponent(lblCaseIdExternalSystem);

        final Label lblLastContactDate =
                new Label(DateFormatHelper.formatDate(referenceContact.getLastContactDate()));
        lblLastContactDate.setWidthUndefined();
        lblLastContactDate.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX,
                ContactDto.LAST_CONTACT_DATE));
        contactDetailsLayout.addComponent(lblLastContactDate);

        final Label lblContactProximity = new Label(referenceContact.getContactProximity() != null ?
                referenceContact.getContactProximity().toString() : "");
        lblContactProximity.setWidthUndefined();
        lblContactProximity.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX,
                ContactDto.CONTACT_PROXIMITY));
        contactDetailsLayout.addComponent(lblContactProximity);

        final Label lblContactClassification = new Label(referenceContact.getContactClassification() != null ?
                referenceContact.getContactClassification().toString() : "");
        lblContactClassification.setWidthUndefined();
        lblContactClassification.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX,
                ContactDto.CONTACT_CLASSIFICATION));
        contactDetailsLayout.addComponent(lblContactClassification);

        final Label lblContactStatus = new Label(referenceContact.getContactStatus() != null ?
                referenceContact.getContactStatus().toString() : "");
        lblContactStatus.setWidthUndefined();
        lblContactStatus.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.CONTACT_STATUS));
        contactDetailsLayout.addComponent(lblContactStatus);

        mainLayout.addComponent(new Panel(contactDetailsLayout));
    }

    private void addSelectContactRadioGroup() {
        rbSelectContact = new RadioButtonGroup<>();
        rbSelectContact.setItems(SELECT_CONTACT);
        rbSelectContact.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.contactSelect));
        CssStyles.style(rbSelectContact, CssStyles.VSPACE_NONE);
        rbSelectContact.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                rbCreateContact.setValue(null);
                contactSelectionGrid.setEnabled(true);
                if (selectionChangeCallback != null) {
                    selectionChangeCallback.accept(contactSelectionGrid.getSelectedRow() != null);
                }
            }
        });

        mainLayout.addComponent(rbSelectContact);
    }

    private void addCreateContactRadioGroup() {
        rbCreateContact = new RadioButtonGroup<>();
        rbCreateContact.setItems(CREATE_CONTACT);
        rbCreateContact.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.contactCreateNew));
        rbCreateContact.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                rbSelectContact.setValue(null);
                contactSelectionGrid.deselectAll();
                contactSelectionGrid.setEnabled(false);
                if (selectionChangeCallback != null) {
                    selectionChangeCallback.accept(true);
                }
            }
        });

        mainLayout.addComponent(rbCreateContact);
    }
}
