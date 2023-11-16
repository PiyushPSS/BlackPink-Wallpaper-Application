package com.lithium.blkpnkwall.MyCode.WallpaperOpen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lithium.blkpnkwall.R;
import com.lithium.blkpnkwall.databinding.ImageFullScreenBinding;

public class FullScreenImageView extends AppCompatActivity {

    ImageFullScreenBinding binding;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.image_full_screen, null);
        setContentView(view);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("imageUrl");

        binding = ImageFullScreenBinding.bind(view);

        binding.goBackFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Glide.with(this)
                .load(imageUrl)
                .into(binding.fullImage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}