package com.example.appdonghua.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        int rank = position + 1;
        holder.rankingNumber.setText(String.valueOf(rank));
        holder.bookCover.setImageResource(novelList.getImage());
        holder.bookTitle.setText(novelList.getTitle());
        holder.bookAuthor.setText("Tác giả:\n " + novelList.getAuthor());
        holder.bookCategory.setText(novelList.getCategory());
        holder.viewCount.setText("👁 " + formatNumber(novelList.getViews()));
        holder.chapterCount.setText("📖 chương " + novelList.getChapter() );

        if (rank == 1) {
            holder.rankingNumber.setBackgroundColor(Color.parseColor("#FFD700"));
            holder.rankingBadge.setVisibility(View.VISIBLE);
        } else if (rank == 2) {
            holder.rankingNumber.setBackgroundColor(Color.parseColor("#C0C0C0"));
            holder.rankingBadge.setVisibility(View.VISIBLE);
        } else if (rank == 3) {
            holder.rankingNumber.setBackgroundColor(Color.parseColor("#CD7F32"));
            holder.rankingBadge.setVisibility(View.VISIBLE);
        } else {
            holder.rankingNumber.setTextColor(Color.WHITE);
            holder.rankingBadge.setVisibility(View.GONE);
        }
    }
    public static String formatNumber(int number) {
        if (number >= 1000000) {
            double result = number / 1000000.0;
            if (result == (int) result) {
                return String.format("%dM", (int) result);
            }
            return String.format("%.1fM", result);
        } else if (number >= 1000) {
            double result = number / 1000.0;
            if (result == (int) result) {
                return String.format("%dk", (int) result);
            }
            return String.format("%.1fk", result);
        } else {
            return String.valueOf(number);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
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
