package de.symeda.sormas.backend.caze.classification;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public abstract class Criteria {
	
	abstract boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests);
	abstract StringBuilder appendDesc(StringBuilder stringBuilder);
	
}