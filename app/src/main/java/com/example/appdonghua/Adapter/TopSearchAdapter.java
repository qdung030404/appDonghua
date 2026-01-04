package com.example.appdonghua.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Model.TopSearch;
import com.example.appdonghua.R;
import com.example.appdonghua.Utils.ScreenUtils;

import java.util.ArrayList;

public class TopSearchAdapter extends RecyclerView.Adapter<TopSearchAdapter.ViewHolder> {

    private ArrayList<TopSearch> topSearchList;
    private OnTopSearchClickListener listener;

    public interface OnTopSearchClickListener {
        void onTopSearchClick(String query);
    }

    public TopSearchAdapter(ArrayList<TopSearch> topSearchList, OnTopSearchClickListener listener) {
        this.topSearchList = topSearchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_search, parent, false);
        Context context = parent.getContext();
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopSearch topSearch = topSearchList.get(position);
        Context context = holder.itemView.getContext();

        int rank = position + 1;
        holder.topSearchTitle.setText(topSearch.gettopSearchBookCover());
        if (topSearch.getImageUrl() != null) {
            // Load từ URL (Firestore)
            Glide.with(context)
                    .load(topSearch.getImageUrl())
                    .placeholder(R.drawable.img_2)
                    .error(R.drawable.img_2)
                    .centerCrop()
                    .into(holder.topSearchimg);
        } else {
            // Load từ resource (local)
            holder.topSearchimg.setImageResource(R.drawable.bottom_nav_background);
        }
        holder.itemView.setOnClickListener(v -> listener.onTopSearchClick(topSearch.gettopSearchBookCover()));

        if (rank == 1) {
            // Top 1 - Vàng
            holder.rankingNumber.setImageResource(R.drawable.first); // Huy chương vàng
            holder.rankingNumber.setVisibility(View.VISIBLE);
            holder.rankingText.setVisibility(View.GONE);

        } else if (rank == 2) {
            // Top 2 - Bạc
            holder.rankingNumber.setImageResource(R.drawable.second); // Huy chương bạc
            holder.rankingNumber.setVisibility(View.VISIBLE);
            holder.rankingText.setVisibility(View.GONE);

        } else if (rank == 3) {
            // Top 3 - Đồng
            holder.rankingNumber.setImageResource(R.drawable.third); // Huy chương đồng
            holder.rankingNumber.setVisibility(View.VISIBLE);
            holder.rankingText.setVisibility(View.GONE);

        } else {
            // Các vị trí còn lại - dùng TEXT
            holder.rankingNumber.setVisibility(View.GONE);
            holder.rankingText.setVisibility(View.VISIBLE);
            holder.rankingText.setText(String.valueOf(rank));
            holder.rankingText.setBackgroundResource(R.drawable.ranking_circle);
        }
    }

    @Override
    public int getItemCount() {
        return topSearchList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rankingNumber;
        TextView topSearchTitle, rankingText;
        ImageView topSearchimg;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            rankingNumber = itemView.findViewById(R.id.rankingNumber);
            topSearchTitle = itemView.findViewById(R.id.topSearchTitle);
            topSearchimg = itemView.findViewById(R.id.topSearchBookCover);
            rankingText = itemView.findViewById(R.id.rankingText);

            ScreenUtils.TextSize textSize = ScreenUtils.calculateTextSize(context);
            topSearchTitle.setTextSize(textSize.title);
        }
    }
}