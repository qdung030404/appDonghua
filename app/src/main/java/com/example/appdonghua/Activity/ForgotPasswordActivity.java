package com.example.appdonghua.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdonghua.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailInput;
    private Button resetPasswordBtn;
    private ImageButton backButton;
    private TextView tvBackToLogin;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        init();
        setupListeners();
    }
    private void init(){
        emailInput = findViewById(R.id.email_input);
        resetPasswordBtn = findViewById(R.id.reset_password_btn);
        backButton = findViewById(R.id.BackButton);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

    }
    private void setupListeners(){
        backButton.setOnClickListener(v -> finish());
        tvBackToLogin.setOnClickListener(v -> finish());
        resetPasswordBtn.setOnClickListener(v -> handleResetPassword());

    }

    private void handleResetPassword() {
        String email = emailInput.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Vui lòng nhập email");
            emailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Email không hợp lệ");
            emailInput.requestFocus();
            return;
        }

        // Disable button to prevent multiple clicks
        resetPasswordBtn.setEnabled(false);
        resetPasswordBtn.setText("Đang gửi...");

        // Send password reset email
        sendPasswordResetEmail(email);
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    resetPasswordBtn.setEnabled(true);
                    resetPasswordBtn.setText("Gửi email");
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Email đặt lại mật khẩu đã được gửi đến " + email,
                                Toast.LENGTH_LONG).show();
                        emailInput.setText("");
                        emailInput.postDelayed(this::finish, 2000);
                    } else {
                        String errorMessage = "Không thể gửi email đặt lại mật khẩu.";

                        if (task.getException() != null) {
                            String exception = task.getException().getMessage();
                            if (exception != null) {
                                if (exception.contains("no user record")) {
                                    errorMessage = "Email này chưa được đăng ký.";
                                } else if (exception.contains("network")) {
                                    errorMessage = "Lỗi kết nối mạng. Vui lòng thử lại.";
                                }
                            }
                        }

                        Toast.makeText(ForgotPasswordActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}