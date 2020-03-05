package com.example.healthin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.widget.Toast.*;


public class MainActivity extends AppCompatActivity {

    AmazonS3 s3Client;
    String bucket = "shetechimage";
    //File uploadToS3 = new File("/storage/emulated/0/DCIM/Camera/IMG_20190115_180108.jpg");
    //File downloadFromS3 = new File("/storage/emulated/0/DCIM/Camera");
    TransferUtility transferUtility;
    List<String> listing;
    CognitoCachingCredentialsProvider credentialsProvider;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
        }
        s3credentialsProvider();

        setTransferUtility();

    }

    public void s3credentialsProvider() {

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-southeast-1:88395f09-58a8-4e8c-a81e-8452802b9c0a", // Identity pool ID
                Regions.AP_SOUTHEAST_1 // Region
        );
    }

//    e5e40f7d-7691-44d2-8806-d8bc9c703b4a
    /**
     * Create a AmazonS3Client constructor and pass the credentialsProvider.
     *
     * @param credentialsProvider
     */
    public void createAmazonS3Client(CognitoCachingCredentialsProvider
                                             credentialsProvider) {

        // Create an S3 client
        s3Client = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
    }

    public void setTransferUtility() {

        transferUtility = new TransferUtility((com.amazonaws.services.s3.AmazonS3) s3Client, getApplicationContext());
    }

    /**
     * This method is used to upload the file to S3 by using TransferUtility class
     *
     * @param view
     */
    public void uploadFileToS3(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);


    }

    public static final int PICK_IMAGE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
//            String path= (String) data.getExtras().get("data");
            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
            TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
            Uri uri = data.getData();
            try {
                String path = PathUtil.getPath(MainActivity.this, uri);
                File file = new File(path);
                TransferObserver transferObserver = transferUtility.upload(bucket, "test", file);
                transferObserverListener(transferObserver);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * This method is used to Download the file to S3 by using transferUtility class
     *
     * @param view
     **/
  /*  public void downloadFileFromS3(View view) {

        TransferObserver transferObserver = transferUtility.download(bucket, "IMG_20190115_180108.jpg", downloadFromS3);
        transferObserverListener(transferObserver);
    } */

    public void fetchFileFromS3(View view) {

        // Get List of files from S3 Bucket
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Looper.prepare();
                    listing = getObjectNamesForBucket(bucket, s3Client);

                    for (int i = 0; i < listing.size(); i++)
                        makeText(MainActivity.this, listing.get(i), LENGTH_LONG).show();
                    Looper.loop();
                    // Log.e("tag", "listing "+ listing);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("tag", "Exception found while listing " + e);
                }

            }
        });
        thread.start();
    }

    /**
     * @param bucket
     * @param s3Client
     * @return object with list of files
     * @desc This method is used to return list of files name from S3 Bucket
     */
    private List<String> getObjectNamesForBucket(String bucket, AmazonS3 s3Client) {
        ObjectListing objects = s3Client.listObjects(bucket);
        List<String> objectNames = new ArrayList<String>(objects.getObjectSummaries().size());
        Iterator<S3ObjectSummary> iterator = objects.getObjectSummaries().iterator();
        while (((Iterator) iterator).hasNext()) {
            objectNames.add(iterator.next().getKey());
        }
        while (objects.isTruncated()) {
            objects = s3Client.listNextBatchOfObjects(objects);
            iterator = objects.getObjectSummaries().iterator();
            while (iterator.hasNext()) {
                objectNames.add(iterator.next().getKey());
            }
        }
        return objectNames;
    }

    /**
     * This is listener method of the TransferObserver
     * Within this listener method, we get status of uploading and downloading file,
     * to display percentage of the part of file to be uploaded or downloaded to S3
     * It displays an error, when there is a problem in  uploading or downloading file to or from S3.
     *
     * @param transferObserver
     */

    public void transferObserverListener(TransferObserver transferObserver) {

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                makeText(getApplicationContext(), "State Change"
                        + state, LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                makeText(getApplicationContext(), "Progress in %"
                        + percentage, LENGTH_SHORT).show();
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error", "error");
            }

        });
    }

}