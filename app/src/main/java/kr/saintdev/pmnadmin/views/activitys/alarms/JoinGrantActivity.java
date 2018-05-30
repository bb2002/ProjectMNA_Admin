package kr.saintdev.pmnadmin.views.activitys.alarms;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.models.datas.constants.InternetConst;
import kr.saintdev.pmnadmin.models.datas.objects.WorkspaceObject;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.pmnadmin.models.tasks.http.HttpRequester;
import kr.saintdev.pmnadmin.models.tasks.http.HttpResponseObject;
import kr.saintdev.pmnadmin.views.fragments.main.StoreFragment;
import kr.saintdev.pmnadmin.views.windows.dialog.DialogManager;
import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.pmnadmin.views.windows.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-30
 */

public class JoinGrantActivity extends AppCompatActivity {
    TextView nameView = null;
    Button setStartTime = null;
    Button setStopTime = null;
    Spinner workspaceList = null;
    Button grantAllow = null;
    Button grantDeny = null;

    OnBackgroundWorkHandler backgroundHandler = null;
    ArrayAdapter workspaceAdapter = null;

    DialogManager dm = null;
    ProgressManager pm = null;

    private static final int REQUEST_STAFF_INFO = 0x0;
    private static final int REQUEST_MY_WORKSPACE = 0x1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant_join);

        this.nameView = findViewById(R.id.grant_staff_name);
        this.setStartTime = findViewById(R.id.grant_part_start);
        this.setStopTime = findViewById(R.id.grant_part_stop);
        this.workspaceList = findViewById(R.id.grant_workspace_spinner);
        this.grantAllow = findViewById(R.id.grant_account_allow);
        this.grantDeny = findViewById(R.id.grant_account_deny);

        this.backgroundHandler = new OnBackgroundWorkHandler();
        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.workspaceAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        this.setStartTime.setOnClickListener(handler);
        this.setStopTime.setOnClickListener(handler);
        this.grantAllow.setOnClickListener(handler);
        this.grantDeny.setOnClickListener(handler);
        this.workspaceList.setAdapter(this.workspaceAdapter);

        this.dm = new DialogManager(this);
        this.dm.setOnYesButtonClickListener(new OnYesClickListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                dialog.dismiss();
            }
        }, "OK");
        this.pm = new ProgressManager(this);


        // 내 근무지를 불러옵니다.
        HttpRequester requester =
                new HttpRequester(InternetConst.UPDATE_MY_WORKSPACE, null, REQUEST_MY_WORKSPACE, backgroundHandler, this);
        requester.execute();

        // 직원 정보도 불러온다.
        HashMap<String, Object> args = new HashMap<>();
        Intent intent = getIntent();
        args.put("stf-uuid", intent.getStringExtra("sender"));

        HttpRequester staffRequester =
                new HttpRequester(InternetConst.STAFF_ACCOUNT_INFO, args, REQUEST_STAFF_INFO, backgroundHandler, this);
        staffRequester.execute();
    }

    /**
     * 버튼을 눌렀을 경우 처리
     */
    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    /**
     * 백그라운드 콜백
     */
    class OnBackgroundWorkHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject respObj = (HttpResponseObject) worker.getResult();

            if(respObj.getResponseResultCode() == InternetConst.HTTP_OK) {
                switch (requestCode) {
                    case REQUEST_MY_WORKSPACE:
                        updateWorkspace(respObj.getBody());
                        break;
                    case REQUEST_STAFF_INFO:        // 직원 정보를 불러왔습니다.
                        updateStaffInfo(respObj.getBody());
                        break;
                }
            } else {
                dm.setTitle("Internal server error");
                dm.setDescription("An error occurred.");
                dm.show();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {

        }

        private void updateWorkspace(JSONObject body) {
            // Workspace 를 Array 에 추가 후 아답터에 뷰 합니다.
            try {
                JSONArray myWorkspaces = body.getJSONArray("workspaces");
                for (int i = 0; i < myWorkspaces.length(); i++) {
                    JSONObject ws = myWorkspaces.getJSONObject(i);
                    workspaceAdapter.add(ws.getString("workspace-name"));
                }
            } catch(JSONException jex) {
                jex.printStackTrace();
            }
        }

        private void updateStaffInfo(JSONObject body) {
            // 직원 이름을 가져다 표시합니다.
            try {
                JSONObject result = body.getJSONObject("result");
                String name = result.getString("kakao-nick");
                nameView.setText("이름 : " + name);
            } catch(JSONException jex) {
                jex.printStackTrace();
            }
        }
    }
}
