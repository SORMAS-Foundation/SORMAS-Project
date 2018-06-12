package de.symeda.sormas.app.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.TeboPropertyField;
import de.symeda.sormas.app.component.tooltip.Tooltip;

/**
 * Created by Orson on 20/11/2017.
 */
public class ControlLabelOnTouchListener implements View.OnClickListener, Tooltip.Callback {
    private TextView lblControlLabel;
    private TeboPropertyField teboPropertyField;
    private Tooltip.TooltipView tooltip;

    public ControlLabelOnTouchListener(TeboPropertyField teboPropertyField) {
        this.teboPropertyField = teboPropertyField;
    }

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        lblControlLabel = (TextView)v;
        if (lblControlLabel != null && event.getAction() == MotionEvent.ACTION_UP) {
            if (lblControlLabel.getError() != null) {
                if (lblControlLabel.isFocused()) {
                    lblControlLabel.clearFocus(); // closes error popup
                    return true;
                }
            } else if (teboPropertyField.getDescription() != null && !teboPropertyField.getDescription().isEmpty()) {
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
                            new Tooltip.Builder()
                                    .anchor(new Point(x, y), Tooltip.Gravity.TOP)
                                    .closePolicy(mClosePolicy, 10000)
                                    .text(teboPropertyField.getDescription())
                                    .withArrow(true)
                                    .withOverlay(false)
                                    .withStyleId(R.style.ToolTipStyleOverride)
                                    .maxWidth((int) (metrics.widthPixels / 1.25))
                                    .withCallback(this)
                                    .build()
                    );
                    tooltip.show();
                } else {
                    tooltip.hide();
                    tooltip = null;
                }
            }
        }
        return false;
    }*/

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
        lblControlLabel = (TextView)v;

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
                    new Tooltip.Builder()
                            .anchor(new Point(x, y), Tooltip.Gravity.TOP)
                            .closePolicy(mClosePolicy, 10000)
                            .text(teboPropertyField.getDescription())
                            .withArrow(true)
                            .withOverlay(false)
                            .withStyleId(R.style.ToolTipStyleOverride)
                            .maxWidth((int) (metrics.widthPixels / 1.25))
                            .withCallback(this)
                            .build()
            );
            tooltip.show();
        } else {
            tooltip.hide();
            tooltip = null;
        }
    }
}
