package com.example.appdonghua.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class OfflineDatabaseHelper extends SQLiteOpenHelper {

    // Tên Database và Version
    private static final String DATABASE_NAME = "user_notes.db";
    private static final int DATABASE_VERSION = 1;

    // Khai báo bảng Note
    private static final String TABLE_NOTE = "notes";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_CONTENT = "content";
    private static final String COL_DATE = "date";

    public OfflineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNoteTable = "CREATE TABLE " + TABLE_NOTE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_CONTENT + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(createNoteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        onCreate(db);
    }

    // --- CÁC HÀM XỬ LÝ NOTE ---

    // 1. Thêm ghi chú
    public void addNote(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_CONTENT, content);

        // Lấy ngày giờ hiện tại
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        String currentDate = sdf.format(new java.util.Date());
        values.put(COL_DATE, currentDate);

        db.insert(TABLE_NOTE, null, values);
        db.close();
    }

    // 2. Lấy tất cả ghi chú
    public ArrayList<String> getAllNotes() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Sắp xếp ghi chú mới nhất lên đầu (ORDER BY id DESC)
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTE + " ORDER BY " + COL_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));

                list.add(title + "\n(" + date + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 3. Xóa ghi chú theo tiêu đề
    public void deleteNote(String titleWithDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        String title = titleWithDate.split("\n")[0];

        db.delete(TABLE_NOTE, COL_TITLE + " = ?", new String[]{title});
        db.close();
    }
}