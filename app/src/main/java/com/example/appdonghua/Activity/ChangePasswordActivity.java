package com.example.appdonghua.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdonghua.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button changePasswordBtn;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        initViews();
        setupClickListeners();
    
    }
    private void initViews() {
        oldPasswordInput = findViewById(R.id.old_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        changePasswordBtn = findViewById(R.id.login_btn);
        backButton = findViewById(R.id.BackButton);
    }
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        changePasswordBtn.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)){
            oldPasswordInput.setError("Vui lòng nhập mật khẩu cũ");
            oldPasswordInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newPassword)){
            newPasswordInput.setError("Vui lòng nhập mật khẩu mới");
            newPasswordInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)){
            confirmPasswordInput.setError("Vui lòng xác nhận mật khẩu");
            confirmPasswordInput.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)){
            confirmPasswordInput.setError("Mật khẩu không khớp");
            confirmPasswordInput.requestFocus();
            return;
        }
        if (oldPassword.equals(newPassword)) {
            newPasswordInput.setError("Mật khẩu mới phải khác mật khẩu cũ");
            newPasswordInput.requestFocus();
            return;
        }
        changePassword(oldPassword, newPassword);
    }
    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            return;

        }
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this,
                                                "Đổi mật khẩu thành công!",
                                                Toast.LENGTH_SHORT).show();
                                        oldPasswordInput.setText("");
                                        newPasswordInput.setText("");
                                        confirmPasswordInput.setText("");
                                        finish();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this,
                                                "Lỗi khi đổi mật khẩu: " +task1.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ChangePasswordActivity.this,
                                "Mật khẩu cũ không đúng",
                                Toast.LENGTH_SHORT).show();
                        oldPasswordInput.setError("Mật khẩu cũ không đúng");
                        oldPasswordInput.requestFocus();
                    }
                });
    }
}