package com.example.appdonghua.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private LinearLayout bottomMenuBar;
    private ScrollView scrollViewContent;
    private ImageButton btnPreviousChapter, btnNextChapter, btnSelectChapter, btnToggleDarkMode, btnBack;
    private TextView tvContent;
    private GestureDetector gestureDetector;
    private boolean isMenuVisible = false;
    private boolean isDarkMode = false;
    private boolean isChangeChapter = false;
    private List<Chapter> chapterList;
    private int currentChapterIndex = 0;
    private int totalChapters;
    private Handler handler = new Handler();
    private static final long MENU_HIDE_DELAY = 3000;
    private int scrollTopBuffer = 0;
    private static final int SCROLL_THRESHOLD = 5;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String comicTitle;
    private Runnable hideMenuRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMenuVisible) {
                hideMenu();
                isMenuVisible = false;
            }
        }
    };
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
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initViews();
        getIntentData();
        loadChapterData();
        setupGestureDetector();
        setupClickListeners();
        setupScrollListener();
        applyDarkMode();
        displayChapter(currentChapterIndex);
    }
    private void getIntentData() {
        if (getIntent() != null) {
            currentChapterIndex = getIntent().getIntExtra("CHAPTER_INDEX", 0);
            totalChapters = getIntent().getIntExtra("TOTAL_CHAPTERS", 0);
            comicTitle = getIntent().getStringExtra("COMIC_TITLE");
        }
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
        scrollViewContent = findViewById(R.id.scrollViewContent);
    }
    private void setupClickListeners() {
        btnPreviousChapter.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                changeChapter(false);
            } else {
                Toast.makeText(this, "Đây là chương đầu tiên", Toast.LENGTH_SHORT).show();
            }
            resetMenuTimer();
        });

        btnNextChapter.setOnClickListener(v -> {
            if (currentChapterIndex < chapterList.size() - 1) {
                changeChapter(true);
            } else {
                Toast.makeText(this, "Đây là chương cuối cùng", Toast.LENGTH_SHORT).show();
            }
            resetMenuTimer();
        });

        btnSelectChapter.setOnClickListener(v -> {
            showChapterListDialog();
            resetMenuTimer();
        });

        btnToggleDarkMode.setOnClickListener(v -> {
            toggleDarkMode();
            resetMenuTimer();
        });
        btnBack.setOnClickListener(v -> finish());
    }
    private void setupScrollListener(){
        scrollViewContent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(){
                if (isChangeChapter) {
                    return;
                }
                View view = scrollViewContent.getChildAt(scrollViewContent.getChildCount() - 1);
                int scrollY = scrollViewContent.getScrollY();
                int diff = (view.getBottom() - (scrollViewContent.getHeight() + scrollViewContent.getScrollY()));

                if (diff <= 10 && diff >= 0){
                    changeChapter(true);
                }
                if (scrollY == 0 && scrollTopBuffer > SCROLL_THRESHOLD) {
                    changeChapter(false);

                }
                scrollTopBuffer = scrollY;
            }
        });
    }

    private void changeChapter(boolean isNext) {
        if (isChangeChapter) {
            return;
        }

        // Kiểm tra điều kiện chuyển chương
        if ((isNext && currentChapterIndex >= chapterList.size() - 1) ||
                (!isNext && currentChapterIndex <= 0)) {
            if (isNext) {
                Toast.makeText(this, "Đây là chương cuối cùng", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        isChangeChapter = true;
        tvContent.animate().alpha(0f).setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Thay đổi chỉ số chương
                        currentChapterIndex += isNext ? 1 : -1;

                        // Hiển thị chương mới
                        displayChapter(currentChapterIndex);

                        // Scroll về đầu trang
                        scrollViewContent.post(new Runnable(){
                            @Override
                            public void run() {
                                scrollViewContent.scrollTo(0, 0);
                            }
                        });

                        // Hiệu ứng fade in
                        tvContent.animate().alpha(1f).setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        isChangeChapter = false;
                                    }
                                }).start();
                    }
                }).start();
    }
    private void changeChapterWithAnimation(int newChapterIndex, boolean isGoingBack) {
        if (isChangeChapter) {
            return;
        }

        isChangeChapter = true;

        // Slide animation direction
        float startX = isGoingBack ? -scrollViewContent.getWidth() : scrollViewContent.getWidth();
        float endX = 0f;

        // Fade out và slide out
        tvContent.animate()
                .alpha(0f)
                .translationX(-startX)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Chuyển chapter
                        currentChapterIndex = newChapterIndex;
                        displayChapter(currentChapterIndex);

                        // Reset vị trí cho animation vào
                        tvContent.setTranslationX(startX);
                        scrollViewContent.scrollTo(0, 0);

                        // Fade in và slide in
                        tvContent.animate()
                                .alpha(1f)
                                .translationX(endX)
                                .setDuration(250)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        isChangeChapter = false;
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private void loadChapterData() {
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
            if (position != currentChapterIndex) {
                boolean isGoingBack = position < currentChapterIndex;
                changeChapterWithAnimation(position, isGoingBack);
            }
            dialog.dismiss();
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
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                    "nisi ut aliquip ex ea commodo consequat.\n\n" +
                    "Duis aute irure dolor in reprehenderit in voluptate velit esse " +
                    "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat " +
                    "cupidatat non proident, sunt in culpa qui officia deserunt mollit " +
                    "anim id est laborum.\n\n" +
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                    "nisi ut aliquip ex ea commodo consequat.\n\n" +
                    "Duis aute irure dolor in reprehenderit in voluptate velit esse " +
                    "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat " +
                    "cupidatat non proident, sunt in culpa qui officia deserunt mollit " +
                    "anim id est laborum.\n\n" +
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
            saveReadingProgress(index);
        }
    }
    private void toggleMenu() {
        if (isMenuVisible) {
            hideMenu();
            handler.removeCallbacks(hideMenuRunnable);
        } else {
            showMenu();
            handler.postDelayed(hideMenuRunnable, MENU_HIDE_DELAY);
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
    private void resetMenuTimer() {
        // Hủy timer cũ
        handler.removeCallbacks(hideMenuRunnable);
        // Đặt timer mới nếu menu đang hiển thị
        if (isMenuVisible) {
            handler.postDelayed(hideMenuRunnable, MENU_HIDE_DELAY);
        }
    }
    private void saveReadingProgress(int chapterIndex) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || comicTitle == null || chapterList == null || chapterIndex >= chapterList.size()) {
            return;
        }

        Map<String, Object> progressData = new HashMap<>();
        progressData.put("lastChapterIndex", chapterIndex);
        progressData.put("lastChapterName", chapterList.get(chapterIndex).getChapter());
        progressData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(currentUser.getUid())
                .collection("history").document(comicTitle)
                .update(progressData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Progress saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error saving progress", e);
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lưu progress khi thoát activity
        saveReadingProgress(currentChapterIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy timer khi activity bị destroy
        handler.removeCallbacks(hideMenuRunnable);
        saveReadingProgress(currentChapterIndex);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Kiểm tra double tap trước
        gestureDetector.onTouchEvent(ev);
        // Sau đó cho phép xử lý bình thường
        return super.dispatchTouchEvent(ev);
    }
}