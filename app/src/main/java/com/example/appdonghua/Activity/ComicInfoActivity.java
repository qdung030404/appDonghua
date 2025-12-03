package com.example.appdonghua.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComicInfoActivity extends AppCompatActivity {
    private ImageButton backButton, favoriteButton, expandButton;
    private ImageView imageCover;
    private TextView texTitle, tvViews, author, status, description, chapterCount, tvGenres;
    private RecyclerView rvChapters;
    private Button viewAllButton, readButton;
    // Data
    private ChapterAdapter chapterAdapter;
    private List<Chapter> allChapters;
    private boolean showingAllChapters = false;
    private static final int INITIAL_CHAPTER_COUNT = 5;
    private boolean isExpanded = false;
    private boolean isFavorite = false;

    // Variables nháº­n tá»« Intent
    private String strTitle, strImage, strAuthor, strDescription;
    private long lViews, strChapterCount;
    private ArrayList<String> genres;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int lastReadChapterIndex = -1;
    private String lastReadChapterName = "";
    private NotificationHelper notificationHelper;

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

        // Khá»Ÿi táº¡o Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        notificationHelper = new NotificationHelper(this);
        initViews();
        getIntentData();
        setupUI();
        setupListener();
        setupChapter();

        addToHistory();
        checkSaved();
        loadReadingProgress();
    }
    private void initViews(){
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.FavoriteButton);
        expandButton = findViewById(R.id.expandButton);
        imageCover = findViewById(R.id.imageCover);
        texTitle = findViewById(R.id.texTitle);
        tvViews = findViewById(R.id.Views);
        author = findViewById(R.id.author);
        status = findViewById(R.id.status);
        description = findViewById(R.id.description);
        chapterCount = findViewById(R.id.chapterCount);
        rvChapters = findViewById(R.id.rvChapters);
        viewAllButton = findViewById(R.id.viewAllButton);
        readButton = findViewById(R.id.readButton);
        tvGenres = findViewById(R.id.tvGenres);
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
    private void loadReadingProgress() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || strTitle == null) {
            readButton.setText("Äá»c truyá»‡n");
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .collection("history").document(strTitle)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Kiá»ƒm tra xem cÃ³ field lastChapterIndex khÃ´ng
                        if (documentSnapshot.contains("lastChapterIndex")) {
                            Long chapterIndex = documentSnapshot.getLong("lastChapterIndex");
                            if (chapterIndex != null) {
                                lastReadChapterIndex = chapterIndex.intValue();
                            }

                            lastReadChapterName = documentSnapshot.getString("lastChapterName");

                            // Cáº­p nháº­t text button
                            if (lastReadChapterIndex > 0) {
                                readButton.setText("Tiáº¿p tá»¥c Ä‘á»c");
                            } else {
                                readButton.setText("Äá»c truyá»‡n");
                            }
                        } else {
                            readButton.setText("Äá»c truyá»‡n");
                        }
                    } else {
                        readButton.setText("Äá»c truyá»‡n");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error loading progress", e);
                    readButton.setText("Äá»c truyá»‡n");
                });
    }
    private void setupChapter(){
        // Sá»¬A: Cáº¥u hÃ¬nh RecyclerView
        rvChapters.setLayoutManager(new LinearLayoutManager(this));

        // Táº¯t nested scrolling Ä‘á»ƒ cuá»™n mÆ°á»£t mÃ  trong NestedScrollView
        rvChapters.setNestedScrollingEnabled(false);

        int count = 20; // Máº·c Ä‘á»‹nh
        if (strChapterCount > 0) {
            count = (int) strChapterCount; // Ã‰p kiá»ƒu long sang int
        }
        allChapters = generateChapter(count);
        showInitialChapters();

        // Show "View All" button if there are more than 5 chapters
        if (count > INITIAL_CHAPTER_COUNT) {
            viewAllButton.setVisibility(View.VISIBLE);
        }
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
    private List<Chapter> generateChapter(int count){
        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            chapters.add(new Chapter(String.valueOf(i + 1), i + 100));
        }
        return chapters;
    }
    private void setupUI() {
        texTitle.setText(strTitle);
        author.setText("TÃ¡c giáº£: " + (strAuthor != null ? strAuthor : "Äang cáº­p nháº­t"));
        tvViews.setText(formatNumber(lViews) + " Views");
        chapterCount.setText(strChapterCount  + " chÆ°Æ¡ng");
        if (genres != null && !genres.isEmpty()) {
            String genresText = String.join(", ", genres);
            tvGenres.setText(genresText);
        } else {
            tvGenres.setText("ChÆ°a phÃ¢n loáº¡i");
        }
        if (strDescription != null && !strDescription.isEmpty()) {
            description.setText(strDescription);
        } else {
            description.setText("Äang cáº­p nháº­t mÃ´ táº£ cho truyá»‡n nÃ y...");
        }
        if (strImage != null) {
            Glide.with(this).load(strImage).into(imageCover);
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

                // Náº¿u cÃ³ progress thÃ¬ tiáº¿p tá»¥c tá»« Ä‘Ã³
                if (lastReadChapterIndex >= 0 && lastReadChapterIndex < allChapters.size()) {
                    chapterIndex = lastReadChapterIndex;
                }

                Chapter chapter = allChapters.get(chapterIndex);
                openReadActivity(chapter, chapterIndex);
            } else {
                Toast.makeText(ComicInfoActivity.this, "ChÆ°a cÃ³ chÆ°Æ¡ng Ä‘á»ƒ Ä‘á»c", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                .set(historyData);
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
                    Toast.makeText(ComicInfoActivity.this, "ÄÃ£ xÃ³a khá»i danh sÃ¡ch yÃªu thÃ­ch", Toast.LENGTH_SHORT).show();
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
    public static String formatNumber(long number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fk", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }
    private void sendReminderNotification() {
        notificationHelper.sendNotification(
                "ÄÃ£ lÆ°u",
                "Truyá»‡n Ä‘Ã£ Ä‘Æ°á»£c lÆ°u vÃ o tá»§ sÃ¡ch"
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadReadingProgress();
    }
}