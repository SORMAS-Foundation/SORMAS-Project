/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class SampleTestGrid extends Grid implements ItemClickListener {

	private static final String EDIT_BTN_ID = "edit";
	private SampleReferenceDto sampleRef;
	private int caseSampleCount;

	public SampleTestGrid(SampleReferenceDto sampleRef, int caseSampleCount) {
		setSizeFull();

		BeanItemContainer<SampleTestDto> container = new BeanItemContainer<SampleTestDto>(SampleTestDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addIconColumn(generatedContainer, EDIT_BTN_ID, FontAwesome.PENCIL_SQUARE);
		setContainerDataSource(generatedContainer);

		setColumns(EDIT_BTN_ID, SampleTestDto.TEST_TYPE, SampleTestDto.TEST_DATE_TIME, SampleTestDto.LAB,
				SampleTestDto.LAB_USER, SampleTestDto.TEST_RESULT, SampleTestDto.TEST_RESULT_VERIFIED);

		getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
		getColumn(EDIT_BTN_ID).setWidth(60);
		getColumn(SampleTestDto.TEST_RESULT_VERIFIED).setRenderer(new BooleanRenderer());

		getColumn(SampleTestDto.TEST_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getLocalShortDateTimeFormat()));

		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
					SampleTestDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}
		
		addItemClickListener(this);

		this.sampleRef = sampleRef;
		this.caseSampleCount = caseSampleCount;
		reload();
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<SampleTestDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SampleTestDto>) container.getWrappedContainer();
	}

	public void reload() {
		List<SampleTestDto> sampleTests = ControllerProvider.getSampleTestController().getSampleTestsBySample(sampleRef);
		getContainer().removeAllItems();
		getContainer().addAll(sampleTests);
		this.setHeightByRows(getContainer().size() < 10 ? (getContainer().size() > 0 ? getContainer().size() : 1) : 10);
	}

	public void refresh(SampleTestDto sample) {
		// We avoid updating the whole table through the backend here so we can
		// get a partial update for the grid
		BeanItem<SampleTestDto> item = getContainer().getItem(sample);
		if(item != null) {
			// Updated product
			@SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(SampleTestDto.UUID);
			p.fireValueChange();
		} else {
			// New product
			getContainer().addBean(sample);
		}
	}

	public void remove(SampleTestDto sample) {
		getContainer().removeItem(sample);
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		SampleTestDto sampleTest = (SampleTestDto)event.getItemId();
		if(event.getPropertyId() != null && (EDIT_BTN_ID.equals(event.getPropertyId()) || event.isDoubleClick())) {
			ControllerProvider.getSampleTestController().edit(sampleTest, caseSampleCount, this::reload);
		}
	}

}
