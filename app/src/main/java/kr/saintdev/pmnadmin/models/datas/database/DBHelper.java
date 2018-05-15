package kr.saintdev.pmnadmin.models.datas.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-15
 */

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase readDB = null;
    private SQLiteDatabase writeDB = null;

    public DBHelper(Context context) {
        super(context, "project_sct", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLQuerys.ADMIN_PROFILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void open() {
        // read 와 write 할 수 있는 db 객체를 가져옵니다.
        this.readDB = getReadableDatabase();
        this.writeDB = getWritableDatabase();
    }

    public Cursor sendReadableQuery(String query) {
        return this.readDB.rawQuery(query, null);
    }

    public void sendWriteableQuery(String query) {
        this.writeDB.execSQL(query);
    }

    public SQLiteDatabase getReadDB() {
        return readDB;
    }

    public SQLiteDatabase getWriteDB() {
        return writeDB;
    }

    public interface SQLQuerys {
        String ADMIN_PROFILE = "CREATE TABLE mna_admin_profile (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "adm_kakao_id TEXT NOT NULL," +
                "adm_kakao_nick TEXT NOT NULL," +
                "adm_kakao_profile_icon TEXT NOT NULL," +
                "adm_mna_uuid TEXT NOT NULL," +
                "adm_mna_public_id TEXT NOT NULL)";
    }
}
