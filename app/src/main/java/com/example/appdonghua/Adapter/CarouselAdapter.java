package com.example.appdonghua.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Activity.ComicInfoActivity;
import com.example.appdonghua.Model.Carousel;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.example.appdonghua.Utils.ScreenUtils;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder>{
    public List<Carousel> carouselItem;
    private List<Story> stories;
    private int carouselWidth;
    private int carouselHeight;

    public CarouselAdapter(List<Carousel> carouselItems) {
        this.carouselItem = carouselItems;
        this.stories = null;
    }

    public CarouselAdapter(List<Carousel> carouselItems, List<Story> stories) {
        this.carouselItem = carouselItems;
        this.stories = stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
        Context context = parent.getContext();
        ScreenUtils.ImageDimensions dims = ScreenUtils.calculateCarouselDimensions(context);
        carouselWidth = dims.width;
        carouselHeight = dims.height;



        return new ViewHolder(view, carouselWidth, carouselHeight);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Carousel carousel = carouselItem.get(position);
        if (carousel == null) return;

        String imageUrl = carousel.getImageUrl();

        Context context = holder.itemView.getContext();

        if (imageUrl != null && context != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img_1)
                    .into(holder.iv_carousel);
        }

        holder.itemView.setOnClickListener(v -> {
            Context context1 = v.getContext();
            Intent intent = new Intent(context1, ComicInfoActivity.class);

            if (stories != null && position < stories.size()) {
                Story story = stories.get(position);

                intent.putExtra("TITLE", story.getTitle());
                intent.putExtra("IMAGE_URL", story.getCoverImageUrl());
                intent.putExtra("AUTHOR", story.getAuthor());
                intent.putStringArrayListExtra("GENRES", story.getGenres());
                intent.putExtra("VIEWS", story.getViewCount());
                intent.putExtra("CHAPTER", story.getChapter());
                intent.putExtra("DESCRIPTION", story.getDescription());
            } else {
                intent.putExtra("TITLE", "Đang cập nhật");
                intent.putExtra("IMAGE_URL", carousel.getImageUrl());
                intent.putExtra("AUTHOR", "Đang cập nhật");
                intent.putExtra("GENRES", "Truyện tranh");
                intent.putExtra("VIEWS", 0);
                intent.putExtra("CHAPTER", "0");
                intent.putExtra("DESCRIPTION", "Đang cập nhật mô tả...");
            }

            context1.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return carouselItem.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_carousel;

        public ViewHolder(@NonNull View itemView, int carouselWidth, int carouselHeight) {
            super(itemView);
            iv_carousel = itemView.findViewById(R.id.iv_carousel);
            ViewGroup.LayoutParams params = iv_carousel.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            iv_carousel.setLayoutParams(params);
            iv_carousel.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
}