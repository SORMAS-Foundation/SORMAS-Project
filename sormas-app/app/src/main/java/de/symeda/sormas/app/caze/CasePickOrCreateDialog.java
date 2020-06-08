/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.caze;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseCriteria;
import de.symeda.sormas.app.backend.caze.CaseSimilarityCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogCasePickOrCreateLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ViewHelper;

public class CasePickOrCreateDialog extends AbstractDialog {

	public static final String TAG = CasePickOrCreateDialog.class.getSimpleName();

	private DialogCasePickOrCreateLayoutBinding contentBinding;

	private CaseSimilarityCriteria criteria;
	private List<Case> similarCases;

	private IEntryItemOnClickListener similarCaseItemClickCallback;
	private final ObservableField<Case> selectedCase = new ObservableField<>();

	// Static methods

	public static void pickOrCreateCase(final Case newCase, final Consumer<Case> pickedCaseCallback) {
		final CasePickOrCreateDialog dialog = new CasePickOrCreateDialog(BaseActivity.getActiveActivity(), newCase);

		if (!dialog.hasSimilarCases()) {
			pickedCaseCallback.accept(newCase);
			return;
		}

		dialog.setPositiveCallback(() -> {
			pickedCaseCallback.accept(dialog.getSelectedCase() != null ? dialog.getSelectedCase() : newCase);
		});

		dialog.show();
		dialog.getPositiveButton().setEnabled(false);
	}

	// Constructors

	private CasePickOrCreateDialog(final FragmentActivity activity, Case newCase) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_case_pick_or_create_layout,
			R.layout.dialog_root_two_button_panel_layout,
			R.string.heading_pick_or_create_case,
			-1);

		CaseCriteria caseCriteria = new CaseCriteria().setRegion(newCase.getRegion()).setDisease(newCase.getDisease());
		this.criteria = new CaseSimilarityCriteria().setCaseCriteria(caseCriteria)
			.setFirstName(newCase.getPerson().getFirstName())
			.setLastName(newCase.getPerson().getLastName())
			.setReportDate(newCase.getReportDate());

		this.setSelectedCase(null);
	}

	// Instance methods

	private boolean hasSimilarCases() {
		updateSimilarCases();
		return !similarCases.isEmpty();
	}

	private void updateSimilarCases() {
		similarCases = DatabaseHelper.getCaseDao().getSimilarCases(criteria);
	}

	private void setupControlListeners() {
		similarCaseItemClickCallback = (v, item) -> {
			if (item == null) {
				return;
			}

			Case caseItem = (Case) item;
			String tag = getActivity().getResources().getString(R.string.tag_row_item_case_pick_or_create);
			ArrayList<View> views = ViewHelper.getViewsByTag(contentBinding.existingCasesList, tag);
			setSelectedCase(null);

			for (View itemView : views) {
				try {
					int itemViewId = itemView.getId();
					int vId = v.getId();

					if (itemViewId == vId && v.isSelected()) {
						itemView.setSelected(false);
					} else if (itemViewId == vId && !v.isSelected()) {
						itemView.setSelected(true);
						setSelectedCase(caseItem);
					} else {
						itemView.setSelected(false);
					}
				} catch (NumberFormatException ex) {
					NotificationHelper.showDialogNotification(CasePickOrCreateDialog.this, NotificationType.ERROR, R.string.error_internal_error);
				}
			}

			if (getSelectedCase() != null) {
				contentBinding.cbCreateCase.setValue(Boolean.FALSE);
				getPositiveButton().setEnabled(true);
			} else {
				getPositiveButton().setEnabled(false);
			}
		};

		contentBinding.cbCreateCase.addValueChangedListener(e -> {
			if (Boolean.TRUE.equals(e.getValue())) {
				setSelectedCase(null);
				getPositiveButton().setEnabled(true);
				String tag = getActivity().getResources().getString(R.string.tag_row_item_case_pick_or_create);
				ArrayList<View> views = ViewHelper.getViewsByTag(contentBinding.existingCasesList, tag);
				for (View itemView : views) {
					itemView.setSelected(false);
				}
			} else {
				getPositiveButton().setEnabled(false);
			}
		});
	}

	private ObservableArrayList makeObservable(List<Case> cases) {
		ObservableArrayList<Case> newList = new ObservableArrayList<>();
		newList.addAll(cases);
		return newList;
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogCasePickOrCreateLayoutBinding) binding;

		// Needs to be done here because callback needs to be initialized before being bound to the layout
		setupControlListeners();

		if (!binding.setVariable(de.symeda.sormas.app.BR.similarCases, makeObservable(similarCases))) {
			Log.e(TAG, "There is no variable 'similarCases' in layout " + layoutName);
		}

		if (!binding.setVariable(de.symeda.sormas.app.BR.similarCaseItemClickCallback, similarCaseItemClickCallback)) {
			Log.e(TAG, "There is no variable 'similarCasesItemClickCallback' in layout " + layoutName);
		}
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding buttonPanelBinding) {
		this.selectedCase.notifyChange();
	}

	@Override
	public int getPositiveButtonText() {
		return R.string.action_confirm;
	}

	@Override
	public int getNegativeButtonText() {
		return R.string.action_dismiss;
	}

	// Getters & setters

	private Case getSelectedCase() {
		return selectedCase.get();
	}

	private void setSelectedCase(Case selectedCase) {
		this.selectedCase.set(selectedCase);
	}
}
