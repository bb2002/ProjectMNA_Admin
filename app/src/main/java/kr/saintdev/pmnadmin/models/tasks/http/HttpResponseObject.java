package kr.saintdev.pmnadmin.models.tasks.http;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-03-19
 */

public class HttpResponseObject {
    private JSONObject header = null;   // 요청 결과에 대한 해더
    private JSONObject body = null;     // 요청 결과에 대한 바디
    private JSONObject response = null; // 응답 객체

    private int responseResultCode = 0;        // 응답 코드

    public HttpResponseObject(String json) throws JSONException {
        this.response = new JSONObject(json);
        this.header = this.response.getJSONObject("header");

        if(!this.response.isNull("body")) {
            this.body = this.response.getJSONObject("body");
        }

        this.responseResultCode = this.header.getInt("code");               // 서버 처리 결과 코드 받기
    }

    public JSONObject getBody() {
        return body;
    }
    public JSONObject getHeader() { return header; }

    public int getResponseResultCode() {
        return this.responseResultCode;     // 서버에서 처리에 대한 결과 코드
    }
}
