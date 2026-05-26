package de.symeda.sormas.backend.patch.customizablefield;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.SinglePatchResult;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueFacadeEjb;
import de.symeda.sormas.backend.patch.DataPatcherImpl;

/**
 * Wrapper arround {@link de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade} to be able to patch customizable field
 * values.
 */
@ApplicationScoped
public class CustomizableFieldDataPatcher {

	@EJB
	private CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal customizableFieldValueFacade;

	public List<Tuple<SinglePatchResult, CustomizableFieldValueDto>> patch(Request request) {
		CaseDataPatchRequest caseDataPatchRequest = request.getCaseDataPatchRequest();

		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> valuesForEntity =
			customizableFieldValueFacade.getValuesForEntity(caseDataPatchRequest.getCaseUuid(), CustomizableFieldContext.CASE);

		return List.of();
	}

	public void save(List<CustomizableFieldValueDto> values) {
		values.forEach(customizableFieldValueFacade::save);
	}

	public static final class Request {

		@NotNull
		private CaseDataPatchRequest caseDataPatchRequest;

		@NotNull
		private List<DataPatcherImpl.SingleFieldPatchResult> patchingTuples;

		@NotNull
		private CaseDataDto caseDataDto;

		public CaseDataPatchRequest getCaseDataPatchRequest() {
			return caseDataPatchRequest;
		}

		public Request setCaseDataPatchRequest(CaseDataPatchRequest caseDataPatchRequest) {
			this.caseDataPatchRequest = caseDataPatchRequest;
			return this;
		}

		public List<DataPatcherImpl.SingleFieldPatchResult> getPatchingTuples() {
			return patchingTuples;
		}

		public Request setPatchingTuples(List<DataPatcherImpl.SingleFieldPatchResult> patchingTuples) {
			this.patchingTuples = patchingTuples;
			return this;
		}

		public CaseDataDto getCaseDataDto() {
			return caseDataDto;
		}

		public Request setCaseDataDto(CaseDataDto caseDataDto) {
			this.caseDataDto = caseDataDto;
			return this;
		}
	}
}
