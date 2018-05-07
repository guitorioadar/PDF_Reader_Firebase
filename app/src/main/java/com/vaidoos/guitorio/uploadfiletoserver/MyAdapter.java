package com.vaidoos.guitorio.uploadfiletoserver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guitorio on 5/7/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> urls = new ArrayList<String>();




    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // to create viewes for recycler view items

        View view = LayoutInflater.from(context).inflate(R.layout.items,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // initialize the elements of individual, items...
        holder.nameOfFile.setText(items.get(position));

    }

    @Override
    public int getItemCount() { // return the no of items
        return items.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameOfFile;

        public ViewHolder(View itemView) { // represents individual list items
            super(itemView);

            nameOfFile = itemView.findViewById(R.id.nameOfFile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = recyclerView.getChildLayoutPosition(view);

                    Intent intent = new Intent();
                    intent.setType(Intent.ACTION_VIEW); // denotes that we are going to view something

                    intent.setData(Uri.parse(urls.get(position)));

                    context.startActivity(intent);

                }
            });

        }
    }

    public void update(String fileName, String url) {

        items.add(fileName);
        urls.add(url);
        notifyDataSetChanged(); // refreshes the recycler view

    }

}
