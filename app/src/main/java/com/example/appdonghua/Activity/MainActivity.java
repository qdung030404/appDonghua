package com.example.appdonghua.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.appdonghua.Adapter.ViewPagerAdapter;
import com.example.appdonghua.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ViewPager vp;
    BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottom_nav.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottom_nav.getMenu().findItem(R.id.audio).setChecked(true);
                        break;
                    case 2:
                        bottom_nav.getMenu().findItem(R.id.classify).setChecked(true);
                        break;
                    case 3:
                        bottom_nav.getMenu().findItem(R.id.bookcase).setChecked(true);
                        break;
                    case 4:
                        bottom_nav.getMenu().findItem(R.id.user).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()  {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        vp.setCurrentItem(0);
                        break;
                    case R.id.audio:
                        vp.setCurrentItem(1);
                        break;
                    case R.id.classify:
                        vp.setCurrentItem(2);
                        break;
                    case R.id.bookcase:
                        vp.setCurrentItem(3);
                        break;
                    case R.id.user:
                        vp.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
    }
}