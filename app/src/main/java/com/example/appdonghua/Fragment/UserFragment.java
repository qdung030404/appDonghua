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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appdonghua.Activity.LoginActivity;
import com.example.appdonghua.Activity.MainActivity;
import com.example.appdonghua.Activity.NoteActivity;
import com.example.appdonghua.Activity.RegisterActivity;
import com.example.appdonghua.Activity.SearchActivity;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserFragment extends Fragment {
    private LinearLayout layoutNotLoggedIn;
    private Button btnLogin, btnRegister;
    private FirebaseFirestore db;
    private View layoutLoggedIn;
    private ImageView imgAvatar;
    private TextView tvUsername, tvEmail;
    private LinearLayout layoutSetting, layoutNote, layoutBookCase, layoutSearch, layoutFeedback, layoutAbout;

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

        // User info
        imgAvatar = layoutLoggedIn.findViewById(R.id.img_avatar);
        tvUsername = layoutLoggedIn.findViewById(R.id.tv_username);
        tvEmail = layoutLoggedIn.findViewById(R.id.tv_email);

        // Menu items
        layoutSetting = layoutLoggedIn.findViewById(R.id.layout_setting);
        layoutNote = layoutLoggedIn.findViewById(R.id.layout_note);
        layoutBookCase = layoutLoggedIn.findViewById(R.id.layout_book_case);;
        layoutAbout = layoutLoggedIn.findViewById(R.id.layout_about);
        layoutFeedback = layoutLoggedIn.findViewById(R.id.layout_feedback);
        layoutSearch = layoutLoggedIn.findViewById(R.id.layout_search);

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
        if (layoutBookCase != null) {
            layoutBookCase.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.findViewById(R.id.vp);
                    androidx.viewpager.widget.ViewPager viewPager =
                            mainActivity.findViewById(R.id.vp);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(2);
                    }
                }
            });
        }

        if (layoutSetting != null) {
            layoutSetting.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            });
        }
        if (layoutNote != null) {
            layoutNote.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NoteActivity.class);
                startActivity(intent);
            });
        }
        if (layoutSearch != null) {
            layoutSearch.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            });
        }

        if (layoutFeedback != null) {
            layoutFeedback.setOnClickListener(v -> showFeedBackDialog());
        }

        // About
        if (layoutAbout != null) {
            layoutAbout.setOnClickListener(v -> {
                showAboutDialog();
            });
        }
    }
    private void showFeedBackDialog() {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_feedback, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() == null) return;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Spinner feedbackTypeSpinner = dialogView.findViewById(R.id.feedback_type_spinner);
        EditText feedbackMessage = dialogView.findViewById(R.id.feedback_message);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);

        String[] feedbackTypes = {"Khác", "Báo lỗi", "Góp ý tính năng", "Phản hồi nội dung", };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, feedbackTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackTypeSpinner.setAdapter(adapter);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                String selectedType = feedbackTypeSpinner.getSelectedItem().toString();
                String message = feedbackMessage.getText().toString();
                if (message.isEmpty()){
                    feedbackMessage.setError("Vui lòng nhập nội dung phản hồi");
                    feedbackMessage.requestFocus();
                    return;
                }
                submitFeedback(selectedType, message);

            });
        }
        dialog.show();

    }
    private void submitFeedback(String type, String message) {
        if (currentUser == null) return;
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("userId", currentUser.getUid());
        feedback.put("userEmail", currentUser.getEmail());
        feedback.put("type", type);
        feedback.put("message", message);
        feedback.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date()));
        feedback.put("status", "pending");
        db.collection("feedback").add(feedback).addOnSuccessListener(documentReference -> {
            Toast.makeText(getContext(), "Gửi phản hồi thành công\nChúng tôi sẽ xem xét và phản hồi sớm nhất.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi gửi phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        });
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
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_about, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() == null) return;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnClose = dialogView.findViewById(R.id.btn_close);
        LinearLayout layoutFacebook = dialogView.findViewById(R.id.layout_facebook);
        LinearLayout layoutInstagram = dialogView.findViewById(R.id.layout_instagram);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
        if (layoutFacebook != null){
            layoutFacebook.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                startActivity(intent);
            });
        }
        if (layoutInstagram != null){
            layoutInstagram.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"));
                startActivity(intent);
            });
        }
        dialog.show();

    }
    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
    }
}