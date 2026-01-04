package com.example.appdonghua.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdonghua.R;
import com.example.appdonghua.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_REMEMBER = "remember";

    private TextView tvRegister, tvForgotPassword;
    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private CheckBox rememberMe;
    private ImageView googleBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private SharedPreferences sharedPreferences;

    // ==================== LIFECYCLE METHODS ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Chào mừng trở lại " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            navigateToHome();
            return;
        }

        initViews();
        initGoogleSignIn();
        initGoogleSignInLauncher();
        loadSavedCredentials();
        setupClickListeners();
    }

    // ==================== INITIALIZATION METHODS ====================

    private void initViews() {
        emailInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);
        rememberMe = findViewById(R.id.rememberMe);
        googleBtn = findViewById(R.id.google_btn);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Log.w(TAG, "Google sign in failed", e);
                            Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Đã hủy đăng nhập Google.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupClickListeners() {
        loginBtn.setOnClickListener(v -> handleLogin());
        googleBtn.setOnClickListener(v -> handleGoogleLogin());
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    // ==================== REMEMBER ME FUNCTIONALITY ====================

    private void loadSavedCredentials() {
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            String savedEmail = sharedPreferences.getString(KEY_USERNAME, "");
            emailInput.setText(savedEmail);
            rememberMe.setChecked(true);
        }
    }

    private void saveRememberMe(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberMe.isChecked()) {
            editor.putString(KEY_USERNAME, email); // Chỉ lưu email
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.remove(KEY_USERNAME);
            editor.putBoolean(KEY_REMEMBER, false);
        }
        editor.apply();
    }

    // ==================== EMAIL/PASSWORD LOGIN ====================

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập Email và Password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            createUserProfileIfNotExist(user); // 1. Tạo hồ sơ trên Firestore
                            saveRememberMe(email); // 2. Chỉ lưu "Remember Me"

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Xác thực thất bại.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ==================== GOOGLE LOGIN ====================

    private void handleGoogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            createUserProfileIfNotExist(user); // 1. Tạo hồ sơ
                            if (acct.getEmail() != null) {
                                saveRememberMe(acct.getEmail()); // 2. Chỉ lưu "Remember Me"
                            }

                            Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Xác thực Google thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ==================== USER PROFILE CREATION ====================

    private void createUserProfileIfNotExist(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return;

        String uid = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String username = firebaseUser.getDisplayName();
        if(username == null || username.isEmpty()){
            assert email != null;
            username = email.split("@")[0];
        }else {
            username = "Người dùng";
        }
        String finalAvatarUrl;
        String defaultAvatarUrl = "https://emerald-accepted-barnacle-132.mypinata.cloud/ipfs/bafkreiev5kbmz2cdp35axx42pvafhhoygl5apokg5wwtsoefnbdiytftye";
        Uri googleAvatarUri = firebaseUser.getPhotoUrl();

        if (googleAvatarUri != null) {

            finalAvatarUrl = googleAvatarUri.toString();
        } else {
            finalAvatarUrl = defaultAvatarUrl;
        }
        String finalUsername = username;

        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null || !document.exists()) {
                    User newUser = new User(uid, finalUsername, email, finalAvatarUrl);

                    userRef.set(newUser)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Hồ sơ user đã được tạo!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Lỗi khi tạo hồ sơ", e));
                } else {
                    Log.d(TAG, "User đã tồn tại, không tạo mới.");
                }
            } else {
                Log.w(TAG, "Lỗi khi kiểm tra user: ", task.getException());
            }
        });
    }

    // ==================== NAVIGATION ====================

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}