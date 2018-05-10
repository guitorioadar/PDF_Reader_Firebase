package com.vaidoos.guitorio.uploadfiletoserver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button btnPick, btnUpload, btnFetch;
    private TextView filePath;

    private String path;

    private ProgressDialog progressDialog;

    private Uri pdfUri; // Uri হল URL এর মত যেটা Local Storag এর জন্য বেবহার করা হয়

    private FirebaseStorage mStorageRef; // used for store data
    private FirebaseDatabase mDatabase; // used for locating location of the data


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPick = findViewById(R.id.btnPick);
        btnUpload = findViewById(R.id.btnUpload);
        btnFetch = findViewById(R.id.btnFetch);

        filePath = findViewById(R.id.tvPath);

        mStorageRef = FirebaseStorage.getInstance(); // Returns an object of firebase storage
        mDatabase = FirebaseDatabase.getInstance(); // Returns object of database

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectPdf();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pdfUri != null) {
                    uploadFile(pdfUri);
                } else {
                    Toast.makeText(MainActivity.this, "Select a file", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MyRecyclerViewActivity.class));

            }
        });

    }

    private void uploadFile(Uri pdfUri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading FIle...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName = System.currentTimeMillis()+".pdf";
        final String fileName1 = System.currentTimeMillis()+"";
        StorageReference storageReference = mStorageRef.getReference(); // referance root storage path

        storageReference.child("Uploads").child(fileName).putFile(pdfUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        progressDialog.dismiss();

                        Toast.makeText(MainActivity.this, "File uploaded successfully in the storage", Toast.LENGTH_SHORT).show();

                        String url = taskSnapshot.getDownloadUrl().toString(); // return the url of the file

                        // store the url to database

                        DatabaseReference databaseReference = mDatabase.getReference();
                        databaseReference.child(fileName1).setValue(url)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "File Successfully Uploaded in the database...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "File Not Successfully Uploaded in the database...", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "File not successfully uploaded, ERROR =:= " + e.toString(), Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                // Track the progress of the upload

                int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);

                /*while (currentProgress == 100){
                    progressDialog.dismiss();
                }*/

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPdf();
        } else {
            Toast.makeText(this, "Please provide permission", Toast.LENGTH_LONG).show();
        }
    }

    private void selectPdf() {

        // to offer user to select file using file manager

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check whether user has selected a file or not

        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData(); // return uri for selected file
            filePath.setText("A file is selected : " + data.getData().getLastPathSegment());
        } else {
            Toast.makeText(this, "Please select the file", Toast.LENGTH_SHORT).show();
        }

    }
}


//        btnPick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                startActivityForResult(intent,7);
//            }
//        });


        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);
        }*/


        /*btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new MaterialFilePicker()
                        .withActivity(MainActivity.this)
                        .withRequestCode(1000)
                        .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                        .withFilterDirectories(true) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();


            }
        });*/


/*
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case 7:
                if(resultCode == RESULT_OK){
                    */
/*String PathHolder = data.getData().getPath();
                    Toast.makeText(this, PathHolder, Toast.LENGTH_SHORT).show();
                    filePath.setText(PathHolder);*//*

                    //filePath.setText(getFileName(Uri.parse(PathHolder)));


                    String selectedImageUri = data.getData().getPath();
                    //String selectedPath = getPath(selectedImageUri);
                    filePath.setText("Uri : "+selectedImageUri);
                }
                break;
        }

    }

    private String getPath(Uri uri) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            cursor.close();


        }

        return path;

    }

    */
/*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1000 && resultCode == RESULT_OK){
            String filepath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file

            textView.setText(filepath);

        }
    }*//*


   */
/* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1001:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*//*

}
*/
