package com.example.appdonghua.Adapter;

import android.content.Context; // Thêm import này
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.List;

public class CellAdapter extends RecyclerView.Adapter<CellAdapter.ViewHolder>{
    private List<Cell> cells;
    private List<NovelList> novelLists;

    public CellAdapter(List<Cell> cells){
        this.cells = cells;
        this.novelLists = new ArrayList<>();
    }
    public CellAdapter(List<Cell> cells, List<NovelList> novelLists){
        this.cells = cells;
        this.novelLists = novelLists;
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
        holder.itemView.setOnClickListener(v -> {
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
        });

        if (cell == null) return;

        // 1. Set Tên truyện
        // (Đảm bảo Model Cell của bạn có hàm getTitle, nếu bạn đặt là getName thì sửa thành getName)
        holder.textView.setText(cell.getTitle());

        // 2. Lấy Context để dùng Glide
        Context context = holder.itemView.getContext();

        // 3. Lấy URL ảnh (Model Cell phải trả về String)
        String imageUrl = cell.getImageUrl();

        // 4. Dùng Glide tải ảnh
        if (context != null && imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_2) // Ảnh hiển thị trong khi chờ tải
                    .error(R.drawable.img_2)       // Ảnh hiển thị nếu link lỗi
                    .centerCrop()                  // Cắt ảnh cho đẹp
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        // Kiểm tra null để tránh lỗi crash
        return cells != null ? cells.size() : 0;
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