package de.symeda.sormas.backend.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class XOfCriteria extends Criteria {

	private final int requiredAmount;
	protected final List<Criteria> criterias;
	
	public XOfCriteria(int requiredAmount, Criteria... criterias) {
		this.requiredAmount = requiredAmount;
		this.criterias = Arrays.asList(criterias);
	}
	
	@Override
	boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		int amount = 0;
		for (Criteria criteria : criterias) {
			if (criteria.eval(caze, sampleTests)) {
				amount++;
				if (amount >= requiredAmount) {
					return true;
				}
			}
		}
		
		return false;
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
	
}
