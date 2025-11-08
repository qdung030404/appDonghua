package com.example.appdonghua.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.appdonghua.Adapter.RankingAdapter;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingBoardFragment extends Fragment {
    private RecyclerView rankingRecyclerView;
    private static final String ARG_CATEGORY = "category";
    private String Category;



    public RankingBoardFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking_board, container, false);
        // Inflate the layout for this fragment
        init(view);
        setupRankingList();
        return view;
    }
    private void init(View view){
        rankingRecyclerView = view.findViewById(R.id.rankingRecyclerView);
    }
    private void setupRankingList(){
        ArrayList<NovelList> items = getDataByCategory(Category);

        LinearLayoutManager layoutManager =new LinearLayoutManager(getContext());
        rankingRecyclerView.setLayoutManager(layoutManager);
        RankingAdapter adapter = new RankingAdapter(items);
        rankingRecyclerView.setAdapter(adapter);
    }
    private ArrayList<NovelList> getDataByCategory(String category){
        ArrayList<NovelList> items =new ArrayList<>();
        switch(category){
            case "nomination":
                items.add(new NovelList(R.drawable.tghm, "Truyện Đề Cử 1", "30k", "Huyền Huyễn", "120", "Thần Đông"));
                items.add(new NovelList(R.drawable.tghm, "Truyện Đề Cử 2", "25k", "Tu Tiên", "150", "Cao Thủ"));
                break;
            case "hot":
                // Data cho Hot nhất
                items.add(new NovelList(R.drawable.tghm, "Truyện Hot 1", "50k", "Huyền Huyễn", "200", "Siêu Phẩm"));
                items.add(new NovelList(R.drawable.tghm, "Truyện Hot 2", "45k", "Tu Tiên", "180", "Đỉnh Cao"));
                break;

            case "full":
                // Data cho Hoàn Thành
                items.add(new NovelList(R.drawable.tghm, "Truyện Full 1", "100k", "Huyền Huyễn", "500", "Hoàn"));
                items.add(new NovelList(R.drawable.tghm, "Truyện Full 2", "95k", "Tu Tiên", "480", "Hoàn"));
                break;

            case "tu_tien":
                // Data cho Tu Tiên
                items.add(new NovelList(R.drawable.tghm, "Tu Tiên 1", "30k", "Tu Tiên", "120", "Cao Thủ"));
                items.add(new NovelList(R.drawable.tghm, "Tu Tiên 2", "28k", "Tu Tiên", "110", "Tiên Đế"));
                break;

            case "magical":
                // Data cho Huyền Huyễn
                items.add(new NovelList(R.drawable.tghm, "Huyền Huyễn 1", "32k", "Huyền Huyễn", "130", "Thánh Vương"));
                items.add(new NovelList(R.drawable.tghm, "Huyền Huyễn 2", "30k", "Huyền Huyễn", "125", "Đế Tôn"));
                break;

            case "xuyen_khong":
                // Data cho Xuyên Không
                items.add(new NovelList(R.drawable.tghm, "Xuyên Không 1", "35k", "Xuyên Không", "140", "Xuyên Không"));
                items.add(new NovelList(R.drawable.tghm, "Xuyên Không 2", "33k", "Xuyên Không", "135", "Trọng Sinh"));
                break;

            case "tien_hiep":
                // Data cho Tiên Hiệp
                items.add(new NovelList(R.drawable.tghm, "Tiên Hiệp 1", "40k", "Tiên Hiệp", "160", "Tiên Tôn"));
                items.add(new NovelList(R.drawable.tghm, "Tiên Hiệp 2", "38k", "Tiên Hiệp", "155", "Chí Tôn"));
                break;

            case "mechanic":
                // Data cho Khoa Huyễn
                items.add(new NovelList(R.drawable.tghm, "Khoa Huyễn 1", "36k", "Khoa Huyễn", "145", "Cơ Giáp"));
                items.add(new NovelList(R.drawable.tghm, "Khoa Huyễn 2", "34k", "Khoa Huyễn", "140", "Vũ Trụ"));
                break;
            default:
                // Data mặc định
                items.add(new NovelList(R.drawable.tghm, "Thế Giới Hoàn Mỹ", "30k", "Huyền Huyễn", "120", "Thần Đồng"));
                break;
        }
        for (int i = 0; i < 11; i++) {
            items.add(new NovelList(R.drawable.tghm, "Truyện " + category + " " + (i+3), "30k", "Thể loại", "120", "Tác giả"));
        }
        return items;
    }

}