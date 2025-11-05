package com.example.appdonghua.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.appdonghua.Adapter.CarouselAdapter;
import com.example.appdonghua.Model.Carousel;
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
    private ImageButton search_Button;
    private RecyclerView recyclerView;
    private List<Carousel> carouselItems;
    private CarouselAdapter carouselAdapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;


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
        return view;
    }
    private void initView(View v){
        carousel = v.findViewById(R.id.carousel);
        search_Button = v.findViewById(R.id.search_Button);
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
    private void setupScoll(){
        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (carousel != null && carouselAdapter != null){
                    int currentItem = carousel.getCurrentItem();
                    int nextItem = currentItem + 1 % carouselItems.size();
                    carousel.setCurrentItem(nextItem, true);
                    autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);

                }
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);

    }
}