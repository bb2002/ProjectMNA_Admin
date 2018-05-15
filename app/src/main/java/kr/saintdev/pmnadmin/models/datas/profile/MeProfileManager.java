package kr.saintdev.pmnadmin.models.datas.profile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import kr.saintdev.pmnadmin.models.datas.database.DBHelper;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-15
 */

public class MeProfileManager {
    private static MeProfileManager profileManager = null;

    private DBHelper dbHelper = null;

    public static MeProfileManager getInstance(Context context) {
        if(MeProfileManager.profileManager == null) {
            MeProfileManager.profileManager= new MeProfileManager(context);
        }

        return MeProfileManager.profileManager;
    }

    public MeProfileManager(Context context) {
        this.dbHelper = new DBHelper(context);
        this.dbHelper.open();
    }

    public MeProfile getProfile() {
        String sql = "SELECT * FROM mna_admin_profile ORDER BY _id DESC";
        Cursor cs = dbHelper.sendReadableQuery(sql);

        if(cs.moveToNext()) {
            String[] datas = new String[]{
                    cs.getString(1),
                    cs.getString(2),
                    cs.getString(3),
                    cs.getString(4),
                    cs.getString(5),
            };

            for(String d : datas) {
                if(d == null) {
                    return null;
                }
            }

            return new MeProfile(datas[0], datas[1], datas[2], datas[3], datas[4]);
        } else {
            return null;
        }
    }

    public void setProfile(MeProfile profile) {
        String sql;

        if(getProfile() == null) {
            sql = "INSERT INTO mna_admin_profile (adm_kakao_id, adm_kakao_nick, adm_kakao_profile_icon, adm_mna_uuid, adm_mna_public_id) VALUES(?,?,?,?,?)";
        } else {
            sql = "UPDATE mna_admin_profile SET adm_kakao_id = ?, adm_kakao_nick = ?, adm_kakao_profile_icon = ?, adm_mna_uuid = ?, adm_mna_public_id = ? WHERE 1";
        }

        SQLiteDatabase db = dbHelper.getWriteDB();
        SQLiteStatement pst = db.compileStatement(sql);
        pst.bindString(1, profile.getKakaoID());
        pst.bindString(2, profile.getKakaoNick());
        pst.bindString(3, profile.getKakaoProfileIcon());
        pst.bindString(4, profile.getMnaUUID());
        pst.bindString(5, profile.getMnaPublicID());
        pst.execute();
    }
}
