package com.lithium.blkpnkwall.MyCode.WallpaperOpen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.lithium.blkpnkwall.MyCode.WallpaperItemModel;
import com.lithium.blkpnkwall.R;
import com.lithium.blkpnkwall.databinding.ActivityWallpaperOpenBinding;
import com.lithium.blkpnkwall.databinding.SetWallpaperDialogBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WallpaperOpenActivity extends AppCompatActivity {

    List<WallpaperItemModel> showItemModels = new ArrayList<>();
    List<WallpaperItemModel> selectedList = new ArrayList<>();
    MoreLikeThisCustomAdapter customAdapter;
    ActivityWallpaperOpenBinding binding;
    FirebaseFirestore firebaseFirestore;

    ArrayList<String> imageUrlArray = new ArrayList<>();
    ArrayList<String> idArray = new ArrayList<>();
    ArrayList<Integer> downloadsArray = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 1;
    WallpaperManager wallpaperManager;
    OutputStream outputStream;

    String id, imageUrl;
    int downloads;
    int random = 0;

    //TODO:CHANGE THE TAG.
    private static final String tag = "BlackPink";

    Bitmap resultImage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_wallpaper_open, null);
        setContentView(view);

        binding = ActivityWallpaperOpenBinding.bind(view);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.setFirestoreSettings(settings);

        Intent getData = getIntent();
        id = getData.getStringExtra("id");
        downloads = getData.getIntExtra("downloads", 0);
        imageUrl = getData.getStringExtra("imageUrl");
        downloadsArray = getData.getIntegerArrayListExtra("downloadsArray");
        idArray = getData.getStringArrayListExtra("idArray");
        imageUrlArray = getData.getStringArrayListExtra("imageUrlArray");

        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        //TODO: CHANGE TITLE.
        customAdapter = new MoreLikeThisCustomAdapter(this, showItemModels, selectedList,
                "BlackPink Wallpapers");
        binding.moreLikeThisRecyclerView.setAdapter(customAdapter);
        binding.moreLikeThisRecyclerView.setLayoutManager(manager);

        getDataForRelatedItems();

        binding.goBackWallpaperOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //TODO: CHANGE TITLE.
        binding.showName.setText("BlackPink Wallpapers");
        binding.imageDownloads.setText("Downloads : " + downloads);
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {

                        resultImage = resource;

                        binding.showImageWallpaperOpen.setImageBitmap(resultImage);
                        return true;
                    }
                }).submit();

        //Download On CLick.
        binding.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAction();
            }
        });

        //Set Wallpaper OnClick.
        binding.setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaperAction();
            }
        });

        //On CLick on Image.
        binding.showImageWallpaperOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WallpaperOpenActivity.this, FullScreenImageView.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        WallpaperOpenActivity.this, binding.showImageWallpaperOpen,
                        ViewCompat.getTransitionName(binding.showImageWallpaperOpen)
                );
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent, optionsCompat.toBundle());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getDataForRelatedItems() {
        final int min = 0;
        final int max = idArray.size() - 1;

        selectedList.clear();
        showItemModels.clear();
        for (int i = 0; i < 10; i++) {
            random = new Random().nextInt((max - min) + 1) + min;
            WallpaperItemModel singularItem = new WallpaperItemModel();
            singularItem.setImageUrl(imageUrlArray.get(random));
            singularItem.setDownloads(downloadsArray.get(random));
            singularItem.setId(idArray.get(random));

            selectedList.add(singularItem);
        }

        for (int i = 0; i < imageUrlArray.size(); i++) {
            WallpaperItemModel singularItem = new WallpaperItemModel();
            singularItem.setImageUrl(imageUrlArray.get(i));
            singularItem.setDownloads(downloadsArray.get(i));
            singularItem.setId(idArray.get(i));

            showItemModels.add(singularItem);
        }

        customAdapter.notifyDataSetChanged();
    }

    //Download Wallpaper Action.
    private void downloadAction() {
        Permissions();
    }

    //Set Wallpaper Action.
    private void setWallpaperAction() {
        View view = getLayoutInflater().inflate(R.layout.set_wallpaper_dialog, null);

        SetWallpaperDialogBinding binding = SetWallpaperDialogBinding.bind(view);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.setLockScreen.setVisibility(View.VISIBLE);
        } else {
            binding.setLockScreen.setVisibility(View.GONE);
        }

        binding.setHomeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                ProgressDialog progressDialog = new ProgressDialog(WallpaperOpenActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Setting Wallpaper");
                progressDialog.setMessage("Please wait..");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setHomeScreenWallpaper(resultImage, progressDialog);
                    }
                }, 2000);

                progressDialog.show();

            }
        });

        binding.setLockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                ProgressDialog progressDialog = new ProgressDialog(WallpaperOpenActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Setting Wallpaper");
                progressDialog.setMessage("Please wait..");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        setLockScreenWallpaper(resultImage, progressDialog);
                    }
                }, 2000);

                progressDialog.show();
            }
        });

        dialog.show();
    }

    //Set HomeScreen Wallpaper.
    private void setHomeScreenWallpaper(Bitmap resource, ProgressDialog dialog) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int phoneHeight = metrics.heightPixels;
            int phoneWidth = metrics.widthPixels;

            Bitmap bitmap = ThumbnailUtils.extractThumbnail(resource, phoneWidth,
                    phoneHeight);
            wallpaperManager.setBitmap(bitmap);

            updateDownloads();

            dialog.dismiss();

        } catch (IOException e) {
            e.printStackTrace();
            Log.w("mes", e.getMessage());
            Toast.makeText(getApplicationContext(), "Something went wrong. " +
                    "\nTry again after some time", Toast.LENGTH_SHORT).show();
        }

    }

    //Set Lock Screen Wallpaper.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setLockScreenWallpaper(Bitmap resource, ProgressDialog progressDialog) {

        try {

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int phoneHeight = metrics.heightPixels;
            int phoneWidth = metrics.widthPixels;

            Bitmap bitmap = ThumbnailUtils.extractThumbnail(resource, phoneWidth,
                    phoneHeight);

            wallpaperManager.setBitmap(bitmap,
                    null, true, WallpaperManager.FLAG_LOCK);

            updateDownloads();

            progressDialog.dismiss();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong. " +
                    "\nTry again after some time", Toast.LENGTH_SHORT).show();
        }
    }

    //Get Permissions.
    private void Permissions() {
        if (ContextCompat.checkSelfPermission(WallpaperOpenActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            downloadImage();
        } else {
            requestPermissionFromUser();
        }
    }

    //Request Permission to download.
    private void requestPermissionFromUser() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Important!!")
                    .setMessage("We need this permission to download this image.")
                    .setPositiveButton("Give Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(WallpaperOpenActivity.this
                                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    }).create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                downloadImage();

            } else {

                Toast.makeText(WallpaperOpenActivity.this,
                        "Permission to download has been denied", Toast.LENGTH_SHORT).show();

            }
        }
    }

    //Check For Folder.
    @SuppressLint("SetTextI18n")
    private void downloadImage() {
        //Do the downloading....

        //TODO: CHANGE STORAGE LOCATION NAME.
        File dir = new File(Environment.getExternalStorageDirectory(), "BlackPink_Walls");

        String fileName = tag + "_" + id.substring(0, 6);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        //TODO: CHANGE FILE NAME.
        File checkForSame = new File(Environment.getExternalStorageDirectory(), "BlackPink_Walls/"
                + fileName + ".jpg");

        if (!checkForSame.exists()) {
            new downloadImageFromServer(fileName, imageUrl, dir, resultImage).execute();
        } else {
            Toast.makeText(WallpaperOpenActivity.this,
                    "This file already exists in your device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateDownloads() {
        //TODO: CHANGE COLLECTION NAME.
        firebaseFirestore.collection("BlackPink")
                .document(id)
                .update("downloads", FieldValue.increment(1));
    }

    //Download Image.
    @SuppressLint("StaticFieldLeak")
    private class downloadImageFromServer extends AsyncTask<Void, Void, Void> {

        String fileName, image;
        File dir;
        Bitmap resultImage;

        ProgressDialog dialog = new ProgressDialog(WallpaperOpenActivity.this);

        public downloadImageFromServer(String fileName, String image, File dir, Bitmap resultImage) {

            this.fileName = fileName;
            this.image = image;
            this.dir = dir;
            this.resultImage = resultImage;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setTitle("Downloading Image");
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait...");

            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            File file = new File(dir, fileName + ".jpg");
            try {
                outputStream = new FileOutputStream(file);

                resultImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("Wallpaper Download", "Error in flushing output stream");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void isSaved) {
            super.onPostExecute(isSaved);

            updateDownloads();
            Handler beforeDownload = new Handler();

            beforeDownload.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("Almost Downloaded");
                }
            }, 1000);


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(WallpaperOpenActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();
                }
            }, 3000);

        }
    }
}