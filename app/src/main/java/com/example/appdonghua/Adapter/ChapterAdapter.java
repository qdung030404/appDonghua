package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Model.Chapter;
import com.example.appdonghua.R;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {
    private List<Chapter> chapters;

    public ChapterAdapter(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.tvChapter.setText(chapter.getChapter());
        holder.tvView.setText(String.valueOf(chapter.getViews()));

        // Thêm sự kiện click vào chương nếu cần
        holder.itemView.setOnClickListener(v -> {
            // Xử lý khi bấm vào một chương (ví dụ: mở màn hình đọc)
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public void setHighlightedPosition(int position) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapter;
        TextView tvView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapter = itemView.findViewById(R.id.tvChapter);
            tvView = itemView.findViewById(R.id.tvView);
        }
    }
}