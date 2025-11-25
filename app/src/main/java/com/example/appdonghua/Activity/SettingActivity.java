package com.example.appdonghua.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdonghua.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    ImageButton backButton;
    LinearLayout layoutLogout;
    Switch switchNightMode;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_NIGHT_MODE = "night_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo Google Sign In Client
        initGoogleSignInClient();

        init();
        loadNightModeSetting();
    }

    private void init() {
        backButton = findViewById(R.id.backButton);
        layoutLogout = findViewById(R.id.layout_logout);
        switchNightMode = findViewById(R.id.switchNightMode);

        backButton.setOnClickListener(v -> finish());

        // Xử lý sự kiện click đăng xuất
        layoutLogout.setOnClickListener(v -> showLogoutDialog());

        // Xử lý sự kiện thay đổi chế độ ban đêm
        switchNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNightMode(isChecked);
        });
    }

    private void loadNightModeSetting() {
        // Lấy trạng thái chế độ ban đêm đã lưu
        boolean isNightMode = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);
        switchNightMode.setChecked(isNightMode);
    }

    private void setNightMode(boolean isNightMode) {
        // Lưu trạng thái vào SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NIGHT_MODE, isNightMode);
        editor.apply();

        // Áp dụng chế độ ban đêm
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Recreate activity để áp dụng theme mới
        recreate();
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
        builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
            logout();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void logout() {
        // 1. Đăng xuất Firebase
        mAuth.signOut();

        // 2. Đăng xuất Google
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // 3. Thông báo và đóng Activity
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // 4. Đóng SettingActivity và quay về UserFragment
            finish();
        });
    }
}