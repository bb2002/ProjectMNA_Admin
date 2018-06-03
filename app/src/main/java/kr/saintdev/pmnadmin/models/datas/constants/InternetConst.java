package kr.saintdev.pmnadmin.models.datas.constants;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-16
 */

public interface InternetConst {
    int HTTP_OK = 200;
    int HTTP_AUTH_ERROR = 400;
    int HTTP_CLIENT_REQUEST_ERROR = 401;
    int HTTP_INTERNAL_SERVER_ERROR = 500;

    String SERVER_HOST = "http://saintdev.kr/mna/admin/";

    String CREATE_ACCOUNT = SERVER_HOST + "account/join.php";
    String AUTO_LOGIN_ACCOUNT = SERVER_HOST + "account/auto-login.php";
    String STAFF_ACCOUNT_INFO = "http://saintdev.kr/mna/staff/account/my-info.php";

    String UPDATE_MY_WORKSPACE = SERVER_HOST + "workspace/my-workspace.php";
    String CREATE_MY_WORKSPACE = SERVER_HOST + "workspace/create-workspace.php";
    String MY_WORKSPACE_STATUS = SERVER_HOST + "workspace/workspace-status.php";

    String UPDATE_MY_ALARM = SERVER_HOST + "alarm/my-alarm.php";
    String DELETE_ALARM = SERVER_HOST + "alarm/delete-alarm.php";

    String STAFF_MANAGEMENT_ALLOW = SERVER_HOST + "management/staff-allow.php";
}
