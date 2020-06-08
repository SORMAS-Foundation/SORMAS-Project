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

package de.symeda.sormas.app.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.tooltip.Tooltip;

/**
 * Created by Orson on 20/11/2017.
 */
public class ControlLabelOnTouchListener implements View.OnClickListener, Tooltip.Callback {

	private TextView lblControlLabel;
	private ControlPropertyField teboPropertyField;
	private Tooltip.TooltipView tooltip;

	public ControlLabelOnTouchListener(ControlPropertyField teboPropertyField) {
		this.teboPropertyField = teboPropertyField;
	}

	/*
	 * @Override
	 * public boolean onTouch(View v, MotionEvent event) {
	 * label = (TextView)v;
	 * if (label != null && event.getAction() == MotionEvent.ACTION_UP) {
	 * if (label.getError() != null) {
	 * if (label.isFocused()) {
	 * label.clearFocus(); // closes error popup
	 * return true;
	 * }
	 * } else if (teboPropertyField.getDescription() != null && !teboPropertyField.getDescription().isEmpty()) {
	 * if (null == tooltip) {
	 * int[] lblControlLabelLocation = new int[2];
	 * label.getLocationOnScreen(lblControlLabelLocation);
	 * int x = lblControlLabelLocation[0];
	 * int y = lblControlLabelLocation[1];
	 * //Shift x by padding
	 * //x = x + (int) getResources().getDimension(R.dimen.tooltipDefaultPadding);
	 * Context context = teboPropertyField.getContext();
	 * DisplayMetrics metrics = teboPropertyField.getResources().getDisplayMetrics();
	 * Tooltip.ClosePolicy mClosePolicy = Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME;
	 * tooltip = Tooltip.make(
	 * context,
	 * new Tooltip.Builder()
	 * .anchor(new Point(x, y), Tooltip.Gravity.TOP)
	 * .closePolicy(mClosePolicy, 10000)
	 * .text(teboPropertyField.getDescription())
	 * .withArrow(true)
	 * .withOverlay(false)
	 * .withStyleId(R.style.ToolTipStyleOverride)
	 * .maxWidth((int) (metrics.widthPixels / 1.25))
	 * .withCallback(this)
	 * .build()
	 * );
	 * tooltip.show();
	 * } else {
	 * tooltip.hide();
	 * tooltip = null;
	 * }
	 * }
	 * }
	 * return false;
	 * }
	 */

	@Override
	public void onTooltipClose(final Tooltip.TooltipView view, final boolean fromUser, final boolean containsTouch) {
		//Log.d(TAG, "onTooltipClose: " + view + ", fromUser: " + fromUser + ", containsTouch: " + containsTouch);
		if (null != tooltip && tooltip.getTooltipId() == view.getTooltipId()) {
			tooltip = null;
		}
	}

	@Override
	public void onTooltipFailed(Tooltip.TooltipView view) {
		//Log.d(TAG, "onTooltipFailed: " + view.getTooltipId());
	}

	@Override
	public void onTooltipShown(Tooltip.TooltipView view) {
		//Log.d(TAG, "onTooltipShown: " + view.getTooltipId());
	}

	@Override
	public void onTooltipHidden(Tooltip.TooltipView view) {
		//Log.d(TAG, "onTooltipHidden: " + view.getTooltipId());
	}

	@Override
	public void onClick(View v) {
		lblControlLabel = (TextView) v;

		if (lblControlLabel == null)
			return;

		if (teboPropertyField.getDescription() == null || teboPropertyField.getDescription().isEmpty())
			return;

		if (null == tooltip) {
			int[] lblControlLabelLocation = new int[2];
			lblControlLabel.getLocationOnScreen(lblControlLabelLocation);

			int x = lblControlLabelLocation[0];
			int y = lblControlLabelLocation[1];

			//Shift x by padding
			//x = x + (int) getResources().getDimension(R.dimen.tooltipDefaultPadding);

			Context context = teboPropertyField.getContext();
			DisplayMetrics metrics = teboPropertyField.getResources().getDisplayMetrics();
			Tooltip.ClosePolicy mClosePolicy = Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME;

			tooltip = Tooltip.make(
				context,
				new Tooltip.Builder().anchor(new Point(x, y), Tooltip.Gravity.TOP)
					.closePolicy(mClosePolicy, 10000)
					.text(teboPropertyField.getDescription())
					.withArrow(true)
					.withOverlay(false)
					.withStyleId(R.style.ToolTipStyleOverride)
					.maxWidth((int) (metrics.widthPixels / 1.25))
					.withCallback(this)
					.build());
			tooltip.show();
		} else {
			tooltip.hide();
			tooltip = null;
		}
	}
}
