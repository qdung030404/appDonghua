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
import com.example.appdonghua.Adapter.CarouselAdapter;
import com.example.appdonghua.Adapter.CellAdapter;
import com.example.appdonghua.Adapter.DateAdapter;
import com.example.appdonghua.Adapter.NoveListAdapter;
import com.example.appdonghua.Adapter.RankingAdapter;
import com.example.appdonghua.Model.Carousel;
import com.example.appdonghua.Model.Cell;
import com.example.appdonghua.Model.Date;
import com.example.appdonghua.Model.NovelList;
import com.example.appdonghua.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final long AUTO_SCROLL_DELAY = 3000;
    private ViewPager2 carousel;
    private ImageButton search_Button, ranking_Button;
    private RecyclerView recyclerView, hotnovelScrollView, dateViews;
    private List<Carousel> carouselItems;
    private CarouselAdapter carouselAdapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private CellAdapter cellAdapter;
    private NoveListAdapter noveListAdapter;
    private DateAdapter datebuttonAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        setupCarousel();
        initDateButton();
        setupScroll();
        initRecycleView();
        initHotNovelScrollView();
        return view;
    }
    private void initView(View v){
        carousel = v.findViewById(R.id.carousel);
        search_Button = v.findViewById(R.id.search_Button);
        recyclerView = v.findViewById(R.id.recyclerView);
        hotnovelScrollView = v.findViewById(R.id.scollView);
        dateViews = v.findViewById(R.id.date_Button);
        ranking_Button = v.findViewById(R.id.ranking_Button);
        ranking_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RankingActivity.class);
                startActivity(intent);

            }
        });
    }
    private void setupCarousel(){
        carouselItems = new ArrayList<>();
        carouselItems.add(new Carousel(R.drawable.img));
        carouselItems.add(new Carousel(R.drawable.img_1));
        carouselItems.add(new Carousel(R.drawable.img));
        carouselItems.add(new Carousel(R.drawable.img_1));
        carouselItems.add(new Carousel(R.drawable.img));
        carouselAdapter = new CarouselAdapter(carouselItems);
        carousel.setAdapter(carouselAdapter);
    }
    private void setupScroll(){
        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (carousel != null && carouselAdapter != null){
                    int currentItem = carousel.getCurrentItem();
                    int nextItem = (currentItem + 1) % carouselItems.size();
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
                if(state == ViewPager2.SCROLL_STATE_DRAGGING){
                    stopAutoScroll();
                }else if(state == ViewPager2.SCROLL_STATE_IDLE){
                    startAutoScroll();
                }
            }
        });
    }
    private void startAutoScroll() {
        if(autoScrollHandler != null && autoScrollRunnable != null){
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
    }
    private void stopAutoScroll(){
        if (autoScrollHandler != null && autoScrollRunnable != null){
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
    private void initDateButton(){
        ArrayList<Date> items =new ArrayList<>();
        items.add(new Date("Mon"));
        items.add(new Date("Tue"));
        items.add(new Date("Wed"));
        items.add(new Date("Thu"));
        items.add(new Date("Fri"));
        items.add(new Date("Sat"));
        items.add(new Date("Sun"));
        LinearLayoutManager layoutManager =new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateViews.setLayoutManager(layoutManager);
        datebuttonAdapter = new DateAdapter(items);
        dateViews.setAdapter(datebuttonAdapter);
        datebuttonAdapter.setOnItemClickListener(new DateAdapter.OnItemClickListener() {
            @Override
            public void onDateClick(Date date, int position) {
                Log.d("HomeFragment", "Date clicked: " + date.getName());
            }
        });
    }
    private void initRecycleView(){
        ArrayList<Cell> items =new ArrayList<>();
        items.add(new Cell(R.drawable.tghm, "Thế Giới Hoàn Mỹ"));
        items.add(new Cell(R.drawable.tghm, "Thế Giới Hoàn Mỹ"));
        items.add(new Cell(R.drawable.tghm, "Thế Giới Hoàn Mỹ"));
        items.add(new Cell(R.drawable.tghm, "Thế Giới Hoàn Mỹ"));
        items.add(new Cell(R.drawable.tghm, "Thế Giới Hoàn Mỹ"));
        items.add(new Cell(R.drawable.tghm, "Thế Giới Hoàn Mỹ"));

        GridLayoutManager layoutManager =new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        cellAdapter = new CellAdapter(items);
        recyclerView.setAdapter(cellAdapter);

    }
    private void initHotNovelScrollView(){
        ArrayList<NovelList> items =new ArrayList<>();
        items.add(new NovelList(R.drawable.tghm, "Thế Giới Hoàn Mỹ", "30k", "Huyền Huyễn", "120", "Thần Đông"));
        items.add(new NovelList(R.drawable.tghm, "Thế Giới Hoàn Mỹ", "30k", "Huyền Huyễn", "120", "Thần Đông"));
        items.add(new NovelList(R.drawable.tghm, "Thế Giới Hoàn Mỹ", "30k", "Huyền Huyễn", "120", "Thần Đông"));
        items.add(new NovelList(R.drawable.tghm, "Thế Giới Hoàn Mỹ", "30k", "Huyền Huyễn", "120", "Thần Đông"));
        items.add(new NovelList(R.drawable.tghm, "Thế Giới Hoàn Mỹ", "30k", "Huyền Huyễn", "120", "Thần Đông"));
        LinearLayoutManager layoutManager =new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        hotnovelScrollView.setLayoutManager(layoutManager);
        noveListAdapter = new NoveListAdapter(items);
        hotnovelScrollView.setAdapter(noveListAdapter);



    }

}