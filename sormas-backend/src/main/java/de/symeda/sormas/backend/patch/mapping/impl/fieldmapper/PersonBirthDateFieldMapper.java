package de.symeda.sormas.backend.patch.mapping.impl.fieldmapper;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.patch.mapping.FieldCustomMapper;
import de.symeda.sormas.api.patch.mapping.FieldPatchRequest;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.patch.mapping.impl.valuemapper.DatePatchMapper;

/**
 * For now this FieldMapper will not be allowed and "deactivated" through the list of forbidden fields.
 */
@ApplicationScoped
public class PersonBirthDateFieldMapper implements FieldCustomMapper {

	@Inject
	private DatePatchMapper dateMapper;

	@Override
	public Optional<DataPatchFailure> map(FieldPatchRequest request) {

		Object untypedTarget = request.getTarget();
		if (!(untypedTarget instanceof PersonDto)) {
			return Optional.of(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL));
		}

		PersonDto person = (PersonDto) untypedTarget;

		Object untypedValue = request.getValue();
		Date birthDate = dateMapper.map(untypedValue, Date.class);

		// TODO: remove duplication.
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		// In calendar API months are indexed from 0 @see https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html#MONTH
		int birthdateMonth = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		if (request.getReplacementType() == DataReplacementStrategy.IF_NOT_ALREADY_PRESENT) {

			Integer currentDayOfMonth = person.getBirthdateDD();
			Integer currentBirthdateMonth = person.getBirthdateMM();
			Integer currentYear = person.getBirthdateYYYY();

			if (currentDayOfMonth != dayOfMonth || currentBirthdateMonth != birthdateMonth || currentYear != year) {
				return Optional.of(
					new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FORBIDDEN_VALUE_OVERRIDE)
						.setProvidedFieldValue(untypedValue));
			}

		}

		person.setBirthdateMM(birthdateMonth);
		person.setBirthdateDD(dayOfMonth);
		person.setBirthdateYYYY(year);

		return Optional.empty();
	}

	@Override
	public Set<String> supportedFields() {
		return Set.of(PersonDto.I18N_PREFIX + "." + PersonDto.BIRTH_DATE);
	}
}
