package com.kaparray.googlebooks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaparray.googlebooks.Api.ApiBook;

import com.kaparray.googlebooks.Data.Item;
import com.kaparray.googlebooks.RealmData.BookRealm;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenBookActivity extends AppCompatActivity {


    public static final String KEY_API = "AIzaSyBA14zOMI_3xSNWED-HYtfl8CidvqMTuRM";

    Item item;
    Realm realm;


    @BindView(R.id.tv_nameOpen) TextView mNameBook;
    @BindView(R.id.tv_authorOpen) TextView mNameAuthor;
    @BindView(R.id.iv_photoOpen) ImageView mPhoto;
    @BindView(R.id.pb_Open)ProgressBar mProgressBar;
    @BindView(R.id.btn_freeSampleBook) Button mButtonFreeSample;
    @BindView(R.id.btn_eBook) Button mEBook;
    @BindView(R.id.tv_description) TextView mDescription;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_open_book);

        ButterKnife.bind(this);

        // Initialize Realm (just once per application)
        Realm.init(getApplicationContext());

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();


        mNameBook.setVisibility(View.GONE);
        mNameAuthor.setVisibility(View.GONE);
        mPhoto.setVisibility(View.GONE);
        mButtonFreeSample.setVisibility(View.GONE);
        mEBook.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);


        Intent intent = getIntent();
        String id = intent.getStringExtra("data");
        String list =  intent.getStringExtra("list" );



        getBookDataFromServer(id, list);



        mButtonFreeSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new
                        Intent(Intent.ACTION_VIEW, Uri.parse(item.getAccessInfo().getWebReaderLink()));
                startActivity(browserIntent);
            }
        });

        mEBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getSaleInfo().getIsEbook()){
                    Intent browserIntent = new
                            Intent(Intent.ACTION_VIEW, Uri.parse(item.getSaleInfo().getBuyLink()));
                    startActivity(browserIntent);
                }else{
                    Toast.makeText(OpenBookActivity.this, "Данная книга не продаётся!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    void getBookDataFromServer(String id, final String list){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiBook apiBook = retrofit.create(ApiBook.class);
        Call<Item> call = apiBook.getBook(id, KEY_API);

        call.enqueue(new Callback<Item>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                item = response.body();


                if(list.equals("false")) {
                    realm.beginTransaction();  //open the database
                    //database operation
                    BookRealm bookDataRealm = realm.createObject(BookRealm.class);


                    RealmResults<BookRealm> results = realm.where(BookRealm.class).findAllAsync();
                    //fetching the data
                    results.load();

                    int last = 0;

                    for (BookRealm information : results) {
                        last = information.getIdBook();
                    }


                    bookDataRealm.setIdBook(last + 1);
                    mNameBook.setText(item.getVolumeInfo().getTitle() + "");
                    bookDataRealm.setKey(item.getId());
                    bookDataRealm.setName(item.getVolumeInfo().getTitle() + "");// Add to realm db
                    bookDataRealm.setAuthor(item.getVolumeInfo().getAuthors().get(0));// Add to realm db
                    try {
                        bookDataRealm.setPhoto(item.getVolumeInfo().getImageLinks().getThumbnail());// Add to realm db
                    } catch (NullPointerException e) {
                        bookDataRealm.setPrice(null);// Add to realm db
                    }

                    try {
                        bookDataRealm.setPrice(item.getSaleInfo().getRetailPrice().getAmount() + " " +
                                item.getSaleInfo().getRetailPrice().getCurrencyCode());// Add to realm db
                    } catch (NullPointerException e) {
                        bookDataRealm.setPrice("None price");// Add to realm db
                    }

                    realm.commitTransaction(); //close the database
                }

                try {
                    String authorString = item.getVolumeInfo().getTitle();

                    if(authorString.length() <= 10) {
                        mNameAuthor.setText(item.getVolumeInfo().getTitle());
                    }else{
                        mNameAuthor.setText(item.getVolumeInfo().getTitle().substring(0,10) + "\n"
                                + item.getVolumeInfo().getTitle().substring(10));
                    }
                }catch (NullPointerException e){
                    mNameBook.setText("None book name");
                }


                try {
                    String authorString = "";
                    for (int i = 0; i < item.getVolumeInfo().getAuthors().size(); i++)
                        authorString = item.getVolumeInfo().getAuthors().get(i) + " ";

                    if(authorString.length() <= 20) {
                        mNameAuthor.setText(authorString);
                    }else{
                        mNameAuthor.setText(authorString.substring(0,18) + "..");
                    }
                }catch (NullPointerException e){
                    mNameAuthor.setText("None author");
                }

                try {
                    Picasso.get().load(item.getVolumeInfo().getImageLinks().getThumbnail()).into(mPhoto);
                }catch (NullPointerException e){
                    mPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                }


                try {
                    mEBook.setText(item.getSaleInfo().getRetailPrice().getAmount() + " " +
                            item.getSaleInfo().getRetailPrice().getCurrencyCode());
                }catch (NullPointerException e){
                    mEBook.setText("None price");
                }



                try {
                    String d = item.getVolumeInfo().getDescription() + "";

                    if(d.equals("null")){
                        mDescription.setText("None description");
                    }else{
                        mDescription.setText(d + "");
                    }

                    Log.d("1111", d);
                }catch (Exception e){
                    mEBook.setText("None description");
                }



                mNameBook.setVisibility(View.VISIBLE);
                mNameAuthor.setVisibility(View.VISIBLE);
                mPhoto.setVisibility(View.VISIBLE);
                mButtonFreeSample.setVisibility(View.VISIBLE);
                mEBook.setVisibility(View.VISIBLE);

                mProgressBar.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }

        });
    }
}
