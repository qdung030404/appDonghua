package com.example.appdonghua.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
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
import com.example.appdonghua.Utils.ScreenUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadActivity extends AppCompatActivity {

    // UI Components
    private ConstraintLayout mainLayout;
    private LinearLayout bottomMenuBar;
    private MaterialCardView settingsCard, chapterListCard;
    private ScrollView scrollViewContent;
    private ImageButton btnPreviousChapter, btnNextChapter, btnSelectChapter, btnReadSetting, btnBack;
    private TextView tvContent, textSize, brightnessTitle, textSizeTitle, backgroundColorTitle;
    private TextView tvChapterCount;
    private SeekBar brightnessSeekbar;
    private Button zoomOutButton, zoomInButton;
    private Button colorButton1, colorButton2, colorButton3, colorButton4;
    private Button sortLatestButton, sortOldestButton;
    private RecyclerView rvChapters;

    // Gesture & State
    private GestureDetector gestureDetector;
    private boolean isMenuVisible = false;
    private boolean isSettingsVisible = false;
    private boolean isChapterListVisible = false;
    private boolean isChangeChapter = false;

    // Chapter Data
    private List<Chapter> chapterList;
    private ChapterAdapter chapterAdapter;
    private int currentChapterIndex = 0;
    private int totalChapters;
    private String comicTitle;
    private boolean isLatestFirst = true;

    // Settings
    private float currentTextSize = 16f;
    private int currentBackgroundColor = 0xFFFFFFFF;

    // Scroll & Timer
    private Handler handler = new Handler();
    private static final long MENU_HIDE_DELAY = 3000;
    private int scrollTopBuffer = 0;
    private static final int SCROLL_THRESHOLD = 5;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Runnable hideMenuRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMenuVisible) {
                hideMenu();
                isMenuVisible = false;
            }
        }
    };

    // ==================== LIFECYCLE METHODS ====================

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
        setupSettingsControls();
        setupChapterList();
        setupResponsiveLayout();
        loadReadingSettings();
        displayChapter(currentChapterIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveReadingProgress(currentChapterIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(hideMenuRunnable);
        saveReadingProgress(currentChapterIndex);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    // ==================== INITIALIZATION METHODS ====================

    private void initViews() {
        mainLayout = findViewById(R.id.main);
        bottomMenuBar = findViewById(R.id.bottomMenuBar);
        settingsCard = findViewById(R.id.settingsCard);
        chapterListCard = findViewById(R.id.chapterListCard);
        btnPreviousChapter = findViewById(R.id.btnPreviousChapter);
        btnNextChapter = findViewById(R.id.btnNextChapter);
        btnSelectChapter = findViewById(R.id.btnSelectChapter);
        btnReadSetting = findViewById(R.id.read_setting_button);
        btnBack = findViewById(R.id.btnBack);
        tvContent = findViewById(R.id.tvContent);
        scrollViewContent = findViewById(R.id.scrollViewContent);
        rvChapters = findViewById(R.id.recyclerViewChapters);
        tvChapterCount = findViewById(R.id.chapterCount);
        sortLatestButton = findViewById(R.id.sort_latest_button);
        sortOldestButton = findViewById(R.id.sort_oldest_button);

        // Settings controls
        brightnessTitle = findViewById(R.id.brightness_title);
        textSizeTitle = findViewById(R.id.text_size_title);
        backgroundColorTitle = findViewById(R.id.background_color_title);
        brightnessSeekbar = findViewById(R.id.brightness_seekbar);
        zoomOutButton = findViewById(R.id.zoom_out_button);
        zoomInButton = findViewById(R.id.zoom_in_button);
        textSize = findViewById(R.id.text_size);
        colorButton1 = findViewById(R.id.color_button_1);
        colorButton2 = findViewById(R.id.color_button_2);
        colorButton3 = findViewById(R.id.color_button_3);
        colorButton4 = findViewById(R.id.color_button_4);

        // Initially hide cards
        settingsCard.setVisibility(View.GONE);
        chapterListCard.setVisibility(View.GONE);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            currentChapterIndex = getIntent().getIntExtra("CHAPTER_INDEX", 0);
            totalChapters = getIntent().getIntExtra("TOTAL_CHAPTERS", 0);
            comicTitle = getIntent().getStringExtra("COMIC_TITLE");
        }
    }

    private void loadChapterData() {
        chapterList = new ArrayList<>();
        if (totalChapters > 0) {
            for (int i = 1; i <= totalChapters; i++) {
                chapterList.add(new Chapter(String.valueOf(i), i * 100));
            }
        }
    }

    // ==================== SETUP METHODS ====================

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                toggleMenu();
                return true;
            }
        });
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
            toggleChapterList();
        });

        btnReadSetting.setOnClickListener(v -> {
            toggleSettings();
        });

        btnBack.setOnClickListener(v -> finish());

        sortLatestButton.setOnClickListener(v -> {
            sortChapters(true);
        });

        sortOldestButton.setOnClickListener(v -> {
            sortChapters(false);
        });
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
    private void setupTextColor(int color){
        textSize.setTextColor(color);
        textSizeTitle.setTextColor(color);
        brightnessTitle.setTextColor(color);
        backgroundColorTitle.setTextColor(color);
        sortLatestButton.setTextColor(color);
        sortOldestButton.setTextColor(color);
    }

    private void setupSettingsControls() {
        // Setup brightness seekbar
        brightnessSeekbar.setMax(255);
        try {
            int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightnessSeekbar.setProgress(brightness);
        } catch (Settings.SettingNotFoundException e) {
            brightnessSeekbar.setProgress(128);
        }

        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setBrightness(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        // Setup text size controls
        textSize.setText(String.valueOf((int) currentTextSize));

        zoomOutButton.setOnClickListener(v -> {
            if (currentTextSize > 14) {
                currentTextSize -= 1;
                tvContent.setTextSize(currentTextSize);
                textSize.setText(String.valueOf((int) currentTextSize));
                saveReadingSettings();
            }
        });

        zoomInButton.setOnClickListener(v -> {
            if (currentTextSize < 34) {
                currentTextSize += 1;
                tvContent.setTextSize(currentTextSize);
                textSize.setText(String.valueOf((int) currentTextSize));
                saveReadingSettings();
            }
        });

        // Setup background color buttons
        colorButton1.setOnClickListener(v -> {
            changeBackgroundColor(0xFFFFFFFF);
            settingsCard.setCardBackgroundColor(0xFFCCCCCC);
            chapterListCard.setCardBackgroundColor(0xFFCCCCCC);
            setupTextColor(0xFF5F639D);
        });

        colorButton2.setOnClickListener(v -> {
            changeBackgroundColor(0xFFDBDBDB);
            settingsCard.setCardBackgroundColor(0xFFA3A3A3);
            chapterListCard.setCardBackgroundColor(0xFFA3A3A3);
            setupTextColor(0xFF5F639D);
        });

        colorButton3.setOnClickListener(v -> {
            changeBackgroundColor(0xFF5F639D);
            settingsCard.setCardBackgroundColor(0xFF7D81C1);
            chapterListCard.setCardBackgroundColor(0xFF7D81C1);
            setupTextColor(0xFFFFFFFF);
        });

        colorButton4.setOnClickListener(v -> {
            changeBackgroundColor(0xFF000000);
            settingsCard.setCardBackgroundColor(0xFF5C5C5C);
            chapterListCard.setCardBackgroundColor(0xFF5C5C5C);
            setupTextColor(0xFFFFFFFF);
        });
    }

    private void setupChapterList() {
        rvChapters.setLayoutManager(new LinearLayoutManager(this));

        tvChapterCount.setText(totalChapters + " chương");

        chapterAdapter = new ChapterAdapter(chapterList, (chapter, position) -> {
            if (position != currentChapterIndex) {
                boolean isGoingBack = position < currentChapterIndex;
                changeChapterWithAnimation(position, isGoingBack);
            }
            hideChapterList();
        });

        rvChapters.setAdapter(chapterAdapter);

        updateSortButtonStates();
    }

    private void setupResponsiveLayout() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        float screenHeightPx = displayMetrics.heightPixels;

        ViewGroup.LayoutParams rvParams = rvChapters.getLayoutParams();
        if (screenWidthDp >= 600) {
            rvParams.height = (int) (screenHeightPx * 0.5);
        } else if (screenWidthDp >= 400) {
            rvParams.height = (int) (screenHeightPx * 0.45);
        } else {
            rvParams.height = (int) (screenHeightPx * 0.4);
        }
        rvChapters.setLayoutParams(rvParams);

        if (screenWidthDp >= 600) {
            if (currentTextSize == 16f || currentTextSize == 18f) {
                currentTextSize = 20f;
            }
            tvContent.setPadding(
                    ScreenUtils.dpToPx(this, 24),
                    ScreenUtils.dpToPx(this, 24),
                    ScreenUtils.dpToPx(this, 24),
                    ScreenUtils.dpToPx(this, 24)
            );
        } else if (screenWidthDp >= 400) {
            if (currentTextSize == 16f) {
                currentTextSize = 18f;
            }
            tvContent.setPadding(
                    ScreenUtils.dpToPx(this, 20),
                    ScreenUtils.dpToPx(this, 20),
                    ScreenUtils.dpToPx(this, 20),
                    ScreenUtils.dpToPx(this, 20)
            );
        } else {
            tvContent.setPadding(
                    ScreenUtils.dpToPx(this, 16),
                    ScreenUtils.dpToPx(this, 16),
                    ScreenUtils.dpToPx(this, 16),
                    ScreenUtils.dpToPx(this, 16)
            );
        }

        ScreenUtils.TextSize textSize = ScreenUtils.calculateTextSize(this);
        tvChapterCount.setTextSize(textSize.title);

        sortLatestButton.setTextSize(textSize.body);
        sortOldestButton.setTextSize(textSize.body);

        brightnessTitle.setTextSize(textSize.title);
        textSizeTitle.setTextSize(textSize.title);
        backgroundColorTitle.setTextSize(textSize.title);
        this.textSize.setTextSize(textSize.subtitle);

        int buttonHeight;
        int buttonWidth;
        if (screenWidthDp >= 600) {
            buttonHeight = ScreenUtils.dpToPx(this, 56);
            buttonWidth = ScreenUtils.dpToPx(this, 90);
            zoomOutButton.setTextSize(14);
            zoomInButton.setTextSize(20);
        } else if (screenWidthDp >= 400) {
            buttonHeight = ScreenUtils.dpToPx(this, 50);
            buttonWidth = ScreenUtils.dpToPx(this, 80);
            zoomOutButton.setTextSize(12);
            zoomInButton.setTextSize(18);
        } else {
            buttonHeight = ScreenUtils.dpToPx(this, 48);
            buttonWidth = ScreenUtils.dpToPx(this, 70);
            zoomOutButton.setTextSize(12);
            zoomInButton.setTextSize(18);
        }

        ViewGroup.LayoutParams zoomOutParams = zoomOutButton.getLayoutParams();
        zoomOutParams.height = buttonHeight;
        zoomOutParams.width = buttonWidth;
        zoomOutButton.setLayoutParams(zoomOutParams);

        ViewGroup.LayoutParams zoomInParams = zoomInButton.getLayoutParams();
        zoomInParams.height = buttonHeight;
        zoomInParams.width = buttonWidth;
        zoomInButton.setLayoutParams(zoomInParams);

        // Color buttons height
        int colorButtonHeight;
        if (screenWidthDp >= 600) {
            colorButtonHeight = ScreenUtils.dpToPx(this, 56);
        } else if (screenWidthDp >= 400) {
            colorButtonHeight = ScreenUtils.dpToPx(this, 50);
        } else {
            colorButtonHeight = ScreenUtils.dpToPx(this, 48);
        }

        setColorButtonHeight(colorButton1, colorButtonHeight);
        setColorButtonHeight(colorButton2, colorButtonHeight);
        setColorButtonHeight(colorButton3, colorButtonHeight);
        setColorButtonHeight(colorButton4, colorButtonHeight);

        // Settings card padding
        int settingsPadding;
        if (screenWidthDp >= 600) {
            settingsPadding = ScreenUtils.dpToPx(this, 20);
        } else if (screenWidthDp >= 400) {
            settingsPadding = ScreenUtils.dpToPx(this, 18);
        } else {
            settingsPadding = ScreenUtils.dpToPx(this, 16);
        }

        View settingsContent = settingsCard.getChildAt(0);
        if (settingsContent != null) {
            settingsContent.setPadding(settingsPadding, settingsPadding, settingsPadding, settingsPadding);
        }

        // Chapter list card padding
        int chapterListPadding;
        if (screenWidthDp >= 600) {
            chapterListPadding = ScreenUtils.dpToPx(this, 20);
        } else if (screenWidthDp >= 400) {
            chapterListPadding = ScreenUtils.dpToPx(this, 16);
        } else {
            chapterListPadding = ScreenUtils.dpToPx(this, 12);
        }

        // Apply padding to chapter list header
        View chapterListContent = chapterListCard.getChildAt(0);
        if (chapterListContent instanceof LinearLayout) {
            LinearLayout headerLayout = (LinearLayout) ((LinearLayout) chapterListContent).getChildAt(0);
            if (headerLayout != null) {
                int topPadding = ScreenUtils.dpToPx(this, screenWidthDp >= 600 ? 16 : 12);
                int bottomPadding = ScreenUtils.dpToPx(this, screenWidthDp >= 600 ? 12 : 8);
                headerLayout.setPadding(chapterListPadding, topPadding, chapterListPadding, bottomPadding);
            }
        }

        // RecyclerView padding
        int rvPadding = screenWidthDp >= 600 ? ScreenUtils.dpToPx(this, 12) : ScreenUtils.dpToPx(this, 8);
        rvChapters.setPadding(rvPadding, rvPadding, rvPadding, rvPadding);

        // Icon buttons size in bottom menu
        int iconSize;
        if (screenWidthDp >= 600) {
            iconSize = ScreenUtils.dpToPx(this, 35);
        } else if (screenWidthDp >= 400) {
            iconSize = ScreenUtils.dpToPx(this, 30);
        } else {
            iconSize = ScreenUtils.dpToPx(this, 25);
        }

        setIconButtonSize(btnPreviousChapter, iconSize);
        setIconButtonSize(btnNextChapter, iconSize);
        setIconButtonSize(btnSelectChapter, iconSize);
        setIconButtonSize(btnReadSetting, iconSize);
        setIconButtonSize(btnBack, iconSize);

        // Bottom menu bar height và padding
        int menuPaddingVertical;
        int menuPaddingHorizontal;
        if (screenWidthDp >= 600) {
            menuPaddingVertical = ScreenUtils.dpToPx(this, 16);
            menuPaddingHorizontal = ScreenUtils.dpToPx(this, 24);
        } else if (screenWidthDp >= 400) {
            menuPaddingVertical = ScreenUtils.dpToPx(this, 14);
            menuPaddingHorizontal = ScreenUtils.dpToPx(this, 16);
        } else {
            menuPaddingVertical = ScreenUtils.dpToPx(this, 12);
            menuPaddingHorizontal = ScreenUtils.dpToPx(this, 12);
        }

        // Apply padding to the control bar inside bottom menu
        View controlBar = bottomMenuBar.getChildAt(bottomMenuBar.getChildCount() - 1);
        if (controlBar instanceof LinearLayout) {
            controlBar.setPadding(menuPaddingHorizontal, menuPaddingVertical, menuPaddingHorizontal, menuPaddingVertical);
        }

        // Spacing between icons in bottom menu
        int iconSpacing;
        if (screenWidthDp >= 600) {
            iconSpacing = ScreenUtils.dpToPx(this, 12);
        } else if (screenWidthDp >= 400) {
            iconSpacing = ScreenUtils.dpToPx(this, 10);
        } else {
            iconSpacing = ScreenUtils.dpToPx(this, 8);
        }

        setIconMargin(btnBack, iconSpacing);
        setIconMargin(btnSelectChapter, iconSpacing);
        setIconMargin(btnReadSetting, iconSpacing);
    }

    private void setIconButtonSize(ImageButton button, int size) {
        ViewGroup.LayoutParams params = button.getLayoutParams();
        params.width = size;
        params.height = size;
        button.setLayoutParams(params);
    }

    private void setColorButtonHeight(Button button, int height) {
        ViewGroup.LayoutParams params = button.getLayoutParams();
        params.height = height;
        button.setLayoutParams(params);
    }

    private void setIconMargin(ImageButton button, int margin) {
        if (button.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
            params.setMargins(margin, 0, margin, 0);
            button.setLayoutParams(params);
        }
    }

    // ==================== CHAPTER MANAGEMENT METHODS ====================

    private void displayChapter(int index) {
        if (index >= 0 && index < chapterList.size()) {
            Chapter chapter = chapterList.get(index);

            String content = chapter.getChapter() + "\n\n" +
                    "Đây là nội dung của chương " + chapter.getChapter() + ".\n\n" +
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

    private void changeChapter(boolean isNext) {
        if (isChangeChapter) {
            return;
        }

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
                        currentChapterIndex += isNext ? 1 : -1;
                        displayChapter(currentChapterIndex);

                        scrollViewContent.post(new Runnable(){
                            @Override
                            public void run() {
                                scrollViewContent.scrollTo(0, 0);
                            }
                        });

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

        float startX = isGoingBack ? -scrollViewContent.getWidth() : scrollViewContent.getWidth();
        float endX = 0f;

        tvContent.animate()
                .alpha(0f)
                .translationX(-startX)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentChapterIndex = newChapterIndex;
                        displayChapter(currentChapterIndex);

                        tvContent.setTranslationX(startX);
                        scrollViewContent.scrollTo(0, 0);

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

    private void sortChapters(boolean latestFirst) {
        isLatestFirst = latestFirst;

        List<Chapter> sortedList = new ArrayList<>(chapterList);
        if (!latestFirst) {
            Collections.reverse(sortedList);
        }

        chapterAdapter = new ChapterAdapter(sortedList, (chapter, position) -> {
            int actualPosition = chapterList.indexOf(chapter);
            if (actualPosition != currentChapterIndex) {
                boolean isGoingBack = actualPosition < currentChapterIndex;
                changeChapterWithAnimation(actualPosition, isGoingBack);
            }
            hideChapterList();
        });

        rvChapters.setAdapter(chapterAdapter);

        // Scroll to current chapter
        int scrollPosition = latestFirst ? currentChapterIndex : (chapterList.size() - 1 - currentChapterIndex);
        rvChapters.scrollToPosition(scrollPosition);

        updateSortButtonStates();
    }

    private void updateSortButtonStates() {
        if (isLatestFirst) {
            sortLatestButton.setTypeface(null, android.graphics.Typeface.BOLD);
            sortOldestButton.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            sortOldestButton.setTypeface(null, android.graphics.Typeface.BOLD);
            sortLatestButton.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    // ==================== MENU METHODS ====================

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
        // Hide settings and chapter list first if visible
        if (isSettingsVisible) {
            hideSettings();
            isSettingsVisible = false;
            return;
        }

        if (isChapterListVisible) {
            hideChapterList();
            return;
        }

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

    private void resetMenuTimer() {
        handler.removeCallbacks(hideMenuRunnable);
        if (isMenuVisible) {
            handler.postDelayed(hideMenuRunnable, MENU_HIDE_DELAY);
        }
    }

    // ==================== CHAPTER LIST METHODS ====================

    private void  toggleChapterList() {
        if (isChapterListVisible) {
            hideChapterList();
        } else {
            showChapterList();
        }
        isChapterListVisible = !isChapterListVisible;
    }

    private void showChapterList() {

        if (isSettingsVisible) {
            settingsCard.setVisibility(View.GONE);
            isSettingsVisible = false;
        }

        handler.removeCallbacks(hideMenuRunnable);
        chapterListCard.setVisibility(View.VISIBLE);
        chapterListCard.setAlpha(0f);
        chapterListCard.setTranslationY(chapterListCard.getHeight());

        chapterListCard.animate()
                .alpha(1f)
                .translationY(0)
                .start();

        int scrollPosition = isLatestFirst ? currentChapterIndex : (chapterList.size() - 1 - currentChapterIndex);
        rvChapters.scrollToPosition(scrollPosition);
    }

    private void hideChapterList() {
        chapterListCard.animate()
                .alpha(0f)
                .translationY(chapterListCard.getHeight())
                .withEndAction(() -> {
                    chapterListCard.setVisibility(View.GONE);
                    isChapterListVisible = false;
                })
                .start();
    }

    // ==================== SETTINGS METHODS ====================

    private void toggleSettings() {
        if (isSettingsVisible) {
            hideSettings();
        } else {
            showSettings();
        }
        isSettingsVisible = !isSettingsVisible;
    }

    private void showSettings() {
        // Hide chapter list if visible
        if (isChapterListVisible) {
            chapterListCard.setVisibility(View.GONE);
            isChapterListVisible = false;
        }

        handler.removeCallbacks(hideMenuRunnable);
        settingsCard.setVisibility(View.VISIBLE);
        settingsCard.setAlpha(0f);
        settingsCard.setTranslationY(settingsCard.getHeight());

        settingsCard.animate()
                .alpha(1f)
                .translationY(0)
                .start();
    }

    private void hideSettings() {
        settingsCard.animate()
                .alpha(0f)
                .translationY(settingsCard.getHeight())
                .withEndAction(() -> {
                    settingsCard.setVisibility(View.GONE);
                    isSettingsVisible = false;
                    hideMenu();
                    isMenuVisible = false;
                })
                .start();
    }

    private void setBrightness(int brightness) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / 255.0f;
        getWindow().setAttributes(layoutParams);
    }

    private void changeBackgroundColor(int color) {
        currentBackgroundColor = color;
        mainLayout.setBackgroundColor(color);
        scrollViewContent.setBackgroundColor(color);

        // Adjust text color based on background
        int textColor;
        if (color == 0xFF000000 || color == 0xFF5F639D) {
            textColor = 0xFFFFFFFF; // White text for dark backgrounds
        } else {
            textColor = 0xFF000000; // Black text for light backgrounds
        }
        tvContent.setTextColor(textColor);

        saveReadingSettings();
    }

    private void saveReadingSettings() {
        getSharedPreferences("ReadingSettings", MODE_PRIVATE)
                .edit()
                .putFloat("textSize", currentTextSize)
                .putInt("backgroundColor", currentBackgroundColor)
                .apply();
    }

    private void loadReadingSettings() {
        android.content.SharedPreferences prefs = getSharedPreferences("ReadingSettings", MODE_PRIVATE);

        // Load saved settings hoặc sử dụng giá trị mặc định dựa trên màn hình
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;

        float defaultTextSize;
        if (screenWidthDp >= 600) {
            defaultTextSize = 20f;
        } else if (screenWidthDp >= 400) {
            defaultTextSize = 18f;
        } else {
            defaultTextSize = 16f;
        }

        currentTextSize = prefs.getFloat("textSize", defaultTextSize);
        currentBackgroundColor = prefs.getInt("backgroundColor", 0xFFFFFFFF);

        tvContent.setTextSize(currentTextSize);
        textSize.setText(String.valueOf((int) currentTextSize));
        changeBackgroundColor(currentBackgroundColor);
    }

    // ==================== FIREBASE METHODS ====================

    private void saveReadingProgress(int chapterIndex) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || comicTitle == null || chapterList == null || chapterIndex >= chapterList.size()) {
            return;
        }
        String chapterName = chapterList.get(chapterIndex).getChapter();
        String chapterNumber = chapterName.replace("Chương ", "").trim();
        Map<String, Object> progressData = new HashMap<>();
        progressData.put("lastChapterIndex", chapterIndex);
        progressData.put("lastChapterName", chapterNumber);
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
}