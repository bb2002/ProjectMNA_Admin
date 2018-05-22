package kr.saintdev.pmnadmin.models.tasks.http;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-03-29
 */

public class HttpResponseObject {
    private JSONObject header = null;   // 요청 결과에 대한 해더
    private JSONObject body = null;     // 요청 결과에 대한 바디
    private JSONObject response = null; // 응답 객체

    private int responseResultCode = 0;        // 응답 코드

    private boolean isErrorOccurred = false;
    private String errorMessage = null;

    public HttpResponseObject(String json) throws JSONException {
        this.response = new JSONObject(json);
        this.header = this.response.getJSONObject("header");

        // body 를 받습니다.
        try {
            if (!this.response.isNull("body")) {
                this.body = this.response.getJSONObject("body");
            }
        } catch(JSONException jex) {
            this.body = null;
        }

        // error 가 발생했는지 확인합니다
        this.isErrorOccurred = this.header.getBoolean("error-occurred");
        if(this.isErrorOccurred) {
            this.errorMessage = this.header.getString("error-message");
        }

        this.responseResultCode = this.header.getInt("code");               // 서버 처리 결과 코드 받기
    }

    public JSONObject getBody() {
        return body;
    }
    public JSONObject getHeader() { return header; }

    public boolean isErrorOccurred() {
        return isErrorOccurred;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getResponseResultCode() {
        return this.responseResultCode;     // 서버에서 처리에 대한 결과 코드
    }
}