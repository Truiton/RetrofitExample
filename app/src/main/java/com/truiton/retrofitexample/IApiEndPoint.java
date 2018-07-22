package com.truiton.retrofitexample;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IApiEndPoint {

    @GET("/api/get/curators.json")
    Call<Curator> getCurators(@Query("api_key") String key);


/*    //Section commented as its for example purpose

    @POST("relative-post-path.json")
    Curator getCurators(@Query("api_key") String key);

    @Headers({"Accept: application/vnd.github.v3.full+json",
            "User-Agent: Android-Retrofit-Tutorial"})
    @GET("/get/curators.json")
    Curator getCurators(@Query("api_key") String key);


    @GET("/get/{dataset}.{format}")
    Curator getCurators(@Path("dataset") String dataset,
                        @Path("format") String format,
                        @Query("api_key") String key);*/
}
