package de.symeda.sormas.api.externalmessage.labmessage;

import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.externalmessage.ExternalMessageReferenceDto;

@Remote
public interface TestReportFacade {

	TestReportDto getByUuid(String uuid);

	TestReportDto saveTestReport(@Valid TestReportDto dto);

	List<TestReportDto> getAllByLabMessage(ExternalMessageReferenceDto labMessageRef);
}
