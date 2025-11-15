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
                break;
            case "hot":
                break;

            case "full":
                break;

            case "tu_tien":
                break;

            case "magical":
                break;

            case "xuyen_khong":
                break;

            case "tien_hiep":
                break;

            case "mechanic":
                break;
            default:
                break;
        }
        for (int i = 0; i < 10; i++) {
            items.add(new NovelList(R.drawable.img_2, "Truyện " + category + " " + (i+1), (30000 - i*1000), "Category", "120", "Tác giả"));
        }
        return items;
    }

}