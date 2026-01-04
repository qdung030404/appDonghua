package com.example.appdonghua.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Adapter.SearchHistoryAdapter;
import com.example.appdonghua.Adapter.TopSearchAdapter;
import com.example.appdonghua.Adapter.StoryAdapter;
import com.example.appdonghua.Model.SearchHistory;
import com.example.appdonghua.Model.TopSearch;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "SearchPrefs";
    private static final String KEY_SEARCH_HISTORY = "searchHistory";
    private static final int MAX_HISTORY_SIZE = 10;

    private EditText searchEditText;
    private ImageButton searchButton, clearButton, backButton, clearAllButton;
    private RecyclerView historyRecyclerView, topSearchRecyclerView, searchResultRecyclerView;
    private SearchHistoryAdapter historyAdapter;
    private TopSearchAdapter topSearchAdapter;
    private StoryAdapter searchResultAdapter;
    private ArrayList<SearchHistory> historyList;
    private ArrayList<TopSearch> topSearchList;
    private ArrayList<Story> searchResultList;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView noResultTextView;

    // ==================== LIFECYCLE METHODS ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        init();
        setupSharedPreferences();
        searchHistory();
        setupTopSearchData();
        setupRecyclerViews();
        setupListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveHistory();
    }

    // ==================== INITIALIZATION METHODS ====================

    private void init(){
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        backButton = findViewById(R.id.backButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        topSearchRecyclerView = findViewById(R.id.topSearchRecyclerView);
        searchResultRecyclerView = findViewById(R.id.searchResultRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noResultTextView = findViewById(R.id.noResultTextView);
    }

    private void setupSharedPreferences(){
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupTopSearchData(){
        topSearchList = new ArrayList<>();
        loadTopSearchFromFirestore();
    }

    private void setupRecyclerViews(){
        // Search result list
        searchResultList = new ArrayList<>();
        searchResultAdapter = new StoryAdapter(searchResultList);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultRecyclerView.setAdapter(searchResultAdapter);
        searchResultRecyclerView.setVisibility(View.GONE);

        historyAdapter = new SearchHistoryAdapter(historyList, new SearchHistoryAdapter.OnHistoryItemClickListener() {
            @Override
            public void onHistoryItemClick(String query) {
                searchEditText.setText(query);
                performSearch(query);
            }

            @Override
            public void onDeleteButtonClick(int position) {
                deleteItem(position);
            }
        });
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);

        // Top search list
        topSearchAdapter = new TopSearchAdapter(topSearchList, new TopSearchAdapter.OnTopSearchClickListener() {
            @Override
            public void onTopSearchClick(String query) {
                searchEditText.setText(query);
                performSearch(query);
            }
        });
        topSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        topSearchRecyclerView.setAdapter(topSearchAdapter);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        clearButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearButton.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
            showHistoryAndTopSearch();
        });

        clearAllButton.setOnClickListener(v -> clearAllItem());

        searchEditText.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.GONE);
                } else {
                    clearButton.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    showHistoryAndTopSearch();
                }
            }
        });

        searchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String query = searchEditText.getText().toString().trim();
                    if(!query.isEmpty()){
                        performSearch(query);
                    }
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            }
        });
    }

    // ==================== SEARCH HISTORY MANAGEMENT ====================

    private void searchHistory(){
        String json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null);
        historyList = new ArrayList<>();
        if (json != null){
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String title = jsonObject.getString("title");
                    historyList.add(new SearchHistory(title));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                historyList = new ArrayList<>();
            }
        }
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        try {
            for (SearchHistory history : historyList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", history.getTitle());
                jsonArray.put(jsonObject);
            }
            editor.putString(KEY_SEARCH_HISTORY, jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addToSearchHistory(String query){
        for (int i = 0; i < historyList.size(); i++){
            if(historyList.get(i).getTitle().equals(query)){
                historyList.remove(i);
                break;
            }
        }
        historyList.add(0, new SearchHistory(query));
        if (historyList.size() > MAX_HISTORY_SIZE) {
            historyList.remove(historyList.size() - 1);
        }
        saveHistory();
        historyAdapter.notifyDataSetChanged();
    }

    private void deleteItem(int position) {
        if (position >= 0 && position < historyList.size()) {
            historyList.remove(position);
            historyAdapter.notifyItemRemoved(position);
            saveHistory();
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAllItem(){
        if(historyList.isEmpty()){
            Toast.makeText(this, "Lịch sử trống", Toast.LENGTH_SHORT).show();
            return;
        }
        historyList.clear();
        historyAdapter.notifyDataSetChanged();
        saveHistory();
        Toast.makeText(this, "Đã xóa lịch sử tìm kiếm", Toast.LENGTH_SHORT).show();
    }

    // ==================== SEARCH FUNCTIONALITY ====================

    private void performSearch(String query){
        if (query.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        addToSearchHistory(query);
        searchStoriesInFirestore(query);
    }

    private void searchStoriesInFirestore(String query) {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        noResultTextView.setVisibility(View.GONE);
        historyRecyclerView.setVisibility(View.GONE);
        topSearchRecyclerView.setVisibility(View.GONE);
        searchResultRecyclerView.setVisibility(View.GONE);

        searchResultList.clear();

        String searchQuery = query.toLowerCase();

        db.collection("stories")
                .orderBy("title")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    searchResultList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Story story = document.toObject(Story.class);

                        if (story.getTitle() != null &&
                                story.getTitle().toLowerCase().contains(searchQuery)) {
                            incrementSearchCount(document.getId(), story.getTitle());
                            String genre = story.getGenres() != null && !story.getGenres().isEmpty()
                                    ? story.getGenres().get(0) : "Chưa phân loại";

                            searchResultList.add(story);
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                    if (searchResultList.isEmpty()) {
                        noResultTextView.setVisibility(View.VISIBLE);
                        noResultTextView.setText("Không tìm thấy kết quả cho \"" + query + "\"");
                        searchResultRecyclerView.setVisibility(View.GONE);
                    } else {
                        searchResultRecyclerView.setVisibility(View.VISIBLE);
                        searchResultAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Tìm thấy " + searchResultList.size() + " kết quả", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    noResultTextView.setVisibility(View.VISIBLE);
                    noResultTextView.setText("Lỗi khi tìm kiếm. Vui lòng thử lại.");
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ==================== LOAD TOP SEARCH ====================

    private void loadTopSearchFromFirestore() {
        // Lấy top 10 truyện có lượt xem cao nhất
        db.collection("stories")
                .orderBy("search", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    topSearchList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Story story = document.toObject(Story.class);

                        // Tạo TopSearch object với URL ảnh và title
                        TopSearch topSearch = new TopSearch(
                                story.getCoverImageUrl(),
                                story.getTitle()
                        );
                        topSearchList.add(topSearch);
                    }

                    // Cập nhật adapter
                    if (topSearchAdapter != null) {
                        topSearchAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải top search: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ==================== FIREBASE OPERATIONS ====================

    private void incrementSearchCount(String documentId, String title) {
        db.collection("stories")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentSearchCount = documentSnapshot.getLong("search");
                        long newSearchCount = (currentSearchCount != null) ? currentSearchCount + 1 : 1;

                        db.collection("stories")
                                .document(documentId)
                                .update("search", newSearchCount)
                                .addOnSuccessListener(aVoid -> {
                                    android.util.Log.d("SearchActivity",
                                            "Updated search count for '" + title + "': " + newSearchCount);
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("SearchActivity",
                                            "Failed to update search count for '" + title + "': " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("SearchActivity",
                            "Failed to get document for search count update: " + e.getMessage());
                });
    }

    // ==================== UI HELPER METHODS ====================

    private void showHistoryAndTopSearch() {
        searchResultRecyclerView.setVisibility(View.GONE);
        noResultTextView.setVisibility(View.GONE);
        historyRecyclerView.setVisibility(View.VISIBLE);
        topSearchRecyclerView.setVisibility(View.VISIBLE);
    }
}