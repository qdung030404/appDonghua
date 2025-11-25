package com.example.appdonghua.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdonghua.Adapter.NoveListAdapter;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ClassifyFragment extends Fragment {
    private String[][] categories = {
            {"all", "Toàn Bộ"},
            {"do_thi", "Đô Thị"},
            {"magical", "Huyền Huyễn"},
            {"xuyen_khong", "Xuyên Không"},
            {"tien_hiep", "Tiên Hiệp"},
            {"mechanic", "Khoa Huyễn"}
    };

    private GridLayout sortMenu;
    private LinearLayout filterMenu;
    private TextView selectedTextView;
    private TextView selectedFilterView;
    private androidx.recyclerview.widget.RecyclerView contentRecyclerView;

    private FirebaseFirestore db;
    private ArrayList<NovelList> contentList;
    private NoveListAdapter adapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ClassifyFragment() {
        // Required empty public constructor
    }

    public static ClassifyFragment newInstance(String param1, String param2) {
        ClassifyFragment fragment = new ClassifyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();
        contentList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify, container, false);

        init(view);
        setupCategories();
        setupContentRecyclerView();

        // Load dữ liệu mặc định
        loadContent();

        return view;
    }

    private void init(View view) {
        sortMenu = view.findViewById(R.id.sortMenu);
        filterMenu = view.findViewById(R.id.filterMenu);
        contentRecyclerView = view.findViewById(R.id.contentRecyclerView);
    }

    private void setupCategories() {
        // Xóa các view cũ (nếu có)
        sortMenu.removeAllViews();

        // Setup GridLayout với 3 cột
        sortMenu.setColumnCount(3);
        sortMenu.setRowCount((int) Math.ceil(categories.length / 3.0));

        for (String[] category : categories) {
            TextView textView = createCategoryTextView(category[0], category[1]);
            sortMenu.addView(textView);
        }

        // Mặc định chọn item đầu tiên
        if (sortMenu.getChildCount() > 0) {
            selectedTextView = (TextView) sortMenu.getChildAt(0);
            setSelectedStyle(selectedTextView);
        }

        // Setup filter menu (Hot nhất và Hoàn thành)
        setupFilterMenu();
    }

    private void setupFilterMenu() {
        filterMenu.removeAllViews();

        // Tạo TextView cho "Hot nhất"
        TextView hotView = createFilterTextView("hot", "Hot nhất");
        filterMenu.addView(hotView);

        // Tạo TextView cho "Hoàn thành"
        TextView completeView = createFilterTextView("complete", "Hoàn thành");
        filterMenu.addView(completeView);

        // Mặc định chọn "Hot nhất"
        selectedFilterView = hotView;
        setSelectedFilterStyle(selectedFilterView);
    }

    private TextView createFilterTextView(String filterId, String filterName) {
        TextView textView = new TextView(getContext());
        textView.setText(filterName);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(40, 25, 40, 25);
        textView.setTag(filterId);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        params.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(params);

        textView.setOnClickListener(v -> {
            onFilterClick(textView);
        });

        return textView;
    }

    private void onFilterClick(TextView clickedTextView) {
        // Reset style của filter trước đó
        if (selectedFilterView != null) {
            setUnselectedFilterStyle(selectedFilterView);
        }

        // Set style cho filter mới
        setSelectedFilterStyle(clickedTextView);
        selectedFilterView = clickedTextView;

        // Xử lý khi click filter
        String filterId = (String) clickedTextView.getTag();
        String filterName = clickedTextView.getText().toString();

        Toast.makeText(getContext(), "Lọc theo: " + filterName, Toast.LENGTH_SHORT).show();

        // Load content dựa trên filter được chọn
        loadContent();
    }

    private void setSelectedFilterStyle(TextView textView) {
        textView.setTextSize(15);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setBackgroundResource(R.drawable.btnbg);
    }

    private void setUnselectedFilterStyle(TextView textView) {
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);
        textView.setBackgroundColor(Color.TRANSPARENT);
    }

    private TextView createCategoryTextView(String categoryId, String categoryName) {
        TextView textView = new TextView(getContext());
        textView.setText(categoryName);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(20, 30, 20, 30);
        textView.setTag(categoryId);

        // Setup GridLayout params
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(params);

        textView.setOnClickListener(v -> {
            onCategoryClick(textView);
        });

        return textView;
    }

    private void onCategoryClick(TextView clickedTextView) {
        // Reset style của item trước đó
        if (selectedTextView != null) {
            setUnselectedStyle(selectedTextView);
        }

        // Set style cho item mới
        setSelectedStyle(clickedTextView);
        selectedTextView = clickedTextView;

        // Xử lý khi click category
        String categoryId = (String) clickedTextView.getTag();
        String categoryName = clickedTextView.getText().toString();

        Toast.makeText(getContext(), "Đã chọn: " + categoryName, Toast.LENGTH_SHORT).show();

        // Load content dựa trên category được chọn
        loadContent();
    }

    private void setSelectedStyle(TextView textView) {
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setBackgroundResource(R.drawable.btnbg);
    }

    private void setUnselectedStyle(TextView textView) {
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);
        textView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setupContentRecyclerView() {
        // Setup RecyclerView với LinearLayoutManager
        androidx.recyclerview.widget.LinearLayoutManager layoutManager =
                new androidx.recyclerview.widget.LinearLayoutManager(getContext());
        contentRecyclerView.setLayoutManager(layoutManager);

        // Khởi tạo adapter
        adapter = new NoveListAdapter(contentList);
        contentRecyclerView.setAdapter(adapter);
    }

    private void loadContent() {
        // Lấy category và filter hiện tại
        String categoryId = selectedTextView != null ? (String) selectedTextView.getTag() : "all";
        String filterId = selectedFilterView != null ? (String) selectedFilterView.getTag() : "hot";

        // Hiển thị loading (có thể thêm ProgressBar)
        Toast.makeText(getContext(), "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();

        // Fetch data từ Firestore
        fetchDataFromFirestore(categoryId, filterId);
    }

    private void fetchDataFromFirestore(String categoryId, String filterId) {
        Query query;

        // Xây dựng query dựa trên category và filter
        if (filterId.equals("complete")) {
            // Filter "Hoàn thành"
            if (categoryId.equals("all")) {
                query = db.collection("stories")
                        .whereEqualTo("status", "Full")
                        .orderBy("viewCount", Query.Direction.DESCENDING);

            } else {
                query = db.collection("stories")
                        .whereEqualTo("status", "Full")
                        .whereArrayContains("genres", getCategoryName(categoryId))
                        .orderBy("viewCount", Query.Direction.DESCENDING);
            }
        } else {
            // Filter "Hot nhất"
            if (categoryId.equals("all")) {
                query = db.collection("stories")
                        .orderBy("viewCount", Query.Direction.DESCENDING);
            } else {
                query = db.collection("stories")
                        .whereArrayContains("genres", getCategoryName(categoryId))
                        .orderBy("viewCount", Query.Direction.DESCENDING);
            }
        }

        // Thực hiện query
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    contentList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Story story = doc.toObject(Story.class);

                            // Lấy genre đầu tiên hoặc "Khác"
                            String genre = (story.getGenres() != null && !story.getGenres().isEmpty())
                                    ? story.getGenres().get(0) : "Khác";

                            // Tạo NovelList từ Story
                            NovelList novel = new NovelList(
                                    story.getCoverImageUrl(),
                                    story.getTitle(),
                                    story.getViewCount(),
                                    story.getGenres(),
                                    story.getChapter(),
                                    story.getAuthor(),
                                    story.getDescription()
                            );

                            contentList.add(novel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Cập nhật UI
                    updateRecyclerView();

                    if (getContext() != null) {
                        Toast.makeText(getContext(),
                                "Đã tải " + contentList.size() + " kết quả",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(),
                                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper method để chuyển đổi categoryId sang tên tiếng Việt
    private String getCategoryName(String categoryId) {
        switch (categoryId) {
            case "do_thi":
                return "Đô Thị";
            case "magical":
                return "Huyền Huyễn";
            case "xuyen_khong":
                return "Xuyên Không";
            case "tien_hiep":
                return "Tiên Hiệp";
            case "mechanic":
                return "Khoa Huyễn";
            default:
                return "";
        }
    }

    private void updateRecyclerView() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadCategoryContent(String categoryId, String categoryName) {
        // TODO: Implement logic để load nội dung theo category
        // Có thể load fragment mới hoặc fetch data từ API
    }

    private void loadFilterContent(String filterId, String filterName) {
        // TODO: Implement logic để load nội dung theo filter (hot/complete)
        // Kết hợp với category đã chọn để filter dữ liệu
    }
}