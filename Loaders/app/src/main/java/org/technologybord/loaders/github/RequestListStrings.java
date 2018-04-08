package org.technologybord.loaders.github;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Orson on 06/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class RequestListStrings extends Request<List<String>> {

    private final Response.Listener<List<String>> listListener;

    public RequestListStrings(Response.Listener<List<String>> listListener, Response.ErrorListener listener) {
        super(Method.GET, "https://api.github.com/users/sampsonorson/repos", listener);
        this.listListener = listListener;
    }

    @Override
    protected Response<List<String>> parseNetworkResponse(NetworkResponse response) {
        String json = new String(response.data);

        List<String> repoNames = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                repoNames.add(jsonObject.getString("name"));
            }

            return Response.success(repoNames, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Response.success(Collections.<String>emptyList(), HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(List<String> response) {
        listListener.onResponse(response);
    }
}
