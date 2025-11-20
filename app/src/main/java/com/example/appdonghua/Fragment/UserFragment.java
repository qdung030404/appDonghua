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
    private LinearLayout layoutSettings, layoutAbout, layoutLogout;
    private ImageButton searchButton, rankingButton;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser currentUser;

    public UserFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // THAY ĐỔI: Chỉ khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // THAY ĐỔI: Khởi tạo GoogleSignInClient (cần thiết cho việc đăng xuất)
        initGoogleSignInClient();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initViews(view); // Khởi tạo các view cơ bản
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // THAY ĐỔI: Kiểm tra trạng thái đăng nhập sau khi View đã được tạo
        checkLoginStatus();
    }

    private void initViews(View view) {
        // Not logged in layout
        layoutNotLoggedIn = view.findViewById(R.id.layout_not_logged_in);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        // Logged in layout (chỉ là container)
        layoutLoggedIn = view.findViewById(R.id.layout_logged_in);

        // Setup login button click listeners
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> navigateToLogin());
        }
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> navigateToRegister());
        }
    }

    // THAY ĐỔI: Khởi tạo Google Client
    private void initGoogleSignInClient() {
        if (getContext() == null) return;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    // THAY ĐỔI: Logic kiểm tra đăng nhập
    private void checkLoginStatus() {
        // Lấy người dùng hiện tại từ Firebase
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // ĐÃ ĐĂNG NHẬP
            layoutNotLoggedIn.setVisibility(View.GONE);
            layoutLoggedIn.setVisibility(View.VISIBLE);

            // Khởi tạo các view con của layout đăng nhập
            initLoggedInViews();
            loadUserInfo(); // Tải thông tin từ FirebaseUser
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
        rankingButton = layoutLoggedIn.findViewById(R.id.ranking_Button);

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
        layoutLogout = layoutLoggedIn.findViewById(R.id.layout_logout);
    }

    // THAY ĐỔI: Tải thông tin từ FirebaseUser
    private void loadUserInfo() {
        if (currentUser == null || tvUsername == null || tvEmail == null) return;

        String uid = currentUser.getUid();

        // 1. Lấy DocumentReference đến user
        DocumentReference userRef = db.collection("users").document(uid);

        // 2. Lấy dữ liệu từ Firestore
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // 3. Chuyển data thành Model User
                User userModel = documentSnapshot.toObject(User.class);

                if (userModel != null) {
                    // 4. Lấy thông tin từ Model (chứ không phải từ Auth)
                    String email = userModel.getEmail();
                    String username = userModel.getUsername();
                    String avatarUrl = userModel.getAvatarUrl(); // <-- Lấy link Pinata/Google từ đây

                    tvEmail.setText(email);

                    // Xử lý Tên (DisplayName)
                    if (username != null && !username.isEmpty()) {
                        tvUsername.setText(username);
                    } else if (email != null && email.contains("@")) {
                        tvUsername.setText(email.split("@")[0]);
                    } else {
                        tvUsername.setText("Người dùng");
                    }

                    // 5. Dùng Glide để tải link từ Firestore
                    if (avatarUrl != null && !avatarUrl.isEmpty() && getContext() != null) {
                        Glide.with(this) // 'this' là Fragment
                                .load(avatarUrl) // <-- Tải link avatar từ Firestore
                                .placeholder(R.drawable.circle_background) // Ảnh mặc định
                                .error(R.drawable.ic_edit) // Ảnh khi lỗi
                                .circleCrop() // Cắt tròn
                                .into(imgAvatar);
                    } else {
                        // (Tùy chọn) Nếu link bị null, bạn có thể set 1 ảnh mặc định
                        // imgAvatar.setImageResource(R.drawable.default_avatar);
                    }
                }
            } else {
                // Trường hợp lỗi: Đã đăng nhập nhưng không tìm thấy hồ sơ
                Toast.makeText(getContext(), "Không tìm thấy hồ sơ người dùng.", Toast.LENGTH_SHORT).show();
                // Có thể đăng xuất user nếu cần
                logout();
            }
        }).addOnFailureListener(e -> {
            // Lỗi khi kết nối
            Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " , Toast.LENGTH_SHORT).show();
        });
    }

    // (Hàm này giữ nguyên - đã tốt)
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

        // Logout
        if (layoutLogout != null) {
            layoutLogout.setOnClickListener(v -> {
                showLogoutDialog();
            });
        }

        // Search button
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Thông báo", Toast.LENGTH_SHORT).show();
            });
        }

        // Settings button
        if (rankingButton != null) {
            rankingButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Cài đặt", Toast.LENGTH_SHORT).show();
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
        // Hiển thị thông tin về ứng dụng
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Về chúng tôi");
        builder.setMessage("Ứng dụng đọc truyện Đông Hoa.\nPhiên bản: 1.0.0");
        builder.setPositiveButton("Đóng", null);
        builder.show();
    }

    private void showLogoutDialog() {
        if (getContext() == null) return;
        // Hiển thị dialog xác nhận đăng xuất
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
        builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
            logout();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // THAY ĐỔI: Logic Đăng xuất
    private void logout() {
        // 1. Đăng xuất Firebase
        mAuth.signOut();

        // 2. Đăng xuất Google (Rất quan trọng)
        if (getActivity() != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                // 3. Thông báo và cập nhật lại UI
                Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                // 4. Chạy lại checkLoginStatus để ẩn layout đã đăng nhập
                checkLoginStatus();
            });
        } else {
            // Fallback nếu activity null (hiếm)
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            checkLoginStatus();
        }

        // THAY ĐỔI: Xóa toàn bộ code SharedPreferences.Editor
    }

    @Override
    public void onResume() {
        super.onResume();
        // THAY ĐỔI: Refresh user info khi fragment quay lại
        // (Ví dụ: sau khi đăng nhập từ LoginActivity)
        checkLoginStatus();
    }
}