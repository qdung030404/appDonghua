package com.example.appdonghua.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

import com.example.appdonghua.Helper.NotificationHelper;
import com.example.appdonghua.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingActivity extends AppCompatActivity {
    ImageButton backButton;
    LinearLayout layoutLogout, languageLayout, account, notificationLayout;
    Switch switchNightMode, switchNotification;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_NOTIFICATION = "notification_enabled";
    private NotificationHelper notificationHelper;
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
        notificationHelper = new NotificationHelper(this);
        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo Google Sign In Client
        initGoogleSignInClient();
        getFCMToken();
        init();
        loadNightModeSetting();
    }

    private void init() {
        backButton = findViewById(R.id.backButton);
        layoutLogout = findViewById(R.id.layout_logout);
        languageLayout = findViewById(R.id.language);
        switchNightMode = findViewById(R.id.switchNightMode);
        account = findViewById(R.id.account);
        notificationLayout = findViewById(R.id.notification_layout);
        switchNotification = findViewById(R.id.switchNotification);
        backButton.setOnClickListener(v -> finish());
        // Xử lý sự kiện click đăng xuất
        layoutLogout.setOnClickListener(v -> showLogoutDialog());

        // Xử lý sự kiện thay đổi chế độ ban đêm
        switchNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNightMode(isChecked);
        });
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNotificationEnabled(isChecked);
        });
        notificationLayout.setOnClickListener(v -> openNotificationSettings());
        languageLayout.setOnClickListener(v -> showLanguageDialog());
        account.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
            startActivity(intent);
        });
    }
    private void loadSettings() {
        // Lấy trạng thái chế độ ban đêm đã lưu
        boolean isNightMode = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);
        switchNightMode.setChecked(isNightMode);

        // Lấy trạng thái thông báo đã lưu
        boolean isNotificationEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATION, true);
        switchNotification.setChecked(isNotificationEnabled);
    }

    private void setNotificationEnabled(boolean isEnabled) {
        // Lưu trạng thái vào SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIFICATION, isEnabled);
        editor.apply();

        if (isEnabled) {
            // Subscribe to topic để nhận thông báo
            FirebaseMessaging.getInstance().subscribeToTopic("all")
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ?
                                "Đã bật thông báo" : "Không thể bật thông báo";
                        Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show();

                        // Gửi thông báo test
                        if (task.isSuccessful()) {
                            notificationHelper.sendNotification(
                                    "Thông báo đã được bật",
                                    "Bạn sẽ nhận được thông báo từ ứng dụng"
                            );
                        }
                    });
        } else {
            // Unsubscribe from topic
            FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
                    .addOnCompleteListener(task -> {
                        String msg = task.isSuccessful() ?
                                "Đã tắt thông báo" : "Không thể tắt thông báo";
                        Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    // TODO: Gửi token này lên server của bạn
                });
    }
    private void openNotificationSettings() {
        try {
            Intent intent = new Intent();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0 trở lên - mở App Notification Settings
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            } else {
                // Android 7.1 trở xuống - mở App Info Settings
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
            }

            startActivity(intent);

        } catch (Exception e) {
            // Fallback: nếu không mở được notification settings, mở app info
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "Không thể mở cài đặt thông báo", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showLanguageDialog() {
        String[] languages = {"Tiếng Việt", "English"};
        int selectedLanguage = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ngôn ngữ");
        builder.setSingleChoiceItems(languages, selectedLanguage, (dialog, which) -> {
            String selected = languages[which];
            Toast.makeText(this, "Chọn ngôn ngữ: " + selected, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
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