package com.example.appdonghua.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Adapter.ChapterAdapter;
import com.example.appdonghua.Helper.NotificationHelper;
import com.example.appdonghua.Model.Chapter;
import com.example.appdonghua.R;
import com.example.appdonghua.Utils.ScreenUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ComicInfoActivity extends AppCompatActivity {
    private ImageButton backButton, favoriteButton, expandButton;
    private ImageView imageCover, imageBackground;
    private TextView texTitle, tvViews, author,description, chapterCount, tvGenres;
    private RecyclerView rvChapters;
    private Button viewAllButton, readButton;
    private CollapsingToolbarLayout collapsingToolbar;
    // Data
    private ChapterAdapter chapterAdapter;
    private List<Chapter> allChapters;
    private boolean showingAllChapters = false;
    private static final int INITIAL_CHAPTER_COUNT = 5;
    private boolean isExpanded = false;
    private boolean isFavorite = false;

    private String strTitle, strImage, strAuthor, strDescription;
    private long lViews, strChapterCount;
    private ArrayList<String> genres;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int lastReadChapterIndex = -1;
    private String lastReadChapterName = "";
    private NotificationHelper notificationHelper;

    // ============ LIFECYCLE METHODS ============
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comic_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            if (v != null) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        notificationHelper = new NotificationHelper(this);
        initViews();
        responsiveLayout();
        getIntentData();
        setupUI();
        setupListener();
        setupChapter();
        addToHistory();
        checkSaved();
        loadReadingProgress();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReadingProgress();
    }

    // ============ INITIALIZATION METHODS ============
    private void initViews(){
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.FavoriteButton);
        expandButton = findViewById(R.id.expandButton);
        imageCover = findViewById(R.id.imageCover);
        imageBackground = findViewById(R.id.imageBackground);
        texTitle = findViewById(R.id.texTitle);
        tvViews = findViewById(R.id.Views);
        author = findViewById(R.id.author);
        description = findViewById(R.id.description);
        chapterCount = findViewById(R.id.chapterCount);
        rvChapters = findViewById(R.id.rvChapters);
        viewAllButton = findViewById(R.id.viewAllButton);
        readButton = findViewById(R.id.readButton);
        tvGenres = findViewById(R.id.tvGenres);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            strTitle = intent.getStringExtra("TITLE");
            strImage = intent.getStringExtra("IMAGE_URL");
            strAuthor = intent.getStringExtra("AUTHOR");
            strDescription = intent.getStringExtra("DESCRIPTION");
            strChapterCount = intent.getLongExtra("CHAPTER", 0);
            lViews = intent.getLongExtra("VIEWS", 0);
            genres = intent.getStringArrayListExtra("GENRES");
        }
    }

    private void responsiveLayout(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        float screenHeightPx = displayMetrics.heightPixels;
        ScreenUtils.TextSize textSize = ScreenUtils.calculateTextSize(this);

        ViewGroup.LayoutParams  collapsingParams = collapsingToolbar.getLayoutParams();
        if (screenWidthDp >= 600) {
            collapsingParams.height = (int) (screenHeightPx * 0.35);
        } else if (screenWidthDp >= 400) {
            collapsingParams.height = (int) (screenHeightPx * 0.32);
        }else {
            collapsingParams.height = (int) (screenHeightPx * 0.30);
        }
        collapsingToolbar.setLayoutParams(collapsingParams);

        ViewGroup.LayoutParams  imageParams = imageCover.getLayoutParams();
        if (screenWidthDp >= 600) {
            imageParams.width = ScreenUtils.dpToPx(this, 150);
            imageParams.height = ScreenUtils.dpToPx(this, 220);
        }else if (screenWidthDp >= 400) {
            imageParams.width = ScreenUtils.dpToPx(this, 130);
            imageParams.height = ScreenUtils.dpToPx(this, 190);
        } else if (screenWidthDp >= 360) {
            imageParams.width = ScreenUtils.dpToPx(this, 120);
            imageParams.height = ScreenUtils.dpToPx(this, 180);
        } else {
            imageParams.width = ScreenUtils.dpToPx(this, 110);
            imageParams.height = ScreenUtils.dpToPx(this, 165);
        }
        imageCover.setLayoutParams(imageParams);
        if (screenWidthDp >= 600) {
            texTitle.setTextSize(28);
            description.setTextSize(14);
            tvViews.setTextSize(14);
        } else if (screenWidthDp >= 400) {
            texTitle.setTextSize(24);
            description.setTextSize(12);
            tvViews.setTextSize(12);
        } else if (screenWidthDp >= 360) {
            texTitle.setTextSize(22);
            description.setTextSize(11);
            tvViews.setTextSize(11);
        } else {
            texTitle.setTextSize(20);
            description.setTextSize(10);
            tvViews.setTextSize(10);
        }

        // Author, Description, Genres, Chapter Count
        author.setTextSize(textSize.subtitle);
        tvGenres.setTextSize(textSize.subtitle);
        description.setTextSize(textSize.body);
        chapterCount.setTextSize(textSize.body);

        ViewGroup.LayoutParams readButtonParams = readButton.getLayoutParams();
        if (screenWidthDp >= 600) {
            readButtonParams.width = ScreenUtils.dpToPx(this, 330);
            readButtonParams.height = ScreenUtils.dpToPx(this, 60);
            readButton.setTextSize(18);
        } else if (screenWidthDp >= 400) {
            readButtonParams.width = ScreenUtils.dpToPx(this, 300);
            readButtonParams.height = ScreenUtils.dpToPx(this, 55);
            readButton.setTextSize(16);
        } else {
            readButtonParams.width = ScreenUtils.dpToPx(this, 270);
            readButtonParams.height = ScreenUtils.dpToPx(this, 50);
            readButton.setTextSize(15);
        }
        readButton.setLayoutParams(readButtonParams);

        int iconSize;
        if (screenWidthDp >= 600) {
            iconSize = ScreenUtils.dpToPx(this, 50);
        } else if (screenWidthDp >= 400) {
            iconSize = ScreenUtils.dpToPx(this, 45);
        } else {
            iconSize = ScreenUtils.dpToPx(this, 40);
        }

        ViewGroup.LayoutParams backParams = backButton.getLayoutParams();
        backParams.width = iconSize;
        backParams.height = iconSize;
        backButton.setLayoutParams(backParams);

        ViewGroup.LayoutParams favParams = favoriteButton.getLayoutParams();
        favParams.width = iconSize;
        favParams.height = iconSize;
        favoriteButton.setLayoutParams(favParams);

        ViewGroup.LayoutParams expandParams = expandButton.getLayoutParams();
        expandParams.width = ScreenUtils.dpToPx(this, 30);
        expandParams.height = ScreenUtils.dpToPx(this, 30);
        expandButton.setLayoutParams(expandParams);
    }

    // ============ SETUP METHODS ============
    private void setupUI() {
        texTitle.setText(strTitle);
        author.setText("tác giả: " + (strAuthor != null ? strAuthor : "Đang cập nhật..."));
        tvViews.setText(formatNumber(lViews) + " Views");
        chapterCount.setText(strChapterCount  + " Chương");
        if (genres != null && !genres.isEmpty()) {
            String genresText = String.join(", ", genres);
            tvGenres.setText(" Thể loại: " + genresText);
        } else {
            tvGenres.setText("Đang cập nhật...");
        }
        if (strDescription != null && !strDescription.isEmpty()) {
            description.setText(strDescription);
        } else {
            description.setText("Đang cập nhật...");
        }
        if (strImage != null) {
            Glide.with(this).load(strImage).into(imageCover);
            Glide.with(this)
                    .load(strImage)
                    .transform(new BlurTransformation(10, 2)) // blur radius 25, sampling 3
                    .into(imageBackground);
        }
    }

    private void setupListener(){
        backButton.setOnClickListener(v -> finish());

        favoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.ic_bookmark_solid);
                sendReminderNotification();
                addToSave();
            } else {
                favoriteButton.setImageResource(R.drawable.ic_bookmark_regular);
                unSave();
            }
        });

        expandButton.setOnClickListener(v -> toggle());
        viewAllButton.setOnClickListener(v -> {
            showAllChapters();
        });
        readButton.setOnClickListener(v -> {
            if (allChapters != null && !allChapters.isEmpty()) {
                int chapterIndex = 0;

                if (lastReadChapterIndex >= 0 && lastReadChapterIndex < allChapters.size()) {
                    chapterIndex = lastReadChapterIndex;
                }

                Chapter chapter = allChapters.get(chapterIndex);
                openReadActivity(chapter, chapterIndex);
            }
        });
    }

    private void setupChapter(){

        rvChapters.setLayoutManager(new LinearLayoutManager(this));

        rvChapters.setNestedScrollingEnabled(false);

        int count = 5;
        if (strChapterCount > 0) {
            count = (int) strChapterCount;
        }
        allChapters = generateChapter(count);
        showInitialChapters();

        // Show "View All" button if there are more than 5 chapters
        if (count > INITIAL_CHAPTER_COUNT) {
            viewAllButton.setVisibility(View.VISIBLE);
        }
    }

    // ============ CHAPTER MANAGEMENT METHODS ============
    private List<Chapter> generateChapter(int count){
        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i <= count; i++) {
            chapters.add(new Chapter( "Chương " + (i+1), i + 100));
        }
        return chapters;
    }

    private void showInitialChapters() {
        List<Chapter> initialChapters;
        if (allChapters.size() > INITIAL_CHAPTER_COUNT) {
            initialChapters = allChapters.subList(0, INITIAL_CHAPTER_COUNT);
        } else {
            initialChapters = allChapters;
        }

        chapterAdapter = new ChapterAdapter(initialChapters, (chapter, position) -> {
            int actualPosition = allChapters.indexOf(chapter);
            openReadActivity(chapter, actualPosition);
        });
        rvChapters.setAdapter(chapterAdapter);
        showingAllChapters = false;
    }

    private void showAllChapters() {
        chapterAdapter = new ChapterAdapter(allChapters, (chapter, position) -> {
            openReadActivity(chapter, position);
        });
        rvChapters.setAdapter(chapterAdapter);
        showingAllChapters = true;
        viewAllButton.setVisibility(View.GONE); // Hide button when showing all
    }

    // ============ FIREBASE METHODS ============
    private void loadReadingProgress() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || strTitle == null) {
            readButton.setText("Đọc truyện");
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .collection("history").document(strTitle)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("lastChapterIndex")) {
                            Long chapterIndex = documentSnapshot.getLong("lastChapterIndex");
                            if (chapterIndex != null) {
                                lastReadChapterIndex = chapterIndex.intValue();
                            }

                            lastReadChapterName = documentSnapshot.getString("lastChapterName");

                            if (lastReadChapterIndex > 0) {
                                readButton.setText("Tiếp tục đọc Ch. " + lastReadChapterName);
                            } else {
                                readButton.setText("Đọc truyện");
                            }
                        } else {
                            readButton.setText("Đọc truyện");
                        }
                    } else {
                        readButton.setText("Đọc truyện");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error loading progress", e);
                    readButton.setText("Đọc truyện");
                });
    }

    private void addToHistory() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || strTitle == null) return;

        Map<String, Object> historyData = new HashMap<>();
        historyData.put("title", strTitle);
        historyData.put("coverImageUrl", strImage);
        historyData.put("author", strAuthor);
        historyData.put("description", strDescription);
        historyData.put("viewCount", lViews);
        historyData.put("chapterCount", strChapterCount);
        historyData.put("genre", genres);
        historyData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(currentUser.getUid())
                .collection("history").document(strTitle)
                .update(historyData)
                .addOnFailureListener(e -> {
                    db.collection("users").document(currentUser.getUid())
                            .collection("history").document(strTitle)
                            .set(historyData);
                });
    }

    private void addToSave(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || strTitle == null) return;
        Map<String, Object> saveData = new HashMap<>();
        saveData.put("title", strTitle);
        saveData.put("coverImageUrl", strImage);
        saveData.put("author", strAuthor);
        saveData.put("description", strDescription);
        saveData.put("viewCount", lViews);
        saveData.put("chapterCount", strChapterCount);
        saveData.put("genre", genres);
        saveData.put("timestamp", FieldValue.serverTimestamp());
        db.collection("users").document(currentUser.getUid())
                .collection("save").document(strTitle).set(saveData);

    }

    private void unSave(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || strTitle == null) return;
        db.collection("users").document(currentUser.getUid())
                .collection("save").document(strTitle).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("firestore", "DocumentSnapshot successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.w("firestore", "Error deleting document", e);
                });
    }

    private void checkSaved(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || strTitle == null) return;
        db.collection("users").document(currentUser.getUid())
                .collection("save").document(strTitle).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isFavorite = true;
                        favoriteButton.setImageResource(R.drawable.ic_bookmark_solid);
                    } else {
                        isFavorite = false;
                        favoriteButton.setImageResource(R.drawable.ic_bookmark_regular);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error checking favorite status", e);
                });
    }

    // ============ UI INTERACTION METHODS ============
    private void toggle(){
        isExpanded = !isExpanded;
        if (isExpanded) {
            description.setMaxLines(3);
            expandButton.setRotation(0);
        } else {
            description.setMaxLines(Integer.MAX_VALUE);
            expandButton.setRotation(180);
        }
    }

    private void openReadActivity(Chapter chapter, int position){
        Intent intent = new Intent(ComicInfoActivity.this, ReadActivity.class);
        intent.putExtra("CHAPTER_INDEX", position);
        intent.putExtra("CHAPTER_NAME", chapter.getChapter());
        intent.putExtra("TOTAL_CHAPTERS", allChapters.size());
        intent.putExtra("COMIC_TITLE", strTitle);
        startActivity(intent);
    }

    private void sendReminderNotification() {
        notificationHelper.sendNotification(
                "Lưu thành công",
                "Truyện đã được lưu vào tủ sách"
        );
    }

    // ============ UTILITY METHODS ============
    public static String formatNumber(long number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fk", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }
}