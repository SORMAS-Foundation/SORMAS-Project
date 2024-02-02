package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;

import java.util.Date;

public class DashboardCriteria extends BaseDashboardCriteria<DashboardCriteria> {

	private CriteriaDateType newCaseDateType;
	private EpiCurveGrouping epiCurveGrouping;
	private boolean showMinimumEntries;
	private CaseMeasure caseMeasure;
	private boolean includeNotACaseClassification;

	private CaseClassification caseClassification;
	private NewDateFilterType dateFilterType;

	private Date dateFrom;
	private Date dateTo;

	private CaseOutcome outcome;
	public DashboardCriteria(NewCaseDateType dateTypeClass) {
		super(DashboardCriteria.class);
	}

	public DashboardCriteria() {
		super(DashboardCriteria.class);
	}

	public DashboardCriteria(Class<? extends CriteriaDateType> dateTypeClass) {
		super(DashboardCriteria.class);
	}


	public CriteriaDateType getNewCaseDateType() {
		return newCaseDateType;
	}

	public DashboardCriteria newCaseDateType(CriteriaDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
		return self;
	}

	public boolean shouldIncludeNotACaseClassification() {
		return includeNotACaseClassification;
	}

	public DashboardCriteria includeNotACaseClassification(boolean includeNotACaseClassification) {
		this.includeNotACaseClassification = includeNotACaseClassification;
		return self;
	}

	public EpiCurveGrouping getEpiCurveGrouping() {
		return epiCurveGrouping;
	}

	public boolean isIncludeNotACaseClassification() {
		return includeNotACaseClassification;
	}

	public boolean isShowMinimumEntries() {
		return showMinimumEntries;
	}

	public CaseMeasure getCaseMeasure() {
		return caseMeasure;
	}



	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public DashboardCriteria caseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
		return this;
	}

	public NewDateFilterType getDateFilterType() {
		return dateFilterType;
	}

	public DashboardCriteria dateFilterType(NewDateFilterType dateFilterType) {
		this.dateFilterType = dateFilterType;
		return this;
	}


	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public DashboardCriteria dateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
		return this;
	}

	public DashboardCriteria dateTo(Date dateTo) {
		this.dateTo = dateTo;
		return this;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

}
