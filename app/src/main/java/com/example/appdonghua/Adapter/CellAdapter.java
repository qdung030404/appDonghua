package com.example.appdonghua.Adapter;

import android.content.Context; // Thêm import này
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Thêm import này
import com.example.appdonghua.Activity.ComicInfoActivity;
import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellAdapter extends RecyclerView.Adapter<CellAdapter.ViewHolder>{
    private List<Cell> cells;
    private List<NovelList> novelLists;
    public boolean isEditMode = false;
    private Set<Integer> seLectedItem = new HashSet<>();
    public interface OnDeleteItemsListener {
        void onDeleteItems(List<String> titles);
    }
    private OnDeleteItemsListener deleteListener;

    public void setOnDeleteItemsListener(OnDeleteItemsListener listener) {
        this.deleteListener = listener;
    }

    public CellAdapter(List<Cell> cells){
        this.cells = cells;
        this.novelLists = new ArrayList<>();
    }
    public CellAdapter(List<Cell> cells, List<NovelList> novelLists){
        this.cells = cells;
        this.novelLists = novelLists;
    }
    public void toggleEdit(){
        isEditMode = !isEditMode;
        if (!isEditMode){
            seLectedItem.clear();
        }
        notifyDataSetChanged();
    }
    public void seLectAll(){
        if (cells != null){
            seLectedItem.clear();
            for (int i = 0; i < cells.size(); i++) {
                seLectedItem.add(i);
            }
            notifyDataSetChanged();
        }
    }
    public void unSelectAll(){
        seLectedItem.clear();
        notifyDataSetChanged();
    }
    public boolean isEditMode(){
        return isEditMode;
    }
    public boolean isSelectedItem(){
        return !seLectedItem.isEmpty();
    }
    public int getSelectedCount() {
        return seLectedItem.size();
    }

    public List<Cell> getSelectedItems() {
        List<Cell> selected = new ArrayList<>();
        for (int position : seLectedItem) {
            if (position < cells.size()) {
                selected.add(cells.get(position));
            }
        }
        return selected;
    }
    public void deleteSelectedItems() {
        List<String> titlesDelete = new ArrayList<>();
        for (Integer position : seLectedItem) {
            if (position < cells.size()) {
                titlesDelete.add(cells.get(position).getTitle());
            }
        }
        List<Cell> itemRemove = getSelectedItems();
        for (int position : seLectedItem){
            if (position < cells.size()){
                itemRemove.add(cells.get(position));

            }
        }
        cells.removeAll(itemRemove);
        seLectedItem.clear();
        notifyDataSetChanged();
        if (deleteListener != null && !titlesDelete.isEmpty()) {
            deleteListener.onDeleteItems(titlesDelete);
        }
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

        holder.checkbox.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            if (!isEditMode){
                Context context = v.getContext();
                Intent intent = new Intent(context, ComicInfoActivity.class); // Chuyển sang màn hình chi tiết

                if (novelLists != null && position < novelLists.size()) {
                    NovelList novelList = novelLists.get(position);

                    // Gửi dữ liệu đầy đủ từ NovelList
                    intent.putExtra("TITLE", novelList.getTitle());
                    intent.putExtra("IMAGE_URL", novelList.getImageUrl());
                    intent.putExtra("AUTHOR", novelList.getAuthor());
                    intent.putExtra("CATEGORY", novelList.getGenre());
                    intent.putExtra("VIEWS", novelList.getViewCount());
                    intent.putExtra("CHAPTER", novelList.getChapterCount());
                    intent.putExtra("DESCRIPTION", novelList.getDescription());
                } else {
                    // Fallback: Chỉ gửi dữ liệu cơ bản từ Cell
                    intent.putExtra("TITLE", cell.getTitle());
                    intent.putExtra("IMAGE_URL", cell.getImageUrl());
                    intent.putExtra("AUTHOR", "Đang cập nhật");
                    intent.putExtra("CATEGORY", "Truyện tranh");
                    intent.putExtra("VIEWS", 0);
                    intent.putExtra("CHAPTER", "0");
                    intent.putExtra("DESCRIPTION", "Đang cập nhật mô tả...");
                }

                context.startActivity(intent);
            }else {
                holder.checkbox.setChecked(!holder.checkbox.isChecked());
            }

        });

        holder.textView.setText(cell.getTitle());

        Context context = holder.itemView.getContext();

        String imageUrl = cell.getImageUrl();

        if (context != null && imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_2) // Ảnh hiển thị trong khi chờ tải
                    .error(R.drawable.img_2)       // Ảnh hiển thị nếu link lỗi
                    .centerCrop()                  // Cắt ảnh cho đẹp
                    .into(holder.imageView);
        }
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(seLectedItem.contains(position));
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                seLectedItem.add(position);
            }else {
                seLectedItem.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Kiểm tra null để tránh lỗi crash
        return cells != null ? cells.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        CheckBox checkbox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.comic_img);
            textView = itemView.findViewById(R.id.tvName);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}