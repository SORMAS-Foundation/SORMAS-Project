package de.symeda.sormas.ui.campaign.jsonHelpers;


/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.AbstractSelect;

/**
 * A filtering dropdown single-select. Suitable for newItemsAllowed, but it's
 * turned of by default to avoid mistakes. Items are filtered based on user
 * input, and loaded dynamically ("lazy-loading") from the server. You can turn
 * on newItemsAllowed and change filtering mode (and also turn it off), but you
 * can not turn on multi-select mode.
 *
 * @author Vaadin Ltd
 *
 * @deprecated As of 8.0 replaced by {@link com.vaadin.ui.ComboBox} based on the
 *             new data binding API
 */
@SuppressWarnings("serial")
@Deprecated
public class BasicRadioGroupHelper extends AbstractSelect
        implements AbstractSelect.Filtering, FieldEvents.BlurNotifier,
        FieldEvents.FocusNotifier {

	@Override
	public void addFocusListener(FocusListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBlurListener(BlurListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeBlurListener(BlurListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFilteringMode(FilteringMode filteringMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FilteringMode getFilteringMode() {
		// TODO Auto-generated method stub
		return null;
	}
}