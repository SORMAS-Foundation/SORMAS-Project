/* Copyright 2012 Charles Harley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.charlesharley.example.android.customdrawablestates;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageListItemView extends RelativeLayout {

    /**
     * Custom message unread state variable for use with a {@link android.graphics.drawable.StateListDrawable}.
     */
    private static final int[] STATE_MESSAGE_UNREAD = {R.attr.state_message_unread};

    private TextView messageSubject;
    private boolean messageUnread;

    public MessageListItemView(Context context) {
        this(context, null);
    }

    public MessageListItemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        loadViews();
    }

    public MessageListItemView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        loadViews();
    }

    private void loadViews() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.message_list_item, this, true);

        int fiveDPInPixels = convertDIPToPixels(5);
        int fiftyDPInPixels = convertDIPToPixels(50);

        setPadding(fiveDPInPixels, fiveDPInPixels, fiveDPInPixels, fiveDPInPixels);
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, fiftyDPInPixels));
        setBackgroundResource(R.drawable.message_list_item_background);

        messageSubject = (TextView) findViewById(R.id.message_subject);
    }

    public int convertDIPToPixels(int dip) {
        // In production code this method would exist in a utility library.
        // e.g see my ScreenUtils class: https://gist.github.com/2504204
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        // If the message is unread then we merge our custom message unread state into
        // the existing drawable state before returning it.
        if (messageUnread) {
            // We are going to add 1 extra state.
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

            mergeDrawableStates(drawableState, STATE_MESSAGE_UNREAD);

            return drawableState;
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }

    public void setMessageSubject(String subject) {
        messageSubject.setText(subject);
    }

    public void setMessageUnread(boolean messageUnread) {
        // Performance optimisation: only update the state if it has changed.
        if (this.messageUnread != messageUnread) {
            this.messageUnread = messageUnread;

            // Refresh the drawable state so that it includes the message unread state if required.
            refreshDrawableState();
        }
    }

}
