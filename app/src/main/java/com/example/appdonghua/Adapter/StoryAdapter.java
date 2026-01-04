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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Activity.ComicInfoActivity;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.example.appdonghua.Utils.ScreenUtils;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    private ArrayList<Story> item;
    private int imageWidth;
    private int imageHeight;
    public StoryAdapter(ArrayList<Story> items){this.item = items;}

    // (Bạn có thể xóa phần OnItemClickListener này nếu không dùng,
    // vì bạn đang set click listener ở onBindViewHolder)
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Story story, int position);
    }
    // -----------------------------------------------------------
    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scroll, parent, false);
        Context context = parent.getContext();
        ScreenUtils.ImageDimensions dims = ScreenUtils.calculateHorizontalImageDimensions(context);
        imageWidth = dims.width;
        imageHeight = dims.height;


        return new ViewHolder(view, imageWidth, imageHeight, context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.ViewHolder holder, int position) {

        Story story = item.get(position);

        // --- SỬA LỖI TẢI ẢNH ---
        Context context = holder.itemView.getContext(); // Lấy context từ view
        String imageUrl = story.getCoverImageUrl(); // Lấy String URL

        if (context != null && imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_2) // Ảnh tạm
                    .error(R.drawable.img_2)       // Ảnh lỗi
                    .centerCrop()
                    .into(holder.bookCover);
        }
        // ------------------------

        holder.bookTitle.setText(story.getTitle());
        // (Đảm bảo model của bạn có các hàm get này)
        holder.viewCount.setText(" " + RankingAdapter.formatNumber((int) story.getViewCount()));
        holder.bookCategory.setText(String.join(", ", story.getGenres()));
        holder.bookAuthor.setText("Tác giả: " + story.getAuthor());
        holder.chapterCount.setText(" chương " + story.getChapter());

        // --- SỬA LỖI CLICK LISTENER ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy context từ view, giờ sẽ không bị null
                Context context = v.getContext();
                Intent intent = new Intent(context, ComicInfoActivity.class);

                // Gửi String URL, không gửi int
                intent.putExtra("IMAGE_URL", story.getCoverImageUrl());
                intent.putExtra("TITLE", story.getTitle());
                intent.putExtra("VIEWS", story.getViewCount());
                intent.putExtra("CHAPTER", story.getChapter());
                intent.putExtra("AUTHOR", story.getAuthor());
                intent.putExtra("DESCRIPTION", story.getDescription());
                intent.putStringArrayListExtra("GENRES", story.getGenres());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item != null ? item.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookCover;
        TextView bookTitle, viewCount, bookCategory, bookAuthor, chapterCount ;
        public ViewHolder(@NonNull View itemView, int imageWidth, int imageHeight, Context context) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.bookCover);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            viewCount = itemView.findViewById(R.id.viewCount);
            bookCategory = itemView.findViewById(R.id.bookCategory);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            chapterCount = itemView.findViewById(R.id.chapterCount);
            ViewGroup.LayoutParams params = bookCover.getLayoutParams();
            params.width = imageWidth;
            params.height = imageHeight;
            bookCover.setLayoutParams(params);

            // Điều chỉnh text size dựa trên màn hình
            ScreenUtils.TextSize textSize = ScreenUtils.calculateTextSize(context);
            bookTitle.setTextSize(textSize.title);
            bookAuthor.setTextSize(textSize.subtitle);
            bookCategory.setTextSize(textSize.subtitle);
            viewCount.setTextSize(textSize.body);
            chapterCount.setTextSize(textSize.body);
        }
    }
}
