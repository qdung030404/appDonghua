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
    LinearLayout layoutLogout, account, notificationLayout;
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

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        initGoogleSignInClient();
        getFCMToken();
        init();
        loadNightModeSetting();
    }

    private void init() {
        backButton = findViewById(R.id.backButton);
        layoutLogout = findViewById(R.id.layout_logout);
        switchNightMode = findViewById(R.id.switchNightMode);
        account = findViewById(R.id.account);
        notificationLayout = findViewById(R.id.notification_layout);
        backButton.setOnClickListener(v -> finish());
        layoutLogout.setOnClickListener(v -> showLogoutDialog());

        switchNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNightMode(isChecked);
        });
        notificationLayout.setOnClickListener(v -> openNotificationSettings());
        account.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
            startActivity(intent);
        });
    }
    private void loadSettings() {
        boolean isNightMode = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);
        switchNightMode.setChecked(isNightMode);

    }


    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    // TODO: Gửi token này lên server của bạn
                });
    }
    private void openNotificationSettings() {
        try {
            Intent intent = new Intent();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
            }

            startActivity(intent);

        } catch (Exception e) {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "Không thể mở cài đặt thông báo", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadNightModeSetting() {
        boolean isNightMode = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);
        switchNightMode.setChecked(isNightMode);
    }

    private void setNightMode(boolean isNightMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NIGHT_MODE, isNightMode);
        editor.apply();

        int nightMode = isNightMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
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
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            
            finish();
        });
    }
}