package com.example.appdonghua.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Utility class để tính toán kích thước responsive cho UI elements
 */
public class ScreenUtils {

    /**
     * Kết quả tính toán kích thước ảnh
     */
    public static class ImageDimensions {
        public int width;
        public int height;

        public ImageDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    public static  ImageDimensions calculateCarouselDimensions(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidthPx = displayMetrics.widthPixels;
        int screenHeightPx = displayMetrics.heightPixels;
        float screenWidthDp = screenWidthPx / displayMetrics.density;
        int imageWidth = 0, imageHeight = 0;

        int containerPadding = (int) (32 * displayMetrics.density);

        // Chiều rộng carousel = màn hình - padding
        imageWidth = screenWidthPx - containerPadding;

        if (screenWidthDp >= 600) {
            // Tablet: Carousel cao hơn (tỷ lệ 16:9)
            imageHeight = (int) (screenHeightPx * 0.30); // 30% chiều cao màn hình
        } else if (screenWidthDp >= 400) {
            // Phone lớn: tỷ lệ 16:9
            imageHeight = (int) (screenHeightPx * 0.25); // 25% chiều cao màn hình
        } else if (screenWidthDp >= 360) {
            // Phone trung bình
            imageHeight = (int) (screenHeightPx * 0.23); // 23% chiều cao màn hình
        } else {
            // Phone nhỏ
            imageHeight = (int) (screenHeightPx * 0.20); // 20% chiều cao màn hình
        }

        return new ImageDimensions(imageWidth, imageHeight);

    }

    /**
     * Tính toán kích thước ảnh cho horizontal scroll items (Hot Novel)
     * Dựa trên % chiều cao màn hình
     */
    public static ImageDimensions calculateHorizontalImageDimensions(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeightPx = displayMetrics.heightPixels;
        int screenWidthPx = displayMetrics.widthPixels;
        float screenWidthDp = screenWidthPx / displayMetrics.density;

        int imageWidth, imageHeight;

        if (screenWidthDp >= 600) {
            // Tablet: ảnh chiếm ~18% chiều cao màn hình
            imageHeight = (int) (screenHeightPx * 0.18);
            imageWidth = (int) (imageHeight * 0.73); // Tỷ lệ 11:15
        } else if (screenWidthDp >= 400) {
            // Phone lớn: ảnh chiếm ~16% chiều cao màn hình
            imageHeight = (int) (screenHeightPx * 0.16);
            imageWidth = (int) (imageHeight * 0.74); // Tỷ lệ 10:13.5
        } else if (screenWidthDp >= 360) {
            // Phone trung bình: ảnh chiếm ~15% chiều cao màn hình
            imageHeight = (int) (screenHeightPx * 0.15);
            imageWidth = (int) (imageHeight * 0.75); // Tỷ lệ 9:12
        } else {
            // Phone nhỏ: ảnh chiếm ~14% chiều cao màn hình
            imageHeight = (int) (screenHeightPx * 0.14);
            imageWidth = (int) (imageHeight * 0.73); // Tỷ lệ 8:11
        }

        return new ImageDimensions(imageWidth, imageHeight);
    }

    /**
     * Tính toán kích thước ảnh cho grid items (Recommended)
     * Dựa trên số cột và chiều rộng màn hình
     */
    public static ImageDimensions calculateGridImageDimensions(Context context, int spanCount) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidthPx = displayMetrics.widthPixels;

        // Tính padding tổng
        int containerPadding = (int) (32 * displayMetrics.density); // 16dp * 2 bên
        int itemPadding = (int) (16 * displayMetrics.density * spanCount); // 8dp * 2 * spanCount
        int totalPadding = containerPadding + itemPadding;

        // Tính chiều rộng mỗi item
        int imageWidth = (screenWidthPx - totalPadding) / spanCount;

        // Chiều cao = chiều rộng * 1.5 (tỷ lệ 2:3)
        int imageHeight = (int) (imageWidth * 1.5);

        return new ImageDimensions(imageWidth, imageHeight);
    }

    /**
     * Tính toán kích thước ảnh cho ranking items
     */
    public static ImageDimensions calculateRankingImageDimensions(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;

        int imageWidth, imageHeight;

        if (screenWidthDp >= 600) {
            // Tablet
            imageWidth = (int) (80 * displayMetrics.density);
            imageHeight = (int) (110 * displayMetrics.density);
        } else if (screenWidthDp >= 400) {
            // Phone lớn
            imageWidth = (int) (70 * displayMetrics.density);
            imageHeight = (int) (100 * displayMetrics.density);
        } else if (screenWidthDp >= 360) {
            // Phone trung bình
            imageWidth = (int) (60 * displayMetrics.density);
            imageHeight = (int) (90 * displayMetrics.density);
        } else {
            // Phone nhỏ
            imageWidth = (int) (55 * displayMetrics.density);
            imageHeight = (int) (80 * displayMetrics.density);
        }

        return new ImageDimensions(imageWidth, imageHeight);
    }

    /**
     * Lấy text size phù hợp dựa trên loại text và kích thước màn hình
     */
    public static class TextSize {
        public float title;
        public float subtitle;
        public float body;
        public float caption;

        public TextSize(float title, float subtitle, float body, float caption) {
            this.title = title;
            this.subtitle = subtitle;
            this.body = body;
            this.caption = caption;
        }
    }

    /**
     * Tính toán text size responsive
     */
    public static TextSize calculateTextSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;

        if (screenWidthDp >= 600) {
            // Tablet
            return new TextSize(18, 14, 13, 12);
        } else if (screenWidthDp >= 400) {
            // Phone lớn
            return new TextSize(16, 12, 12, 11);
        } else if (screenWidthDp >= 360) {
            // Phone trung bình
            return new TextSize(15, 11, 11, 10);
        } else {
            // Phone nhỏ
            return new TextSize(14, 10, 10, 9);
        }
    }

    /**
     * Kiểm tra xem có phải tablet không
     */
    public static boolean isTablet(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return screenWidthDp >= 600;
    }

    /**
     * Lấy screen width trong dp
     */
    public static float getScreenWidthDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density;
    }

    /**
     * Lấy screen height trong dp
     */
    public static float getScreenHeightDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels / displayMetrics.density;
    }

    /**
     * Convert dp sang px
     */
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    /**
     * Convert px sang dp
     */
    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (px / displayMetrics.density);
    }
}