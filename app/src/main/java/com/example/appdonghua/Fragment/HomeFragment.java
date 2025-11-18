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
import com.example.appdonghua.Model.Story; // THAY ĐỔI: Import model Story
import com.example.appdonghua.R;
import com.google.firebase.firestore.FirebaseFirestore; // THAY ĐỔI: Import
import com.google.firebase.firestore.Query; // THAY ĐỔI: Import
import com.google.firebase.firestore.QueryDocumentSnapshot; // THAY ĐỔI: Import

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final long AUTO_SCROLL_DELAY = 3000;
    private static final String TAG = "HomeFragment";

    // --- Firebase ---
    private FirebaseFirestore db; // THAY ĐỔI: Thêm Firestore

    // --- Views ---
    private ViewPager2 carousel;
    private ImageButton search_Button, ranking_Button;
    private RecyclerView recyclerView, hotnovelScrollView, dateViews, comicsByDateRecyclerView;

    // --- Adapters ---
    private CarouselAdapter carouselAdapter;
    private CellAdapter recommendedAdapter; // THAY ĐỔI: Đổi tên cho rõ
    private NoveListAdapter hotNovelAdapter; // THAY ĐỔI: Đổi tên cho rõ
    private DateAdapter datebuttonAdapter;
    private CellAdapter comicsByDayAdapter; // THAY ĐỔI: Adapter riêng cho mục này

    // --- Data Lists ---
    private List<Carousel> carouselItems = new ArrayList<>();

    // --- Carousel Auto Scroll ---
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    // (Xóa các biến ARG_PARAM và newInstance() nếu bạn không dùng)

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // THAY ĐỔI: Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();
        // Xóa initComicsByDayData() vì sẽ lấy từ Firebase
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view); // 1. Ánh xạ View
        setupEmptyAdapters(); // 2. Cài đặt LayoutManager và Adapter rỗng
        fetchDataFromFirestore(); // 3. Bắt đầu tải dữ liệu

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
     * THAY ĐỔI: Khởi tạo các RecyclerView với Adapter rỗng
     * Dữ liệu sẽ được "đổ" vào sau khi Firebase trả về.
     */
    private void setupEmptyAdapters() {
        // Carousel
        carouselAdapter = new CarouselAdapter(carouselItems);
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

    /**
     * THAY ĐỔI: Hàm tổng để gọi tất cả các hàm tải dữ liệu
     */
    private void fetchDataFromFirestore() {
        fetchCarouselData();
        fetchRecommendedData();
        fetchHotNovelsData();
        initDateButton(); // (Hàm này sẽ tự fetch khi được click)
    }

    // --- 1. Tải dữ liệu cho CAROUSEL ---
    private void fetchCarouselData() {
        db.collection("stories")
                .whereEqualTo("featured", true) // Lấy truyện có "featured" = true
                .limit(5) // Lấy 5 truyện
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    carouselItems.clear(); // Xóa data cũ
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        // THAY ĐỔI: Dùng model Carousel mới với String URL
                        carouselItems.add(new Carousel(story.getCoverImageUrl()));
                    }
                    carouselAdapter.notifyDataSetChanged();
                    setupCarouselScroll(); // CHỈ setup scroll SAU KHI có data
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting carousel data", e);
                });
    }

    // --- 2. Tải dữ liệu cho RECOMMENDED (Đề xuất - Grid 3 cột) ---
    private void fetchRecommendedData() {
        db.collection("stories")
                .limit(6) // Lấy 6 truyện
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Cell> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        // THAY ĐỔI: Dùng model Cell mới với String URL
                        items.add(new Cell(story.getCoverImageUrl(), story.getTitle()));
                    }
                    // THAY ĐỔI: Cập nhật dữ liệu cho adapter
                    recommendedAdapter = new CellAdapter(items);
                    recyclerView.setAdapter(recommendedAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting recommended data", e);
                });
    }

    // --- 3. Tải dữ liệu cho HOT NOVELS (Truyện hot) ---
    private void fetchHotNovelsData() {
        db.collection("stories")
                .orderBy("viewCount", Query.Direction.DESCENDING) // Sắp xếp theo lượt xem
                .limit(10) // Lấy 10 truyện
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<NovelList> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        // THAY ĐỔI: Dùng model NovelList mới với String URL
                        // (Giả sử "chapterCount" bạn sẽ lưu ở đâu đó, tạm để "120")
                        String genre = (story.getGenres() != null && !story.getGenres().isEmpty()) ? story.getGenres().get(0) : "Khác";
                        items.add(new NovelList(
                                story.getCoverImageUrl(),
                                story.getTitle(),
                                story.getViewCount(),
                                genre,
                                "120", // Bạn cần thêm trường này vào model Story
                                "Tác Giả", // Bạn cần thêm trường "author" vào model Story
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

        // THAY ĐỔI: Click listener giờ sẽ gọi hàm fetch từ Firebase
        datebuttonAdapter.setOnItemClickListener(new DateAdapter.OnItemClickListener() {
            @Override
            public void onDateClick(Date date, int position) {
                Log.d(TAG, "Date clicked: " + date.getName());
                fetchComicsByDay(date.getName()); // Gọi hàm tải data
            }
        });

        // Tải dữ liệu cho ngày đầu tiên (Mon) làm mặc định
        if (!items.isEmpty()) {
            fetchComicsByDay(items.get(0).getName());
        }
    }

    /**
     * THAY ĐỔI: Hàm mới thay thế cho loadComicsByDate()
     * Hàm này sẽ truy vấn Firestore
     */
    private void fetchComicsByDay(String day) {
        db.collection("stories")
                .whereEqualTo("releaseDay", day) // Lấy truyện có ngày ra = "Mon", "Tue"...
                .limit(6) // Lấy 6 truyện
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Cell> comics = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        comics.add(new Cell(story.getCoverImageUrl(), story.getTitle()));
                    }
                    comicsByDayAdapter = new CellAdapter(comics);
                    comicsByDateRecyclerView.setAdapter(comicsByDayAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting comics for day: " + day, e);
                });
    }

    // --- Các hàm xử lý Carousel Auto-Scroll (Giữ nguyên) ---
    // THAY ĐỔI: Tách hàm setup scroll ra
    private void setupCarouselScroll() {
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
}
