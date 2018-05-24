package kr.saintdev.pmnadmin.views.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.models.datas.constants.EmbeddedConst;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.pmnadmin.models.tasks.raspi.EmbeddedGet;
import kr.saintdev.pmnadmin.models.tasks.raspi.EmbeddedPost;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-23
 */

public class WorkspaceActivity extends AppCompatActivity {
    ImageButton[] lightSwitch = null;   // 전등 스위치
    ImageButton[] doorSwitch = null;    // 도어록 스위치
    TextView tempView = null;       // 온도를 표시 합니다.
    TextView humiView = null;       // 습도를 표시합니다.
    TextView workspaceNameView = null;  // 작업장 이름을 표시합니다.

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

        // 라즈베리파이에 현재 상태값을 요청합니다.
        EmbeddedGet requestGet = new EmbeddedGet(EmbeddedConst.OFFICE_STATUS, REQUEST_STATUS, null);
        requestGet.execute();

        lightStatus = new boolean[]{ false, false, false };
        isDoorLocked = true;
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
                default: return;
            }

            requester.execute();
        }
    }

    class OnBackgroundWorkHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            boolean result = (boolean) worker.getResult();
            Toast.makeText(getApplicationContext(), result+"", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailed(int requestCode, Exception ex) {

        }
    }
}
