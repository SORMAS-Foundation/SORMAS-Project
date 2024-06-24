package de.symeda.sormas.backend.selfreport;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.selfreport.SelfReportListEntryDto;
import org.hibernate.transform.ResultTransformer;

import java.util.Date;
import java.util.List;

public class SelfReportEntryDtoResultTransformer  implements ResultTransformer {
    @Override
    public Object transformTuple(Object[] objects, String[] strings) {

        boolean referred = objects[4] !=null;
        return new SelfReportListEntryDto((String) objects[0], (Date) objects[1], (String) objects[2], (Disease) objects[3], (Date) objects[4]);
    }

    @Override
    public List transformList(List list) {
        return list;
    }
}
