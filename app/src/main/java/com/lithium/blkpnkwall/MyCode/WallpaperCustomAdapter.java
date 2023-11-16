package com.lithium.blkpnkwall.MyCode;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lithium.blkpnkwall.MyCode.WallpaperOpen.WallpaperOpenActivity;
import com.lithium.blkpnkwall.R;
import com.lithium.blkpnkwall.databinding.ItemsLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class WallpaperCustomAdapter extends RecyclerView.Adapter<WallpaperCustomAdapter.MyViewHolder> {

    Context context;
    List<WallpaperItemModel> showItemModels;

    ArrayList<String> imageUrlArray = new ArrayList<>();
    ArrayList<Integer> downloadsArray = new ArrayList<>();
    ArrayList<String> idArray = new ArrayList<>();

    public WallpaperCustomAdapter(Context context, List<WallpaperItemModel> showItemModels) {
        this.context = context;
        this.showItemModels = showItemModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WallpaperItemModel singularItem = showItemModels.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(20));
        Glide.with(context)
                .load(singularItem.getImageUrl())
                .apply(requestOptions)
                .into(holder.binding.showImageShowOpen);

        holder.binding.downloadsCount.setText(String.valueOf(singularItem.getDownloads()));

        holder.binding.showOpenItemRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadsArray.clear();
                idArray.clear();
                imageUrlArray.clear();
                for (int i = 0; i < showItemModels.size(); i++) {
                    imageUrlArray.add(showItemModels.get(i).getImageUrl());
                    downloadsArray.add(showItemModels.get(i).getDownloads());
                    idArray.add(showItemModels.get(i).getId());
                }

                Intent openImage = new Intent(context, WallpaperOpenActivity.class);
                openImage.putExtra("imageUrl", singularItem.getImageUrl());
                openImage.putExtra("downloads", singularItem.getDownloads());
                openImage.putExtra("id", singularItem.getId());
                openImage.putStringArrayListExtra("idArray", idArray);
                openImage.putIntegerArrayListExtra("downloadsArray", downloadsArray);
                openImage.putStringArrayListExtra("imageUrlArray", imageUrlArray);
                context.startActivity(openImage);

            }
        });
    }

    @Override
    public int getItemCount() {
        return showItemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ItemsLayoutBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemsLayoutBinding.bind(itemView);
        }
    }
}

