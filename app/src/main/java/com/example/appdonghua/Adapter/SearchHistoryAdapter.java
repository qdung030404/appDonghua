package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Model.SearchHistory;
import com.example.appdonghua.R;

import java.util.ArrayList;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private ArrayList<SearchHistory> historyList;
    private OnHistoryItemClickListener listener;
    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(String query);
        void onDeleteButtonClick(int position);
    }
    public SearchHistoryAdapter(ArrayList<SearchHistory> historyList, OnHistoryItemClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public SearchHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_search, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryAdapter.ViewHolder holder, int position) {
        SearchHistory historyItem = historyList.get(position);
        holder.title.setText(historyItem.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onHistoryItemClick(historyItem.getTitle()));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteButtonClick(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageButton deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.topSearchTitle);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
    public void  updateList(ArrayList<SearchHistory> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        if (position >= 0 && position < historyList.size()) {
            historyList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clearList() {
        historyList.clear();
        notifyDataSetChanged();
    }


}
