package com.kaparray.googlebooks.Api;

import com.kaparray.googlebooks.Data.BooksData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiMagazine {


    @GET("volumes?printType=magazines")
    Call<BooksData> getMagazine(@Query("q") String q, @Query("key") String key, @Query("maxResults") String maxResults);
}
