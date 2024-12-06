package de.symeda.sormas.ui.person;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PersonFormHelper {

	public static Window warningSimilarPersons(String nationalHealthId, String currentPersonUuid, Runnable callback) {
		if (StringUtils.isNoneBlank(nationalHealthId)) {
			PersonDto currentPerson = new PersonDto();
			currentPerson.setNationalHealthId(nationalHealthId);
			List<SimilarPersonDto> similarPersonDtos =
				FacadeProvider.getPersonFacade().getSimilarPersonDtos(PersonSimilarityCriteria.forPerson(currentPerson, true));
			List<SimilarPersonDto> filteredSimilarPersons = similarPersonDtos.stream()
				.filter(similarPersonDto -> !similarPersonDto.getUuid().equals(currentPersonUuid))
				.collect(Collectors.toList());

			if (!filteredSimilarPersons.isEmpty()) {
				PersonSelectionGrid similarPersonGrid = new PersonSelectionGrid();
				similarPersonGrid.loadData(filteredSimilarPersons);

				final CommitDiscardWrapperComponent<PersonSelectionGrid> component = new CommitDiscardWrapperComponent<>(similarPersonGrid);
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionDone));
				component.getDiscardButton().setVisible(false);
				component.getWrappedComponent().setWidth(800, Sizeable.Unit.PIXELS);

				Window popupWindow = VaadinUiUtil.showPopupWindow(component);
				component.addDoneListener(() -> {
					popupWindow.close();
					callback.run();
				});
				popupWindow.setCaption(I18nProperties.getString(Strings.headingSimilarPerson));
				popupWindow.setWidth(900, Sizeable.Unit.PIXELS);
				return popupWindow;
			}
		}
		return null;
	}
}
