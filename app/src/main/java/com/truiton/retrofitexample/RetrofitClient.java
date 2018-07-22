package com.truiton.retrofitexample;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String API_URL = "http://freemusicarchive.org";
    public static final String API_KEY = "----------";
    private static String LOG_TAG = "RetrofitClient";
    private static Retrofit basicClient;
    private static Retrofit clientWithCaching;
    private static Retrofit clientWithRetry;

    public synchronized static Retrofit getBasicClient() {
        if (basicClient == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            basicClient = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return basicClient;
    }

    public synchronized static Retrofit getClientWithCaching(Context ctx) {
        if (clientWithCaching == null) {
            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(ctx.getCacheDir(), cacheSize);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .cache(cache)
                    .build();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client);
            clientWithCaching = builder.build();
        }
        return clientWithCaching;
    }

    public static IApiEndPoint getApiEndPoint(Context ctx) {
        return getClientWithRetry(ctx).create(IApiEndPoint.class);
    }

    public synchronized static Retrofit getClientWithRetry(final Context ctx) {
        if (clientWithRetry == null) {
            Interceptor responseCodeInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    if (response.code() == 401) {
                        Log.d(LOG_TAG, "Intercepted Req: " + response.toString());
                        Response r = retryWithFreshToken(request, chain);
                        return r;
                    }
                    return response;
                }
            };

            int cacheSize = 10 * 1024 * 1024; // 10 MB
            Cache cache = new Cache(ctx.getCacheDir(), cacheSize);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(responseCodeInterceptor)
                    .cache(cache)
                    .build();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client);
            clientWithRetry = builder.build();
        }
        return clientWithRetry;
    }

    private static Response retryWithFreshToken(Request req, Interceptor.Chain
            chain) throws IOException {
        Log.d(LOG_TAG, "Retrying with new token");
        String newToken = refreshToken();
        Request newRequest;
        newRequest = req.newBuilder().header("Authorization", " Token " + newToken).build();
        return chain.proceed(newRequest);
    }

    private static String refreshToken() throws IOException {
        IApiEndPoint methods = RetrofitClient.getBasicClient().create(IApiEndPoint.class);
        Call<Curator> call = methods.getCurators(RetrofitClient.API_KEY);
        return call.execute().body().toString();
    }
}
