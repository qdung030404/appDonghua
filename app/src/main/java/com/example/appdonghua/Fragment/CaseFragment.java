package com.example.appdonghua.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdonghua.Adapter.CellAdapter;
import com.example.appdonghua.Helper.NotificationHelper;
import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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
    private ArrayList<Story> storyList;
    private String[][] menuItems = {
            {"history", "Lịch Sử"},
            {"save", "Đã Lưu"},
    };
    private NotificationHelper notificationHelper;

    // ==================== LIFECYCLE METHODS ====================

    public CaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo Firebase
        notificationHelper = new NotificationHelper(requireContext());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_case, container, false);
        init(view);
        setupRecyclerView();
        setupMenuItems();

        steupListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedTextView != null) {
            String itemId = (String) selectedTextView.getTag();
            handleMenuSelection(itemId);
        }
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("CaseFragmentPrefs", Context.MODE_PRIVATE);
            String pendingTab = prefs.getString("pending_tab", null);

            if (pendingTab != null) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    selectTabById(pendingTab);
                }, 100);

                prefs.edit().remove("pending_tab").apply();
            } else {
                if (cellList.isEmpty() && case_menu.getChildCount() > 0) {
                    case_menu.getChildAt(0).performClick();
                }
            }
        }
    }

    // ==================== INITIALIZATION METHODS ====================

    private void init(View view){
        case_menu = view.findViewById(R.id.case_menu);
        editButton = view.findViewById(R.id.editButton);
        caseRecyclerView = view.findViewById(R.id.case_RecyclerView);
        emptyTextView = view.findViewById(R.id.empty_text_view);
        bottomEditBar = view.findViewById(R.id.bottom_edit_bar);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        deleteButton = view.findViewById(R.id.deleteButton);

    }

    private void setupRecyclerView() {
        // Sử dụng GridLayoutManager 3 cột (giống HomeFragment)
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        caseRecyclerView.setLayoutManager(layoutManager);

        cellList = new ArrayList<>();
        storyList = new ArrayList<>();
        adapter = new CellAdapter(cellList, storyList);
        caseRecyclerView.setAdapter(adapter);
        adapter.setOnDeleteItemsListener(titles -> deleteFromFirebase(titles));
    }

    private void setupMenuItems(){
        for (String[] item : menuItems) {
            TextView textView = createMenuItem(item[0], item[1]);
            case_menu.addView(textView);
        }
        if (case_menu.getChildCount() > 0) {
            selectedTextView = (TextView) case_menu.getChildAt(0);
            setSelectedStyle(selectedTextView);
        }
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
                final int selectedCount = adapter.getSelectedCount();
                new AlertDialog.Builder(getContext()).setTitle("Xác Nhận")
                        .setMessage("Bạn có chắc muốn xóa " + selectedCount + " mục?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            adapter.deleteSelectedItems();
                            adapter.unSelectAll();
                            notificationHelper.sendNotification(
                                    "Đã xóa thành công",
                                    "Đã xóa " +  selectedCount + " truyện khỏi tủ sách"
                            );
                        }).setNegativeButton("Không", null).show();

            }else {
                Toast.makeText(getContext(), "Chưa chọn item nào", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // ==================== CREATE UI ELEMENTS ====================

    private TextView createMenuItem(String id, String name) {
        TextView textView = new TextView(getContext());
        textView.setText(name);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTextSize(14);
        textView.setGravity(getResources().getColor(R.color.app_text_primary));
        textView.setPadding(40, 30, 40, 30);
        textView.setTag(id);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 30);
        textView.setLayoutParams(params);
        textView.setOnClickListener(v -> {
            onMenuItemClick(textView);
        });
        return textView;
    }

    // ==================== CLICK HANDLERS ====================

    private void onMenuItemClick(TextView clickedView) {
        if (selectedTextView != null) {
            setUnselectedStyle(selectedTextView);
        }
        setSelectedStyle(clickedView);
        selectedTextView = clickedView;
        String itemId = (String) clickedView.getTag();
        handleMenuSelection(itemId);
    }

    private void selectTabById(String tabId) {
        // Duyệt qua tất cả các TextView trong case_menu
        for (int i = 0; i < case_menu.getChildCount(); i++) {
            View child = case_menu.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                String tag = (String) textView.getTag();

                // Nếu tìm thấy tab có tag khớp với tabId
                if (tag != null && tag.equals(tabId)) {
                    textView.performClick(); // Giả lập click vào tab
                    return;
                }
            }
        }

        // Nếu không tìm thấy tab, load tab đầu tiên
        if (case_menu.getChildCount() > 0) {
            case_menu.getChildAt(0).performClick();
        }
    }

    // ==================== STYLING METHODS ====================

    private void setSelectedStyle(TextView textView) {
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setBackgroundResource(R.drawable.btn_bg);

    }

    private void setUnselectedStyle(TextView textView) {
        textView.setTextSize(14);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
    }

    // ==================== MENU SELECTION HANDLER ====================

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
        }
    }

    // ==================== DATA LOADING FROM FIREBASE ====================

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
                        storyList.clear();

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
                                Story story = new Story(
                                        image,
                                        title,
                                        viewCount != null ? viewCount : 0,
                                        genreList,
                                        chapterCount != null ? chapterCount : 0,
                                        author != null ? author : "Đang cập nhật",
                                        description != null ? description : "Đang cập nhật mô tả..."
                                );
                                storyList.add(story);
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
                        storyList.clear();

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

                                Story story = new Story(
                                        image,
                                        title,
                                        viewCount != null ? viewCount : 0,
                                        genreList,
                                        chapterCount != null ? chapterCount : 0,
                                        author != null ? author : "Đang cập nhật",
                                        description != null ? description : "Đang cập nhật mô tả..."
                                );
                                storyList.add(story);
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
    // ==================== DATA PROCESSING ====================
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

    // ==================== FIREBASE OPERATIONS ====================

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

    // ==================== UI HELPER METHODS ====================

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