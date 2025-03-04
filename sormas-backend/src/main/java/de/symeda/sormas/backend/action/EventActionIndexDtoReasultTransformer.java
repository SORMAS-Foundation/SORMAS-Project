package de.symeda.sormas.backend.action;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionPriority;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventIdentificationSource;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventManagementStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.user.UserReferenceDto;

public class EventActionIndexDtoReasultTransformer implements ResultTransformer {

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		UserReferenceDto eventReportingUser = new UserReferenceDto((String) objects[13], (String) objects[14], (String) objects[15]);
		UserReferenceDto eventResponsibleUser = new UserReferenceDto((String) objects[16], (String) objects[17], (String) objects[18]);
		UserReferenceDto actionLastModifiedBy = new UserReferenceDto((String) objects[27], (String) objects[28], (String) objects[29]);
		UserReferenceDto actionCreatorUser = new UserReferenceDto((String) objects[30], (String) objects[31], (String) objects[32]);
		return new EventActionIndexDto(
			(String) objects[1],
			(String) objects[2],
			(Disease) objects[3],
			(String) objects[4],
			(String) objects[5],
			(EventIdentificationSource) objects[6],
			(Date) objects[7],
			(Date) objects[8],
			(EventStatus) objects[9],
			(RiskLevel) objects[10],
			(EventInvestigationStatus) objects[11],
			(EventManagementStatus) objects[12],
			eventReportingUser,
			eventResponsibleUser,
			(String) objects[21],
			(Date) objects[20],
			(Date) objects[22],
			(Date) objects[23],
			(Date) objects[24],
			(ActionStatus) objects[25],
			(ActionPriority) objects[26],
			actionLastModifiedBy,
			actionCreatorUser,
			(DeletionReason) objects[42],
			(String) objects[43]);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List list) {
		return list;
	}
}
