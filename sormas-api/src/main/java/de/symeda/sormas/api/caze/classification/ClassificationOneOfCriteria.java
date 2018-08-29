package de.symeda.sormas.backend.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class OneOfCriteria extends Criteria {

	protected final List<Criteria> criterias;

	public OneOfCriteria(Criteria... criterias) {
		this.criterias = Arrays.asList(criterias);
	}

	@Override
	boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		for (Criteria criteria : criterias) {
			if (criteria.eval(caze, sampleTests))
				return true;
		}
		return false;
	}

	@Override
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		stringBuilder.append("one of:");
		for (int i=0; i<criterias.size(); i++) {
			stringBuilder.append("\n- ");
			criterias.get(i).appendDesc(stringBuilder);	
		}
		return stringBuilder;
	}
	
}