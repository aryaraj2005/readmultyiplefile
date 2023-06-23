package com.example.readmultiplefile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    StorageReference storageReference;
    ArrayList<String> files , status;
    Button upload;
    RecyclerView recyclerView;
    myAdapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         storageReference = FirebaseStorage.getInstance().getReference();

         files = new ArrayList<>();
         status = new ArrayList<>();

         upload = findViewById(R.id.uploadbtn);
         recyclerView = findViewById(R.id.recycl);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         adapter = new myAdapter(files , status);
         recyclerView.setAdapter(adapter);

    // now uploading the data in firebase storage
           upload.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                // first manage the runtime permission
                   Dexter.withContext(getApplicationContext())
                           .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                           .withListener(new PermissionListener() {
                               @Override
                               public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                   Intent intent = new Intent();
                                   intent.setType("image/*");
                                   intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE , true);
                                   intent.setAction(Intent.ACTION_GET_CONTENT);
                                   startActivityForResult(Intent.createChooser(intent , "please Select the images") , 101);

                               }

                               @Override
                               public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                               }

                               @Override
                               public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                           permissionToken.continuePermissionRequest();
                               }
                           }).check();



              }
           });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode == RESULT_OK){
            // now check the data is selected or not
            if(data.getClipData() !=null){
                for (int i = 0; i <data.getClipData().getItemCount(); i++) {
                    // iske help se hum sare image ke uri ko pata kar payenge
                    Uri fileuri = data.getClipData().getItemAt(i).getUri();
                    // now giving the name to each file
                    String filename = getfilenamefromuri(fileuri);
                     files.add(filename);
                     status.add("loading...");
                     // due to new changes we have to tell to the adapter
                    // iske help se adapter bar bar check karega new changes ko
                    adapter.notifyDataSetChanged();
                    // Now start the firestorage work
                    // filname se hi save hoga
                   final  int index = i;
                    StorageReference uploader= storageReference.child("/multiplesimage").child(filename);
                    // now it stored under firebase by uploader,put
                     uploader.putFile(fileuri)
                             .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                 @Override
                                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // after uploading we have to change the status
                                    status.remove(index);
                                    status.add(index , "done");
                                    adapter.notifyDataSetChanged();
                                    // after that we work over the adapter
                                 }
                             });

                }
            }
        }
    }
    @SuppressLint("Range")
    public String getfilenamefromuri(Uri filepath)
    {
        String result = null;
        if (filepath.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = filepath.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}