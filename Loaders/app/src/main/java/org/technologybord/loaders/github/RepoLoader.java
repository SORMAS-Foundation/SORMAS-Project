package org.technologybord.loaders.github;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crossbow.volley.toolbox.Crossbow;

import java.util.List;

/**
 * Created by Orson on 06/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class RepoLoader extends Loader<List<String>> {

    public static final String TAG = RepoLoader.class.getSimpleName();

    private List<String> cachedData;
    private RequestQueue queue;

    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public RepoLoader(@NonNull Context context) {
        super(context);
        queue = Crossbow.get(getContext()).getRequestQueue();
    }

    @Override
    protected void onStartLoading() {
        if (cachedData == null) {
            forceLoad();
        } else {
            super.deliverResult(cachedData);
        }
    }

    @Override
    protected void onForceLoad() {
        //Start request
        queue.cancelAll(TAG);

        Log.e(TAG, "Requesting new data");
        RequestListStrings requestListStrings = new RequestListStrings(new Response.Listener<List<String>>() {
            @Override
            public void onResponse(List<String> response) {
                deliverResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestListStrings.setTag(TAG);
        queue.add(requestListStrings);
    }

    @Override
    protected void onReset() {
        //Cancel request
        super.onReset();
    }

    @Override
    public void deliverResult(@Nullable List<String> data) {
        cachedData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
