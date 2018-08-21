package de.symeda.sormas.backend.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class AllOfCriteria extends Criteria {

	protected final List<Criteria> criterias;

	public AllOfCriteria(Criteria... criterias) {
		this.criterias = Arrays.asList(criterias);
	}

	@Override
	boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		for (Criteria criteria : criterias) {
			if (!criteria.eval(caze, sampleTests))
				return false;
		}
		return true;
	}

	@Override
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		for (int i=0; i<criterias.size(); i++) {
			if (i > 0) {
				stringBuilder.append("\nAND ");
			}
			criterias.get(i).appendDesc(stringBuilder);	
		}
		return stringBuilder;
	}
	
	public static class AllOfSubCriteria extends AllOfCriteria {
		
		public AllOfSubCriteria(Criteria... criterias) {
			super(criterias);
		}

		@Override
		StringBuilder appendDesc(StringBuilder stringBuilder) {
			for (int i=0; i<criterias.size(); i++) {
				if (i > 0) {
					if (i+1 < criterias.size()) {
						stringBuilder.append(", ");
					} else {
						stringBuilder.append(" and ");
					}
				}
				criterias.get(i).appendDesc(stringBuilder);	
			}
			return stringBuilder;
		}
		
	}
	
}