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

package de.symeda.sormas.app.core;

import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.symeda.sormas.app.R;

public final class TimeAgo {

	@Nullable
	private Context context;

	/**
	 * Instantiates a new Time ago.
	 */
	private TimeAgo(Context context) {
		super();
		this.context = context;
	}

	public static TimeAgo using(@NonNull Context context) {
		return new TimeAgo(context);
	}

	/**
	 * <p>
	 * Returns the 'time ago' formatted text using date time.
	 * </p>
	 *
	 * @param time
	 *            the date time for parsing
	 * @return the 'time ago' formatted text using date time
	 * @see TimeAgoMessages
	 */
	public String with(final long time) {
		return with(time, new TimeAgoMessages.Builder(context).build());
	}

	/**
	 * <p>
	 * Returns the 'time ago' formatted text using date time.
	 * </p>
	 *
	 * @param time
	 *            the date time for parsing
	 * @param resources
	 *            the resources for localizing messages
	 * @return the 'time ago' formatted text using date time
	 * @see TimeAgoMessages
	 */
	public String with(final long time, final TimeAgoMessages resources) {
		final long dim = getTimeDistanceInMinutes(time);
		final StringBuilder timeAgo = buildTimeagoText(resources, dim);
		return timeAgo.toString();
	}

	public String with(Date date) {
		return with(date, new TimeAgoMessages.Builder(context).build());
	}

	public String with(Date date, TimeAgoMessages resources) {
		final long dim = getTimeDistanceInMinutes(date.getTime());
		final StringBuilder timeAgo = buildTimeagoText(resources, dim);
		return timeAgo.toString();
	}

	/**
	 * Build timeago text string builder.
	 *
	 * @param resources
	 *            the resources
	 * @param dim
	 *            the distance in minutes from now
	 * @return the string builder
	 */
	private static StringBuilder buildTimeagoText(TimeAgoMessages resources, long dim) {
		final StringBuilder timeAgo = new StringBuilder();

		final Periods foundTimePeriod = Periods.findByDistanceMinutes(dim);
		if (foundTimePeriod != null) {
			final int periodKey = foundTimePeriod.getPropertyKey();
			switch (foundTimePeriod) {
			case XMINUTES_PAST:
				timeAgo.append(resources.getPropertyValue(periodKey, dim));
				break;
			case XHOURS_PAST:
				int hours = Math.round(dim / 60);
				final String xHoursText = handlePeriodKeyAsPlural(resources, R.string.time_past_one_hour, periodKey, hours);
				timeAgo.append(xHoursText);
				break;
			case XDAYS_PAST:
				int days = Math.round(dim / 1440);
				final String xDaysText = handlePeriodKeyAsPlural(resources, R.string.time_past_one_day, periodKey, days);
				timeAgo.append(xDaysText);
				break;
			case XWEEKS_PAST:
				int weeks = Math.round(dim / 7560);
				final String xWeeksText = handlePeriodKeyAsPlural(resources, R.string.time_past_one_week, periodKey, weeks);
				timeAgo.append(xWeeksText);
				break;
			case XMONTHS_PAST:
				int months = Math.round(dim / 43200);
				final String xMonthsText = handlePeriodKeyAsPlural(resources, R.string.time_past_one_month, periodKey, months);
				timeAgo.append(xMonthsText);
				break;
			case XYEARS_PAST:
				int years = Math.round(dim / 525600);
				timeAgo.append(resources.getPropertyValue(periodKey, years));
				break;
			case XMINUTES_FUTURE:
				timeAgo.append(resources.getPropertyValue(periodKey, Math.abs((float) dim)));
				break;
			case XHOURS_FUTURE:
				int hours1 = Math.abs(Math.round(dim / 60f));
				final String yHoursText = hours1 == 24
					? resources.getPropertyValue(R.string.time_future_one_day)
					: handlePeriodKeyAsPlural(resources, R.string.time_future_one_hour, periodKey, hours1);
				timeAgo.append(yHoursText);
				break;
			case XDAYS_FUTURE:
				int days1 = Math.abs(Math.round(dim / 1440f));
				final String yDaysText = handlePeriodKeyAsPlural(resources, R.string.time_future_one_day, periodKey, days1);
				timeAgo.append(yDaysText);
				break;
			case XWEEKS_FUTURE:
				int weeks1 = Math.abs(Math.round(dim / 7560f));
				final String yWeeksText = handlePeriodKeyAsPlural(resources, R.string.time_future_one_week, periodKey, weeks1);
				timeAgo.append(yWeeksText);
			case XMONTHS_FUTURE:
				int months1 = Math.abs(Math.round(dim / 43200f));
				final String yMonthsText = months1 == 12
					? resources.getPropertyValue(R.string.time_future_one_year)
					: handlePeriodKeyAsPlural(resources, R.string.time_future_one_month, periodKey, months1);
				timeAgo.append(yMonthsText);
				break;
			case XYEARS_FUTURE:
				int years1 = Math.abs(Math.round(dim / 525600f));
				timeAgo.append(resources.getPropertyValue(periodKey, years1));
				break;
			default:
				timeAgo.append(resources.getPropertyValue(periodKey));
				break;
			}
		}
		return timeAgo;
	}

	/**
	 * Handle period key as plural string.
	 *
	 * @param resources
	 *            the resources
	 * @param periodKey
	 *            the period key
	 * @param value
	 *            the value
	 * @return the string
	 */
	private static String handlePeriodKeyAsPlural(final TimeAgoMessages resources, final int periodKey, final int pluralKey, final int value) {
		return value == 1 ? resources.getPropertyValue(periodKey) : resources.getPropertyValue(pluralKey, value);
	}

	/**
	 * Returns the time distance in minutes.
	 *
	 * @param time
	 *            the date time
	 * @return the time distance in minutes
	 */
	private static long getTimeDistanceInMinutes(long time) {
		long timeDistance = System.currentTimeMillis() - time;
		return Math.round((timeDistance / 1000) / 60);
	}

	/**
	 * The enum Periods.
	 *
	 * @author marlonlom
	 * @version 3.0.1
	 * @since 2.0.0
	 */
	private enum Periods {

		NOW(R.string.time_now, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance == 0;
			}
		}),
		ONEMINUTE_PAST(R.string.time_past_one_minute, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance == 1;
			}
		}),
		XMINUTES_PAST(R.string.time_past_x_minutes, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 2 && distance <= 44;
			}
		}),
		ABOUTANHOUR_PAST(R.string.time_past_one_hour, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 45 && distance <= 89;
			}
		}),
		XHOURS_PAST(R.string.time_past_x_hours, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 90 && distance <= 1439;
			}
		}),
		ONEDAY_PAST(R.string.time_past_one_day, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 1440 && distance <= 2519;
			}
		}),
		XDAYS_PAST(R.string.time_past_x_days, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 2520 && distance <= 7559;
			}
		}),
		ONEWEEK_PAST(R.string.time_past_one_week, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 7560 && distance <= 14918;
			}
		}),
		XWEEKS_PAST(R.string.time_past_x_weeks, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 14919 && distance <= 43199;
			}
		}),
		ABOUTAMONTH_PAST(R.string.time_past_one_month, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 43200 && distance <= 86399;
			}
		}),
		XMONTHS_PAST(R.string.time_past_x_months, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 86400 && distance <= 525599;
			}
		}),
		ABOUTAYEAR_PAST(R.string.time_past_one_year, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 525600 && distance <= 655199;
			}
		}),
		OVERAYEAR_PAST(R.string.time_past_over_one_year, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 655200 && distance <= 914399;
			}
		}),
		ALMOSTTWOYEARS_PAST(R.string.time_past_almost_two_years, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= 914400 && distance <= 1051199;
			}
		}),
		XYEARS_PAST(R.string.time_past_x_years, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return Math.round(distance / 525600) > 1;
			}
		}),
		ONEMINUTE_FUTURE(R.string.time_future_one_minute, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance == -1;
			}
		}),
		XMINUTES_FUTURE(R.string.time_future_x_minutes, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -2 && distance >= -44;
			}
		}),
		ABOUTANHOUR_FUTURE(R.string.time_future_one_hour, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -45 && distance >= -89;
			}
		}),
		XHOURS_FUTURE(R.string.time_future_x_hours, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -90 && distance >= -1439;
			}
		}),
		ONEDAY_FUTURE(R.string.time_future_one_day, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -1440 && distance >= -2519;
			}
		}),
		XDAYS_FUTURE(R.string.time_future_x_days, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -2520 && distance >= -7559;
			}
		}),
		ONEWEEK_FUTURE(R.string.time_future_one_week, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= -7560 && distance <= -14918;
			}
		}),
		XWEEKS_FUTURE(R.string.time_future_x_weeks, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance >= -14919 && distance <= -43199;
			}
		}),
		ABOUTAMONTH_FUTURE(R.string.time_future_one_month, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -43200 && distance >= -86399;
			}
		}),
		XMONTHS_FUTURE(R.string.time_future_x_months, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -86400 && distance >= -525599;
			}
		}),
		ABOUTAYEAR_FUTURE(R.string.time_future_one_year, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -525600 && distance >= -655199;
			}
		}),
		OVERAYEAR_FUTURE(R.string.time_future_over_one_year, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -655200 && distance >= -914399;
			}
		}),
		ALMOSTTWOYEARS_FUTURE(R.string.time_future_almost_two_years, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return distance <= -914400 && distance >= -1051199;
			}
		}),
		XYEARS_FUTURE(R.string.time_future_x_years, new DistancePredicate() {

			@Override
			public boolean validateDistanceMinutes(final long distance) {
				return Math.round(distance / 525600) < -1;
			}
		});

		/**
		 * The property key.
		 */
		private int mPropertyKey;
		/**
		 * The predicate.
		 */
		private DistancePredicate mPredicate;

		Periods(int propertyKey, DistancePredicate predicate) {
			this.mPropertyKey = propertyKey;
			this.mPredicate = predicate;
		}

		/**
		 * Find by distance minutes periods.
		 *
		 * @param distanceMinutes
		 *            the distance minutes
		 * @return the periods
		 */
		public static Periods findByDistanceMinutes(final long distanceMinutes) {
			final Periods[] values = Periods.values();
			for (final Periods item : values) {
				final boolean successful = item.getPredicate().validateDistanceMinutes(distanceMinutes);
				if (successful) {
					return item;
				}
			}
			return null;
		}

		/**
		 * Gets predicate.
		 *
		 * @return the predicate
		 */
		private DistancePredicate getPredicate() {
			return mPredicate;
		}

		/**
		 * Gets property key.
		 *
		 * @return the property key
		 */
		public int getPropertyKey() {
			return mPropertyKey;
		}
	}

	/**
	 * Interface definition for handling distance validations or periods.
	 *
	 * @author marlonlom
	 * @version 3.0.1
	 * @see Periods
	 * @since 1.0.0
	 */
	private interface DistancePredicate {

		/**
		 * Validate distance minutes boolean.
		 *
		 * @param distance
		 *            the distance
		 * @return the boolean
		 */
		boolean validateDistanceMinutes(final long distance);
	}

	private static class TimeAgoMessages {

		/**
		 * The Resources.
		 */
		private Resources resources;

		/**
		 * Instantiates a new time ago messages.
		 */
		private TimeAgoMessages() {
			super();
		}

		/**
		 * Gets the resources.
		 *
		 * @return the resources
		 */
		public Resources getResources() {
			return resources;
		}

		/**
		 * Sets the resources.
		 *
		 * @param resources
		 */
		public void setResources(Resources resources) {
			this.resources = resources;
		}

		/**
		 * Gets the property value.
		 *
		 * @param property
		 *            the property key
		 * @return the property value
		 */
		public String getPropertyValue(final int property) {
			final String propertyVal = getResources().getString(property);
			return propertyVal;
		}

		/**
		 * Gets the property value.
		 *
		 * @param property
		 *            the property key
		 * @param values
		 *            the property values
		 * @return the property value
		 */
		public String getPropertyValue(final int property, Object... values) {
			final String propertyVal = getResources().getString(property, values);
			return propertyVal;
		}

		/**
		 * The Inner Class Builder for <i>TimeAgoMessages</i>.
		 *
		 * @author marlonlom
		 */
		public static final class Builder {

			private Context context;
			private Configuration conf;
			private Resources resources;
			private Locale savedLocale;

			public Builder(Context context) {
				this(context, null);
			}

			public Builder(Context context, Locale locale) {
				this.context = context;
				this.savedLocale = locale;

				conf = this.context.getResources().getConfiguration();
				conf = new Configuration(conf);

				if (locale != null) {
					withLocale(locale);
				} else {
					this.savedLocale = Locale.getDefault();
					withDefaultLocale();
				}
			}

			/**
			 * Build messages with the default locale.
			 *
			 * @return the builder
			 */
			public Builder withDefaultLocale() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // use latest api
					Context localizedContext = context.createConfigurationContext(conf);
					resources = localizedContext.getResources();
				} else { // support older android versions
					resources = context.getResources();
				}

				return this;
			}

			/**
			 * Build messages with the selected locale.
			 *
			 * @param locale
			 *            the locale
			 * @return the builder
			 */
			public Builder withLocale(Locale locale) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // use latest api
					conf.setLocale(locale);
					Context localizedContext = context.createConfigurationContext(conf);
					resources = localizedContext.getResources();
				} else { // support older android versions
					resources = context.getResources();
					Configuration conf = resources.getConfiguration();
					savedLocale = conf.locale;
					conf.locale = locale;
					resources.updateConfiguration(conf, null);

					// restore original locale
					conf.locale = savedLocale;
					resources.updateConfiguration(conf, null);
				}

				return this;
			}

			/**
			 * Builds the TimeAgoMessages instance.
			 *
			 * @return the time ago messages instance.
			 */
			public TimeAgoMessages build() {
				TimeAgoMessages resources = new TimeAgoMessages();
				resources.setResources(this.getInnerResources());
				return resources;
			}

			/**
			 * Gets the inner resources.
			 *
			 * @return the inner bundle
			 */
			public Resources getInnerResources() {
				return resources;
			}

			/**
			 * Sets the inner resources.
			 *
			 * @param resources
			 *            the new inner bundle
			 */
			public void setInnerResources(Resources resources) {
				this.resources = resources;
			}
		}
	}
}
