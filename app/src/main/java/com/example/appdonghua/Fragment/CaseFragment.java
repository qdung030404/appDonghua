package com.example.appdonghua.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdonghua.Activity.LoginActivity;
import com.example.appdonghua.Adapter.CellAdapter;
import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseFragment extends Fragment {

    // --- Views ---
    LinearLayout case_menu;
    Button editButton, selectAllButton, deleteButton;
    TextView selectedTextView, emptyTextView;
    RecyclerView caseRecyclerView;
    RelativeLayout bottomEditBar;


    // --- Firebase ---
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // --- Adapter & Data ---
    private CellAdapter adapter;
    private ArrayList<Cell> cellList;
    private String currentTab = "history";
    private ArrayList<NovelList> novelList;
    private String[][] menuItems = {
            {"history", "Lịch Sử"},
            {"save", "Đã Lưu"}, // Tương lai bạn làm tính năng Favorites
            {"download", "Tải Xuống"} // Tương lai bạn làm tính năng Download
    };

    public CaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_case, container, false);
        init(view);
        setupRecyclerView(); // Cài đặt danh sách
        setupMenuItems();

        // Mặc định load tab đầu tiên (History)
        if (case_menu.getChildCount() > 0) {
            // Giả lập click vào tab đầu tiên để load data
            case_menu.getChildAt(0).performClick();
        }
        steupListener();
        return view;
    }

    private void init(View view){
        case_menu = view.findViewById(R.id.case_menu);
        editButton = view.findViewById(R.id.editButton);
        caseRecyclerView = view.findViewById(R.id.case_RecyclerView);
        emptyTextView = view.findViewById(R.id.empty_text_view);
        bottomEditBar = view.findViewById(R.id.bottom_edit_bar);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        deleteButton = view.findViewById(R.id.deleteButton);

    }
    private void steupListener() {
        editButton.setOnClickListener(v -> {
            adapter.toggleEdit();
            if (bottomEditBar.getVisibility() == View.GONE) {
                bottomEditBar.setVisibility(View.VISIBLE);
                editButton.setText("Hủy");
            } else {
                bottomEditBar.setVisibility(View.GONE);
                editButton.setText("Sửa");
            }
        });
        selectAllButton.setOnClickListener(v -> {
            if(adapter != null){
                if( adapter.getSelectedCount() == adapter.getItemCount()){
                    adapter.unSelectAll();
                    selectAllButton.setText("Chọn Tất Cả");
                }else {
                    adapter.seLectAll();
                    selectAllButton.setText("Hủy Chọn");
                }
            }
        });
        deleteButton.setOnClickListener(v -> {
            if( adapter != null && adapter.isSelectedItem()){
                new AlertDialog.Builder(getContext()).setTitle("Xác Nhận")
                        .setMessage("Bạn có chắc muốn xóa " + adapter.getSelectedCount() + " mục?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            adapter.deleteSelectedItems();
                            adapter.unSelectAll();
                            Toast.makeText(getContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                        }).setNegativeButton("Không", null).show();
            }else {
                Toast.makeText(getContext(), "Chưa chọn item nào", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void deleteFromFirebase(List<String> titles) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || titles.isEmpty()) return;

        // Xác định collection dựa vào tab hiện tại
        String collection = currentTab; // "history" hoặc "save"

        for (String title : titles) {
            db.collection("users").document(user.getUid())
                    .collection(collection)
                    .document(title)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("CaseFragment", "Deleted: " + title);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CaseFragment", "Error deleting: " + title, e);
                    });
        }
    }
    private void setupRecyclerView() {
        // Sử dụng GridLayoutManager 3 cột (giống HomeFragment)
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        caseRecyclerView.setLayoutManager(layoutManager);

        cellList = new ArrayList<>();
        novelList = new ArrayList<>(); // Khởi tạo novelList
        adapter = new CellAdapter(cellList, novelList); // Truyền cả 2 list
        caseRecyclerView.setAdapter(adapter);
        adapter.setOnDeleteItemsListener(titles -> deleteFromFirebase(titles));
    }

    private void setupMenuItems(){
        for (String[] item : menuItems) {
            TextView textView = createMenuItem(item[0], item[1]);
            case_menu.addView(textView);
        }
        // Set style mặc định cho item đầu tiên (nhưng việc load data sẽ do performClick ở onCreateView lo)
        if (case_menu.getChildCount() > 0) {
            selectedTextView = (TextView) case_menu.getChildAt(0);
            setSelectedStyle(selectedTextView);
        }
    }

    private TextView createMenuItem(String id, String name) {
        TextView textView = new TextView(getContext());
        textView.setText(name);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTextSize(14);
        textView.setGravity(getResources().getColor(R.color.app_text_primary));
        textView.setPadding(20, 20, 20, 30);
        textView.setTag(id);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);
        textView.setLayoutParams(params);
        textView.setOnClickListener(v -> {
            onMenuItemClick(textView);
        });
        return textView;
    }

    private void onMenuItemClick(TextView clickedView) {
        if (selectedTextView != null) {
            setUnselectedStyle(selectedTextView);
        }
        setSelectedStyle(clickedView);
        selectedTextView = clickedView;
        String itemId = (String) clickedView.getTag();
        handleMenuSelection(itemId);
    }

    private void setSelectedStyle(TextView textView) {
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setBackgroundResource(R.drawable.btnbg);
    }

    private void setUnselectedStyle(TextView textView) {
        textView.setTextSize(14);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
    }

    private void handleMenuSelection(String itemId) {
        currentTab = itemId;
        cellList.clear();
        adapter.notifyDataSetChanged();

        switch (itemId) {
            case "history":
                loadHistoryData();
                break;
            case "save":
                loadSaveData();

                break;
            case "download":
                // loadDownloadsData(); // Làm sau
                showEmpty("Chưa có truyện tải xuống");
                break;
        }
    }

    // --- HÀM QUAN TRỌNG: Tải Lịch Sử ---
    private void loadHistoryData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            showEmpty("Vui lòng đăng nhập để xem lịch sử");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showEmpty("Bạn chưa xem truyện nào");
                    } else {
                        hideEmpty();
                        cellList.clear();
                        novelList.clear();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String title = doc.getString("title");
                            String image = doc.getString("coverImageUrl");
                            String author = doc.getString("author");
                            String description = doc.getString("description");
                            ArrayList<String> genreList = getGenreFromDocument(doc);
                            Long viewCount = doc.getLong("viewCount");
                            Long chapterCount = doc.getLong("chapterCount");

                            if (title != null && image != null) {
                                // Thêm Cell cho hiển thị grid
                                cellList.add(new Cell(image, title));

                                // Thêm NovelList để truyền đầy đủ thông tin khi click
                                novelList.add(new NovelList(
                                        image,
                                        title,
                                        viewCount != null ? viewCount : 0,
                                        genreList,
                                        chapterCount != null ? chapterCount : 0,
                                        author != null ? author : "Đang cập nhật",
                                        description != null ? description : "Đang cập nhật mô tả..."
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    showEmpty("Lỗi tải dữ liệu: " + e.getMessage());
                    Log.e("CaseFragment", "Error loading history", e);
                });
    }
    private void loadSaveData(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            showEmpty("Vui lòng đăng nhập để xem truyện đã lưu");
            return;
        }

        db.collection("users").document(user.getUid())
                .collection("save")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showEmpty("Bạn chưa lưu truyện nào");
                    } else {
                        hideEmpty();
                        cellList.clear();
                        novelList.clear();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String title = doc.getString("title");
                            String image = doc.getString("coverImageUrl");
                            String author = doc.getString("author");
                            String description = doc.getString("description");
                            ArrayList<String> genreList = getGenreFromDocument(doc);
                            Long viewCount = doc.getLong("viewCount");
                            Long chapterCount = doc.getLong("chapterCount");

                            if (title != null && image != null) {
                                cellList.add(new Cell(image, title));

                                novelList.add(new NovelList(
                                        image,
                                        title,
                                        viewCount != null ? viewCount : 0,
                                        genreList,
                                        chapterCount != null ? chapterCount : 0,
                                        author != null ? author : "Đang cập nhật",
                                        description != null ? description : "Đang cập nhật mô tả..."
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    showEmpty("Lỗi tải dữ liệu: " + e.getMessage());
                    Log.e("CaseFragment", "Error loading saved items", e);
                });
    }
    private ArrayList<String> getGenreFromDocument(QueryDocumentSnapshot doc) {
        try {
            ArrayList<String> genreList = (ArrayList<String>) doc.get("genre");
            if (genreList != null && !genreList.isEmpty()) {
                return genreList;
            }
        } catch (Exception e) {
            Log.e("CaseFragment", "Error getting genre: " + e.getMessage());
        }

        // Trả về giá trị mặc định
        ArrayList<String> defaultList = new ArrayList<>();
        defaultList.add("Truyện tranh");
        return defaultList;
    }
    // Hàm hiển thị thông báo trống
    private void showEmpty(String message) {
        caseRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        emptyTextView.setText(message);
    }

    // Hàm ẩn thông báo trống (hiện list)
    private void hideEmpty() {
        caseRecyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
    }
}