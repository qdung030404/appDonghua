package com.example.appdonghua.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Activity.ComicInfoActivity;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.R;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {
    private ArrayList<NovelList> items;

    public RankingAdapter(ArrayList<NovelList> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        NovelList novelList = items.get(position);
        if (novelList == null) return;

        int rank = position + 1;
        holder.rankingNumber.setText(String.valueOf(rank));

        // Tải ảnh với Glide
        Context context = holder.itemView.getContext();
        String imageUrl = novelList.getImageUrl();

        if (context != null && imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_2)
                    .error(R.drawable.img_2)
                    .centerCrop()
                    .into(holder.bookCover);
        }

        // Thiết lập thông tin truyện
        holder.bookTitle.setText(novelList.getTitle());
        holder.bookAuthor.setText("Tác giả: " + novelList.getAuthor());
        holder.bookCategory.setText(novelList.getGenre());
        holder.viewCount.setText("Views: " + formatNumber(novelList.getViewCount()));
        holder.chapterCount.setText("\uD83D\uDCD6 chương " + novelList.getChapterCount());

        // Đổi màu cho top 3
        if (rank == 1) {
            holder.rankingNumber.setBackgroundColor(Color.parseColor("#FFD700"));
            holder.rankingNumber.setTextColor(Color.BLACK);
            holder.rankingBadge.setVisibility(View.VISIBLE);
        } else if (rank == 2) {
            holder.rankingNumber.setBackgroundColor(Color.parseColor("#C0C0C0"));
            holder.rankingNumber.setTextColor(Color.BLACK);
            holder.rankingBadge.setVisibility(View.VISIBLE);
        } else if (rank == 3) {
            holder.rankingNumber.setBackgroundColor(Color.parseColor("#CD7F32"));
            holder.rankingNumber.setTextColor(Color.BLACK);
            holder.rankingBadge.setVisibility(View.VISIBLE);
        } else {
            holder.rankingNumber.setBackgroundColor(Color.TRANSPARENT);
            holder.rankingNumber.setTextColor(Color.WHITE);
            holder.rankingBadge.setVisibility(View.GONE);
        }

        // Thêm click listener để mở ComicInfoActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ComicInfoActivity.class);

                // Truyền dữ liệu truyện
                intent.putExtra("IMAGE_URL", novelList.getImageUrl());
                intent.putExtra("TITLE", novelList.getTitle());
                intent.putExtra("VIEWS", novelList.getViewCount());
                intent.putExtra("CATEGORY", novelList.getGenre());
                intent.putExtra("CHAPTER", novelList.getChapterCount());
                intent.putExtra("AUTHOR", novelList.getAuthor());
                intent.putExtra("DESCRIPTION", novelList.getDescription());

                context.startActivity(intent);
            }
        });
    }

    public static String formatNumber(long number) {
        if (number >= 1000000) {
            double result = number / 1000000.0;
            if (result == (long) result) {
                return String.format("%dM", (long) result);
            }
            return String.format("%.1fM", result);
        } else if (number >= 1000) {
            double result = number / 1000.0;
            if (result == (long) result) {
                return String.format("%dk", (long) result);
            }
            return String.format("%.1fk", result);
        } else {
            return String.valueOf(number);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Phương thức cập nhật dữ liệu
    public void updateData(ArrayList<NovelList> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankingNumber, bookTitle, bookAuthor, bookCategory, viewCount, chapterCount;
        ImageView bookCover, rankingBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankingNumber = itemView.findViewById(R.id.rankingNumber);
            bookCover = itemView.findViewById(R.id.bookCover);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookCategory = itemView.findViewById(R.id.bookCategory);
            viewCount = itemView.findViewById(R.id.viewCount);
            chapterCount = itemView.findViewById(R.id.chapterCount);
            rankingBadge = itemView.findViewById(R.id.rankingBadge);
        }
    }
}