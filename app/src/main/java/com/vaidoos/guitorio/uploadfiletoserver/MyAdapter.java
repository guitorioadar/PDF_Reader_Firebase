package com.vaidoos.guitorio.uploadfiletoserver;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Guitorio on 5/7/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> urls = new ArrayList<String>();

    private PDFView pdfView;
    private FrameLayout frameLayout;




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
        DownloadManager downloadManager;


        public ViewHolder(View itemView) { // represents individual list items
            super(itemView);
            nameOfFile = itemView.findViewById(R.id.nameOfFile);
            pdfView = itemView.findViewById(R.id.pdfView);
            //frameLayout = itemView.findViewById(R.id.frmaLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //frameLayout.setVisibility(View.VISIBLE);

                    int position = recyclerView.getChildLayoutPosition(view);

                    String url = urls.get(position);

                    // To download the file

                    downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Long referance = downloadManager.enqueue(request);


                    // To open in google Drive

                    /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(intent, "Choose an Application:"));*/
                    //context.startActivity(intent);

                    /*Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }*/

                    //urls.get(position);

                    //Toast.makeText(context, "This item Clicked : "+urls.get(position), Toast.LENGTH_LONG).show();

                    //new RetrivePDFStream().execute(urls.get(position));


                    /*Intent intent = new Intent();
                    intent.setType(Intent.ACTION_VIEW); // denotes that we are going to view something
                    intent.setData(Uri.parse(urls.get(position)));
                    context.startActivity(intent);*/

                }
            });

        }
    }

    public void update(String fileName, String url) {

        items.add(fileName);
        urls.add(url);
        notifyDataSetChanged(); // refreshes the recycler view

    }

    class RetrivePDFStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {

                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }

            } catch (IOException e) {

                return null;

            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }

}
