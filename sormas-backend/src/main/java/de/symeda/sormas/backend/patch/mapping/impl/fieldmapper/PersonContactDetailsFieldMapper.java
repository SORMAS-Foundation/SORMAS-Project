package de.symeda.sormas.backend.patch.mapping.impl.fieldmapper;

import static de.symeda.sormas.backend.patch.PatchFieldHelper.PATH_SEPARATOR;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.FieldCustomMapper;
import de.symeda.sormas.api.patch.mapping.FieldPatchRequest;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.utils.DataHelper;

@ApplicationScoped
public class PersonContactDetailsFieldMapper implements FieldCustomMapper {

	private static final Logger logger = LoggerFactory.getLogger(PersonContactDetailsFieldMapper.class);

	@Override
	public Optional<DataPatchFailure> map(FieldPatchRequest request) {
		Object untypedTarget = request.getTarget();
		if (!(untypedTarget instanceof PersonDto)) {
			return Optional.of(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL));
		}
		PersonDto personDto = (PersonDto) untypedTarget;

		Supplier<PersonContactDetailDto> appropriateSupplier;
		Predicate<PersonContactDetailDto> appropriatePredicate;

		if (request.getFieldName().contains(PersonContactDetailDto.PHONE_NUMBER_TYPE)) {
			appropriatePredicate = buildPredicateFor(PersonContactDetailType.PHONE);
			appropriateSupplier = () -> buildPhoneContactDetail(request, personDto);

		} else {
			appropriatePredicate = buildPredicateFor(PersonContactDetailType.EMAIL);
			appropriateSupplier = () -> buildEmailContactDetail(request, personDto);
		}

		Optional<PersonContactDetailDto> alreadyPresentContactDetail = personDto.getPersonContactDetails()
			.stream()
			.filter(appropriatePredicate)
			.filter(contactDetail -> contactDetail.getDetails().equals(request.getValue()))
			.findAny();

		if (alreadyPresentContactDetail.isPresent()) {
			logger.debug("Person contact details already present nothing to do: [{}]", alreadyPresentContactDetail.get());
		} else {
			PersonContactDetailDto contactDetail = appropriateSupplier.get();
			logger.debug("Person contact details not already present, therefore added: [{}]", contactDetail);
			personDto.getPersonContactDetails().add(contactDetail);
		}

		return Optional.empty();
	}

	private static @NotNull Predicate<PersonContactDetailDto> buildPredicateFor(PersonContactDetailType email) {
		return contactDetail -> contactDetail.getPersonContactDetailType().equals(email);
	}

	private PersonContactDetailDto buildGenericContactDetail(FieldPatchRequest request, PersonDto personDto) {
		PersonContactDetailDto detail = new PersonContactDetailDto();
		detail.setUuid(DataHelper.createUuid());
		detail.setPerson(personDto.toReference());
		detail.setDetails((String) request.getValue());
		detail.setAdditionalInformation(request.getOrigin());

		return detail;
	}

	private PersonContactDetailDto buildPhoneContactDetail(FieldPatchRequest request, PersonDto personDto) {
		PersonContactDetailDto detail = buildGenericContactDetail(request, personDto);
		detail.setPersonContactDetailType(PersonContactDetailType.PHONE);
		detail.setPhoneNumberType(PhoneNumberType.OTHER);

		return detail;
	}

	private PersonContactDetailDto buildEmailContactDetail(FieldPatchRequest request, PersonDto personDto) {
		PersonContactDetailDto detail = buildGenericContactDetail(request, personDto);
		detail.setPersonContactDetailType(PersonContactDetailType.EMAIL);

		return detail;
	}

	@Override
	public Set<String> supportedFields() {
		return Stream.of(PersonContactDetailDto.PHONE_NUMBER_TYPE, PersonContactDetailDto.DETAILS)
			.map(suffix -> PersonDto.I18N_PREFIX + PATH_SEPARATOR + PersonDto.PERSON_CONTACT_DETAILS + PATH_SEPARATOR + suffix)
			.collect(Collectors.toSet());
	}
}
