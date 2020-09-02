package de.symeda.sormas.app.backend.sormastosormas;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.sormastosormas.HealthDepartmentServerReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSourceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class SormasToSormasOriginInfoDtoHelper extends AdoDtoHelper<SormasToSormasOriginInfo, SormasToSormasSourceDto> {

	@Override
	protected Class<SormasToSormasOriginInfo> getAdoClass() {
		return SormasToSormasOriginInfo.class;
	}

	@Override
	protected Class<SormasToSormasSourceDto> getDtoClass() {
		return SormasToSormasSourceDto.class;
	}

	@Override
	protected Call<List<SormasToSormasSourceDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<SormasToSormasSourceDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<SormasToSormasSourceDto> sormasToSormasSourceDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected void fillInnerFromDto(SormasToSormasOriginInfo sormasToSormasOriginInfo, SormasToSormasSourceDto dto) {
		sormasToSormasOriginInfo.setHealthDepartment(dto.getHealthDepartment().getUuid());
		sormasToSormasOriginInfo.setOwnershipHandedOver(dto.isOwnershipHandedOver());
		sormasToSormasOriginInfo.setSenderName(dto.getSenderName());
		sormasToSormasOriginInfo.setSenderEmail(dto.getSenderEmail());
		sormasToSormasOriginInfo.setSenderPhoneNumber(dto.getSenderPhoneNumber());
		sormasToSormasOriginInfo.setComment(dto.getComment());
	}

	@Override
	protected void fillInnerFromAdo(SormasToSormasSourceDto dto, SormasToSormasOriginInfo sormasToSormasOriginInfo) {
		dto.setHealthDepartment(new HealthDepartmentServerReferenceDto(sormasToSormasOriginInfo.getHealthDepartment()));
		dto.setOwnershipHandedOver(sormasToSormasOriginInfo.isOwnershipHandedOver());
		dto.setSenderName(sormasToSormasOriginInfo.getSenderName());
		dto.setSenderEmail(sormasToSormasOriginInfo.getSenderEmail());
		dto.setSenderPhoneNumber(sormasToSormasOriginInfo.getSenderPhoneNumber());
		dto.setComment(sormasToSormasOriginInfo.getComment());
	}
}
