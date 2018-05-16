package com.kaparray.googlebooks.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaparray.googlebooks.OpenBookActivity;
import com.kaparray.googlebooks.R;
import com.kaparray.googlebooks.RealmData.BookRealm;
import com.kaparray.googlebooks.adapters.MyAdapterHistoryBook;
import com.kaparray.googlebooks.adapters.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryFragment extends Fragment{


    Realm realm;


    View rootView;

    @BindView(R.id.rv_history)
    RecyclerView mRecyclerViewBooks;
    @BindView(R.id.tv_notFound)
    TextView mNotFound;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<BookRealm> listB;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fr_history, container, false);


        ButterKnife.bind(this, rootView);
        // Initialize Realm (just once per application)
        Realm.init(getContext());

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();


        setHistory();


        return rootView;
    }





    void setHistory(){
        listB = new ArrayList<>();
        mRecyclerViewBooks.setVisibility(View.GONE);


        final RealmResults<BookRealm> results = realm.where(BookRealm.class).findAllAsync();
        //fetching the data
        results.load();


        for(BookRealm information: results){
            listB.add(information);
        }

        Collections.reverse(listB);




        try {
            if (listB.size() > 0) {

                mRecyclerViewBooks.setVisibility(View.VISIBLE);
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                mRecyclerViewBooks.setHasFixedSize(true);

                // use a linear layout manager
                mLayoutManager = new LinearLayoutManager(rootView.getContext());
                mRecyclerViewBooks.setLayoutManager(mLayoutManager);

                // specify an adapter (see also next example)
                mAdapter = new MyAdapterHistoryBook(listB, getContext());
                mRecyclerViewBooks.setAdapter(mAdapter);
                mRecyclerViewBooks.setVisibility(View.VISIBLE);

            } else {
                mNotFound.setVisibility(View.VISIBLE);
                mNotFound.setText("Не найдено ни одного результата");
            }
        }catch (Exception e){
            mNotFound.setVisibility(View.VISIBLE);
            mNotFound.setText("Не найдено ни одного результата");
        }

        mRecyclerViewBooks.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mRecyclerViewBooks, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever

                        Intent intent = new Intent(getActivity(), OpenBookActivity.class);
                        intent.putExtra("data",  listB.get(position).getKey());
                        intent.putExtra("list",  "true");
                        startActivity(intent);


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }


}
