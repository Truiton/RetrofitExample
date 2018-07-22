package com.truiton.retrofitexample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.io.IOException;
import retrofit2.Call;

public class MainActivitySynchronous extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        BackgroundTask task = new BackgroundTask();
        task.execute();
    }

    private class BackgroundTask extends AsyncTask<Void, Void,
            Curator> {
        Call<Curator> call;
        @Override
        protected void onPreExecute() {
            IApiEndPoint methods = RetrofitClient.getApiEndPoint(MainActivitySynchronous.this);
            call = methods.getCurators(RetrofitClient.API_KEY);
        }

        @Override
        protected Curator doInBackground(Void... params) {
            Curator curators = null;
            try {
                curators = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return curators;
        }

        @Override
        protected void onPostExecute(Curator curators) {
            textView.setText(curators.title + "\n\n");
            for (Curator.Dataset dataset : curators.dataset) {
                textView.setText(textView.getText() + dataset.curator_title +
                        " - " + dataset.curator_tagline + "\n");
            }
        }
    }
}