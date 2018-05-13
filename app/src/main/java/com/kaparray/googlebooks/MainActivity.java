package com.kaparray.googlebooks;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kaparray.googlebooks.Api.ApiBooks;
import com.kaparray.googlebooks.Data.BooksData;
import com.kaparray.googlebooks.Data.Item;
import com.kaparray.googlebooks.adapters.MyAdapter;
import com.kaparray.googlebooks.adapters.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{

    public static final String KEY_API = "AIzaSyBA14zOMI_3xSNWED-HYtfl8CidvqMTuRM";
    BooksData booksDataList;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String maxResults = "40";


    @BindView(R.id.rv_books) RecyclerView mRecyclerViewBooks;
    @BindView(R.id.et_search) EditText mTextSearch;
    @BindView(R.id.floatingActionButton) FloatingActionButton mSearch;
    @BindView(R.id.tv_noResults) TextView mNoResults;
    @BindView(R.id.pb_Download) ProgressBar mProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.GONE);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                // Work with view in activity
                mRecyclerViewBooks.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mNoResults.setVisibility(View.GONE);

                getDataFromServer(mTextSearch.getText().toString());
            }
        });



        mTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    View view = MainActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }


                    // Work with view in activity
                    mRecyclerViewBooks.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mNoResults.setVisibility(View.GONE);

                    getDataFromServer(mTextSearch.getText().toString());
                }
                return false;
            }
        });



    }


    void getDataFromServer(String nameBook){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiBooks apiBooks = retrofit.create(ApiBooks.class);
        Call<BooksData> call = apiBooks.getBooks(nameBook, KEY_API, maxResults);

        call.enqueue(new Callback<BooksData>() {
            @Override
            public void onResponse(Call<BooksData> call, Response<BooksData> response) {
                booksDataList = response.body();


                mProgressBar.setVisibility(View.GONE);


                try {
                    if (booksDataList.getItems().size() > 0) {
                        mRecyclerViewBooks.setVisibility(View.VISIBLE);
                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        mRecyclerViewBooks.setHasFixedSize(true);

                        // use a linear layout manager
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mRecyclerViewBooks.setLayoutManager(mLayoutManager);

                        // specify an adapter (see also next example)
                        mAdapter = new MyAdapter(booksDataList, getApplicationContext());
                        mRecyclerViewBooks.setAdapter(mAdapter);
                    } else {
                        mNoResults.setText("Не найдено ни одного результата");
                    }
                }catch (Exception e){
                    mNoResults.setVisibility(View.VISIBLE);
                    mNoResults.setText("Не найдено ни одного результата");
                }

                mRecyclerViewBooks.addOnItemTouchListener(
                        new RecyclerItemClickListener(getApplicationContext(), mRecyclerViewBooks, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                // do whatever

                                Intent intent = new Intent(MainActivity.this, OpenBookActivity.class);
                                intent.putExtra("data", booksDataList.getItems().get(position).getId());
                                startActivity(intent);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                // do whatever
                            }
                        })
                );

            }

            @Override
            public void onFailure(Call<BooksData> call, Throwable t) {

            }

        });
    }

}