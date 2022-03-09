package de.symeda.sormas.ui.contact;

import java.util.Date;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.MergeContactIndexDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractMergeGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeContactsGrid extends AbstractMergeGrid<MergeContactIndexDto, ContactCriteria> {

	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public MergeContactsGrid() {
		super(
			MergeContactIndexDto.class,
			ContactDataView.VIEW_NAME,
			MergeContactIndexDto.I18N_PREFIX,
			Strings.confirmationMergeContactAndDeleteOther,
			Strings.confirmationPickContactAndDeleteOther);
	}

	@Override
	protected void buildColumns() {
		Column<MergeContactIndexDto, String> diseaseColumn =
			addColumn(contact -> DiseaseHelper.toString(contact.getDisease(), contact.getDiseaseDetails()));
		diseaseColumn.setId(COLUMN_DISEASE);

		setColumns(
			COLUMN_UUID,
			MergeContactIndexDto.CAZE,
			COLUMN_DISEASE,
			MergeContactIndexDto.CONTACT_CLASSIFICATION,
			MergeContactIndexDto.PERSON_FIRST_NAME,
			MergeContactIndexDto.PERSON_LAST_NAME,
			MergeContactIndexDto.AGE_AND_BIRTH_DATE,
			MergeContactIndexDto.SEX,
			MergeContactIndexDto.DISTRICT_NAME,
			MergeContactIndexDto.REPORT_DATE_TIME,
			MergeContactIndexDto.CREATION_DATE,
			COLUMN_COMPLETENESS,
			COLUMN_ACTIONS);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<MergeContactIndexDto, Date>) getColumn(MergeContactIndexDto.REPORT_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<MergeContactIndexDto, Date>) getColumn(MergeContactIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<MergeContactIndexDto, AgeAndBirthDateDto>) getColumn(MergeContactIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: PersonHelper.getAgeAndBirthdateString(
					value.getAge(),
					value.getAgeType(),
					value.getDateOfBirthDD(),
					value.getDateOfBirthMM(),
					value.getDateOfBirthYYYY()),
			new TextRenderer());
	}

	@Override
	protected List<MergeContactIndexDto[]> getItemForDuplicateMerging() {
		return FacadeProvider.getContactFacade().getContactsForDuplicateMerging(criteria, ignoreRegion);
	}

	@Override
	protected void merge(MergeContactIndexDto targetedContact, MergeContactIndexDto contactToMergeAndDelete) {
		FacadeProvider.getContactFacade().mergeContact(targetedContact.getUuid(), contactToMergeAndDelete.getUuid());
		try {
			FacadeProvider.getContactFacade().deleteContactAsDuplicate(contactToMergeAndDelete.getUuid(), targetedContact.getUuid());
		} catch (ExternalSurveillanceToolException e) {
			logger.error("The contact with uuid:" + contactToMergeAndDelete.getUuid() + "could not be deleted");
		}

		if (FacadeProvider.getContactFacade().isDeleted(contactToMergeAndDelete.getUuid())) {
			reload();
			new Notification(I18nProperties.getString(Strings.messageContactsMerged), Notification.Type.TRAY_NOTIFICATION).show(Page.getCurrent());
		} else {
			new Notification(I18nProperties.getString(Strings.errorContactMerging), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
		}
	}

	@Override
	protected void pick(MergeContactIndexDto targetedContact, MergeContactIndexDto contactToDelete) {
		try {
			FacadeProvider.getContactFacade().deleteContactAsDuplicate(contactToDelete.getUuid(), targetedContact.getUuid());
		} catch (ExternalSurveillanceToolException e) {
			e.printStackTrace();
		}

		if (FacadeProvider.getContactFacade().isDeleted(contactToDelete.getUuid())) {
			reload();
			new Notification(I18nProperties.getString(Strings.messageContactDuplicateDeleted), Notification.Type.TRAY_NOTIFICATION)
				.show(Page.getCurrent());
		} else {
			new Notification(I18nProperties.getString(Strings.errorContactDuplicateDeletion), Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void calculateCompletenessValues() {
		TreeDataProvider<MergeContactIndexDto> dataProvider = (TreeDataProvider<MergeContactIndexDto>) getDataProvider();
		TreeData<MergeContactIndexDto> data = dataProvider.getTreeData();

		for (MergeContactIndexDto parent : data.getRootItems()) {
			FacadeProvider.getContactFacade().updateCompleteness(parent.getUuid());
			FacadeProvider.getContactFacade().updateCompleteness(data.getChildren(parent).get(0).getUuid());
		}

		reload();
	}
}
