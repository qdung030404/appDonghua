package com.example.appdonghua.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appdonghua.Adapter.RankingAdapter;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class RankingBoardFragment extends Fragment {
    private RecyclerView rankingRecyclerView;
    private static final String ARG_CATEGORY = "category";
    private String Category;
    private RankingAdapter adapter;
    private FirebaseFirestore db;
    private static final String TAG = "RankingBoardFragment";

    public RankingBoardFragment() {
        // Required empty public constructor
    }

    public static RankingBoardFragment newInstance(String Category) {
        RankingBoardFragment fragment = new RankingBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, Category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Category = getArguments().getString(ARG_CATEGORY);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking_board, container, false);
        init(view);
        setupRankingList();
        return view;
    }

    private void init(View view){
        rankingRecyclerView = view.findViewById(R.id.rankingRecyclerView);
    }

    private void setupRankingList(){
        ArrayList<NovelList> items = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rankingRecyclerView.setLayoutManager(layoutManager);

        adapter = new RankingAdapter(items);
        rankingRecyclerView.setAdapter(adapter);

        // Lấy dữ liệu từ Firebase
        fetchDataFromFirebase(Category);
    }

    private void fetchDataFromFirebase(String category) {
        Query query;

        switch(category) {
            case "full":
                // Lấy truyện đã hoàn thành
                query = db.collection("stories")
                        .whereEqualTo("status", "Full") // Hoặc "full"
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
            case "hot":
                // Lấy truyện hot nhất
                query = db.collection("stories")
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
            case "tu_tien":
                // Lấy truyện thể loại Tu Tiên
                query = db.collection("stories")
                        .whereArrayContains("genres", "Tu Tiên")
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
            case "magical":
                // Lấy truyện thể loại Huyền Huyễn
                query = db.collection("stories")
                        .whereArrayContains("genres", "Huyền Huyễn")
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
            case "xuyen_khong":
                // Lấy truyện thể loại Xuyên Không
                query = db.collection("stories")
                        .whereArrayContains("genres", "Xuyên Không")
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
            case "tien_hiep":
                // Lấy truyện thể loại Tiên Hiệp
                query = db.collection("stories")
                        .whereArrayContains("genres", "Tiên Hiệp")
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
            case "mechanic":
                // Lấy truyện thể loại Khoa Huyễn
                query = db.collection("stories")
                        .whereArrayContains("genres", "Khoa Huyễn")
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
            default:
                // Mặc định lấy tất cả truyện
                query = db.collection("stories")
                        .orderBy("viewCount", Query.Direction.DESCENDING)
                        .limit(10);
                break;
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<NovelList> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        String genre = (story.getGenres() != null && !story.getGenres().isEmpty())
                                ? story.getGenres().get(0) : "Khác";

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

                    if (items.isEmpty()) {
                        Log.d(TAG, "No data found for category: " + category);
                        // Thêm dữ liệu mẫu nếu không có dữ liệu
                    }

                    adapter.updateData(items);
                    Log.d(TAG, "Loaded " + items.size() + " items for category: " + category);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching data for category " + category + ": " + e.getMessage());
                    // Thêm dữ liệu mẫu khi có lỗi
                    ArrayList<NovelList> sampleItems = new ArrayList<>();
                    adapter.updateData(sampleItems);
                });
    }


}