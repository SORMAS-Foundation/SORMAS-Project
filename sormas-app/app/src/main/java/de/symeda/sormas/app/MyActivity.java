package de.symeda.sormas.app;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.app.R;
import de.symeda.sormas.api.caze.Case;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        try {
            Case result = new RestTestTask().execute().get();
            new AlertDialog.Builder(this).setTitle(result.getDescription()).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private class RestTestTask extends AsyncTask<Void, Void, Case> {
        @Override
        protected Case doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/sormas-rest/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            // SSL not working because of missing certificate

            // TODO set uuid manually
            CaseFacadeRetro caseFacade = retrofit.create(CaseFacadeRetro.class);
            Call<Case> cazeCall = caseFacade.getByUuid("2a8c5a51-5b78-4114-a5db-a1dffc27aee6");
            try {
                return cazeCall.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Case result) {
        }
    }
}
