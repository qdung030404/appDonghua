package com.example.appdonghua.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // <-- THÊM IMPORT NÀY
import com.example.appdonghua.Activity.ComicInfoActivity;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.R;

import java.util.ArrayList;

public class NoveListAdapter extends RecyclerView.Adapter<NoveListAdapter.ViewHolder>{
    private ArrayList<NovelList> item;



    public NoveListAdapter(ArrayList<NovelList> items){this.item = items;}

    // (Bạn có thể xóa phần OnItemClickListener này nếu không dùng,
    // vì bạn đang set click listener ở onBindViewHolder)
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(NovelList novelList, int position);
    }
    // -----------------------------------------------------------

    @NonNull
    @Override
    public NoveListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scroll, parent, false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoveListAdapter.ViewHolder holder, int position) {

        NovelList novelList = item.get(position);

        // --- SỬA LỖI TẢI ẢNH ---
        Context context = holder.itemView.getContext(); // Lấy context từ view
        String imageUrl = novelList.getImageUrl(); // Lấy String URL

        if (context != null && imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_2) // Ảnh tạm
                    .error(R.drawable.img_2)       // Ảnh lỗi
                    .centerCrop()
                    .into(holder.bookCover);
        }
        // ------------------------

        holder.bookTitle.setText(novelList.getTitle());
        // (Đảm bảo model của bạn có các hàm get này)
        holder.viewCount.setText(" " + RankingAdapter.formatNumber((int) novelList.getViewCount()));
        holder.bookCategory.setText(String.join(", ", novelList.getGenre()));
        holder.bookAuthor.setText("Tác giả: " + novelList.getAuthor());
        holder.chapterCount.setText(" chương " + novelList.getChapterCount());

        // --- SỬA LỖI CLICK LISTENER ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy context từ view, giờ sẽ không bị null
                Context context = v.getContext();
                Intent intent = new Intent(context, ComicInfoActivity.class);

                // Gửi String URL, không gửi int
                intent.putExtra("IMAGE_URL", novelList.getImageUrl());
                intent.putExtra("TITLE", novelList.getTitle());
                intent.putExtra("VIEWS", novelList.getViewCount());
                intent.putExtra("CHAPTER", novelList.getChapterCount());
                intent.putExtra("AUTHOR", novelList.getAuthor());
                intent.putExtra("DESCRIPTION", novelList.getDescription());
                intent.putStringArrayListExtra("GENRES", novelList.getGenre());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item != null ? item.size() : 0; // Thêm check null
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookCover;
        TextView bookTitle, viewCount, bookCategory, bookAuthor, chapterCount ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.bookCover);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            viewCount = itemView.findViewById(R.id.viewCount);
            bookCategory = itemView.findViewById(R.id.bookCategory);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            chapterCount = itemView.findViewById(R.id.chapterCount);
        }
    }
}
