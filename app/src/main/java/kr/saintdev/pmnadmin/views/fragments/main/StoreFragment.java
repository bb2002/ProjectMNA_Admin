package kr.saintdev.pmnadmin.views.fragments.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
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
import kr.saintdev.pmnadmin.views.fragments.SuperFragment;
import kr.saintdev.pmnadmin.views.windows.dialog.DialogManager;
import kr.saintdev.pmnadmin.views.windows.dialog.TextEditorDialog;
import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnYesClickListener;
import kr.saintdev.pmnadmin.views.windows.progress.ProgressManager;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-17
 */

public class StoreFragment extends SuperFragment {
    MainActivity control = null;

    ListView workspaceListview = null;
    ArrayAdapter workspaceAdapter = null;
    Button createStore = null;

    ArrayList<WorkspaceObject> workspaces = null;
    ProgressManager pm = null;
    DialogManager dm = null;

    private static final int REQUEST_UPDATE_WORKSPACE = 0x0;        // 내 사업장 정보를 업데이트 한다.
    private static final int REQUEST_CREATE_WORKSPACE = 0x1;        // 새 사업장을 만든다.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_workspace, container, false);
        this.control = (MainActivity) getActivity();

        this.workspaceListview = v.findViewById(R.id.workspace_listview);
        this.createStore = v.findViewById(R.id.workspace_create_button);
        this.createStore.setOnClickListener(new OnButtonClickHandler());
        this.workspaceAdapter = new ArrayAdapter(control, android.R.layout.simple_list_item_1);

        this.workspaceListview.setAdapter(this.workspaceAdapter);
        this.pm = new ProgressManager(control);
        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnDialogClickHandler(), "OK");
        this.workspaces = new ArrayList<>();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWorkspaceList();
    }

    private void updateWorkspaceList() {
        // 자신의 사업장 정보를 가져옵니다.
        this.pm.setMessage("사업장 정보를 가져오는 중...");
        HttpRequester requester =
                new HttpRequester(InternetConst.UPDATE_MY_WORKSPACE, null, REQUEST_UPDATE_WORKSPACE, new OnBackgroundHandler(), control);
        requester.execute();
    }

    class OnButtonClickHandler implements View.OnClickListener, DialogInterface.OnDismissListener {
        TextEditorDialog dialog = null;

        @Override
        public void onClick(View v) {
            // 새 사업장을 만들려고 합니다.
            // 이름을 입력 받습니다.
            this.dialog = new TextEditorDialog(control, null);
            this.dialog.setTitle("새 매장 생성");
            this.dialog.show();
            this.dialog.setOnDismissListener(this);
        }

        @Override
        public void onDismiss(DialogInterface a) {
            String workspaceName = dialog.getData();

            if(workspaceName != null) {
                // 새 작업 공간을 만듭니다.
                HashMap<String, Object> args = new HashMap<>();
                args.put("workspace-name", workspaceName);
                HttpRequester requester =
                        new HttpRequester(InternetConst.CREATE_MY_WORKSPACE, args, REQUEST_CREATE_WORKSPACE, new OnBackgroundHandler(), control);
                requester.execute();
            }
        }
    }

    class OnBackgroundHandler implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject resp = (HttpResponseObject) worker.getResult();

            try {
                if(requestCode == REQUEST_UPDATE_WORKSPACE) {
                    if (resp.getResponseResultCode() == InternetConst.HTTP_OK) {
                        // 이 운영자의 Workspace 정보를 불러왔습니다.
                        JSONObject body = resp.getBody();

                        // Workspace 를 Array 에 추가 후 아답터에 뷰 합니다.
                        JSONArray myWorkspaces = body.getJSONArray("workspaces");
                        workspaces.clear();
                        workspaceAdapter.clear();

                        for (int i = 0; i < myWorkspaces.length(); i++) {
                            JSONObject ws = myWorkspaces.getJSONObject(i);
                            WorkspaceObject workObj = new WorkspaceObject(
                                    ws.getString("workspace-name"),
                                    ws.getString("workspace-uuid"),
                                    ws.getString("created")
                            );

                            workspaces.add(workObj);
                        }

                        // 정의된 ArrayList 를 Adapter 에 정의합니다.
                        for (WorkspaceObject o : workspaces) {
                            workspaceAdapter.add(o.getWorkspaceName());
                        }

                        // ListView 를 업데이트 합니다.
                        workspaceAdapter.notifyDataSetChanged();
                    } else if(requestCode == REQUEST_CREATE_WORKSPACE){
                        dm.setTitle("Fatal error");
                        dm.setDescription("Internal server error.\n" + resp.getResponseResultCode());
                        dm.show();
                    }
                } else if(requestCode == REQUEST_CREATE_WORKSPACE) {
                    // 새로운 작업 영역을 만들었습니다.
                    if(resp.getResponseResultCode() == InternetConst.HTTP_OK) {
                        // 생성 성공
                        updateWorkspaceList();
                    } else {
                        // 오류 발생
                        dm.setTitle("Internal server error.");
                        dm.setDescription("작업 영역 생성에 실패했습니다!");
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

        }
    }

    class OnDialogClickHandler implements OnYesClickListener {
        @Override
        public void onClick(DialogInterface dialog) {

        }
    }
}
