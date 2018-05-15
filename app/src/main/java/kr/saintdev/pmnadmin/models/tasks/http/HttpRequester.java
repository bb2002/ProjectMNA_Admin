package kr.saintdev.pmnadmin.models.tasks.http;

import android.content.Context;

import java.util.HashMap;
import java.util.Iterator;

import kr.saintdev.pmnadmin.models.datas.profile.MeProfile;
import kr.saintdev.pmnadmin.models.datas.profile.MeProfileManager;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-04-22
 */

public class HttpRequester extends BackgroundWork<HttpResponseObject> {
    private String url = null;
    private HashMap<String, Object> param = null;
    private MeProfileManager profileManager = null;

    public HttpRequester(String url, HashMap<String, Object> args, int requestCode, OnBackgroundWorkListener listener, Context context) {
        super(requestCode, listener);
        this.url = url;
        this.param = args;

        // 인증서 관련 데이터를 가지고 있다.
        this.profileManager = MeProfileManager.getInstance(context);
    }

    @Override
    protected HttpResponseObject script() throws Exception {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder reqBuilder = new FormBody.Builder();

        // 사용자 인증 값을 넣습니다.
        MeProfile profile = this.profileManager.getProfile();

        if(profile != null) {
            // 인증서가 있을 때 값을 넣습니다.
            reqBuilder.add("X-kakao-id", profile.getKakaoID());
            reqBuilder.add("X-mna-uuid", profile.getMnaUUID());
        }

        // 인자 값이 있다면 넣어줍니다.
        if(param != null) {
            Iterator keyIterator = param.keySet().iterator();

            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                Object value = param.get(key);

                if(value == null) {
                    reqBuilder.add(key, "null");
                } else {
                    reqBuilder.add(key, value.toString());
                }
            }
        }

        RequestBody reqBody = reqBuilder.build();
        Request request = new Request.Builder().url(this.url).post(reqBody).build();

        Response response = client.newCall(request).execute();
        String jsonScript = response.body().string();

        HttpResponseObject responseObj = new HttpResponseObject(jsonScript);

        // 응답 완료
        response.close();

        return responseObj;
    }
}
