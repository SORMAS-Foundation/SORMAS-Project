package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractMergeGrid;

public class MergeCasesGrid extends AbstractMergeGrid<CaseIndexDto, CaseCriteria> {

	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;

	public MergeCasesGrid() {
		super(
			CaseIndexDto.class,
			CaseDataView.VIEW_NAME,
			CaseIndexDto.I18N_PREFIX,
			Strings.confirmationMergeCaseAndDeleteOther,
			Strings.confirmationPickCaseAndDeleteOther);
	}

	@Override
	protected void buildColumns() {
		Column<CaseIndexDto, String> diseaseColumn = addColumn(caze -> DiseaseHelper.toString(caze.getDisease(), caze.getDiseaseDetails()));
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
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<CaseIndexDto, AgeAndBirthDateDto>) getColumn(CaseIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
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
	protected List<CaseIndexDto[]> getItemForDuplicateMerging() {
		return FacadeProvider.getCaseFacade().getCasesForDuplicateMerging(criteria, ignoreRegion);
	}

	@Override
	protected void merge(CaseIndexDto targetedCase, CaseIndexDto caseToMergeAndDelete) {
		FacadeProvider.getCaseFacade().mergeCase(targetedCase.getUuid(), caseToMergeAndDelete.getUuid());
		boolean deletePerformed = deleteCaseAsDuplicate(targetedCase, caseToMergeAndDelete);

		if (deletePerformed && FacadeProvider.getCaseFacade().isDeleted(caseToMergeAndDelete.getUuid())) {
			reload();
			new Notification(I18nProperties.getString(Strings.messageCasesMerged), Type.TRAY_NOTIFICATION).show(Page.getCurrent());
		} else {
			new Notification(I18nProperties.getString(Strings.errorCaseMerging), Type.ERROR_MESSAGE).show(Page.getCurrent());
		}
	}

	@Override
	protected void pick(CaseIndexDto targetedCase, CaseIndexDto caseToDelete) {
		boolean deletePerformed = deleteCaseAsDuplicate(targetedCase, caseToDelete);

		if (deletePerformed && FacadeProvider.getCaseFacade().isDeleted(caseToDelete.getUuid())) {
			reload();
			new Notification(I18nProperties.getString(Strings.messageCaseDuplicateDeleted), Type.TRAY_NOTIFICATION).show(Page.getCurrent());
		} else {
			new Notification(I18nProperties.getString(Strings.errorCaseDuplicateDeletion), Type.ERROR_MESSAGE).show(Page.getCurrent());
		}
	}

	@SuppressWarnings("unchecked")
	private boolean deleteCaseAsDuplicate(CaseIndexDto caze, CaseIndexDto caseToMergeAndDelete) {
		try {
			FacadeProvider.getCaseFacade().deleteCaseAsDuplicate(caseToMergeAndDelete.getUuid(), caze.getUuid());
		} catch (ExternalSurveillanceToolException e) {
			return false;
		}

		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void calculateCompletenessValues() {
		TreeDataProvider<CaseIndexDto> dataProvider = (TreeDataProvider<CaseIndexDto>) getDataProvider();
		TreeData<CaseIndexDto> data = dataProvider.getTreeData();

		for (CaseIndexDto parent : data.getRootItems()) {
			FacadeProvider.getCaseFacade().updateCompleteness(parent.getUuid());
			FacadeProvider.getCaseFacade().updateCompleteness(data.getChildren(parent).get(0).getUuid());
		}

		reload();
	}
}
