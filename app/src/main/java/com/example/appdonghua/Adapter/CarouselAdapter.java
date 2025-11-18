package com.example.appdonghua.Adapter;

import android.content.Context; // <-- THÊM IMPORT NÀY
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // <-- THÊM IMPORT NÀY
import com.example.appdonghua.Model.Carousel;
import com.example.appdonghua.R;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder>{
    public List<Carousel> carouselItem;

    public CarouselAdapter(List<Carousel> carouselItems) {this.carouselItem = carouselItems;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 1. Lấy item
        Carousel items = carouselItem.get(position);
        if (items == null) return;

        // 2. Lấy URL ảnh (bây giờ là String)
        String imageUrl = items.getImageUrl();

        // 3. Lấy Context từ View
        Context context = holder.itemView.getContext();

        // 4. Dùng Glide để tải ảnh từ URL
        if (imageUrl != null && context != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img) // (Tùy chọn) Ảnh tạm khi đang tải
                    .error(R.drawable.img_1) // (Tùy chọn) Ảnh khi lỗi
                    .into(holder.iv_carousel);
        }

        // Dòng này sai vì getImageUrl() là String, không phải int
        // holder.iv_carousel.setImageResource(Items.getImageUrl()); // <-- LỖI CŨ
    }

    @Override
    public int getItemCount() {
        return carouselItem.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_carousel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_carousel = itemView.findViewById(R.id.iv_carousel);
        }
    }
}