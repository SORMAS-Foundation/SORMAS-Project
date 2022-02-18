package de.symeda.sormas.app.backend.sormastosormas;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class SormasToSormasOriginInfoDtoHelper extends AdoDtoHelper<SormasToSormasOriginInfo, SormasToSormasOriginInfoDto> {

	@Override
	protected Class<SormasToSormasOriginInfo> getAdoClass() {
		return SormasToSormasOriginInfo.class;
	}

	@Override
	protected Class<SormasToSormasOriginInfoDto> getDtoClass() {
		return SormasToSormasOriginInfoDto.class;
	}

	@Override
	protected Call<List<SormasToSormasOriginInfoDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid)  throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<SormasToSormasOriginInfoDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<SormasToSormasOriginInfoDto> sormasToSormasSourceDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected void fillInnerFromDto(SormasToSormasOriginInfo sormasToSormasOriginInfo, SormasToSormasOriginInfoDto dto) {
		sormasToSormasOriginInfo.setOrganizationId(dto.getOrganizationId());
		sormasToSormasOriginInfo.setOwnershipHandedOver(dto.isOwnershipHandedOver());
		sormasToSormasOriginInfo.setWithAssociatedContacts(dto.isWithAssociatedContacts());
		sormasToSormasOriginInfo.setWithSamples(dto.isWithSamples());
		sormasToSormasOriginInfo.setWithEventParticipants(dto.isWithEventParticipants());
		sormasToSormasOriginInfo.setSenderName(dto.getSenderName());
		sormasToSormasOriginInfo.setSenderEmail(dto.getSenderEmail());
		sormasToSormasOriginInfo.setSenderPhoneNumber(dto.getSenderPhoneNumber());
		sormasToSormasOriginInfo.setComment(dto.getComment());
	}

	@Override
	protected void fillInnerFromAdo(SormasToSormasOriginInfoDto dto, SormasToSormasOriginInfo sormasToSormasOriginInfo) {
		dto.setOrganizationId(sormasToSormasOriginInfo.getOrganizationId());
		dto.setOwnershipHandedOver(sormasToSormasOriginInfo.isOwnershipHandedOver());
		dto.setWithAssociatedContacts(sormasToSormasOriginInfo.isWithAssociatedContacts());
		dto.setWithSamples(sormasToSormasOriginInfo.isWithSamples());
		dto.setWithEventParticipants(sormasToSormasOriginInfo.isWithEventParticipants());
		dto.setSenderName(sormasToSormasOriginInfo.getSenderName());
		dto.setSenderEmail(sormasToSormasOriginInfo.getSenderEmail());
		dto.setSenderPhoneNumber(sormasToSormasOriginInfo.getSenderPhoneNumber());
		dto.setComment(sormasToSormasOriginInfo.getComment());
	}

    @Override
    protected long getApproximateJsonSizeInBytes() {
        return 0;
    }
}
