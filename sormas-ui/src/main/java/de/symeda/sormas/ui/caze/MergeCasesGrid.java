package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseMergeIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractMergeGrid;

public class MergeCasesGrid extends AbstractMergeGrid<CaseMergeIndexDto, CaseCriteria> {

	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;

	public MergeCasesGrid() {
		super(
			CaseMergeIndexDto.class,
			FacadeProvider.getCaseFacade(),
			CaseDataView.VIEW_NAME,
			CaseIndexDto.I18N_PREFIX,
			Messages.of(
				Strings.confirmationMergeCaseAndDeleteOther,
				Strings.confirmationPickCaseAndDeleteOther,
				Strings.messageCasesMerged,
				Strings.errorCaseMerging,
				Strings.messageCaseDuplicateDeleted,
				Strings.errorCaseDuplicateDeletion));
	}

	@Override
	protected void buildColumns() {
		Column<CaseMergeIndexDto, String> diseaseColumn = addColumn(caze -> DiseaseHelper.toString(caze.getDisease(), caze.getDiseaseDetails()));
		diseaseColumn.setId(COLUMN_DISEASE);

		setColumns(
			COLUMN_UUID,
			COLUMN_DISEASE,
			CaseIndexDto.CASE_CLASSIFICATION,
			CaseIndexDto.PERSON_FIRST_NAME,
			CaseIndexDto.PERSON_LAST_NAME,
			CaseIndexDto.AGE_AND_BIRTH_DATE,
			CaseIndexDto.SEX,
			CaseIndexDto.RESPONSIBLE_DISTRICT_NAME,
			CaseIndexDto.HEALTH_FACILITY_NAME,
			CaseIndexDto.REPORT_DATE,
			CaseIndexDto.CREATION_DATE,
			COLUMN_COMPLETENESS,
			COLUMN_ACTIONS);

		getColumn(COLUMN_ACTIONS).setMinimumWidth(280);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<CaseMergeIndexDto, Date>) getColumn(CaseIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<CaseMergeIndexDto, Date>) getColumn(CaseIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<CaseMergeIndexDto, AgeAndBirthDateDto>) getColumn(CaseIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
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
			getColumn(CaseIndexDto.RESPONSIBLE_DISTRICT_NAME).setHidden(true);
		}
	}

	@Override
	protected List<CaseMergeIndexDto[]> getItemsForDuplicateMerging(int limit) {
		return FacadeProvider.getCaseFacade().getCasesForDuplicateMerging(criteria, limit, ignoreRegion);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void calculateCompletenessValues() {
		TreeDataProvider<CaseMergeIndexDto> dataProvider = (TreeDataProvider<CaseMergeIndexDto>) getDataProvider();
		TreeData<CaseMergeIndexDto> data = dataProvider.getTreeData();

		for (CaseMergeIndexDto parent : data.getRootItems()) {
			FacadeProvider.getCaseFacade().updateCompleteness(parent.getUuid());
			FacadeProvider.getCaseFacade().updateCompleteness(data.getChildren(parent).get(0).getUuid());
		}

		reload();
	}
}
