package com.truiton.retrofitexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        IApiEndPoint methods = RetrofitClient.getApiEndPoint(this);
        Call<Curator> call = methods.getCurators(RetrofitClient
                .API_KEY);
        call.enqueue(new Callback<Curator>() {
            @Override
            public void onResponse(Call<Curator> call, Response<Curator> response) {
                Curator curators = response.body();
                textView.setText(curators.title + "\n\n");
                for (Curator.Dataset dataset : curators.dataset) {
                    textView.setText(textView.getText() + dataset.curator_title +
                            " - " + dataset.curator_tagline + "\n");
                }
            }

            @Override
            public void onFailure(Call<Curator> call, Throwable t) {

            }
        });
    }
}