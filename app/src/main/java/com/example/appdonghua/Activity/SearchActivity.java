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
import com.example.appdonghua.Model.SearchHistory;
import com.example.appdonghua.Model.TopSearch;
import com.example.appdonghua.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private ImageButton searchButton, clearButton, backButton, clearAllButton;
    private RecyclerView historyRecyclerView, topSearchRecyclerView;
    private SearchHistoryAdapter historyAdapter;
    private TopSearchAdapter topSearchAdapter;
    private ArrayList<SearchHistory> historyList;
    private ArrayList<TopSearch> topSearchList;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SearchPrefs";
    private static final String KEY_SEARCH_HISTORY = "searchHistory";
    private static final int MAX_HISTORY_SIZE = 10;




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
        init();
        setupSharedPreferences();
        searchHistory();
        setupTopSearchData();
        setupRecyclerViews();
        setupListeners();
    }
    private void init(){
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        backButton = findViewById(R.id.backButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        topSearchRecyclerView = findViewById(R.id.topSearchRecyclerView);

    }
    private void setupSharedPreferences(){
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    }
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
    private void setupTopSearchData(){
        topSearchList = new ArrayList<>();
        topSearchList.add(new TopSearch(R.drawable.img_2, "Thế Giới Hoàn Mỹ"));
        topSearchList.add(new TopSearch(R.drawable.img_2, "Thế Giới Hoàn Mỹ"));
        topSearchList.add(new TopSearch(R.drawable.img_2, "Thế Giới Hoàn Mỹ"));
        topSearchList.add(new TopSearch(R.drawable.img_2, "Thế Giới Hoàn Mỹ"));
        topSearchList.add(new TopSearch(R.drawable.img_2, "Thế Giới Hoàn Mỹ"));
    }
    private void setupRecyclerViews(){
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
        });
        clearAllButton.setOnClickListener(v -> cLearALLItem());
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
                }
            }
        });
        searchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String query = searchEditText.getText().toString();
                    if(!query.isEmpty()){
                        performSearch(query);
                    }return true;

                }
                return false;
            }


        });
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!query.isEmpty()) {
                performSearch(query);
            }
        });
    }
    private void performSearch(String query){
        if (query.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        }
        addToSearchHistory(query);
        Toast.makeText(this, "Đang tìm kiếm: " + query, Toast.LENGTH_SHORT).show();

    }
    private void addToSearchHistory(String query){
        for (int i = 0; i < historyList.size();i++){
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
    private void cLearALLItem(){
        if(historyList.isEmpty()){
            Toast.makeText(this, "Lịch sử trống", Toast.LENGTH_SHORT).show();
            return;
        }
        historyList.clear();
        historyAdapter.notifyDataSetChanged();
        saveHistory();
        Toast.makeText(this, "Đã xóa lịch sử tìm kiếm", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveHistory();
    }
}