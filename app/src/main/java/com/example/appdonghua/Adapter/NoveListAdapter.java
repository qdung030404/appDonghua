package com.example.appdonghua.Adapter;

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

public class NoveListAdapter extends RecyclerView.Adapter<NoveListAdapter.ViewHolder>{
    private ArrayList<NovelList> item;
    public NoveListAdapter(ArrayList<NovelList> items){this.item = items;}

    @NonNull
    @Override
    public NoveListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scroll, parent, false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoveListAdapter.ViewHolder holder, int position) {

        NovelList novelList = item.get(position);

        holder.bookCover.setImageResource(novelList.getImage());
        holder.bookTitle.setText(novelList.getTitle());
        holder.viewCount.setText("👁 " + novelList.getViews());
        holder.bookCategory.setText(novelList.getCategory());
        holder.bookAuthor.setText("Tác giả: " + novelList.getAuthor());
        holder.chapterCount.setText("📖 chương " + novelList.getChapter());

    }

    @Override
    public int getItemCount() {
        return item.size();
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
