package de.symeda.sormas.app.core.timeago;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Created by Orson on 02/01/2018.
 */

public final class TimeAgoMessages {

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
     * @param property the property key
     * @return the property value
     */
    public String getPropertyValue(final int property) {
        final String propertyVal = getResources().getString(property);
        return propertyVal;
    }

    /**
     * Gets the property value.
     *
     * @param property the property key
     * @param values   the property values
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
            }
            else { // support older android versions
                resources = context.getResources();
            }

            return this;
        }

        /**
         * Build messages with the selected locale.
         *
         * @param locale the locale
         * @return the builder
         */
        public Builder withLocale(Locale locale) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // use latest api
                conf.setLocale(locale);
                Context localizedContext = context.createConfigurationContext(conf);
                resources = localizedContext.getResources();
            }
            else { // support older android versions
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
         * @param resources the new inner bundle
         */
        public void setInnerResources(Resources resources) {
            this.resources = resources;
        }
    }
}
