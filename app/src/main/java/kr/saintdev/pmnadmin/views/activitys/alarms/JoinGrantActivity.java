package kr.saintdev.pmnadmin.views.activitys.alarms;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
import kr.saintdev.pmnadmin.views.windows.dialog.TimePickerDialog;
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
    EditText moneyEditor = null;

    OnBackgroundWorkHandler backgroundHandler = null;
    ArrayAdapter workspaceAdapter = null;

    DialogManager dm = null;
    ProgressManager pm = null;
    TimePickerDialog tmPicker = null;

    String staffUUID = null;        // 직원 UUID
    ArrayList<WorkspaceObject> workspaces = null;

    private static final int REQUEST_STAFF_INFO = 0x0;
    private static final int REQUEST_MY_WORKSPACE = 0x1;
    private static final int REQUEST_ALLOW_STAFF = 0x2;

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
        this.moneyEditor = findViewById(R.id.grant_staff_money);

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
                finish();
            }
        }, "OK");
        this.pm = new ProgressManager(this);
        this.tmPicker = new TimePickerDialog(this);
        this.tmPicker.setOnDismissListener(new OnTimePickedHandler());


        // 내 근무지를 불러옵니다.
        HttpRequester requester =
                new HttpRequester(InternetConst.UPDATE_MY_WORKSPACE, null, REQUEST_MY_WORKSPACE, backgroundHandler, this);
        requester.execute();

        // 직원 정보도 불러온다.
        Intent intent = getIntent();
        this.staffUUID = intent.getStringExtra("sender");

        HashMap<String, Object> args = new HashMap<>();
        args.put("stf-uuid", staffUUID);

        HttpRequester staffRequester =
                new HttpRequester(InternetConst.STAFF_ACCOUNT_INFO, args, REQUEST_STAFF_INFO, backgroundHandler, this);
        staffRequester.execute();
        pm.enable();
    }

    /**
     * 버튼을 눌렀을 경우 처리
     */
    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.grant_part_start:     // 파트 시작 시간 설정
                    tmPicker.setCurrectId(0x0);
                    tmPicker.show();
                    break;
                case R.id.grant_part_stop:      // 파트 종료 시간 설정
                    tmPicker.setCurrectId(0x1);
                    tmPicker.show();
                    break;
                case R.id.grant_account_allow:  // 직원 승인
                    requestAllow();
                    break;
                case R.id.grant_account_deny:   // 요청 거부

                    finish();
                    break;
            }
        }

        private void requestAllow() {
            if(moneyEditor.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "급여를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> args = new HashMap<>();
                args.put("staff-uuid", staffUUID);
                args.put("staff-start-time", setStartTime.getText());
                args.put("staff-stop-time", setStopTime.getText());
                WorkspaceObject obj = workspaces.get(workspaceList.getSelectedItemPosition());
                args.put("staff-workspace", obj.getWorkspaceUUID());
                args.put("staff-money", moneyEditor.getText());

                HttpRequester requester =
                        new HttpRequester(InternetConst.STAFF_MANAGEMENT_ALLOW, args, REQUEST_ALLOW_STAFF, backgroundHandler, getApplicationContext());
                requester.execute();
                pm.setMessage("승인 중 입니다...");
                pm.enable();
            }
        }
    }

    /**
     * TimePicker 가 닫혔을 경우
     */
    class OnTimePickedHandler implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
            switch(tmPicker.getCurrectId()) {
                case 0x0:
                    setStartTime.setText(tmPicker.getPickedTime());
                    break;
                case 0x1:
                    setStopTime.setText(tmPicker.getPickedTime());
                    break;
            }
        }
    }

    /**
     * 백그라운드 콜백
     */
    class OnBackgroundWorkHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject respObj = (HttpResponseObject) worker.getResult();

            pm.disable();

            if(respObj.getResponseResultCode() == InternetConst.HTTP_OK) {
                switch (requestCode) {
                    case REQUEST_MY_WORKSPACE:
                        updateWorkspace(respObj.getBody());
                        break;
                    case REQUEST_STAFF_INFO:        // 직원 정보를 불러왔습니다.
                        updateStaffInfo(respObj.getBody());
                        break;
                    case REQUEST_ALLOW_STAFF:       // 직원을 승인하였습니다.
                        if(respObj.getResponseResultCode() == InternetConst.HTTP_OK) {
                            if(respObj.isErrorOccurred()) {
                                dm.setTitle("An error occurred");
                                dm.setDescription(respObj.getErrorMessage());
                                dm.show();
                            } else {
                                dm.setTitle("승인 완료");
                                dm.setDescription("직원을 승인하였습니다.\n직원은 앱을 실행하여 확인 할 수 있습니다.");
                                dm.show();
                            }
                        } else {
                            dm.setTitle("Internal server error.");
                            dm.setDescription("Code : " + respObj.getResponseResultCode());
                            dm.show();
                        }
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
            pm.disable();

            dm.setTitle("Fatal error");
            dm.setDescription("An error occurred.\n" + ex.getMessage());
            dm.show();
        }

        private void updateWorkspace(JSONObject body) {
            // Workspace 를 Array 에 추가 후 아답터에 뷰 합니다.
            try {
                JSONArray myWorkspaces = body.getJSONArray("workspaces");
                workspaces = new ArrayList<>();

                for (int i = 0; i < myWorkspaces.length(); i++) {
                    JSONObject ws = myWorkspaces.getJSONObject(i);
                    WorkspaceObject workObj = new WorkspaceObject(
                            ws.getString("workspace-name"),
                            ws.getString("workspace-uuid"),
                            ws.getString("created")
                    );

                    workspaces.add(workObj);
                    workspaceAdapter.add(workObj.getWorkspaceName());
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
