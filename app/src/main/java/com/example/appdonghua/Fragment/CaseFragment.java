package com.example.appdonghua.Fragment;

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
import android.widget.TextView;

import com.example.appdonghua.Activity.LoginActivity;
import com.example.appdonghua.Adapter.CellAdapter;
import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CaseFragment extends Fragment {

    // --- Views ---
    LinearLayout case_menu;
    Button editButton;
    TextView selectedTextView, emptyTextView;
    RecyclerView caseRecyclerView;

    // --- Firebase ---
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // --- Adapter & Data ---
    private CellAdapter adapter;
    private ArrayList<Cell> cellList;

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
        return view;
    }

    private void init(View view){
        case_menu = view.findViewById(R.id.case_menu);
        editButton = view.findViewById(R.id.button);
        caseRecyclerView = view.findViewById(R.id.case_RecyclerView);
        emptyTextView = view.findViewById(R.id.empty_text_view);
    }

    private void setupRecyclerView() {
        // Sử dụng GridLayoutManager 3 cột (giống HomeFragment)
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        caseRecyclerView.setLayoutManager(layoutManager);

        cellList = new ArrayList<>();
        adapter = new CellAdapter(cellList);
        caseRecyclerView.setAdapter(adapter);
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
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(20, 20, 20, 30);
        textView.setTag(id);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 0);
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
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setBackgroundColor(Color.parseColor("#5F639D"));
    }

    private void setUnselectedStyle(TextView textView) {
        textView.setTextSize(16);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);
        textView.setTextColor(Color.WHITE);
    }

    private void handleMenuSelection(String itemId) {
        // Xóa dữ liệu cũ trước khi load mới
        cellList.clear();
        adapter.notifyDataSetChanged();

        switch (itemId) {
            case "history":
                loadHistoryData();
                break;
            case "save":
                // loadFavoritesData(); // Làm sau
                showEmpty("Chưa có truyện đã lưu");
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
            // (Tùy chọn) Chuyển về màn hình Login
            return;
        }

        // Truy vấn vào collection: users -> [uid] -> history
        // Sắp xếp theo thời gian xem mới nhất (timestamp giảm dần)
        db.collection("users").document(user.getUid())
                .collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showEmpty("Bạn chưa xem truyện nào");
                    } else {
                        hideEmpty();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // Lấy dữ liệu từ document lịch sử
                            String title = doc.getString("title");
                            String image = doc.getString("coverImageUrl");

                            // Tạo đối tượng Cell và thêm vào list
                            if (title != null && image != null) {
                                cellList.add(new Cell(image, title));
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