package com.example.appdonghua.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Activity.ComicInfoActivity;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.example.appdonghua.Utils.ScreenUtils;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {
    private ArrayList<Story> items;

    public RankingAdapter(ArrayList<Story> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        Context context = parent.getContext();

        ScreenUtils.ImageDimensions dimensions = ScreenUtils.calculateRankingImageDimensions(context);

        return new ViewHolder(view, dimensions.width, dimensions.height, context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        Story story = items.get(position);
        if (story == null) return;

        int rank = position + 1;

        // Tải ảnh với Glide
        Context context = holder.itemView.getContext();
        String imageUrl = story.getCoverImageUrl();

        if (context != null && imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_2)
                    .error(R.drawable.img_2)
                    .centerCrop()
                    .into(holder.bookCover);
        }

        // Thiết lập thông tin truyện
        holder.bookTitle.setText(story.getTitle());
        holder.bookAuthor.setText("Tác giả: " + story.getAuthor());
        holder.bookCategory.setText(String.join(", ", story.getGenres()));
        holder.viewCount.setText("Views: \n" + formatNumber(story.getViewCount()));
        holder.chapterCount.setText("Chương:\n " + story.getChapter());

        // Đổi màu cho top 3
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
            holder.rankingText.setTextColor(ContextCompat.getColor(context, R.color.app_text_primary));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ComicInfoActivity.class);

                // Truyền dữ liệu truyện
                intent.putExtra("IMAGE_URL", story.getCoverImageUrl());
                intent.putExtra("TITLE", story.getTitle());
                intent.putExtra("VIEWS", story.getViewCount());
                intent.putStringArrayListExtra("GENRES", story.getGenres());
                intent.putExtra("CHAPTER", story.getChapter());
                intent.putExtra("AUTHOR", story.getAuthor());
                intent.putExtra("DESCRIPTION", story.getDescription());

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
    public void updateData(ArrayList<Story> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor, bookCategory, viewCount, chapterCount,rankingText;
        ImageView bookCover, rankingNumber;

        public ViewHolder(@NonNull View itemView, int imageWidth, int imageHeight, Context context) {
            super(itemView);
            rankingNumber = itemView.findViewById(R.id.rankingNumber);
            bookCover = itemView.findViewById(R.id.bookCover);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookCategory = itemView.findViewById(R.id.bookCategory);
            viewCount = itemView.findViewById(R.id.viewCount);
            chapterCount = itemView.findViewById(R.id.chapterCount);
            rankingText = itemView.findViewById(R.id.rankingText);

            ViewGroup.LayoutParams params = bookCover.getLayoutParams();
            params.width = imageWidth;
            params.height = imageHeight;
            bookCover.setLayoutParams(params);

            // ✅ SỬ DỤNG ScreenUtils cho text size
            ScreenUtils.TextSize textSize = ScreenUtils.calculateTextSize(context);
            bookTitle.setTextSize(textSize.title);
            bookAuthor.setTextSize(textSize.subtitle);
            bookCategory.setTextSize(textSize.subtitle);
            viewCount.setTextSize(textSize.caption);
            chapterCount.setTextSize(textSize.caption);
        }
    }
}