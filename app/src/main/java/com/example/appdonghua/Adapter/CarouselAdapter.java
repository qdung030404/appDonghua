package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        Carousel Items = carouselItem.get(position);
        holder.iv_carousel.setImageResource(Items.getImage());
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
