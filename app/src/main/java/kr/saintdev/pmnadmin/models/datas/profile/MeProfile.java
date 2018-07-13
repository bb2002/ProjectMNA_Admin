package kr.saintdev.pmnadmin.models.datas.profile;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-15
 */

public class MeProfile {
    private String kakaoID = null;
    private String kakaoNick = null;
    private String kakaoProfileIcon = null;
    private String mnaUUID = null;
    private String mnaPublicID = null;

    public MeProfile(String kakaoID, String kakaoNick, String kakaoProfileIcon, String mnaUUID, String mnaPublicID) {
        this.kakaoID = kakaoID;
        this.kakaoNick = kakaoNick;
        this.kakaoProfileIcon = kakaoProfileIcon;
        this.mnaUUID = mnaUUID;
        this.mnaPublicID = mnaPublicID;
    }

    public String getKakaoID() {
        return kakaoID;
    }

    public String getKakaoNick() {
        return kakaoNick;
    }

    public String getKakaoProfileIcon() {
        if(kakaoProfileIcon == null) {
            this.kakaoProfileIcon = "http://saintdev.kr/mna/user_default_icon";
        }

        return kakaoProfileIcon;
    }

    public String getMnaUUID() {
        return mnaUUID;
    }

    public String getMnaPublicID() {
        return mnaPublicID;
    }
}
