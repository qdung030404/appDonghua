package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Model.HotNovelScroll;
import com.example.appdonghua.R;

import java.util.ArrayList;

public class HotNovelScrollAdapter extends RecyclerView.Adapter<HotNovelScrollAdapter.ViewHolder>{
    private ArrayList<HotNovelScroll> hotNovelScrolls;
    public HotNovelScrollAdapter(ArrayList<HotNovelScroll> hotNovelScrolls){this.hotNovelScrolls = hotNovelScrolls;}

    @NonNull
    @Override
    public HotNovelScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scroll, parent, false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotNovelScrollAdapter.ViewHolder holder, int position) {
        HotNovelScroll hotNovelScroll = hotNovelScrolls.get(position);
        holder.novel_img.setImageResource(hotNovelScroll.getImage());
        holder.tvtitle.setText(hotNovelScroll.getTitle());
        holder.tvviews.setText(hotNovelScroll.getViews());
        holder.tvcategory.setText(hotNovelScroll.getCategory());

    }

    @Override
    public int getItemCount() {
        return hotNovelScrolls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView novel_img;
        TextView tvtitle, tvviews, tvcategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            novel_img = itemView.findViewById(R.id.novel_img);
            tvtitle = itemView.findViewById(R.id.tvtitle);
            tvviews = itemView.findViewById(R.id.tvviews);
            tvcategory = itemView.findViewById(R.id.tvcategory);
        }
    }
}
