package com.lithium.blkpnkwall.MyCode;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lithium.blkpnkwall.R;
import com.lithium.blkpnkwall.databinding.ActivityWallpaperBaseBinding;

import java.util.ArrayList;
import java.util.List;

public class WallpaperBaseActivity extends AppCompatActivity {

    List<WallpaperItemModel> showItemModels = new ArrayList<>();
    WallpaperCustomAdapter customAdapter;
    ActivityWallpaperBaseBinding binding;
    FirebaseFirestore firebaseFirestore;

    private static final String TAG = "WallpaperBaseActivity";
    int scrolledViews, currentViews, totalViews;
    boolean isScrolling = false;
    List<DocumentSnapshot> snapshotList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_wallpaper_base, null);
        setContentView(view);
        binding = ActivityWallpaperBaseBinding.bind(view);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.setFirestoreSettings(settings);

        //TODO : CHANGE NAME.
        binding.showOpenTitle.setText("BlackPink Wallpapers");

        GridLayoutManager manager = new GridLayoutManager(this, 2);
        customAdapter = new WallpaperCustomAdapter(this, showItemModels);
        binding.showOpenRecyclerView.setAdapter(customAdapter);
        binding.showOpenRecyclerView.setLayoutManager(manager);

        getDataFromFirebase("", true);

        binding.goBackShowOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.showOpenRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentViews = manager.getChildCount();
                scrolledViews = manager.findFirstVisibleItemPosition();
                totalViews = manager.getItemCount();

                if (isScrolling && ((currentViews + scrolledViews) == totalViews)) {
                    if (totalViews % 20 == 0) {
                        getDataFromFirebase(snapshotList.get(totalViews - 1), false);
                    } else {
                        binding.progressShowOpen.setVisibility(View.GONE);
                    }
                    isScrolling = false;
                }
            }
        });
    }

    private void getDataFromFirebase(Object object, boolean isNew) {

        Query selectedData;

        if (isNew) {

            //TODO : CHANGE COLLECTION NAME.
            selectedData = firebaseFirestore.collection("BlackPink")
                    .orderBy("downloads", Query.Direction.DESCENDING)
                    .limit(20);

        } else {
            DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;

            //TODO : CHANGE COLLECTION NAME.
            selectedData = firebaseFirestore.collection("BlackPink")
                    .orderBy("downloads", Query.Direction.DESCENDING)
                    .limit(20)
                    .startAfter(documentSnapshot);
        }

        selectedData.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (isNew) {
                            showItemModels.clear();
                        }

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                            WallpaperItemModel singularItem = snapshot.toObject(WallpaperItemModel.class);
                            singularItem.setId(snapshot.getId());
                            showItemModels.add(singularItem);
                            snapshotList.add(snapshot);
                        }
                        customAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, e.getMessage());
                        Toast.makeText(WallpaperBaseActivity.this, e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}