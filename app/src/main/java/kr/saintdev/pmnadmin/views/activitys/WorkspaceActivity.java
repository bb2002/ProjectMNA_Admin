package kr.saintdev.pmnadmin.views.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.models.datas.constants.EmbeddedConst;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.pmnadmin.models.tasks.raspi.EmbeddedGet;
import kr.saintdev.pmnadmin.models.tasks.raspi.EmbeddedPost;
import kr.saintdev.pmnadmin.views.windows.dialog.DialogManager;
import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.pmnadmin.views.windows.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-23
 */

public class WorkspaceActivity extends AppCompatActivity {
    ImageButton[] lightSwitch = null;   // 전등 스위치       [홀 주방 야외]
    ImageButton[] doorSwitch = null;    // 도어록 스위치     [잠금 해제]
    TextView tempView = null;       // 온도를 표시 합니다.
    TextView humiView = null;       // 습도를 표시합니다.
    TextView workspaceNameView = null;  // 작업장 이름을 표시합니다.

    DialogManager dm = null;
    ProgressManager pm = null;

    boolean[] lightStatus = null;   // 전등 상태를 논리적으로 저장
    boolean isDoorLocked = false;   // 잠금 상태를 논리적으로 표현

    private static final int REQUEST_CONTROLL_HALL_LIGHT = 0x0;     // 홀
    private static final int REQUEST_CONTROLL_KITCHEN_LIGHT = 0x1;  // 주방
    private static final int REQUEST_CONTROLL_TERRACE_LIGHT = 0x2;  // 야외
    private static final int REQUEST_CONTROL_DOOR = 0x3;            // 문
    private static final int REQUEST_STATUS = 0x4;

    OnBackgroundWorkHandler handler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);

        this.lightSwitch = new ImageButton[] {
                findViewById(R.id.workspace_light_holl),
                findViewById(R.id.workspace_light_kitchen),
                findViewById(R.id.workspace_light_terrace)
        };
        this.doorSwitch = new ImageButton[] {
                findViewById(R.id.workspace_door_lock),
                findViewById(R.id.workspace_door_open)
        };
        this.tempView = findViewById(R.id.workspace_air_temp);
        this.humiView = findViewById(R.id.workspace_air_humi);
        this.workspaceNameView = findViewById(R.id.workspace_title_view);

        this.dm = new DialogManager(this);
        this.dm.setOnYesButtonClickListener(new OnYesClickListener() {
            @Override
            public void onClick(DialogInterface dialog) {
                dialog.dismiss();
            }
        }, "OK");
        this.pm = new ProgressManager(this);
        this.pm.setMessage("Loading, please wait...");

        OnButtonClickHandler handler = new OnButtonClickHandler();
        this.lightSwitch[0].setOnClickListener(handler);
        this.lightSwitch[1].setOnClickListener(handler);
        this.lightSwitch[2].setOnClickListener(handler);
        this.doorSwitch[0].setOnClickListener(handler);
        this.doorSwitch[1].setOnClickListener(handler);

        Intent intent = getIntent();
        String name = intent.getStringExtra("workspace-name");
        this.workspaceNameView.setText(name);

        // 백그라운드 핸들러를 생성합니다.
        this.handler = new OnBackgroundWorkHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateWorkspaceStatus();
    }

    private void updateWorkspaceStatus() {
        // 라즈베리파이에 현재 상태값을 요청합니다.
        EmbeddedGet requestGet = new EmbeddedGet(EmbeddedConst.OFFICE_STATUS, REQUEST_STATUS, handler);
        requestGet.execute();
        pm.enable();
    }

    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            EmbeddedPost requester = null;

            switch(v.getId()) {
                case R.id.workspace_light_holl:
                    requester = new EmbeddedPost(EmbeddedConst.OFFICE_CONTROL_HALL_LIGHT, !lightStatus[0], REQUEST_CONTROLL_HALL_LIGHT, handler);
                    break;
                case R.id.workspace_light_kitchen:
                    requester = new EmbeddedPost(EmbeddedConst.OFFICE_CONTROL_KITCHEN_LIGHT, !lightStatus[1], REQUEST_CONTROLL_KITCHEN_LIGHT, handler);
                    break;
                case R.id.workspace_light_terrace:
                    requester = new EmbeddedPost(EmbeddedConst.OFFICE_CONTROL_TERRACE_LIGHT, !lightStatus[2], REQUEST_CONTROLL_TERRACE_LIGHT, handler);
                    break;
                case R.id.workspace_door_lock:
                    requester = new EmbeddedPost(EmbeddedConst.OFFICE_CONTROL_DOOR, true, REQUEST_CONTROL_DOOR, handler);
                    break;
                case R.id.workspace_door_open:
                    requester = new EmbeddedPost(EmbeddedConst.OFFICE_CONTROL_DOOR, false, REQUEST_CONTROL_DOOR, handler);
                    break;
                default: return;
            }

            requester.execute();
            pm.enable();
        }
    }

    class OnBackgroundWorkHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            pm.disable();

            try {
                switch (requestCode) {
                    case REQUEST_STATUS:        // 현재 스테이터스 값을 업데이트 하였다.
                        JSONObject result = (JSONObject) worker.getResult();
                        lightStatus = new boolean[]{
                                result.getInt("hall") != 0,
                                result.getInt("kitchen") != 0,
                                result.getInt("terrace") != 0
                        };
                        isDoorLocked = result.getInt("door_lock") != 0;

                        tempView.setText("온도 : " + result.getString("temp"));
                        humiView.setText("습도 : " + result.getString("humi"));
                        updateView();
                        break;
                    case REQUEST_CONTROLL_HALL_LIGHT:       // 다른 모든 수정 업데이트에 대하여
                    case REQUEST_CONTROLL_KITCHEN_LIGHT:
                    case REQUEST_CONTROLL_TERRACE_LIGHT:
                    case REQUEST_CONTROL_DOOR:
                        if((boolean)worker.getResult()) {
                            updateWorkspaceStatus();
                        } else {
                            Toast.makeText(getApplicationContext(), "조작에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    default:
                        return;
                }
            } catch (JSONException jex) {
                dm.setTitle("An error occurred");
                dm.setDescription("JSON 파싱에 실패했습니다.\n" + jex.getMessage());
                dm.show();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            pm.disable();

            dm.setTitle("An error occurred");
            dm.setDescription(ex.getMessage());
            dm.show();
        }

        private void updateView() {
            for(int i = 0; i < lightStatus.length; i ++)
                lightSwitch[i].setImageResource(lightStatus[i] ? R.drawable.ic_led_up : R.drawable.ic_led_off);

            // Door 에 대한 처리
            if(isDoorLocked) {
                doorSwitch[0].setBackgroundColor(Color.rgb(76,140,245));
                doorSwitch[1].setBackgroundColor(Color.WHITE);
            } else {
                doorSwitch[1].setBackgroundColor(Color.rgb(76,140,245));
                doorSwitch[0].setBackgroundColor(Color.WHITE);
            }
        }
    }
}
