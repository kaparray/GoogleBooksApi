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

import com.kaparray.googlebooks.Data.BooksData;
import com.kaparray.googlebooks.Data.SaleInfo;
import com.kaparray.googlebooks.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {



    private BooksData booksDataList;
    private Context context;


    public MyAdapter(BooksData booksDataList, Context context) {
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
            holder.setTitleName(booksDataList.getItems().get(position).getVolumeInfo().getTitle());
        }catch(Exception e){
            holder.setTitleName("None book name");
        }

        try {
            holder.setPhoto(booksDataList.getItems().get(position).getVolumeInfo().getImageLinks().getThumbnail());
        }catch(Exception e){
            holder.setPhoto(context.getResources().getDrawable(R.drawable.ic_error));
        }

        try {
            holder.setAuthor(booksDataList.getItems().get(position).getVolumeInfo().getAuthors());
        }catch(Exception e){
            holder.setAuthor("None author");
        }

        try {
            holder.setPrice(booksDataList.getItems().get(position).getSaleInfo());
        }catch(Exception e){
            holder.setPrice("None price");
        }

    }

    @Override
    public int getItemCount() {
        Log.d("Message", booksDataList.getItems().size()+"");
        return booksDataList.getItems().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitleName(String Name) {
            if(Name.length() <= 30) {
                TextView name = mView.findViewById(R.id.tv_nameBook);
                name.setText(Name + " ");
            }else{
                TextView name = mView.findViewById(R.id.tv_nameBook);
                name.setText(Name.substring(0,30) + "..");
            }
        }



        public void setPhoto(String Link) {
            ImageView imageView = mView.findViewById(R.id.iv_photo);
            Picasso.get().load(Link).into(imageView);
        }

        public void setPhoto(Drawable drawable) {
            ImageView imageView = mView.findViewById(R.id.iv_photo);
            imageView.setImageDrawable(drawable);
        }

        public void setAuthor(List<String> Author){
            TextView author = mView.findViewById(R.id.tv_author);
           for(int i = 0; i < Author.size(); i++){
               author.setText(Author.get(i) + ", ");
           }
        }

        public void setAuthor(String Author){
            TextView author = mView.findViewById(R.id.tv_author);
            author.setText(Author);
        }

        @SuppressLint("SetTextI18n")
        public void setPrice(SaleInfo saleInfo) {
            TextView price = mView.findViewById(R.id.tv_price);
            if (saleInfo.getSaleability().equals("FOR_SALE")) {
                price.setText(saleInfo.getRetailPrice().getAmount() + saleInfo.getRetailPrice().getCurrencyCode());
            }else if(saleInfo.getSaleability().equals("NOT_FOR_SALE")){
                price.setText("Not for sale");
            }
        }


        public void setPrice(String Price) {
            TextView price = mView.findViewById(R.id.tv_price);
            price.setText(Price);
        }
    }
}