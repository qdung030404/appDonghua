package com.example.appdonghua.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Adapter.ChapterAdapter;
import com.example.appdonghua.Model.Chapter;
import com.example.appdonghua.R;

import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private LinearLayout bottomMenuBar;
    private ImageButton btnPreviousChapter, btnNextChapter, btnSelectChapter, btnToggleDarkMode, btnBack;
    private TextView tvContent;
    private View touchInterceptor;
    private GestureDetector gestureDetector;
    private boolean isMenuVisible = false;
    private boolean isDarkMode = true; // Mặc định là Dark Mode

    private List<Chapter> chapterList;
    private int currentChapterIndex = 0;
    private int totalChapters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getIntentData();
        loadChapterData();
        setupGestureDetector();
        setupClickListeners();
        applyDarkMode(); // Áp dụng Dark Mode mặc định
        displayChapter(currentChapterIndex);
    }

    private void initViews() {
        mainLayout = findViewById(R.id.main);
        bottomMenuBar = findViewById(R.id.bottomMenuBar);
        btnPreviousChapter = findViewById(R.id.btnPreviousChapter);
        btnNextChapter = findViewById(R.id.btnNextChapter);
        btnSelectChapter = findViewById(R.id.btnSelectChapter);
        btnToggleDarkMode = findViewById(R.id.btnToggleDarkMode);
        btnBack = findViewById(R.id.btnBack);
        tvContent = findViewById(R.id.tvContent);
        touchInterceptor = findViewById(R.id.touchInterceptor);
    }
    private void getIntentData() {
        if (getIntent() != null) {
            currentChapterIndex = getIntent().getIntExtra("CHAPTER_INDEX", 0);
            totalChapters = getIntent().getIntExtra("TOTAL_CHAPTERS", 0);
        }
    }
    private void loadChapterData() {
        // Dữ liệu mẫu - Thay bằng dữ liệu thực từ database hoặc API
        chapterList = new ArrayList<>();
        if (totalChapters > 0) {
            for (int i = 1; i <= totalChapters; i++) {
                chapterList.add(new Chapter("Chapter " + i, i * 100));
            }
        }
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                toggleMenu();
                return true;
            }
        });

        // Sử dụng View trong suốt để bắt touch event
        touchInterceptor.setOnTouchListener((v, event) -> {
            return gestureDetector.onTouchEvent(event);
        });
    }

    private void setupClickListeners() {
        // Nút chương trước
        btnPreviousChapter.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                currentChapterIndex--;
                displayChapter(currentChapterIndex);
                Toast.makeText(this, "Chương trước", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút chương sau
        btnNextChapter.setOnClickListener(v -> {
            if (currentChapterIndex < chapterList.size() - 1) {
                currentChapterIndex++;
                displayChapter(currentChapterIndex);
                Toast.makeText(this, "Chương sau", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đây là chương cuối cùng", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút chọn chương
        btnSelectChapter.setOnClickListener(v -> {
            showChapterListDialog();
        });

        // Nút chuyển đổi chế độ sáng/tối
        btnToggleDarkMode.setOnClickListener(v -> {
            toggleDarkMode();
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void showChapterListDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_chapter_list);

        dialog.getWindow().setLayout(
                (getResources().getDisplayMetrics().widthPixels ),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.6)
        );

        // Thiết lập RecyclerView
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo adapter với listener
        ChapterAdapter adapter = new ChapterAdapter(chapterList, (chapter, position) -> {
            // Khi click vào một chương
            currentChapterIndex = position;
            displayChapter(currentChapterIndex);
            dialog.dismiss();
            Toast.makeText(this, "Đã chọn: " + chapter.getChapter(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        // Cuộn đến chương đang đọc
        recyclerView.scrollToPosition(currentChapterIndex);

        dialog.show();
    }

    private void displayChapter(int index) {
        if (index >= 0 && index < chapterList.size()) {
            Chapter chapter = chapterList.get(index);

            // Hiển thị nội dung chương (dữ liệu mẫu)
            String content = chapter.getChapter() + "\n\n" +
                    "Đây là nội dung của " + chapter.getChapter() + ".\n\n" +
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                    "nisi ut aliquip ex ea commodo consequat.\n\n" +
                    "Duis aute irure dolor in reprehenderit in voluptate velit esse " +
                    "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat " +
                    "cupidatat non proident, sunt in culpa qui officia deserunt mollit " +
                    "anim id est laborum.\n\n" +
                    "Lượt xem: " + chapter.getViews();

            tvContent.setText(content);
        }
    }

    private void toggleMenu() {
        if (isMenuVisible) {
            hideMenu();
        } else {
            showMenu();
        }
        isMenuVisible = !isMenuVisible;
    }

    private void showMenu() {
        bottomMenuBar.setVisibility(View.VISIBLE);

        TranslateAnimation animate = new TranslateAnimation(
                0, 0,
                bottomMenuBar.getHeight(), 0);
        animate.setDuration(300);
        animate.setFillAfter(true);
        bottomMenuBar.startAnimation(animate);
    }

    private void hideMenu() {
        TranslateAnimation animate = new TranslateAnimation(
                0, 0,
                0, bottomMenuBar.getHeight());
        animate.setDuration(300);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                bottomMenuBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        bottomMenuBar.startAnimation(animate);
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        applyDarkMode();
    }

    private void applyDarkMode() {
        if (isDarkMode) {
            // Chế độ tối
            mainLayout.setBackgroundResource(R.color.background);
            tvContent.setTextColor(getResources().getColor(android.R.color.white));
            bottomMenuBar.setBackgroundResource(R.color.background);
            btnBack.setColorFilter(0xFFFFFFFF);
            btnPreviousChapter.setColorFilter(0xFFFFFFFF);
            btnNextChapter.setColorFilter(0xFFFFFFFF);
            btnSelectChapter.setColorFilter(0xFFFFFFFF);
            btnToggleDarkMode.setColorFilter(0xFFFFFFFF);
            btnToggleDarkMode.setImageResource(R.drawable.ic_dark_mode);

        } else {
            // Chế độ sáng
            mainLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
            tvContent.setTextColor(getResources().getColor(android.R.color.black));
            bottomMenuBar.setBackgroundColor(0xFFF5F5F5);
            btnBack.setColorFilter(0xFF333333);
            btnPreviousChapter.setColorFilter(0xFF333333);
            btnNextChapter.setColorFilter(0xFF333333);
            btnSelectChapter.setColorFilter(0xFF333333);
            btnToggleDarkMode.setColorFilter(0xFF333333);
            btnToggleDarkMode.setImageResource(R.drawable.ic_light_mode);
        }
    }
}