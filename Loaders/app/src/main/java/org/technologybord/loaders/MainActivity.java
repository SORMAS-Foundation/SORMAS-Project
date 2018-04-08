package org.technologybord.loaders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button1);

        Loader<ArrayList<String>> loader = this.getSupportLoaderManager().initLoader(5, null, this);
        //loader.startLoading();
    }

    @NonNull
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, @Nullable Bundle args) {
        RandomStringLoader loader = new RandomStringLoader(this);
        /*loader.registerListener(id, new Loader.OnLoadCompleteListener<ArrayList<String>>() {
            @Override
            public void onLoadComplete(@NonNull Loader<ArrayList<String>> loader, @Nullable ArrayList<String> data) {
                String kkk = "";
            }
        });*/
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String>> loader, ArrayList<String> data) {
        textView.setText("");
        for (String text: data) {
            textView.setText(textView.getText() + text);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String>> loader) {

    }

    public void reloadStrings(View view) {
        Intent intent = new Intent();
        intent.setAction(RandomStringLoader.STRING_LOADER_RELOAD);
        sendBroadcast(intent);
    }
}
