package com.example.appdonghua.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Model.TopSearch;
import com.example.appdonghua.R;

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopSearch topSearch = topSearchList.get(position);

        int rank = position + 1;
        holder.rankingNumber.setText(String.valueOf(rank));
        holder.topSearchTitle.setText(topSearch.gettopSearchBookCover());
        holder.topSearchimg.setImageResource(topSearch.getImage());
        holder.itemView.setOnClickListener(v -> listener.onTopSearchClick(topSearch.gettopSearchBookCover()));


    }

    @Override
    public int getItemCount() {
        return topSearchList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankingNumber;
        TextView topSearchTitle;
        ImageView topSearchimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankingNumber = itemView.findViewById(R.id.rankingNumber);
            topSearchTitle = itemView.findViewById(R.id.topSearchTitle);
            topSearchimg = itemView.findViewById(R.id.topSearchBookCover);
        }
    }
}