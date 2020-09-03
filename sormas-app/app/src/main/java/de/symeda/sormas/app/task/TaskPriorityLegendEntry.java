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

package de.symeda.sormas.app.task;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.data.BaseLegendEntry;

/**
 * Created by Orson on 29/11/2017.
 */
public abstract class TaskPriorityLegendEntry extends BaseLegendEntry {

	public static final TaskPriorityLegendEntry NORMAL_PRIORITY = new NormalPriorityLegendEntry();
	public static final TaskPriorityLegendEntry LOW_PRIORITY = new LowPriorityLegendEntry();
	public static final TaskPriorityLegendEntry HIGH_PRIORITY = new HighPriorityLegendEntry();

	@Override
	public abstract int getKey();

	public static BaseLegendEntry findByKey(int key) {
		if (key == NORMAL_PRIORITY.getKey()) {
			return NORMAL_PRIORITY;
		} else if (key == LOW_PRIORITY.getKey()) {
			return LOW_PRIORITY;
		} else if (key == HIGH_PRIORITY.getKey()) {
			return HIGH_PRIORITY;
		}

		throw new IllegalArgumentException("The provided Task Priority Key is invalid.");
	}

	@Override
	public abstract int getLegendColor();

	@Override
	public abstract String getPriorityName();

	@Override
	public abstract int getLengendShape();

	@Override
	public abstract BaseLegendEntry setValue(float value);

	@Override
	public abstract BaseLegendEntry setPercentage(float percentage);

	@Override
	public abstract float getValue();

	@Override
	public abstract float getPercentage();

	private static class NormalPriorityLegendEntry extends TaskPriorityLegendEntry {

		private int key;
		private int legendColor;
		private int legendShape;
		private String name;

		private float value;
		private float percentage;

		public NormalPriorityLegendEntry() {
			this.key = 0;
			this.legendColor = R.color.normalPriority;
			this.legendShape = R.drawable.background_legend_normal_priority;
			this.name = TaskPriority.NORMAL.toString();
		}

		@Override
		public int getKey() {
			return this.key;
		}

		@Override
		public int getLegendColor() {
			return this.legendColor;
		}

		@Override
		public String getPriorityName() {
			return this.name;
		}

		@Override
		public int getLengendShape() {
			return this.legendShape;
		}

		@Override
		public BaseLegendEntry setValue(float value) {
			this.value = value;

			return this;
		}

		@Override
		public BaseLegendEntry setPercentage(float percentage) {
			this.percentage = percentage;

			return this;
		}

		@Override
		public float getValue() {
			return this.value;
		}

		@Override
		public float getPercentage() {
			return this.percentage;
		}
	}

	private static class LowPriorityLegendEntry extends TaskPriorityLegendEntry {

		private int key;
		private int legendColor;
		private int legendShape;
		private String name;

		private float value;
		private float percentage;

		public LowPriorityLegendEntry() {
			this.key = 1;
			this.legendColor = R.color.lowPriority;
			this.legendShape = R.drawable.background_legend_low_priority;
			this.name = TaskPriority.LOW.toString();
		}

		@Override
		public int getKey() {
			return this.key;
		}

		@Override
		public int getLegendColor() {
			return this.legendColor;
		}

		@Override
		public String getPriorityName() {
			return this.name;
		}

		@Override
		public int getLengendShape() {
			return this.legendShape;
		}

		@Override
		public float getValue() {
			return this.value;
		}

		@Override
		public BaseLegendEntry setValue(float value) {
			this.value = value;

			return this;
		}

		@Override
		public float getPercentage() {
			return this.percentage;
		}

		@Override
		public BaseLegendEntry setPercentage(float percentage) {
			this.percentage = percentage;

			return this;
		}
	}

	private static class HighPriorityLegendEntry extends TaskPriorityLegendEntry {

		private int key;
		private int legendColor;
		private int legendShape;
		private String name;

		private float value;
		private float percentage;

		public HighPriorityLegendEntry() {
			this.key = 2;
			this.legendColor = R.color.highPriority;
			this.legendShape = R.drawable.background_legend_high_priority;
			this.name = TaskPriority.HIGH.toString();
		}

		@Override
		public int getKey() {
			return this.key;
		}

		@Override
		public int getLegendColor() {
			return this.legendColor;
		}

		@Override
		public String getPriorityName() {
			return this.name;
		}

		@Override
		public int getLengendShape() {
			return this.legendShape;
		}

		@Override
		public float getValue() {
			return this.value;
		}

		@Override
		public BaseLegendEntry setValue(float value) {
			this.value = value;

			return this;
		}

		@Override
		public float getPercentage() {
			return this.percentage;
		}

		@Override
		public BaseLegendEntry setPercentage(float percentage) {
			this.percentage = percentage;

			return this;
		}
	}
}
