package com.example.appdonghua.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.appdonghua.Activity.RankingActivity;
import com.example.appdonghua.Activity.SearchActivity;
import com.example.appdonghua.Adapter.CarouselAdapter;
import com.example.appdonghua.Adapter.CellAdapter;
import com.example.appdonghua.Adapter.DateAdapter;
import com.example.appdonghua.Adapter.NoveListAdapter;
import com.example.appdonghua.Model.Carousel;
import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.Model.Date;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final long AUTO_SCROLL_DELAY = 3000;
    private static final String TAG = "HomeFragment";

    // --- Firebase ---
    private FirebaseFirestore db;

    // --- Views ---
    private ViewPager2 carousel;
    private ImageButton search_Button, ranking_Button;
    private RecyclerView recyclerView, hotnovelScrollView, dateViews, comicsByDateRecyclerView;

    // --- Adapters ---
    private CarouselAdapter carouselAdapter;
    private CellAdapter recommendedAdapter;
    private NoveListAdapter hotNovelAdapter;
    private DateAdapter datebuttonAdapter;
    private CellAdapter comicsByDayAdapter;

    // --- Data Lists ---
    private List<Carousel> carouselItems = new ArrayList<>();

    // --- Carousel Auto Scroll ---
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        setupEmptyAdapters();
        fetchDataFromFirestore();

        return view;
    }

    private void initView(View v) {
        carousel = v.findViewById(R.id.carousel);
        search_Button = v.findViewById(R.id.search_Button);
        recyclerView = v.findViewById(R.id.recyclerView);
        hotnovelScrollView = v.findViewById(R.id.scollView);
        dateViews = v.findViewById(R.id.date_Button);
        ranking_Button = v.findViewById(R.id.ranking_Button);
        comicsByDateRecyclerView = v.findViewById(R.id.comicsByDateRecyclerView);

        search_Button.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        ranking_Button.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RankingActivity.class);
            startActivity(intent);
        });
    }

    /**
     * SỬA LỖI: Khởi tạo đúng adapter cho carousel
     */
    private void setupEmptyAdapters() {
        // Carousel - SỬA LỖI: Dùng CarouselAdapter thay vì NoveListAdapter
        carouselAdapter = new CarouselAdapter(new ArrayList<>());
        carousel.setAdapter(carouselAdapter);

        // Recommended (Đề xuất)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recommendedAdapter = new CellAdapter(new ArrayList<>());
        recyclerView.setAdapter(recommendedAdapter);

        // Hot Novel
        LinearLayoutManager hotNovelLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        hotnovelScrollView.setLayoutManager(hotNovelLayoutManager);
        hotNovelAdapter = new NoveListAdapter(new ArrayList<>());
        hotnovelScrollView.setAdapter(hotNovelAdapter);

        // Comics by Date
        LinearLayoutManager comicsByDayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        comicsByDateRecyclerView.setLayoutManager(comicsByDayLayoutManager);
        comicsByDayAdapter = new CellAdapter(new ArrayList<>());
        comicsByDateRecyclerView.setAdapter(comicsByDayAdapter);
    }

    private void fetchDataFromFirestore() {
        fetchCarouselData();
        fetchRecommendedData();
        fetchHotNovelsData();
        initDateButton();
    }

    // --- 1. Tải dữ liệu cho CAROUSEL ---
    private void fetchCarouselData() {
        db.collection("stories")
                .whereEqualTo("featured", true)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    carouselItems.clear();
                    List<NovelList> novelLists = new ArrayList<>(); // THÊM: Tạo novelLists cho carousel

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        carouselItems.add(new Carousel(story.getCoverImageUrl()));

                        // THÊM: Tạo NovelList để truyền đầy đủ thông tin
                        String genre = (story.getGenres() != null && !story.getGenres().isEmpty()) ? story.getGenres().get(0) : "Khác";
                        NovelList novelList = new NovelList(
                                story.getCoverImageUrl(),
                                story.getTitle(),
                                story.getViewCount(),
                                genre,
                                story.getChapter(),
                                story.getAuthor(),
                                story.getDescription()
                        );
                        novelLists.add(novelList);
                    }

                    // SỬA: Cập nhật adapter với cả carouselItems và novelLists
                    carouselAdapter = new CarouselAdapter(carouselItems, novelLists);
                    carousel.setAdapter(carouselAdapter);
                    setupCarouselScroll();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting carousel data", e);
                });
    }

    // --- 2. Tải dữ liệu cho RECOMMENDED (Đề xuất - Grid 3 cột) ---
    private void fetchRecommendedData() {
        db.collection("stories")
                .limit(6)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Cell> items = new ArrayList<>();
                    ArrayList<NovelList> novelLists = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        String genre = (story.getGenres() != null && !story.getGenres().isEmpty()) ? story.getGenres().get(0) : "Khác";
                        NovelList novelList = new NovelList(
                                story.getCoverImageUrl(),
                                story.getTitle(),
                                story.getViewCount(),
                                genre,
                                story.getChapter(),
                                story.getAuthor(),
                                story.getDescription()
                        );
                        novelLists.add(novelList);
                        items.add(new Cell(story.getCoverImageUrl(), story.getTitle()));
                    }
                    recommendedAdapter = new CellAdapter(items, novelLists);
                    recyclerView.setAdapter(recommendedAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting recommended data", e);
                });
    }

    // --- 3. Tải dữ liệu cho HOT NOVELS (Truyện hot) ---
    private void fetchHotNovelsData() {
        db.collection("stories")
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<NovelList> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        String genre = (story.getGenres() != null && !story.getGenres().isEmpty()) ? story.getGenres().get(0) : "Khác";
                        items.add(new NovelList(
                                story.getCoverImageUrl(),
                                story.getTitle(),
                                story.getViewCount(),
                                genre,
                                story.getChapter(),
                                story.getAuthor(),
                                story.getDescription()
                        ));
                    }
                    hotNovelAdapter = new NoveListAdapter(items);
                    hotnovelScrollView.setAdapter(hotNovelAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting hot novel data", e);
                });
    }

    // --- 4. Tải dữ liệu cho LỊCH RA TRUYỆN (Comics by Date) ---
    private void initDateButton() {
        ArrayList<Date> items = new ArrayList<>();
        items.add(new Date("Mon"));
        items.add(new Date("Tue"));
        items.add(new Date("Wed"));
        items.add(new Date("Thu"));
        items.add(new Date("Fri"));
        items.add(new Date("Sat"));
        items.add(new Date("Sun"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateViews.setLayoutManager(layoutManager);
        datebuttonAdapter = new DateAdapter(items);
        dateViews.setAdapter(datebuttonAdapter);

        datebuttonAdapter.setOnItemClickListener(new DateAdapter.OnItemClickListener() {
            @Override
            public void onDateClick(Date date, int position) {
                Log.d(TAG, "Date clicked: " + date.getName());
                fetchComicsByDay(date.getName());
            }
        });

        // Tải dữ liệu cho ngày đầu tiên (Mon) làm mặc định
        if (!items.isEmpty()) {
            fetchComicsByDay(items.get(0).getName());
        }
    }

    private void fetchComicsByDay(String day) {
        db.collection("stories")
                .whereEqualTo("releaseDay", day)
                .limit(6)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Cell> items = new ArrayList<>();
                    ArrayList<NovelList> novelLists = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        String genre = (story.getGenres() != null && !story.getGenres().isEmpty()) ? story.getGenres().get(0) : "Khác";
                        NovelList novelList = new NovelList(
                                story.getCoverImageUrl(),
                                story.getTitle(),
                                story.getViewCount(),
                                genre,
                                story.getChapter(),
                                story.getAuthor(),
                                story.getDescription()
                        );
                        novelLists.add(novelList);
                        items.add(new Cell(story.getCoverImageUrl(), story.getTitle()));
                    }
                    comicsByDayAdapter = new CellAdapter(items, novelLists);
                    comicsByDateRecyclerView.setAdapter(comicsByDayAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting comics for day: " + day, e);
                });
    }

    // --- Các hàm xử lý Carousel Auto-Scroll ---
    private void setupCarouselScroll() {
        if (carouselAdapter == null || carouselAdapter.getItemCount() == 0) {
            return; // Không setup scroll nếu không có dữ liệu
        }

        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (carousel != null && carouselAdapter != null && carouselAdapter.getItemCount() > 0) {
                    int currentItem = carousel.getCurrentItem();
                    int nextItem = (currentItem + 1) % carouselAdapter.getItemCount();
                    carousel.setCurrentItem(nextItem, true);
                    autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
                }
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);

        carousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    stopAutoScroll();
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    startAutoScroll();
                }
            }
        });
    }

    private void startAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
    }

    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoScroll();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
    }
}