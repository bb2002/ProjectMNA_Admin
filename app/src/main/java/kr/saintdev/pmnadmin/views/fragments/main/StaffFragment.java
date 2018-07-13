package kr.saintdev.pmnadmin.views.fragments.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import kr.saintdev.pmnadmin.views.activitys.MainActivity;
import kr.saintdev.pmnadmin.views.adapters.StaffInfoAdapter;
import kr.saintdev.pmnadmin.views.fragments.SuperFragment;
import kr.saintdev.pmnadmin.views.windows.dialog.ContextDialog;
import kr.saintdev.pmnadmin.views.windows.dialog.DialogManager;
import kr.saintdev.pmnadmin.views.windows.dialog.OnContextItemClickListener;
import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.pmnadmin.views.windows.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-17
 */

public class StaffFragment extends SuperFragment {
    MainActivity control = null;

    Spinner selectWorkspace = null;
    ListView staffList = null;
    RelativeLayout staffListContainer = null;
    RelativeLayout staffEmptyContainer = null;
    TextView nowWorkspaceStatus = null;

    DialogManager dm = null;        // 대화 상자
    ProgressManager pm = null;      // 진행 프로그레스 바

    ArrayAdapter workspaceAdapter = null;   // 내 사업장 목록 ListView
    StaffInfoAdapter staffAdapter = null;   // 해당 사업장 내 직원 Adapter
    OnBackgroundWorkHandler backgroundWorkHandler = null;
    ArrayList<WorkspaceObject> myWorkspaceArray = null;     // 내 작업장 객체

    private static final int REQUEST_UPDATE_WORKSPACE = 0x0;        // 내 사업장 정보를 업데이트 한다.
    private static final int REQUEST_UPDATE_STAFF_INFO = 0x1;       // 사업장 내 직원 정보를 업데이트 한다.
    private static final int REQUEST_SEND_MONEYOK_ALARM = 0x2;      // 월급 입금 완료 안내 발송

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_staff, container, false);
        this.control = (MainActivity) getActivity();

        // 뷰 객체를 찾습니다.
        this.selectWorkspace = v.findViewById(R.id.staff_workspace_spinner);
        this.staffList = v.findViewById(R.id.staff_info_listview);
        this.staffListContainer = v.findViewById(R.id.staff_info_container);
        this.staffEmptyContainer = v.findViewById(R.id.staff_empty_view);
        this.nowWorkspaceStatus = v.findViewById(R.id.staff_info_nowworking);
        this.selectWorkspace.setOnItemSelectedListener(new OnWorkspaceSelectHandler());

        // 대화창을 생성합니다.
        this.backgroundWorkHandler = new OnBackgroundWorkHandler();
        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnDialogButtonClickHandler(), "OK");
        this.pm = new ProgressManager(control);

        // 아답터를 설정합니다.
        this.workspaceAdapter = new ArrayAdapter(control, android.R.layout.simple_spinner_dropdown_item);
        this.staffAdapter = new StaffInfoAdapter();
        this.selectWorkspace.setAdapter(this.workspaceAdapter);     // Adapter 를 설정합니다.
        this.staffList.setAdapter(this.staffAdapter);
        this.staffList.setOnItemClickListener(new OnStaffSelectListener());

        this.myWorkspaceArray = new ArrayList<>();

        // 사업장 정보를 업데이트 합니다.
        updateWorkspaceList();

        return v;
    }

    class OnWorkspaceSelectHandler implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // 해당 workspace 에서 근무하는 staff 를 불러옵니다.
            HashMap<String, Object> args = new HashMap<>();
            WorkspaceObject workspaceObject = myWorkspaceArray.get(position);
            args.put("workspace-uuid", workspaceObject.getWorkspaceUUID());

            HttpRequester requester =
                    new HttpRequester(InternetConst.MY_WORKSPACE_STATUS, args, REQUEST_UPDATE_STAFF_INFO, backgroundWorkHandler, control);
            requester.execute();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class OnBackgroundWorkHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject resp = (HttpResponseObject) worker.getResult();

            try {
                if (requestCode == REQUEST_UPDATE_WORKSPACE) {
                    if (resp.getResponseResultCode() == InternetConst.HTTP_OK) {
                        // 이 운영자의 Workspace 정보를 불러왔습니다.
                        JSONObject body = resp.getBody();

                        int length = body.getInt("length");
                        if(length == 0) {
                            // 이 사용자는 Workspace 가 없습니다.
                            myWorkspaceArray.clear();

                            staffEmptyContainer.setVisibility(View.VISIBLE);
                            staffListContainer.setVisibility(View.INVISIBLE);
                        } else {
                            staffEmptyContainer.setVisibility(View.INVISIBLE);
                            staffListContainer.setVisibility(View.VISIBLE);

                            // Workspace 를 Array 에 추가 후 아답터에 뷰 합니다.
                            JSONArray myWorkspaces = body.getJSONArray("workspaces");
                            for(int i = 0; i < myWorkspaces.length(); i ++) {
                                JSONObject ws = myWorkspaces.getJSONObject(i);
                                WorkspaceObject workObj = new WorkspaceObject(
                                        ws.getString("workspace-name"),
                                        ws.getString("workspace-uuid"),
                                        ws.getString("created")
                                );

                                myWorkspaceArray.add(workObj);
                            }
                        }

                        // 정의된 ArrayList 를 Adapter 에 정의합니다.
                        workspaceAdapter.clear();
                        for(WorkspaceObject o : myWorkspaceArray) {
                            workspaceAdapter.add(o.getWorkspaceName());
                        }

                        // ListView 를 업데이트 합니다.
                        workspaceAdapter.notifyDataSetChanged();
                    } else {
                        dm.setTitle("Internal server error.");
                        dm.setDescription("An error occurred. : " + resp.getResponseResultCode());
                        dm.show();
                    }
                } else if (requestCode == REQUEST_UPDATE_STAFF_INFO) {
                    // 해당 Workspace 의 직원 정보를 불러왔습니다.
                    if(resp.getResponseResultCode() == InternetConst.HTTP_OK) {
                        // 응답 성공
                        JSONObject body = resp.getBody();
                        staffAdapter.clear();

                        int workspaceStaffSize = body.getInt("workspace-staff-size");
                        int workspaceWorkingSize = body.getInt("workspace-working-staff");
                        JSONArray datas = body.getJSONArray("data");

                        nowWorkspaceStatus.setText(workspaceWorkingSize + "/" + workspaceStaffSize + " 명 근무중");

                        for(int i = 0; i < datas.length(); i ++) {
                            JSONObject d = datas.getJSONObject(i);
                            staffAdapter.addItem(d);
                        }

                        staffAdapter.notifyDataSetChanged();
                    } else {
                        dm.setTitle("Internal server error.");
                        dm.setDescription("An error occurred. : " + resp.getResponseResultCode());
                        dm.show();
                    }
                } else if(requestCode == REQUEST_SEND_MONEYOK_ALARM) {
                    if(resp.getResponseResultCode() == InternetConst.HTTP_OK) {
                        Toast.makeText(control, "메세지가 발송되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        dm.setTitle("Internal server error.");
                        dm.setDescription("An error occurred. : " + resp.getResponseResultCode());
                        dm.show();
                    }
                }
            } catch(Exception ex) {
                // 예외 발생
                dm.setTitle("Fatal error");
                dm.setDescription("An error occurred.\n" + ex.getMessage());
                dm.show();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            dm.setTitle("Fatal error");
            dm.setDescription(ex.getMessage());
            dm.show();
        }
    }

    private ContextDialog dialog = null;
    class OnStaffSelectListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dialog = new ContextDialog(control, "직원 설정", new String[] { "월급 입금 메세지" },  new OnContextItemClick(position));
            dialog.show();
        }

        class OnContextItemClick implements OnContextItemClickListener {
            private int pos = 0;

            OnContextItemClick(int pos) {
                this.pos = pos;
            }

            @Override
            public void onItemClick(int position) {
                switch(position) {
                    case 0:         // 월급 입금 메세지를 전송한다.
                        sendMoneyOkMessage(this.pos);
                        break;
                }
            }
        }

        private void sendMoneyOkMessage(int targetStaff) {
            // 해당 직원 정보를 불러와서 송금 완료 메세지를 보낸다.
            JSONObject data = staffAdapter.getItem(targetStaff);

            try {
                String staffUUID = data.getString("staff-uuid");

                HashMap<String, Object> args = new HashMap<>();
                args.put("target-staff-uuid", staffUUID);
                args.put("alarm-title", "급여 송금 완료");
                args.put("alarm-content", "월급이 입금되었습니다.\n통장을 확인해보세요.");
                args.put("alarm-type", "money");

                HttpRequester requester =
                        new HttpRequester(InternetConst.SEND_ALARM, args, REQUEST_SEND_MONEYOK_ALARM, new OnBackgroundWorkHandler(), control);
                requester.execute();

                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch(JSONException ex) {
                Toast.makeText(control, "직원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class OnDialogButtonClickHandler implements OnYesClickListener {
        @Override
        public void onClick(DialogInterface dialog) {
            dialog.dismiss();
        }
    }

    private void updateWorkspaceList() {
        // 자신의 사업장 정보를 가져옵니다.
        this.pm.setMessage("사업장 정보를 가져오는 중...");
        HttpRequester requester =
                new HttpRequester(InternetConst.UPDATE_MY_WORKSPACE, null, REQUEST_UPDATE_WORKSPACE, this.backgroundWorkHandler, control);
        requester.execute();
    }
}
