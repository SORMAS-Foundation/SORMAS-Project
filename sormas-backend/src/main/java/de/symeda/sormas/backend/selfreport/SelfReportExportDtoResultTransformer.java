package de.symeda.sormas.backend.selfreport;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.selfreport.SelfReportExportDto;
import de.symeda.sormas.api.selfreport.SelfReportInvestigationStatus;
import de.symeda.sormas.api.selfreport.SelfReportProcessingStatus;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SelfReportExportDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = 1L;

	@Override
	public SelfReportExportDto transformTuple(Object[] tuple, String[] aliases) {
		int index = -1;

		//@formatter:off
        return new SelfReportExportDto(
                (String) tuple[++index], (SelfReportType) tuple[++index], (Date) tuple[++index],
                (String) tuple[++index], (Disease) tuple[++index], (String) tuple[++index], (DiseaseVariant) tuple[++index],
                (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (Sex) tuple[++index], 
                (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
                new BirthDateDto((Integer)tuple[++index], (Integer) tuple[++index], (Integer) tuple[++index]),
                (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (Date) tuple[++index],
                (Date) tuple[++index], (String) tuple[++index], (Date) tuple[++index], (Date) tuple[++index],
                (Date) tuple[++index], (String) tuple[++index], new UserReferenceDto((String) tuple[++index],
                (String) tuple[++index], (String) tuple[++index]), (SelfReportInvestigationStatus) tuple[++index], 
                (SelfReportProcessingStatus) tuple[++index], (DeletionReason) tuple[++index], (String) tuple[++index]);
        //@formatter:on
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
