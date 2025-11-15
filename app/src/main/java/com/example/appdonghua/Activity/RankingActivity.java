package com.example.appdonghua.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.appdonghua.Fragment.RankingBoardFragment;
import com.example.appdonghua.R;
import com.google.android.material.navigation.NavigationView;

public class RankingActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        setupListener();
        loadFragment("nomination");
    }



    private void init(){
        navigationView = findViewById(R.id.filter_Button);
        backButton = findViewById(R.id.backButton);

    }
    private void setupListener(){
        backButton.setOnClickListener(v -> finish());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                String category = "";
                int id = menuItem.getItemId();
                if (id == R.id.nomination) {
                    category = "nomination";
                } else if (id == R.id.hot) {
                    category = "hot";
                } else if (id == R.id.full) {
                    category = "full";
                } else if (id == R.id.tu_tien) {
                    category = "tu_tien";
                } else if (id == R.id.magical) {
                    category = "magical";
                } else if (id == R.id.xuyen_khong) {
                    category = "xuyen_khong";
                } else if (id == R.id.tien_hiep) {
                    category = "tien_hiep";
                } else if (id == R.id.mechanic) {
                    category = "mechanic";
                }
                loadFragment(category);
                return false;
            }

        });
    }
    private void loadFragment(String nomination) {
        RankingBoardFragment fragment = RankingBoardFragment.newInstance(nomination);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

    }
}