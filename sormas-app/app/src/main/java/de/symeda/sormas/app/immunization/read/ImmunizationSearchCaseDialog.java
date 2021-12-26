package de.symeda.sormas.app.immunization.read;

import android.content.Context;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.databinding.DialogImmunizationSearchForCaseLayoutBinding;
import de.symeda.sormas.app.util.Consumer;

public class ImmunizationSearchCaseDialog extends AbstractDialog {

	private DialogImmunizationSearchForCaseLayoutBinding contentBinding;

	private ImmunizationSearchCaseDialog(FragmentActivity activity) {

		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_immunization_search_for_case_layout,
			R.layout.dialog_root_two_button_panel_layout,
			R.string.heading_search_for_case,
			-1);
	}

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogImmunizationSearchForCaseLayoutBinding) binding;
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		this.contentBinding.setSearchFieldContent("");
	}

	public static void searchCaseToLinkImmunization(FragmentActivity activity, Consumer<String> caseSearchCallback) {
		final ImmunizationSearchCaseDialog searchCaseDialog = new ImmunizationSearchCaseDialog(activity);

		searchCaseDialog.setPositiveCallback(() -> {
			caseSearchCallback.accept(searchCaseDialog.contentBinding.searchField.getValue());
		});
		searchCaseDialog.show();
	}
}
