package com.lithium.blkpnkwall.MyCode;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lithium.blkpnkwall.R;
import com.lithium.blkpnkwall.StickerCode.EntryActivity;
import com.lithium.blkpnkwall.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    //THIS WILL BE THE MAIN FILE, CHANGE ACCORDINGLY.
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);

        binding = ActivityMainBinding.bind(view);
        //TODO: CHANGE THE MAIN ACTIVITY XML FILE.

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//
//            }
//        });

        //TODO: CHANGE AD UNIT ID.
//        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-6078310243369312/8061965141")
//                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                    @Override
//                    public void onNativeAdLoaded(NativeAd nativeAd) {
//                        // Show the ad.
//                        NativeTemplateStyle styles = new
//                                NativeTemplateStyle.Builder().build();
//                        TemplateView template = findViewById(R.id.my_template);
//                        template.setStyles(styles);
//                        template.setNativeAd(nativeAd);
//
//                        // If this callback occurs after the activity is destroyed, you
//                        // must call destroy and return or you may get a memory leak.
//                        // Note `isDestroyed()` is a method on Activity.
//                        if (isDestroyed()) {
//                            nativeAd.destroy();
//                            return;
//                        }
//                    }
//                })
//                .withAdListener(new AdListener() {
//                    @Override
//                    public void onAdFailedToLoad(LoadAdError adError) {
//                        Log.w("ad error", adError.toString());
//                        // Handle the failure by logging, altering the UI, and so on.
//                    }
//                })
//                .withNativeAdOptions(new NativeAdOptions.Builder()
//                        // Methods in the NativeAdOptions.Builder class can be
//                        // used here to specify individual options settings.
//                        .build())
//                .build();
//        adLoader.loadAd(new AdRequest.Builder().build());

//        MediationTestSuite.launch(MainActivity.this);

        binding.stickersActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EntryActivity.class));
            }
        });

        binding.wallpaperActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WallpaperBaseActivity.class));
            }
        });

    }
}