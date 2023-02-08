


package com.uog.exercise;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.uog.exercise.database.DatabaseHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // START OF Logbook section 1 code
    private WebView webView;
    private ImageButton btnPrevious, btnNext, btnAdd, btnCamera;
    private TextView txtLocation;
    // END OF section 1


    // START OF Logbook section 2 code
    private List<IImage> images = new ArrayList<>();
    private IImage currentSelectedImage;
    private int index =0;
    private static final int ADD_IMAGE_REQUEST_CODE =1;
    private static final int CAMERA_REQUEST_CODE =2;
    // END OF section 2

    // START OF Logbook section 1 code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        imageMovementActionRegister( );
        addImageActionRegister( );
        takePhotoActionRegister( );
    }
    // END OF section 1


    // START OF Logbook section 1 code
    private void bindViews(){
        webView =findViewById(R.id.webView);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        btnPrevious =findViewById(R.id.btnPrevious);
        btnNext =findViewById(R.id.btnNext);
        btnAdd =findViewById(R.id.btnAdd);
        btnCamera =findViewById(R.id.btnCamera);
        txtLocation =findViewById(R.id.txtLocation);
    }
    // END OF section 1


    // START OF Logbook section 1 code
    private void imageMovementActionRegister(){
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( index >0) {
                    --index;
                    currentSelectedImage =images.get(index);
                    displayImage();
                }else{
                    Toast.makeText(MainActivity.this, "No more photo!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( index < images.size() - 1) {
                    ++index;
                    currentSelectedImage =images.get(index);
                    displayImage();
                }else {
                    Toast.makeText(MainActivity.this, "No more photo!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayImage(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String html = "<html><head></head><body><img src=\""+ currentSelectedImage.getPath() + "\"></body></html>";
                webView.loadDataWithBaseURL("", html, "text/html","utf-8", "");

                if( currentSelectedImage.getLatitude() !=null
                        && currentSelectedImage.getLongitude() !=null ){
                    txtLocation.setText("Location:" + currentSelectedImage.getLatitude() + "," + currentSelectedImage.getLongitude());
                }else{
                    txtLocation.setText("");
                }
            }
        });
    }
    // END OF section 1


    // START OF Logbook section 2 code
    private void addImageActionRegister(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent( MainActivity.this, EntryActivity.class ), ADD_IMAGE_REQUEST_CODE);
            }
        });
    }
    // END OF section 2


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // START OF Logbook section 2 code
        if (requestCode ==ADD_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String imageUrl = data.getStringExtra(EntryActivity.IMAGE_URL);
                IImage iImage =new IImage(images.size() + 1, imageUrl, IImage.IMAGE_TYPE_URL );
                images.add( iImage );
                index =images.size() -1;
                currentSelectedImage =images.get(index);
                displayImage();
                save( iImage );
            }
            // END OF section 2

            // START OF Logbook section 4 code
        }else if( requestCode ==CAMERA_REQUEST_CODE ){
            if (resultCode == RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                //TODO write file and save to database
                String filePath =saveImageToFile( image );
                if( filePath !=null ){
                    IImage iImage =new IImage(images.size() + 1, "file://" + filePath, IImage.IMAGE_TYPE_FILE,
                            location !=null? location.getLatitude() : null, location !=null? location.getLongitude() : null );
                    images.add( iImage );
                    index =images.size() -1;
                    currentSelectedImage =images.get(index);
                    displayImage( );
                    save( iImage );
                }
            }
        }
        // END OF section 4
        super.onActivityResult(requestCode, resultCode, data);
    }

    // START OF Logbook section 3 code
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        list();
        registerLocationService();
    }
    private void save( IImage iImage ){
        try {
            DatabaseHelper database =new DatabaseHelper(getBaseContext());
            long result =0;
            result = database.save( iImage );
            if( result >0 ) {
                Toast.makeText(getBaseContext(), "Trip information has been saved!", Toast.LENGTH_SHORT).show();
            }
            database.close();
        }catch (Exception e){e.printStackTrace();}
    }

    private void list(){
        try {
            DatabaseHelper database =new DatabaseHelper(getBaseContext());
            images =database.list();
            database.close();
            if( images !=null && !images.isEmpty()){currentSelectedImage =images.get(index);displayImage();}
        }catch (Exception e){e.printStackTrace();}
    }
    // END OF section 3


    // START OF Logbook section 4 code
    private void takePhotoActionRegister( ) {
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });
    }

    private String saveImageToFile( Bitmap bitmap ) {
        String filePath =null;

        try {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File myDir = new File(root + "/saved_images");
            myDir.mkdirs();

            String fileName = "Image-"+ System.currentTimeMillis() +".jpg";
            File file = new File (myDir, fileName);
            if ( file.exists() )
                file.delete ();

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            filePath =file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    private FusedLocationProviderClient locationClient;
    private final int REQUEST_PERMISSION_FINE_LOCATION = 3;
    private Location location;
    private void registerLocationService( ){
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINE_LOCATION);
        }else{ getLocation(); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_LONG).show();
                    getLocation();
                }else{
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();
                }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) { MainActivity.this.location =location; }
        });
        locationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()) { location = task.getResult(); }
            }
        });
    }
    // END OF section 4

}