package com.example.appdonghua.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.appdonghua.Adapter.ViewPagerAdapter;
import com.example.appdonghua.Model.Story;
import com.example.appdonghua.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager vp;
    ChipNavigationBar bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init(){
        vp = findViewById(R.id.vp);
        bottom_nav = findViewById(R.id.bottom_nav);
        vp.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);
        bottom_nav.setItemSelected(R.id.home, true);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottom_nav.setItemSelected(R.id.home, true);
                        break;
                    case 1:
                        bottom_nav.setItemSelected(R.id.classify, true);
                        break;
                    case 2:
                        bottom_nav.setItemSelected(R.id.bookcase, true);
                        break;
                    case 3:
                        bottom_nav.setItemSelected(R.id.user, true);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        bottom_nav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                switch (id){
                    case R.id.home:
                        vp.setCurrentItem(0);
                        break;

                    case R.id.classify:
                        vp.setCurrentItem(1);
                        break;
                    case R.id.bookcase:
                        vp.setCurrentItem(2);
                        break;
                    case R.id.user:
                        vp.setCurrentItem(3);
                        break;
                }
            }
        });
    }


}