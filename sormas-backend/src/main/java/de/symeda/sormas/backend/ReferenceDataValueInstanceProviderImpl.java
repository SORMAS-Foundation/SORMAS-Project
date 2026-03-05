package de.symeda.sormas.backend;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.util.InstanceProvider;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class ReferenceDataValueInstanceProviderImpl implements ReferenceDataValueInstanceProvider {

	public static final Date DATE_ALL_VALUES = new Date(0);
	private Map<Class<? extends ReferenceDto>, Supplier<List<? extends ReferenceDto>>> dictionary;

	@PostConstruct
	private void init() {
		dictionary = Map.ofEntries(
			Map.entry(CountryReferenceDto.class, () -> getInstance(CountryFacade.class).getAllActiveAsReference()),
			Map.entry(RegionReferenceDto.class, () -> getInstance(RegionFacade.class).getAllActiveAsReference()),
			Map.entry(DistrictReferenceDto.class, () -> getInstance(DistrictFacade.class).getAllActiveAsReference()),
			Map.entry(
				CommunityReferenceDto.class,
				() -> getInstance(CommunityFacadeEjb.class).getAllAfter(DATE_ALL_VALUES)
					.stream()
					.map(CommunityDto::toReference)
					.collect(Collectors.toList())),
			Map.entry(
				FacilityReferenceDto.class,
				() -> getInstance(FacilityFacadeEjb.class).getAllWithoutRegionAfter(DATE_ALL_VALUES)
					.stream()
					.map(FacilityDto::toReference)
					.collect(Collectors.toList())),
			Map.entry(
				PointOfEntryReferenceDto.class,
				() -> getInstance(PointOfEntryFacade.class).getAllAfter(DATE_ALL_VALUES)
					.stream()
					.map(PointOfEntryDto::toReference)
					.collect(Collectors.toList())));
	}

	private <T> T getInstance(Class<T> ejb) {
		return InstanceProvider.getInstanceFor(ejb);
	}

	@Override
	public <T extends ReferenceDto> List<T> getAll(Class<T> referenceType) {
		return (List<T>) Optional.ofNullable(dictionary.get(referenceType).get()).orElse(Collections.emptyList());
	}

	@Override
	public <T extends ReferenceDto> Optional<T> getOne(String caption, Class<T> referenceType) {
		return getAll(referenceType).stream()
			.filter(referenceDto -> StringNormalizer.normalize(referenceDto.getCaption()).equals(StringNormalizer.normalize(caption)))
			.findAny();
	}

}
