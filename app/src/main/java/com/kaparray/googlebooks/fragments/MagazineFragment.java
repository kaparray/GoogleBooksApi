package com.kaparray.googlebooks.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.kaparray.googlebooks.Api.ApiMagazine;
import com.kaparray.googlebooks.Data.BooksData;
import com.kaparray.googlebooks.OpenBookActivity;
import com.kaparray.googlebooks.R;
import com.kaparray.googlebooks.RealmData.BooksDataRealm;
import com.kaparray.googlebooks.adapters.MyAdapter;
import com.kaparray.googlebooks.adapters.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MagazineFragment extends Fragment{


    View rootView;


    public static final String KEY_API = "AIzaSyBA14zOMI_3xSNWED-HYtfl8CidvqMTuRM";
    BooksData booksDataList;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String maxResults = "40";

    Realm realm;


    @BindView(R.id.rv_booksMagazine)
    RecyclerView mRecyclerViewBooks;
    @BindView(R.id.et_searchMagazine)
    EditText mTextSearch;
    @BindView(R.id.floatingActionButtonMagazine)
    FloatingActionButton mSearch;
    @BindView(R.id.tv_noResultsMagazine)
    TextView mNoResults;
    @BindView(R.id.pb_DownloadMagazine)
    ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_magazine, container, false);


        ButterKnife.bind(this, rootView);




        mProgressBar.setVisibility(View.GONE); // Set visible progress bar


        // Initialize Realm (just once per application)
        Realm.init(getContext());

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();


        RealmResults<BooksDataRealm> results = realm.where(BooksDataRealm.class).findAllAsync();
        //fetching the data
        results.load();

        String last = "";

        for (BooksDataRealm information : results) {
            last = information.getName();
        }
        // Dll focuse fron edit text in start
        mTextSearch.setText(last);
        mTextSearch.requestFocus();


        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTextSearch.getText().toString().length() != 0) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    // Work with view in activity
                    mRecyclerViewBooks.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mNoResults.setVisibility(View.GONE);


                    RealmResults<BooksDataRealm> results = realm.where(BooksDataRealm.class).findAllAsync();
                    //fetching the data
                    results.load();

                    int last = 0;

                    for (BooksDataRealm information : results) {
                        last = information.getIdBook();
                    }

                    realm.beginTransaction();  //open the database
                    //database operation
                    BooksDataRealm booksDataRealm = realm.createObject(BooksDataRealm.class);

                    booksDataRealm.setKey(mTextSearch.getText().toString());

                    booksDataRealm.setIdBook(last + 1);

                    realm.commitTransaction(); //close the database


                    getDataFromServer(mTextSearch.getText().toString());
                }else {
                    Toast.makeText(getActivity(), "Введите название", Toast.LENGTH_SHORT).show();
                }
            }
        });







        mTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(mTextSearch.getText().toString().length() != 0) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }


                        // Work with view in activity
                        mRecyclerViewBooks.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mNoResults.setVisibility(View.GONE);


                        RealmResults<BooksDataRealm> results = realm.where(BooksDataRealm.class).findAllAsync();
                        //fetching the data
                        results.load();

                        int last = 0;

                        for (BooksDataRealm information : results) {
                            last = information.getIdBook();
                        }

                        realm.beginTransaction();  //open the database
                        //database operation
                        BooksDataRealm booksDataRealm = realm.createObject(BooksDataRealm.class);

                        booksDataRealm.setKey(mTextSearch.getText().toString());
                        booksDataRealm.setIdBook(last + 1);

                        realm.commitTransaction(); //close the database


                        getDataFromServer(mTextSearch.getText().toString());
                    }
                }else{
                    Toast.makeText(getContext(), "Вы не ввели название", Toast.LENGTH_LONG).show();
                }
                return false; // do your stuff return true; }
            }
        });


        getLastBook();


        return rootView;
    }

    void getDataFromServer(String nameBook){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiMagazine apiMagazine = retrofit.create(ApiMagazine.class);
        Call<BooksData> call = apiMagazine.getMagazine(nameBook, KEY_API, maxResults);

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
                        mLayoutManager = new LinearLayoutManager(rootView.getContext());
                        mRecyclerViewBooks.setLayoutManager(mLayoutManager);

                        // specify an adapter (see also next example)
                        mAdapter = new MyAdapter(booksDataList, getContext());
                        mRecyclerViewBooks.setAdapter(mAdapter);
                    } else {
                        mNoResults.setText("Не найдено ни одного результата");
                    }
                }catch (Exception e){
                    mNoResults.setVisibility(View.VISIBLE);
                    mNoResults.setText("Не найдено ни одного результата");
                }

                mRecyclerViewBooks.addOnItemTouchListener(
                        new RecyclerItemClickListener(getContext(), mRecyclerViewBooks, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                // do whatever

                                Intent intent = new Intent(getActivity(), OpenBookActivity.class);
                                intent.putExtra("data", booksDataList.getItems().get(position).getId());
                                intent.putExtra("list",  "false");
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



    void getLastBook(){
        // Work with view in activity
        mRecyclerViewBooks.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mNoResults.setVisibility(View.GONE);

        RealmResults<BooksDataRealm> results = realm.where(BooksDataRealm.class).findAllAsync();
        //fetching the data
        results.load();

        String lastBook = "";

        for(BooksDataRealm information:results){
            lastBook = information.getKey();
        }
        getDataFromServer(lastBook);
    }

}
