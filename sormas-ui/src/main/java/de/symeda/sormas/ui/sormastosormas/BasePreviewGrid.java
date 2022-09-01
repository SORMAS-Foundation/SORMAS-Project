/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.sormastosormas;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.util.ReflectTools;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasPersonPreview;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ShowDetailsListener;

public abstract class BasePreviewGrid<T extends PseudonymizableDto> extends Grid<T> {

	private static final long serialVersionUID = -8437095723489884925L;

	private static final String PERSON_NAME = "personName";
	private static final String BIRTH_DATE = "birthdDate";

	public BasePreviewGrid(
		Class<T> beanType,
		String entityCaptionTag,
		Function<String, Boolean> existsUuidFn,
		Consumer<String> navigateToUuid,
		boolean isPendingRequest) {
		super(beanType);

		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		buildGrid();
		addUuidColumnHandler(entityCaptionTag, existsUuidFn, navigateToUuid, isPendingRequest);
	}

	protected abstract void buildGrid();

	private void addUuidColumnHandler(
		String entityCaptionTag,
		Function<String, Boolean> existsFn,
		Consumer<String> navigateToUuid,
		boolean isPendingRequest) {
		addItemClickListener(new ShowDetailsListener<>(EntityDto.UUID, e -> {
			String uuid = e.getUuid();
			if (existsFn.apply(uuid)) {
				navigateToUuid.accept(uuid);
				fireEvent(new NavigateEvent(this));
			} else if (isPendingRequest) {
				new Notification(I18nProperties.getString(Strings.messageAcceptRequestToNavigate), Notification.Type.TRAY_NOTIFICATION)
					.show(Page.getCurrent());
			} else {
				new Notification(
					String.format(I18nProperties.getString(Strings.messageEntityNotFound), I18nProperties.getCaption(entityCaptionTag).toLowerCase()),
					Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
			}
		}));
	}

	protected List<String> createPersonColumns(Function<T, SormasToSormasPersonPreview> getPerson) {
		((Grid.Column<T, ?>) addComponentColumn(previewData -> {
			SormasToSormasPersonPreview person = getPerson.apply(previewData);
			if (person.isPseudonymized()) {
				return new Label(I18nProperties.getCaption(Captions.inaccessibleValue));
			}
			return new Label(person.getFirstName() + " " + person.getLastName());
		})).setId(PERSON_NAME).setStyleGenerator(item -> {
			if (item.isPseudonymized()) {
				return CssStyles.INACCESSIBLE_COLUMN;
			}

			return "";
		});
		addComponentColumn(
			previewData -> new Label(
				DateFormatHelper.formatDate(
					getPerson.apply(previewData).getBirthdateDD(),
					getPerson.apply(previewData).getBirthdateMM(),
					getPerson.apply(previewData).getBirthdateYYYY()))).setId(BIRTH_DATE);
		addComponentColumn(previewData -> new Label(getPerson.apply(previewData).getSex().toString())).setId(SormasToSormasPersonPreview.SEX);
		addComponentColumn(previewData -> new Label(LocationHelper.buildLocationString(getPerson.apply(previewData).getAddress())))
			.setId(SormasToSormasPersonPreview.ADDRESS);

		return Arrays.asList(PERSON_NAME, BIRTH_DATE, SormasToSormasPersonPreview.SEX, SormasToSormasPersonPreview.ADDRESS);
	}

	@Override
	public void setItems(Collection<T> items) {
		super.setItems(items);
		setHeightByRows(items.size() > 0 ? (Math.min(items.size(), 10)) : 1);
	}

	public Registration addNavigateListener(NavigateListener listener) {
		return addListener(NavigateEvent.class, listener, NavigateListener.ON_NAVIGATE_METHOD);
	}

	public interface NavigateListener extends SerializableEventListener {

		Method ON_NAVIGATE_METHOD = ReflectTools.findMethod(NavigateListener.class, "onNavigate", NavigateEvent.class);

		void onNavigate(NavigateEvent event);

	}

	public static final class NavigateEvent extends Component.Event {

		private static final long serialVersionUID = -6258483442328832173L;

		public NavigateEvent(BasePreviewGrid<?> source) {
			super(source);
		}
	}
}
