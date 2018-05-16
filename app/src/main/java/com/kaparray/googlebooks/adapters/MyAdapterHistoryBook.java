package com.kaparray.googlebooks.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaparray.googlebooks.Data.SaleInfo;
import com.kaparray.googlebooks.R;

import com.kaparray.googlebooks.RealmData.BookRealm;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;


public class MyAdapterHistoryBook extends RecyclerView.Adapter<MyAdapterHistoryBook.ViewHolder> {


    private List<BookRealm> booksDataList;
    private Context context;


    public MyAdapterHistoryBook(List<BookRealm> booksDataList, Context context) {
        this.booksDataList = booksDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.setTitleName(booksDataList.get(position).getName());
        } catch (NullPointerException e) {
            holder.setTitleName("None book name");
            Log.w("Message", "Error: " + e.getStackTrace());
        }

        try {
            if(booksDataList.get(position).getPhoto() == null){
                holder.setPhoto(context.getResources().getDrawable(R.drawable.ic_error));
            }else {
                holder.setPhoto(booksDataList.get(position).getPhoto());
            }
        } catch (Exception e) {
            holder.setPhoto(context.getResources().getDrawable(R.drawable.ic_error));
        }

        try {
            holder.setAuthor(booksDataList.get(position).getAuthor());
        } catch (NullPointerException e) {
            holder.setAuthor("None author");
            Log.w("Message", "Error: " + e.getStackTrace());
        }

        try {
            holder.setPrice(booksDataList.get(position).getPrice());
        } catch (NullPointerException e) {
            holder.setPrice("None price");
            Log.w("Message", "Error: " + e.getStackTrace());
        }

    }

    @Override
    public int getItemCount() {
        Log.d("Message", booksDataList.size() + "");
        return booksDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Log.d("Error", itemView+"");
        }

        public void setTitleName(String Name) {
            TextView name = mView.findViewById(R.id.tv_nameBook);

            if (Name.length() <= 30) {
                name.setText(Name);
            } else {
                name.setText(Name.substring(0, 30) + "..");
            }
            Log.d("Error Name", Name+"");
        }


        public void setPhoto(String Link) {
            ImageView imageView = mView.findViewById(R.id.iv_photo);
            Picasso.get().load(Link).into(imageView);
            Log.d("Error Photo", Link+"");

        }

        public void setPhoto(Drawable drawable) {
            ImageView imageView = mView.findViewById(R.id.iv_photo);
            imageView.setImageDrawable(drawable);
            Log.d("Error Photo", drawable+"");
        }

        public void setAuthor(List<String> Author) {
            TextView author = mView.findViewById(R.id.tv_author);
            String authorString = "";
            for (int i = 0; i < Author.size(); i++)
                authorString = Author.get(i) + " ";

            if (authorString.length() <= 20) {
                author.setText(authorString);
            } else {
                author.setText(authorString.substring(0, 18) + "..");
            }
            Log.d("Error author", Author+"");
        }

        public void setAuthor(String Author) {
            TextView author = mView.findViewById(R.id.tv_author);
            author.setText(Author);
            Log.d("Error author", Author+"");

        }

        @SuppressLint("SetTextI18n")
        public void setPrice(SaleInfo saleInfo) {
            TextView price = mView.findViewById(R.id.tv_price);
            if (saleInfo.getSaleability().equals("FOR_SALE")) {
                price.setText(saleInfo.getRetailPrice().getAmount() + saleInfo.getRetailPrice().getCurrencyCode());
            } else if (saleInfo.getSaleability().equals("NOT_FOR_SALE")) {
                price.setText("Not for sale");
            }
        }


        public void setPrice(String Price) {
            TextView price = mView.findViewById(R.id.tv_price);
            price.setText(Price);
        }
    }
}