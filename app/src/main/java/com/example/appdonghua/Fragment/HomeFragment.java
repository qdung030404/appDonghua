package com.example.appdonghua.Fragment;

import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import jp.wasabeef.glide.transformations.BlurTransformation;
import com.example.appdonghua.Activity.RankingActivity;
import com.example.appdonghua.Activity.SearchActivity;
import com.example.appdonghua.Adapter.CarouselAdapter;
import com.example.appdonghua.Adapter.CellAdapter;
import com.example.appdonghua.Adapter.DateButtonAdapter;
import com.example.appdonghua.Adapter.StoryAdapter;
import com.example.appdonghua.Model.Carousel;
import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.Model.Date;
import com.example.appdonghua.Model.Story;  // ✅ THAY ĐỔI
import com.example.appdonghua.R;
import com.example.appdonghua.Utils.ScreenUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;

public class HomeFragment extends Fragment {
    private static final long AUTO_SCROLL_DELAY = 3000;
    private static final String TAG = "HomeFragment";

    // --- Firebase ---
    private FirebaseFirestore db;

    // --- Views ---
    private ViewPager2 carousel;
    private ImageButton search_Button, ranking_Button;
    private RecyclerView recyclerView, hotnovelScrollView, dateViews, comicsByDateRecyclerView;
    private ImageView imageBackgroundCarousel;

    // --- Adapters ---
    private CarouselAdapter carouselAdapter;
    private CellAdapter recommendedAdapter;
    private StoryAdapter hotNovelAdapter;
    private DateButtonAdapter datebuttonAdapter;
    private CellAdapter comicsByDayAdapter;

    // --- Data Lists ---
    private List<Carousel> carouselItems = new ArrayList<>();

    // --- Carousel Auto Scroll ---
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    // ==================== LIFECYCLE METHODS ====================

    public HomeFragment() {}

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

        setupRecyclerView();
        setupCarouselResponsive();
        setupEmptyAdapters();
        fetchCarouselData();
        fetchRecommendedData();
        fetchHotNovelsData();
        initDateButton();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoScroll();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
    }

    // ==================== INITIALIZATION METHODS ====================

    private void initView(View v) {
        carousel = v.findViewById(R.id.carousel);
        imageBackgroundCarousel = v.findViewById(R.id.imageBackgroundCarousel);
        search_Button = v.findViewById(R.id.search_Button);
        dateViews = v.findViewById(R.id.date_Button);
        ranking_Button = v.findViewById(R.id.ranking_Button);
        recyclerView = v.findViewById(R.id.recyclerView);
        hotnovelScrollView = v.findViewById(R.id.scollView);
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

    private void setupEmptyAdapters() {
        // Carousel
        carouselAdapter = new CarouselAdapter(new ArrayList<>());
        carousel.setAdapter(carouselAdapter);

        // Hot Novel
        LinearLayoutManager hotNovelLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        hotnovelScrollView.setLayoutManager(hotNovelLayoutManager);
        hotNovelAdapter = new StoryAdapter(new ArrayList<>());
        hotnovelScrollView.setAdapter(hotNovelAdapter);

        // Comics by Date
        LinearLayoutManager comicsByDayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        comicsByDateRecyclerView.setLayoutManager(comicsByDayLayoutManager);
        comicsByDayAdapter = new CellAdapter(new ArrayList<>());
        comicsByDateRecyclerView.setAdapter(comicsByDayAdapter);
    }

    // ==================== SETUP RECYCLERVIEW ====================

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        int spacing = (int) (8 * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, spacing, true));

        recommendedAdapter = new CellAdapter(new ArrayList<>());
        recyclerView.setAdapter(recommendedAdapter);
    }

    // ==================== FETCH DATA FROM FIREBASE ====================

    // --- 1. Tải Data list cho CAROUSEL ---
    private void fetchCarouselData() {
        db.collection("stories")
                .whereEqualTo("featured", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    carouselItems.clear();
                    List<Story> stories = new ArrayList<>();  // ✅ THAY ĐỔI
                    List<QueryDocumentSnapshot> allDocs = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        allDocs.add(doc);
                    }

                    // Random với seed
                    Random random = new Random(System.currentTimeMillis());
                    Collections.shuffle(allDocs, random);

                    // Lấy tối đa 5 items
                    int limit = Math.min(5, allDocs.size());
                    for (int i = 0; i < limit; i++) {
                        QueryDocumentSnapshot doc = allDocs.get(i);
                        Story story = doc.toObject(Story.class);  // ✅ THAY ĐỔI
                        carouselItems.add(new Carousel(story.getCoverImageUrl()));
                        stories.add(story);  // ✅ THAY ĐỔI: Không cần tạo NovelList nữa!
                    }

                    carouselAdapter = new CarouselAdapter(carouselItems, stories);  // ✅ THAY ĐỔI
                    carousel.setAdapter(carouselAdapter);
                    setupCarouselScroll();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting carousel data", e);
                });
    }

    // --- 2. Tải Data list cho RECOMMENDED (Đề xuất - Grid 3 cột) ---
    private void fetchRecommendedData() {
        db.collection("stories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Cell> allItems = new ArrayList<>();
                    ArrayList<Story> allStories = new ArrayList<>();  // ✅ THAY ĐỔI

                    // Lấy tất cả items
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);  // ✅ THAY ĐỔI

                        allStories.add(story);  // ✅ THAY ĐỔI: Trực tiếp add Story
                        allItems.add(new Cell(story.getCoverImageUrl(), story.getTitle()));
                    }

                    // Random và lấy 6 items
                    ArrayList<Cell> randomItems = new ArrayList<>();
                    ArrayList<Story> randomStories = new ArrayList<>();  // ✅ THAY ĐỔI

                    if (allItems.size() > 6) {
                        List<Integer> indices = new ArrayList<>();
                        for (int i = 0; i < allItems.size(); i++) {
                            indices.add(i);
                        }
                        Collections.shuffle(indices);

                        for (int i = 0; i < Math.min(6, indices.size()); i++) {
                            int index = indices.get(i);
                            randomItems.add(allItems.get(index));
                            randomStories.add(allStories.get(index));  // ✅ THAY ĐỔI
                        }
                    } else {
                        randomItems = allItems;
                        randomStories = allStories;  // ✅ THAY ĐỔI
                    }

                    recommendedAdapter = new CellAdapter(randomItems, randomStories);  // ✅ THAY ĐỔI
                    recyclerView.setAdapter(recommendedAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting recommended data", e);
                });
    }

    // --- 3. Tải Data list cho HOT NOVELS (Truyện hot) ---
    private void fetchHotNovelsData() {
        db.collection("stories")
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Story> items = new ArrayList<>();  // ✅ THAY ĐỔI
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);  // ✅ THAY ĐỔI
                        items.add(story);  // ✅ THAY ĐỔI: Trực tiếp add Story!
                    }
                    hotNovelAdapter = new StoryAdapter(items);  // ✅ THAY ĐỔI
                    hotnovelScrollView.setAdapter(hotNovelAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting hot novel data", e);
                });
    }

    // --- 4. Tải Data list cho lịch ra truyện (Comics by Date) ---
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
        datebuttonAdapter = new DateButtonAdapter(items);
        dateViews.setAdapter(datebuttonAdapter);

        datebuttonAdapter.setOnItemClickListener(new DateButtonAdapter.OnItemClickListener() {
            @Override
            public void onDateClick(Date date, int position) {
                Log.d(TAG, "Date clicked: " + date.getName());
                fetchComicsByDay(date.getName());
            }
        });

        // Tải Data list cho các ngày trong tuần (Mon) mặc định là thứ 2
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
                    ArrayList<Story> stories = new ArrayList<>();  // ✅ THAY ĐỔI
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);  // ✅ THAY ĐỔI
                        stories.add(story);  // ✅ THAY ĐỔI: Trực tiếp add Story
                        items.add(new Cell(story.getCoverImageUrl(), story.getTitle()));
                    }
                    comicsByDayAdapter = new CellAdapter(items, stories);  // ✅ THAY ĐỔI
                    comicsByDateRecyclerView.setAdapter(comicsByDayAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting comics for day: " + day, e);
                });
    }

    // ==================== CAROUSEL METHODS ====================

    private void setupCarouselResponsive() {
        ScreenUtils.ImageDimensions dims = ScreenUtils.calculateCarouselDimensions(getContext());
        ViewGroup.LayoutParams params = carousel.getLayoutParams();
        params.height = dims.height;
        carousel.setLayoutParams(params);
    }

    private void setupCarouselScroll() {
        if (carouselAdapter == null || carouselAdapter.getItemCount() == 0) {
            return;
        }
        if (!carouselItems.isEmpty()) {
            updateCarouselBackground(carouselItems.get(0).getImageUrl());
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
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position < carouselItems.size()) {
                    updateCarouselBackground(carouselItems.get(position).getImageUrl());
                }
            }
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

    private void updateCarouselBackground(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty() && imageBackgroundCarousel != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 3)))
                    .into(imageBackgroundCarousel);
        }
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

    // ==================== HELPER CLASSES ====================

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}