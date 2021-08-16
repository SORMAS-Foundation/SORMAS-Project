package de.symeda.sormas.api.labmessage;

import javax.ejb.Remote;
import javax.validation.Valid;
import java.util.List;

@Remote
public interface TestReportFacade {

	TestReportDto getByUuid(String uuid);

	TestReportDto saveTestReport(@Valid TestReportDto dto);

	List<TestReportDto> getAllByLabMessage(LabMessageReferenceDto labMessageRef);
}
