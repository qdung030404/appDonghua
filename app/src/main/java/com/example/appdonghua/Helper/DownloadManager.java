package com.example.appdonghua.Helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
    private static DownloadManager instance;
    private final Context context;
    private final ExecutorService executorService;
    private final Handler handler;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private final Map<String, DownloadTask> activeDownloads;

    private DownloadManager(Context context) {
        // Khởi tạo DownloadManager nếu cần
        this.context = context.getApplicationContext();
        this.activeDownloads = new HashMap<>();
        this.executorService = Executors.newFixedThreadPool(3);
        this.handler = new Handler(Looper.getMainLooper());
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }
    public static  synchronized DownloadManager getInstance(Context context){
        if (instance == null){
            instance = new DownloadManager(context);
        }
        return instance;
    }
    public void downloadNovel(String title, String coverUrl, String author,
                              String description, long chapterCount,
                              List<String> genres, DownloadListener listener) {

        if (activeDownloads.containsKey(title)) {
            if (listener != null) {
                listener.onError("Truyện đang được tải xuống");
            }
            return;
        }

        DownloadTask task = new DownloadTask(title, coverUrl, author, description,
                chapterCount, genres, listener);
        activeDownloads.put(title, task);
        executorService.execute(task);
    }
    public void cancelDownload(String title) {
        DownloadTask task = activeDownloads.remove(title);
        if (task != null) {
            task.cancel();
            activeDownloads.remove(title);
        }
    }
    public boolean isDownloading(String title) {
        return activeDownloads.containsKey(title);
    }
    public void checkDownloadProgress(String title, DownloadStatusListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            listener.onStatus(false, 0);
            return;
        }
        db.collection("users").document(user.getUid())
                .collection("download").document(title)
                .get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Long downloadProgress = doc.getLong("downloadProgress");
                        listener.onStatus(true, downloadProgress != null ? downloadProgress.intValue() : 0);
                    } else {
                        listener.onStatus(false, 0);
                    }
                }).addOnFailureListener(e -> listener.onStatus(false, 0));
    }
    private class DownloadTask implements Runnable {
        private final String title;
        private final String coverUrl;
        private final String author;
        private final String description;
        private final long chapterCount ;
        private final List<String> genres;
        private final DownloadListener downloadListener;
        private volatile boolean cancelled = false;


        private DownloadTask(String title, String coverUrl, String author, String description, long chapterCount, List<String> genres, DownloadListener downloadListener) {
            this.title = title;
            this.coverUrl = coverUrl;
            this.author = author;
            this.description = description;
            this.chapterCount = chapterCount;
            this.genres = genres;
            this.downloadListener = downloadListener;
        }

        public void cancel() {
            cancelled = true;
        }

        @Override
        public void run() {
            try {
                File downloadDir = new File(context.getFilesDir(), "downloads" + title);
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs();
                }
                File coverFile = new File(downloadDir, "cover.jpg");
                if (!downLoadImage(coverUrl, coverFile)) {
                    throw new Exception("Error downloading cover image");
                }
                if (cancelled) {
                    cleanDownloads(downloadDir);
                    return;
                }
                for (int i = 0; i < chapterCount && !cancelled; i++) {
                    int progress = (int) ((i + 1) * 100 / chapterCount);
                    notifyProgress(progress, "Đang tải chương " + (i + 1) + "/" + chapterCount);

                    // Giả lập download chapter
                    Thread.sleep(100); // Thời gian tải mỗi chapter
                }

                if (cancelled) {
                    cleanDownloads(downloadDir);
                    return;
                }
                saveToFirestore(coverFile.getAbsolutePath());

                // Thông báo hoàn thành
                notifySuccess();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private boolean downLoadImage(String imageUrl, File file) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    return false;
                }
                try (InputStream inputStream = connection.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(file)){
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1 && !cancelled){
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    return !cancelled;
                } catch (Exception e){
                    Log.e("DownloadManager", "Error downloading image", e);
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        private void saveToFirestore(String localCoverPath){
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;
            Map<String, Object> downloadData = new HashMap<>();
            downloadData.put("title", title);
            downloadData.put("coverImageUrl", coverUrl);
            downloadData.put("localCoverPath", localCoverPath);
            downloadData.put("author", author);
            downloadData.put("description", description);
            downloadData.put("chapterCount", chapterCount);
            downloadData.put("genre", genres);
            downloadData.put("downloadProgress", 100);
            downloadData.put("timestamp", FieldValue.serverTimestamp());
            db.collection("users").document(user.getUid())
                    .collection("download").document(title).set(downloadData);
        }
        private void cleanDownloads(File downloadDir) {
            if (downloadDir.exists()) {
                deleteRecursive(downloadDir);
            }
        }
        private void deleteRecursive(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory()) {
                File[] children = fileOrDirectory.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteRecursive(child);
                    }
                }
            }
            fileOrDirectory.delete();
        }
        private  void notifyProgress(int progress, String message){
            handler.post(() -> {
                if (downloadListener != null) {
                    downloadListener.onProgress(progress, message);
                }
            });
        }
        private void notifySuccess(){
            handler.post(() -> {
                if (downloadListener != null) {
                    downloadListener.onSuccess();
                }
            });
        }
        private void notifyError(String Error){
            handler.post(() -> {
                if (downloadListener != null) {
                    downloadListener.onError(Error);
                }
            });
        }
    }
    public interface DownloadListener {
        void onProgress(int progress, String message);
        void onSuccess();
        void onError(String error);

    }

    public interface DownloadStatusListener {
        void onStatus(boolean isDownloaded, int progress);
    }
}
