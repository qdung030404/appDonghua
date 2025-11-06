package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.R;

import java.util.List;

public class CellAdapter extends RecyclerView.Adapter<CellAdapter.ViewHolder>{
    private List<Cell> cells;
    public CellAdapter(List<Cell> cells){
        this.cells = cells;
    }
    @NonNull
    @Override
    public CellAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CellAdapter.ViewHolder holder, int position) {
        Cell cell = cells.get(position);
        holder.imageView.setImageResource(cell.getImage());
        holder.textView.setText(cell.getName());
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.comic_img);
            textView = itemView.findViewById(R.id.tvName);
        }
    }
}
