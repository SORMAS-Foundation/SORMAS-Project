package de.symeda.sormas.ui.contact;

import java.util.Date;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.MergeContactIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.utils.AbstractMergeGrid;

public class MergeContactsGrid extends AbstractMergeGrid<MergeContactIndexDto, ContactCriteria> {

	private static final String CASE_UUID = Captions.Contact_caze;
	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;

	public MergeContactsGrid() {
		super(
			MergeContactIndexDto.class,
			FacadeProvider.getContactFacade(),
			ContactDataView.VIEW_NAME,
			MergeContactIndexDto.I18N_PREFIX,
			Messages.of(
				Strings.confirmationMergeContactAndDeleteOther,
				Strings.confirmationPickContactAndDeleteOther,
				Strings.messageContactsMerged,
				Strings.errorContactMerging,
				Strings.messageContactDuplicateDeleted,
				Strings.errorContactDuplicateDeletion));
	}

	@Override
	protected void buildColumns() {
		Column<MergeContactIndexDto, String> diseaseColumn =
			addColumn(contact -> DiseaseHelper.toString(contact.getDisease(), contact.getDiseaseDetails()));
		diseaseColumn.setId(COLUMN_DISEASE);

		addComponentColumn(indexDto -> {
			Link link = new Link(
				DataHelper.getShortUuid(indexDto.getCaze().getUuid()),
				new ExternalResource(
					SormasUI.get().getPage().getLocation().getRawPath() + "#!" + CaseDataView.VIEW_NAME + "/" + indexDto.getCaze().getUuid()));
			link.setTargetName("_blank");
			return link;
		}).setId(CASE_UUID);

		setColumns(
			COLUMN_UUID,
			CASE_UUID,
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

		getColumn(COLUMN_ACTIONS).setMinimumWidth(280);

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

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(MergeContactIndexDto.DISTRICT_NAME).setHidden(true);
		}
	}

	@Override
	protected List<MergeContactIndexDto[]> getItemsForDuplicateMerging(int limit) {
		return FacadeProvider.getContactFacade().getContactsForDuplicateMerging(criteria, limit, ignoreRegion);
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
