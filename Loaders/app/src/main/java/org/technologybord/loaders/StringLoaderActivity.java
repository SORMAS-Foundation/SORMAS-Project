package org.technologybord.loaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import org.technologybord.loaders.github.RepoLoader;

import java.util.Collections;
import java.util.List;

/**
 * Created by Orson on 06/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class StringLoaderActivity extends AppCompatActivity {

    private ListView listView;
    private LoaderAdapter adapter;
    private StringLoader loader;
    private RepoLoader repoLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_loader);

        adapter = new LoaderAdapter(this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        findViewById(R.id.button_force).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.loadNewStrings();
                /*Intent intent = new Intent(StringLoader.ACTION);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);*/
            }
        });

        //adapter.swapData(Arrays.asList(getResources().getStringArray(R.array.items)));

        //loader = (StringLoader) getSupportLoaderManager().initLoader(R.id.string_loader_id, null, loaderCallbacks);
        repoLoader = (RepoLoader) getSupportLoaderManager().initLoader(R.id.repo_loader_id, null, loaderCallbacks);


        /*Crossbow.get(this).async(new RequestListStrings(new Response.Listener<List<String>>() {
            @Override
            public void onResponse(List<String> response) {
                adapter.swapData(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));*/
    }

    private LoaderManager.LoaderCallbacks<List<String>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            return new RepoLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            adapter.swapData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            adapter.swapData(Collections.<String>emptyList());
            loader = null;
            repoLoader = null;
        }
    };



    /*private LoaderManager.LoaderCallbacks<List<String>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            return new StringLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            adapter.swapData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            adapter.swapData(Collections.<String>emptyList());
            loader = null;
            repoLoader = null;
        }
    };*/
}
