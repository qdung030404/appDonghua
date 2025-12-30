package com.example.appdonghua.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.appdonghua.Fragment.RankingBoardFragment;
import com.example.appdonghua.R;

public class RankingActivity extends AppCompatActivity {
    private LinearLayout filterButton;
    private ImageButton backButton;
    private TextView selectedTextView;

    // Cập nhật categories theo NovelList
    private String[][] categories = {
            {"all", "Đề Xuất"},
            {"hot", "Hot nhất"},
            {"full", "Hoàn thành"},
            {"do_thi", "Đô Thị"},
            {"magical", "Huyền Huyễn"},
            {"xuyen_khong", "Xuyên Không"},
            {"tien_hiep", "Tiên Hiệp"},
            {"mechanic", "Khoa Huyễn"}
    };

    // ==================== LIFECYCLE METHODS ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        setupCategories();
        setupListener();
        loadFragment("all"); // Mặc định load tất cả
    }

    // ==================== INITIALIZATION METHODS ====================

    private void init(){
        filterButton = findViewById(R.id.filter_Button);
        backButton = findViewById(R.id.backButton);
    }

    private void setupListener(){
        backButton.setOnClickListener(v -> finish());
    }

    // ==================== SETUP CATEGORIES ====================

    private void setupCategories() {
        // Xóa các view cũ (nếu có)
        filterButton.removeAllViews();

        for (String[] category : categories) {
            TextView textView = createCategoryTextView(category[0], category[1]);
            filterButton.addView(textView);
        }

        if (filterButton.getChildCount() > 0) {
            selectedTextView = (TextView) filterButton.getChildAt(0);
            setSelectedStyle(selectedTextView);
        }
    }

    // ==================== CREATE UI ELEMENTS ====================

    private TextView createCategoryTextView(String categoryId, String categoryName) {
        TextView textView = new TextView(this);
        textView.setText(categoryName);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(10, 50, 20, 50);
        textView.setTag(categoryId);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 5, 0, 5);
        textView.setLayoutParams(params);

        textView.setOnClickListener(v -> {
            onCategoryClick(textView);
        });

        return textView;
    }

    // ==================== CLICK HANDLERS ====================

    private void onCategoryClick(TextView clickedTextView) {
        // Reset style của item trước đó
        if (selectedTextView != null) {
            setUnselectedStyle(selectedTextView);
        }

        // Set style cho item mới
        setSelectedStyle(clickedTextView);
        selectedTextView = clickedTextView;

        // Load fragment với category mới
        String category = (String) clickedTextView.getTag();
        loadFragment(category);
    }

    // ==================== STYLING METHODS ====================

    private void setSelectedStyle(TextView textView) {
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setBackgroundResource(R.drawable.btn_bg);
    }

    private void setUnselectedStyle(TextView textView) {
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.app_text_primary));
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);
        textView.setBackgroundColor(Color.TRANSPARENT);
    }

    // ==================== FRAGMENT MANAGEMENT ====================

    private void loadFragment(String category) {
        RankingBoardFragment fragment = RankingBoardFragment.newInstance(category);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}