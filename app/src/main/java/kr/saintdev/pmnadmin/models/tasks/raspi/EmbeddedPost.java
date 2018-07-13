package kr.saintdev.pmnadmin.models.tasks.raspi;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import kr.saintdev.pmnadmin.models.datas.constants.InternetConst;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-25
 */

public class EmbeddedPost extends BackgroundWork<Boolean> {
    private String targetUrl = null;
    private boolean status = false;

    public EmbeddedPost(String reqUrl, boolean status, int requestCode, OnBackgroundWorkListener listener) {
        super(requestCode, listener);

        this.targetUrl = reqUrl;
        this.status = status;
    }

    @Override
    protected Boolean script() throws Exception {
        HttpURLConnection   conn    = null;

        OutputStream          os   = null;
        InputStream           is   = null;
        ByteArrayOutputStream baos = null;
        URL url = new URL(targetUrl);

        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setReadTimeout(5 * 1000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        JSONObject job = new JSONObject();
        job.put("status", status ? "on" : "off");

        os = conn.getOutputStream();
        os.write(job.toString().getBytes());
        os.flush();

        String response;

        int responseCode = conn.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK) {

            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData = null;
            int nLength = 0;
            while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();

            response = new String(byteData);

            if(response.equals("success")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
