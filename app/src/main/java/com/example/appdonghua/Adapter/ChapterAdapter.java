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
    private OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterClick(Chapter chapter, int position);
    }

    public ChapterAdapter(List<Chapter> chapters, OnChapterClickListener listener) {
        this.chapters = chapters;
        this.listener = listener;
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
        holder.tvView.setText(String.valueOf(chapter.getViews()) + " lượt xem");

        // Thêm sự kiện click vào chương
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChapterClick(chapter, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
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