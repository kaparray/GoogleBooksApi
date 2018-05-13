package com.kaparray.googlebooks.Api;

import com.kaparray.googlebooks.Data.BooksData;
import com.kaparray.googlebooks.Data.Item;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiBook {
    @GET("volumes/{id}?projection=full")
    Call<Item> getBook(@Path("id") String id, @Query("key") String key);
}
