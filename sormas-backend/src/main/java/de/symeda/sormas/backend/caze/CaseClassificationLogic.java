package de.symeda.sormas.backend.caze;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

@Singleton
public class CaseClassificationLogic {

	private Map<Disease, Criteria> suspectCriterias = new HashMap<Disease, Criteria>();
	private Map<Disease, Criteria> probableCriterias = new HashMap<Disease, Criteria>();
	private Map<Disease, Criteria> confirmedCriterias = new HashMap<Disease, Criteria>();
	
	
	public CaseClassification getClassification(CaseDataDto caze, List<SampleTestDto> sampleTests) {

		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}
		
		Disease disease = caze.getDisease();
		
		if (confirmedCriterias.containsKey(disease)
				&& confirmedCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.CONFIRMED;
		} else if (probableCriterias.containsKey(disease)
				&& probableCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.PROBABLE;
		} else if (suspectCriterias.containsKey(disease)
				&& suspectCriterias.get(disease).eval(caze, sampleTests)) {
			return CaseClassification.SUSPECT;
		} else {
			return CaseClassification.NOT_CLASSIFIED;
		}
	}

	public String getClassificationDescription(Disease disease, CaseClassification caseClassification) {
		if (suspectCriterias.isEmpty()) {
			buildCriterias();
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		switch (caseClassification) {
		case CONFIRMED:
			if (confirmedCriterias.containsKey(disease)) {
				confirmedCriterias.get(disease).appendDesc(stringBuilder);
			}
			break;
		case PROBABLE:
			if (probableCriterias.containsKey(disease)) {
				probableCriterias.get(disease).appendDesc(stringBuilder);
			}
			break;
		case SUSPECT:
			if (suspectCriterias.containsKey(disease)) {
				suspectCriterias.get(disease).appendDesc(stringBuilder);
			}
			break;
		case NOT_CLASSIFIED:
		case NO_CASE:
			break;
		default:
			throw new IllegalArgumentException(caseClassification.toString());
		}
		
		return stringBuilder.toString();
	}

	private void buildCriterias() {
		
		Criteria suspect, probable, confirmed;
		
		// EVD
		suspect = allOf(
				symptom(SymptomsDto.FEVER),
				oneOf(
						allOfSub(symptom(SymptomsDto.BLOOD_IN_STOOL), symptom(SymptomsDto.DIARRHEA)),
						symptom(SymptomsDto.GUMS_BLEEDING), 
						symptom(SymptomsDto.SKIN_BRUISING),
						allOfSub(symptom(SymptomsDto.EYES_BLEEDING), symptom(SymptomsDto.BLOOD_URINE))));
		probable = allOf(
				caseData(CaseDataDto.OUTCOME, CaseOutcome.DECEASED), 
				suspect, 
				oneOf(
						epiData(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE),
						epiData(EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE), 
						epiData(EpiDataDto.PERCUTANEOUS_CASE_BLOOD),
						allOfSub(epiData(EpiDataDto.AREA_CONFIRMED_CASES), epiData(EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE))));
		confirmed = allOf(
				suspect, 
				positiveTestResult(SampleTestType.IGM_SERUM_ANTIBODY, SampleTestType.PCR_RT_PCR, SampleTestType.VIRUS_ISOLATION));
		addCriterias(Disease.EVD, suspect, probable, confirmed);
		
	}
	
	private void addCriterias(Disease disease, Criteria suspect, Criteria probable, Criteria confirmed) {
		suspectCriterias.put(disease, suspect);
		probableCriterias.put(disease, probable);
		confirmedCriterias.put(disease, confirmed);
	}

	private static AllOfCriteria allOf(Criteria... criterias) {
		return new AllOfCriteria(criterias);
	}

	private static OneOfCriteria oneOf(Criteria... criterias) {
		return new OneOfCriteria(criterias);
	}

	private static AllOfSubCriteria allOfSub(Criteria... criterias) {
		return new AllOfSubCriteria(criterias);
	}

	private static CaseCriteria caseData(String propertyId, Object... propertyValues) {
		return new CaseCriteria(propertyId, propertyValues);
	}

	private static SymptomCriteria symptom(String propertyId) {
		return new SymptomCriteria(propertyId);
	}

	private static EpiDataCriteria epiData(String propertyId) {
		return new EpiDataCriteria(propertyId);
	}

	private static SampleTestPositiveResultCriteria positiveTestResult(SampleTestType... sampleTestTypes) {
		return new SampleTestPositiveResultCriteria(sampleTestTypes);
	}

	private static abstract class Criteria {
		abstract boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests);
		abstract StringBuilder appendDesc(StringBuilder stringBuilder);
	}

	private static class AllOfCriteria extends Criteria {

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
	}

	private static class AllOfSubCriteria extends AllOfCriteria {

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

	private static class OneOfCriteria extends Criteria {

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

	private static class CaseCriteria extends Criteria {

		protected final String propertyId;
		protected final List<Object> propertyValues;
		protected Method method;

		public CaseCriteria(String propertyId, Object... propertyValues) {
			this.propertyId = propertyId;
			this.propertyValues = Arrays.asList(propertyValues);
		}

		protected Class<? extends EntityDto> getInvokeClass() {
			return CaseDataDto.class;
		}

		protected Object getInvokeObject(CaseDataDto caze) {
			return caze;
		}

		@Override
		boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
			if (method == null) {
				try {
					method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
				} catch (NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			
			try {
				Object value = method.invoke(getInvokeObject(caze));
				return propertyValues.contains(value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		
		protected StringBuilder appendDescValues(StringBuilder stringBuilder) {
			if (propertyValues.size() == 1 && propertyValues.get(0) instanceof YesNoUnknown)
				return stringBuilder;

			stringBuilder.append(" ");
			for (int i=0; i<propertyValues.size(); i++) {
				if (i > 0) stringBuilder.append(", ");
				stringBuilder.append(propertyValues.get(i).toString());
			}
			return stringBuilder;
		}

		@Override
		StringBuilder appendDesc(StringBuilder stringBuilder) {
			stringBuilder.append(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, propertyId));
			appendDescValues(stringBuilder);
			return stringBuilder;
		}
	}

	private static class SymptomCriteria extends CaseCriteria {

		public SymptomCriteria(String propertyId) {
			super(propertyId, SymptomState.YES);
		}

		public SymptomCriteria(String propertyId, Object... propertyValues) {
			super(propertyId, propertyValues);
		}

		@Override
		protected Class<? extends EntityDto> getInvokeClass() {
			return SymptomsDto.class;
		}

		@Override
		protected Object getInvokeObject(CaseDataDto caze) {
			return caze.getSymptoms();
		}

		@Override
		StringBuilder appendDesc(StringBuilder stringBuilder) {
			stringBuilder.append(I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, propertyId));
			if (!(propertyValues.get(0) instanceof SymptomState)) {
				appendDescValues(stringBuilder);
			}
			return stringBuilder;
		}
	}

	private static class EpiDataCriteria extends CaseCriteria {

		public EpiDataCriteria(String propertyId) {
			super(propertyId, YesNoUnknown.YES);
		}

		public EpiDataCriteria(String propertyId, Object... propertyValues) {
			super(propertyId, propertyValues);
		}
		
		@Override
		protected Class<? extends EntityDto> getInvokeClass() {
			return EpiDataDto.class;
		}

		@Override
		protected Object getInvokeObject(CaseDataDto caze) {
			return caze.getEpiData();
		}

		@Override
		StringBuilder appendDesc(StringBuilder stringBuilder) {
			stringBuilder.append(I18nProperties.getPrefixFieldCaption(EpiDataDto.I18N_PREFIX, propertyId));
			return stringBuilder;
		}
	}
	
	private static class SampleTestPositiveResultCriteria extends Criteria {

		protected final List<SampleTestType> sampleTestTypes;

		public SampleTestPositiveResultCriteria(SampleTestType... sampleTestTypes) {
			this.sampleTestTypes = Arrays.asList(sampleTestTypes);
		}

		@Override
		boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
			for (SampleTestDto sampleTest : sampleTests) {
				if (sampleTest.getTestResult() == SampleTestResultType.POSITIVE
						&& sampleTestTypes.contains(sampleTest.getTestType())) {
					return true;
				}
			}
			return false;
		}

		@Override
		StringBuilder appendDesc(StringBuilder stringBuilder) {
			stringBuilder.append("one positive lab result of: ");
			for (int i=0; i<sampleTestTypes.size(); i++) {
				if (i > 0) stringBuilder.append(", ");
				stringBuilder.append(sampleTestTypes.get(i).toString());	
			}

			return stringBuilder;
		}
	}
}
