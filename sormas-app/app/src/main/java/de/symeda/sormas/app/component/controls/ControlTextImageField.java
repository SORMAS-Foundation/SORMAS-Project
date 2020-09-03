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

package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.util.DateFormatHelper;

public class ControlTextImageField extends ControlTextReadField {

	// Constants

	public static final int DEFAULT_IMG_WIDTH = 24;
	public static final int DEFAULT_IMG_HEIGHT = 24;

	// Views

	protected ImageView imageView;

	// Attributes
	private int imageColor;
	private int imageWidth;
	private int imageHeight;

	// Other variables

	private int image;

	// Constructors

	public ControlTextImageField(Context context) {
		super(context);
	}

	public ControlTextImageField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlTextImageField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Instance methods

	public void setImageBackground(int imageResource, int tintResource) {
		if (imageView != null) {
			Context context = imageView.getContext();
			Drawable background = ContextCompat.getDrawable(context, imageResource);

			applyBackground(background, tintResource, context);
		}
	}

	public void setImageBackground(int tintResource) {
		if (imageView != null) {
			Context context = imageView.getContext();
			Drawable background = ContextCompat.getDrawable(context, getImage());

			applyBackground(background, tintResource, context);
		}
	}

	private void applyBackground(Drawable background, int tintResource, Context context) {
		if (background != null) {
			background.setTint(context.getResources().getColor(tintResource));
		}

		imageView.setBackground(background);

		ViewGroup.LayoutParams params = imageView.getLayoutParams();
		params.width = imageWidth;
		params.height = imageHeight;
	}

	// Overrides

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		super.initialize(context, attrs, defStyle);
		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlTextImageField, 0, 0);

			try {
				image = a.getResourceId(R.styleable.ControlTextImageField_image, R.drawable.blank);
				imageWidth = a.getDimensionPixelSize(R.styleable.ControlTextImageField_imageWidth, DEFAULT_IMG_WIDTH);
				imageHeight = a.getDimensionPixelSize(R.styleable.ControlTextImageField_imageHeight, DEFAULT_IMG_HEIGHT);
			} finally {
				a.recycle();
			}
		}
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			inflater.inflate(R.layout.control_textfield_image_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		imageView = (ImageView) this.findViewById(R.id.image);
	}

	// Data binding, getters & setters

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public int getImageColor() {
		return imageColor;
	}

	public void setImageColor(int imageColor) {
		this.imageColor = imageColor;
	}

	// Task priority
	@BindingAdapter(value = {
		"taskPriorityValue",
		"defaultValue" }, requireAll = false)
	public static void setTaskPriorityValue(ControlTextImageField textImageField, TaskPriority priority, String defaultValue) {
		if (priority == null) {
			textImageField.setFieldValue(textImageField.getDefaultValue(defaultValue));
			textImageField.applyDefaultValueStyle();
		} else {
			// TODO reset default style?

			textImageField.setValue(priority.toString(), priority);

			if (priority == TaskPriority.HIGH) {
				textImageField.setImageBackground(R.color.indicatorTaskPriorityHigh);
			} else if (priority == TaskPriority.NORMAL) {
				textImageField.setImageBackground(R.color.indicatorTaskPriorityNormal);
			} else if (priority == TaskPriority.LOW) {
				textImageField.setImageBackground(R.color.indicatorTaskPriorityLow);
			}
		}
	}

	// Task status
	@BindingAdapter(value = {
		"taskStatusValue",
		"defaultValue" }, requireAll = false)
	public static void setTaskStatusValue(ControlTextImageField textImageField, TaskStatus status, String defaultValue) {
		if (status == null) {
			textImageField.setFieldValue(textImageField.getDefaultValue(defaultValue));
			textImageField.applyDefaultValueStyle();
		} else {
			// TODO reset default style?

			textImageField.setValue(status.toString(), status);

			if (status == TaskStatus.PENDING) {
				textImageField.setImageBackground(R.color.indicatorTaskPending);
			} else if (status == TaskStatus.DONE) {
				textImageField.setImageBackground(R.color.indicatorTaskDone);
			} else if (status == TaskStatus.REMOVED) {
				textImageField.setImageBackground(R.color.indicatorTaskRemoved);
			} else if (status == TaskStatus.NOT_EXECUTABLE) {
				textImageField.setImageBackground(R.color.indicatorTaskNotExecutable);
			}
		}
	}

	@BindingAdapter(value = {
		"shipmentStatus",
		"defaultValue" }, requireAll = false)
	public static void setShipmentStatus(ControlTextImageField textImageField, Sample sample, String defaultValue) {
		if (sample == null) {
			textImageField.setValue(textImageField.getDefaultValue(defaultValue), false);
			textImageField.applyDefaultValueStyle();
		} else {
			// TODO reset default style?

			if (sample.isShipped()) {
				textImageField.setValue(DateFormatHelper.formatLocalDate(sample.getShipmentDate()), true);
				textImageField.setImageBackground(R.drawable.ic_check_circle_24dp, R.color.green);
			} else {
				textImageField.setValue(textImageField.getResources().getString(R.string.no), false);
				textImageField.setImageBackground(R.drawable.ic_cancel_24dp, R.color.red);
			}
		}
	}

	@BindingAdapter(value = {
		"receivedStatus",
		"defaultValue" }, requireAll = false)
	public static void setReceivedStatus(ControlTextImageField textImageField, Sample sample, String defaultValue) {
		if (sample == null) {
			textImageField.setValue(textImageField.getDefaultValue(defaultValue), false);
			textImageField.applyDefaultValueStyle();
		} else {
			// TODO reset default style?

			if (sample.isReceived()) {
				textImageField.setValue(DateFormatHelper.formatLocalDate(sample.getReceivedDate()), true);
				textImageField.setImageBackground(R.drawable.ic_check_circle_24dp, R.color.green);
			} else {
				textImageField.setValue(textImageField.getResources().getString(R.string.no), false);
				textImageField.setImageBackground(R.drawable.ic_cancel_24dp, R.color.red);
			}
		}
	}

	@BindingAdapter(value = {
		"value",
		"valueFormat",
		"defaultValue" }, requireAll = false)
	public static void setValue(ControlTextImageField textImageField, Boolean booleanValue, String valueFormat, String defaultValue) {
		if (booleanValue) {
			textImageField.setImageBackground(R.drawable.ic_check_circle_24dp, R.color.green);
		} else {
			textImageField.setImageBackground(R.drawable.ic_cancel_24dp, R.color.red);
		}
		ControlTextReadField.setValue(textImageField, booleanValue, valueFormat, defaultValue);
	}
}
