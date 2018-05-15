package kr.saintdev.pmnadmin.models.datas.profile.kakao;

import android.app.Application;

import com.kakao.auth.KakaoSDK;

/**
 * Created by 5252b on 2018-03-26.
 */

public class GlobalApplication extends Application {
    private static volatile GlobalApplication obj = null;

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplication() {
        return obj;
    }
}
