package com.example.appdonghua.Activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdonghua.Adapter.ChapterAdapter;
import com.example.appdonghua.Model.Chapter;
import com.example.appdonghua.R;

import java.util.ArrayList;
import java.util.List;

public class ComicInfoActivity extends AppCompatActivity {
    ImageButton backButton, FavoriteButton, expandButton;
    ImageView imageCover;
    TextView texTitle, Views, author, status, introduce;
    ListView lvChapters;
    ChapterAdapter chapterAdapter;
    boolean isExpanded, isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comic_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        SetupListener();
        setupChapter();
    }
    private void init(){
        backButton = findViewById(R.id.backButton);
        FavoriteButton = findViewById(R.id.FavoriteButton);
        expandButton = findViewById(R.id.expandButton);
        imageCover = findViewById(R.id.imageCover);
        texTitle = findViewById(R.id.texTitle);
        Views = findViewById(R.id.Views);
        author = findViewById(R.id.author);
        status = findViewById(R.id.status);
        introduce = findViewById(R.id.introduce);
        lvChapters = findViewById(R.id.lvChapters);
    }
    private void SetupListener(){
        backButton.setOnClickListener(v -> finish());
        FavoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) {
                FavoriteButton.setImageResource(R.drawable.ic_bookmark_regular);
            } else {
                FavoriteButton.setImageResource(R.drawable.ic_bookmark_solid);
            }
            Toast.makeText(ComicInfoActivity.this,
                    isFavorite ? "Đã xóa khỏi danh sách yêu thích" : "Đã thêm vào danh sách yêu thích",
                    Toast.LENGTH_SHORT).show();
        });
        expandButton.setOnClickListener(v -> {
            toggle();
        });
    }
    private void toggle(){
        isExpanded = !isExpanded;
        if (isExpanded) {
            introduce.setMaxLines(Integer.MAX_VALUE);
            expandButton.setRotation(180);
        } else {
            introduce.setMaxLines(3);
            expandButton.setRotation(0);
        }
    }
    private void setupChapter(){
        List<Chapter> chapters = generateChapter(160);
        chapterAdapter = new ChapterAdapter(chapters);
        lvChapters.setAdapter(chapterAdapter);

    }
    private List<Chapter> generateChapter(int count){
        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            chapters.add(new Chapter("Chapter " + (i + 1), i + 100));
        }
        return chapters;
    }
}