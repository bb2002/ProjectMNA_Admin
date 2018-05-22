package kr.saintdev.pmnadmin.views.fragments.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.models.datas.constants.InternetConst;
import kr.saintdev.pmnadmin.models.datas.objects.AlarmObject;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.pmnadmin.models.tasks.http.HttpRequester;
import kr.saintdev.pmnadmin.models.tasks.http.HttpResponseObject;
import kr.saintdev.pmnadmin.views.activitys.MainActivity;
import kr.saintdev.pmnadmin.views.adapters.AlarmAdapter;
import kr.saintdev.pmnadmin.views.fragments.SuperFragment;
import kr.saintdev.pmnadmin.views.windows.dialog.DialogManager;
import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.pmnadmin.views.windows.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-17
 */

public class AlarmFragment extends SuperFragment {
    MainActivity control = null;

    ListView alarmListview = null;
    RelativeLayout emptyAlarmView = null;

    ArrayList<AlarmObject> alarms = null;
    DialogManager dm = null;
    AlarmAdapter adapter = null;

    OnBackgroundWorker backgroundWorker = null;

    private static final int REQUEST_UPDATE_ALARM = 0x0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_alarm, container, false);
        this.control = (MainActivity) getActivity();

        this.alarmListview = v.findViewById(R.id.alarm_list);
        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnDialogClickHandler(), "OK");
        this.emptyAlarmView = v.findViewById(R.id.alarm_empty);

        this.adapter = new AlarmAdapter();
        this.alarms = new ArrayList<>();
        this.alarmListview.setAdapter(this.adapter);

        this.adapter.setDeleteClickHandler(new OnAlarmDeleteHandler());
        this.backgroundWorker = new OnBackgroundWorker();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAlarmList();
    }

    private void updateAlarmList() {
        // 알림 데이터를 업데이트 받습니다.
        HttpRequester requester =
                new HttpRequester(InternetConst.UPDATE_MY_ALARM, null, REQUEST_UPDATE_ALARM, backgroundWorker, control);
        requester.execute();
    }

    class OnBackgroundWorker implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            try {
                if (requestCode == 0x0) {
                    // 알림 내용을 업데이트 했습니다.
                    HttpResponseObject resp = (HttpResponseObject) worker.getResult();

                    if (resp.getResponseResultCode() == InternetConst.HTTP_OK) {
                        // 이 관리자의 알림 목록을 불러왔습니다.
                        // 응답 성공
                        JSONObject body = resp.getBody();

                        int length = body.getInt("length");
                        if(length == 0) {
                            // 업데이트 목록이 없습니다.
                            emptyAlarmView.setVisibility(View.VISIBLE);
                        } else {
                            emptyAlarmView.setVisibility(View.GONE);

                            // Workspace 를 Array 에 추가 후 아답터에 뷰 합니다.
                            JSONArray myAlarms = body.getJSONArray("alarms");
                            alarms.clear();
                            adapter.clear();

                            for (int i = 0; i < myAlarms.length(); i++) {
                                JSONObject a = myAlarms.getJSONObject(i);

                                AlarmObject alarmObj = new AlarmObject(
                                        a.getInt("_id"),
                                        a.getString("alarm-title"),
                                        a.getString("alarm-content"),
                                        a.getString("alarm-target-uuid"),
                                        a.getString("alarm-sender-uuid"),
                                        a.getString("alarm-type"),
                                        a.getString("created")
                                );

                                alarms.add(alarmObj);
                            }

                            // 정의된 ArrayList 를 Adapter 에 정의합니다.
                            for (AlarmObject o : alarms) {
                                adapter.addAlarmItem(o);
                            }

                            // ListView 를 업데이트 합니다.
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        // 응답 실패
                        showErrorDialog("Internal server error.", "알림 목록을 업데이트 받을 수 없습니다.");
                    }
                } else if(requestCode == 0x1) {
                    // 알림을 삭제했습니다.
                    updateAlarmList();
                }
            } catch(JSONException jex) {
                showErrorDialog("Fatal error1", "An error occurred.\n" + jex.getMessage());
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            showErrorDialog("Fatal error2", "An error occurred.\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    class OnDialogClickHandler implements OnYesClickListener {
        @Override
        public void onClick(DialogInterface dialog) {
            dialog.dismiss();
        }
    }

    class OnAlarmDeleteHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlarmObject obj = (AlarmObject) v.getTag();

            HashMap<String, Object> args = new HashMap<>();
            args.put("alarm-id", obj.getAlarmId());

            HttpRequester deleteRequester = new HttpRequester(
                    InternetConst.DELETE_ALARM,
                    args,
                    0x1,
                    backgroundWorker,
                    control
            );
            deleteRequester.execute();
        }
    }

    private void showErrorDialog(String title, String content) {
        dm.setTitle(title);
        dm.setDescription(content);
        dm.show();
    }
}
