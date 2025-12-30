package com.example.appdonghua.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdonghua.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {
    private ImageButton backButton;
    private LinearLayout layoutDeleteAcc, layoutChangePassword;
    private FirebaseAuth mAuth;
    private TextView loginMethodText;

    // ==================== LIFECYCLE METHODS ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        initViews();
        setListeners();

    }

    // ==================== INITIALIZATION METHODS ====================

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        layoutDeleteAcc = findViewById(R.id.layoutDeleteAcc);
        layoutChangePassword = findViewById(R.id.changePassword);
        loginMethodText = findViewById(R.id.loginMethodText);
        loginMethod();
    }

    private void setListeners() {
        backButton.setOnClickListener(v -> finish());
        layoutDeleteAcc.setOnClickListener(v -> deleteAccountDialog());
        layoutChangePassword.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, ChangePasswordActivity.class)));
    }

    // ==================== LOGIN METHOD DETECTION ====================

    private void loginMethod() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String loginMethod = "";
            for (com.google.firebase.auth.UserInfo userInfo : user.getProviderData()) {
                String providerId = userInfo.getProviderId();
                switch (providerId) {
                    case "google.com":
                        loginMethod = "Google";
                        break;
                    case "password":
                        loginMethod = "Email";
                        break;
                }
            }
            loginMethodText.setText(loginMethod);
            if (loginMethod.equals("Email")) {
                layoutChangePassword.setVisibility(View.VISIBLE);
            }else {
                layoutChangePassword.setVisibility(View.GONE);
            }
        }
    }

    // ==================== DELETE ACCOUNT ====================

    private void deleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa Tài Khoản");
        builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AccountActivity.this, "Tài khoản đã được xóa thành công", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    finish();
                } else{
                    Toast.makeText(AccountActivity.this,
                            "Lỗi khi xóa tài khoản: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                    if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                        Toast.makeText(AccountActivity.this,
                                "Vui lòng đăng nhập lại để xóa tài khoản",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}