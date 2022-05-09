package de.symeda.sormas.backend.action;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionMeasure;
import de.symeda.sormas.api.action.ActionPriority;
import de.symeda.sormas.api.action.ActionStatus;
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
		UserReferenceDto eventReportingUser = new UserReferenceDto((String) objects[12], (String) objects[13], (String) objects[14], null);
		UserReferenceDto eventResponsibleUser = new UserReferenceDto((String) objects[15], (String) objects[16], (String) objects[17], null);
		UserReferenceDto actionLastModifiedBy = new UserReferenceDto((String) objects[26], (String) objects[27], (String) objects[28], null);
		UserReferenceDto actionCreatorUser = new UserReferenceDto((String) objects[29], (String) objects[30], (String) objects[31], null);
		return new EventActionIndexDto(
			(String) objects[0],
			(String) objects[1],
			(Disease) objects[2],
			(DiseaseVariant) objects[3],
			(String) objects[4],
			(EventIdentificationSource) objects[5],
			(Date) objects[6],
			(Date) objects[7],
			(EventStatus) objects[8],
			(RiskLevel) objects[9],
			(EventInvestigationStatus) objects[10],
			(EventManagementStatus) objects[11],
			eventReportingUser,
			eventResponsibleUser,
			(String) objects[20],
			(Date) objects[19],
			(Date) objects[21],
			(Date) objects[22],
			(Date) objects[23],
			(ActionStatus) objects[24],
			(ActionPriority) objects[25],
			actionLastModifiedBy,
			actionCreatorUser);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List list) {
		return list;
	}
}
