package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlPropertyField;

/**
 * A dedicated dialog that lists the captions of all ControlPropertyFields in a layout and their
 * corresponding description.
 */
public class HelpDialog  {

    private AlertDialog.Builder builder;
    private String title;

    /**
     * @param context The context from which the constructor is called
     * @param rootView The view used as the root when searching for ControlPropertyFields
     */
    public HelpDialog(Context context, ViewGroup rootView) {
        builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(
                context.getResources().getText(R.string.action_close),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        title = context.getResources().getText(R.string.headline_help).toString();

        StringBuilder helpStringBuilder = new StringBuilder();
        extendHelpString(helpStringBuilder, rootView);
        builder.setMessage(Html.fromHtml(helpStringBuilder.toString()));
    }

    public void show() {
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setTitle(title);
        dialog.show();
    }

    private static void extendHelpString(StringBuilder sb, ViewGroup parent) {
        if (parent == null) return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyField && child.getVisibility() == View.VISIBLE) {
                ControlPropertyField propertyField = (ControlPropertyField) child;
                if (propertyField.getCaption() != null) {
                    sb.append("<b>").append(Html.escapeHtml(propertyField.getCaption())).append("</b>").append("<br>");
                    if (!StringUtils.isEmpty(propertyField.getDescription())) {
                        sb.append(Html.escapeHtml(propertyField.getDescription()));
                    } else {
                        sb.append(Html.escapeHtml("-"));
                    }
                    sb.append("<br><br>");
                }
            } else if (child instanceof ViewGroup) {
                extendHelpString(sb, (ViewGroup) child);
            }
        }
    }

}

