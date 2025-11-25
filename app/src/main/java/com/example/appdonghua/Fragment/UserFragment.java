package com.example.appdonghua.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Activity.LoginActivity;
import com.example.appdonghua.Activity.RegisterActivity;
import com.example.appdonghua.Activity.SettingActivity;
import com.example.appdonghua.Model.User;
import com.example.appdonghua.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserFragment extends Fragment {
    private LinearLayout layoutNotLoggedIn;
    private Button btnLogin, btnRegister;
    private FirebaseFirestore db;
    private View layoutLoggedIn;
    private ImageView imgAvatar;
    private TextView tvUsername, tvEmail;
    private LinearLayout layoutFavorites, layoutHistory, layoutDownloads;
    private LinearLayout layoutSettings, layoutAbout;
    private ImageButton searchButton, settingButton;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser currentUser;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        initGoogleSignInClient();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkLoginStatus();
    }

    private void initViews(View view) {
        // Not logged in layout
        layoutNotLoggedIn = view.findViewById(R.id.layout_not_logged_in);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        // Logged in layout
        layoutLoggedIn = view.findViewById(R.id.layout_logged_in);

        // Setup login button click listeners
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> navigateToLogin());
        }
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> navigateToRegister());
        }
    }

    private void initGoogleSignInClient() {
        if (getContext() == null) return;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    private void checkLoginStatus() {
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // ĐÃ ĐĂNG NHẬP
            layoutNotLoggedIn.setVisibility(View.GONE);
            layoutLoggedIn.setVisibility(View.VISIBLE);

            initLoggedInViews();
            loadUserInfo();
            setupLoggedInClickListeners();
        } else {
            // CHƯA ĐĂNG NHẬP
            layoutNotLoggedIn.setVisibility(View.VISIBLE);
            layoutLoggedIn.setVisibility(View.GONE);
        }
    }

    private void initLoggedInViews() {
        if (layoutLoggedIn == null) return;

        // Header buttons
        searchButton = layoutLoggedIn.findViewById(R.id.search_Button);
        settingButton = layoutLoggedIn.findViewById(R.id.settingButton);

        // User info
        imgAvatar = layoutLoggedIn.findViewById(R.id.img_avatar);
        tvUsername = layoutLoggedIn.findViewById(R.id.tv_username);
        tvEmail = layoutLoggedIn.findViewById(R.id.tv_email);

        // Menu items
        layoutFavorites = layoutLoggedIn.findViewById(R.id.layout_favorites);
        layoutHistory = layoutLoggedIn.findViewById(R.id.layout_history);
        layoutDownloads = layoutLoggedIn.findViewById(R.id.layout_downloads);
        layoutSettings = layoutLoggedIn.findViewById(R.id.layout_settings);
        layoutAbout = layoutLoggedIn.findViewById(R.id.layout_about);
    }

    private void loadUserInfo() {
        if (currentUser == null || tvUsername == null || tvEmail == null) return;

        String uid = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                User userModel = documentSnapshot.toObject(User.class);

                if (userModel != null) {
                    String email = userModel.getEmail();
                    String username = userModel.getUsername();
                    String avatarUrl = userModel.getAvatarUrl();

                    tvEmail.setText(email);

                    if (username != null && !username.isEmpty()) {
                        tvUsername.setText(username);
                    } else if (email != null && email.contains("@")) {
                        tvUsername.setText(email.split("@")[0]);
                    } else {
                        tvUsername.setText("Người dùng");
                    }

                    if (avatarUrl != null && !avatarUrl.isEmpty() && getContext() != null) {
                        Glide.with(this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.circle_background)
                                .error(R.drawable.ic_edit)
                                .circleCrop()
                                .into(imgAvatar);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy hồ sơ người dùng.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " , Toast.LENGTH_SHORT).show();
        });
    }

    private void setupLoggedInClickListeners() {
        // Favorites
        if (layoutFavorites != null) {
            layoutFavorites.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Phim yêu thích", Toast.LENGTH_SHORT).show();
            });
        }

        // History
        if (layoutHistory != null) {
            layoutHistory.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Lịch sử xem", Toast.LENGTH_SHORT).show();
            });
        }

        // Downloads
        if (layoutDownloads != null) {
            layoutDownloads.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Tải xuống", Toast.LENGTH_SHORT).show();
            });
        }

        // Settings (Feedback)
        if (layoutSettings != null) {
            layoutSettings.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Ý kiến phản hồi", Toast.LENGTH_SHORT).show();
            });
        }

        // About
        if (layoutAbout != null) {
            layoutAbout.setOnClickListener(v -> {
                showAboutDialog();
            });
        }

        // Search button
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Thông báo", Toast.LENGTH_SHORT).show();
            });
        }

        // Settings button
        if (settingButton != null) {
            settingButton.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            });
        }
    }

    private void navigateToLogin() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void navigateToRegister() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intent);
    }

    private void showAboutDialog() {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Về chúng tôi");
        builder.setMessage("Ứng dụng đọc truyện Đông Hoa.\nPhiên bản: 1.0.0");
        builder.setPositiveButton("Đóng", null);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
    }
}