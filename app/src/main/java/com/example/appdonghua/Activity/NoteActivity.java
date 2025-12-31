package com.example.appdonghua.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdonghua.Helper.OfflineDatabaseHelper;
import com.example.appdonghua.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton fabAdd;
    OfflineDatabaseHelper db;
    ArrayList<String> noteList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tận dụng layout có sẵn hoặc tạo layout mới đơn giản
        // Nếu lười tạo layout xml, bạn có thể copy đoạn xml bên dưới bài này
        setContentView(R.layout.activity_note);

        listView = findViewById(R.id.listViewNote);
        fabAdd = findViewById(R.id.fabAdd);
        db = new OfflineDatabaseHelper(this);
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        loadNotes();

        TextView tvEmpty = findViewById(R.id.tv_empty);
        listView.setEmptyView(tvEmpty);

        // Sự kiện thêm ghi chú
        fabAdd.setOnClickListener(v -> showAddDialog());

        // Sự kiện xóa (Giữ lì để xóa)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String selectedItem = noteList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Xóa ghi chú")
                    .setMessage("Bạn muốn xóa ghi chú này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        db.deleteNote(selectedItem);
                        loadNotes();
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        });

        // Nút back trên toolbar (nếu có)
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadNotes() {
        noteList = db.getAllNotes();
        // Dùng layout mặc định của Android cho nhanh, đỡ phải tạo custom adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteList);
        listView.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Ghi Chú");

        // Tạo layout nhập liệu bằng code Java (đỡ phải tạo file XML mới)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("Tiêu đề");
        layout.addView(inputTitle);

        final EditText inputContent = new EditText(this);
        inputContent.setHint("Nội dung");
        inputContent.setHeight(300); // Cao một chút để nhập nhiều
        layout.addView(inputContent);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String content = inputContent.getText().toString();
            if (!title.isEmpty()) {
                db.addNote(title, content);
                loadNotes();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}