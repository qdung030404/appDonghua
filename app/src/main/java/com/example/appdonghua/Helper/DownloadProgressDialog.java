package com.example.appdonghua.Helper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.appdonghua.R;

/**
 * Helper class để tạo và quản lý Download Progress Dialog
 */
public class DownloadProgressDialog {
    private AlertDialog dialog;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private TextView tvMessage;
    private OnCancelListener cancelListener;

    public interface OnCancelListener {
        void onCancel();
    }

    public DownloadProgressDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(
                R.layout.dialog_download_progress, null);

        progressBar = dialogView.findViewById(R.id.progressBar);
        tvProgress = dialogView.findViewById(R.id.tvProgress);
        tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Cấu hình ProgressBar
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancel();
            }
            dismiss();
        });

        builder.setView(dialogView);
        builder.setCancelable(false);
        dialog = builder.create();
    }

    /**
     * Hiển thị dialog
     */
    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * Ẩn dialog
     */
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Cập nhật progress (0-100)
     */
    public void setProgress(int progress) {
        if (progressBar != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                progressBar.setProgress(progress, true);
            } else {
                progressBar.setProgress(progress);
            }

            if (tvProgress != null) {
                tvProgress.setText(progress + "%");
            }
        }
    }

    /**
     * Cập nhật message
     */
    public void setMessage(String message) {
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
    }

    /**
     * Cập nhật cả progress và message
     */
    public void update(int progress, String message) {
        setProgress(progress);
        setMessage(message);
    }

    /**
     * Set listener cho nút Cancel
     */
    public void setOnCancelListener(OnCancelListener listener) {
        this.cancelListener = listener;
    }

    /**
     * Kiểm tra dialog có đang hiển thị không
     */
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    /**
     * Reset về trạng thái ban đầu
     */
    public void reset() {
        setProgress(0);
        setMessage("Đang chuẩn bị...");
    }
}