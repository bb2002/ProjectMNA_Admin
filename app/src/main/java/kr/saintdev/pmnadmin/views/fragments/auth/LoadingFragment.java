package kr.saintdev.pmnadmin.views.fragments.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import org.json.JSONObject;

import java.util.HashMap;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.models.datas.constants.InternetConst;
import kr.saintdev.pmnadmin.models.datas.profile.MeProfile;
import kr.saintdev.pmnadmin.models.datas.profile.MeProfileManager;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.pmnadmin.models.tasks.http.HttpRequester;
import kr.saintdev.pmnadmin.models.tasks.http.HttpResponseObject;
import kr.saintdev.pmnadmin.views.activitys.LoadingActivity;
import kr.saintdev.pmnadmin.views.activitys.MainActivity;
import kr.saintdev.pmnadmin.views.fragments.SuperFragment;
import kr.saintdev.pmnadmin.views.windows.dialog.DialogManager;
import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnYesClickListener;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-15
 */

public class LoadingFragment extends SuperFragment {
    LoadingActivity control = null;
    MeProfileManager profileManager = null;

    DialogManager dm = null;

    private static final int REQUEST_CREATE_ACCOUNT = 0x0;
    private static final int REQUEST_AUTO_LOGIN = 0x1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_auth_loading, container, false);
        this.control = (LoadingActivity) getActivity();
        this.control.setActionBarTitle(null);

        this.profileManager = MeProfileManager.getInstance(control);

        this.dm = new DialogManager(control);
        this.dm.setOnYesButtonClickListener(new OnDialogDismissHandler(), "OK");

        // 카카오 로그인을 시도합니다.
        UserManagement.getInstance().requestMe(new OnKakaoLoginHandler());
        return v;
    }



    class OnKakaoLoginHandler extends MeResponseCallback {
        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            onNotSignedUp();
        }

        @Override
        public void onNotSignedUp() {
            control.switchFragment(new KakaoLoginFragment());
        }

        @Override
        public void onSuccess(UserProfile result) {
            MeProfile profile = profileManager.getProfile();

            HttpRequester requester = null;
            HashMap<String, Object> args = new HashMap<>();

            // 카카오 계정 정보를 가져온다.
            String kakaoId = result.getId()+"";
            String kakaoNick = result.getNickname();
            String kakaoProfile = result.getProfileImagePath();
            OnBackgroundWorkHandler handler = new OnBackgroundWorkHandler();

            if(profile == null) {
                // 회원가입 처리를 한다.
                args.put("kakao-id", kakaoId);
                args.put("kakao-nick", kakaoNick);
                args.put("kakao-profile-icon", kakaoProfile);

                requester = new HttpRequester(InternetConst.CREATE_ACCOUNT, args, REQUEST_CREATE_ACCOUNT, handler, control);
            } else {
                // 자동 로그인 처리를 한다.
                requester = new HttpRequester(InternetConst.AUTO_LOGIN_ACCOUNT, args, REQUEST_AUTO_LOGIN, handler, control);
            }

            requester.execute();
        }
    }

    class OnBackgroundWorkHandler extends MeResponseCallback implements OnBackgroundWorkListener {
        String mnaUUID = null;
        String mnaPublicId = null;

        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            HttpResponseObject httpResp = (HttpResponseObject) worker.getResult();

            if(httpResp.isErrorOccurred()) {
                // 서버 요청 오류
                dm.setTitle("오류");
                dm.setDescription("Internal server error.\n" + httpResp.getErrorMessage());
                dm.show();
            } else {
                Intent startActivity = null;

                try {
                    if (requestCode == REQUEST_CREATE_ACCOUNT) {
                        // 가입에 성공했다면, 인증서를 만듭니다.
                        JSONObject body = httpResp.getBody();
                        this.mnaUUID = body.getString("mna-uuid");
                        this.mnaPublicId = body.getString("mna-public-id");

                        UserManagement.getInstance().requestMe(this);
                    } else if (requestCode == REQUEST_AUTO_LOGIN) {
                        // 로그인 처리 결과
                        JSONObject body = httpResp.getBody();

                        // 자동 로그인 성공?
                        if (httpResp.getResponseResultCode() == InternetConst.HTTP_AUTH_ERROR) {
                            // 잘못된 인증서 입니다.
                            dm.setTitle("오류");
                            dm.setDescription("유효하지 않은 인증서 입니다!");
                            dm.show();

                            // 인증서를 제거한다.
                            profileManager.clear();
                        } else if (httpResp.getResponseResultCode() != InternetConst.HTTP_OK) {
                            // 다른 오류
                            dm.setTitle("오류");
                            dm.setDescription("알 수 없는 오류가 발생하였습니다.");
                            dm.show();
                        } else {
                            // 계정 데이터를 업데이트 합니다.
                            if (!body.getBoolean("result")) {
                                // 계정 업데이트 실패
                                Toast.makeText(control, "Can not update account", Toast.LENGTH_SHORT).show();
                            }

                            // 메인 화면으로 이동합니다.
                            startActivity = new Intent(control, MainActivity.class);
                        }
                    }
                } catch (Exception ex) {
                    dm.setTitle("Fatal error!");
                    dm.setDescription("An error occurred.\n" + ex.getMessage());
                    dm.show();
                }

                // 상태에 맞는 액티비티를 실행합니다.
                startActivity(startActivity);
                control.finish();
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
// 실패했습니다.
            String title;
            if(requestCode == REQUEST_CREATE_ACCOUNT) {
                // 회원가입 처리 결과
                title = "계정 생성에 실패했습니다!";
            } else  if(requestCode == REQUEST_AUTO_LOGIN) {
                // 로그인 처리 결과
                title = "자동 로그인에 실패했습니다!";
            } else {
                title ="Unknwon request!";
            }

            dm.setTitle("Fatal error");
            dm.setDescription(title + ex.getMessage());
            dm.show();

            ex.printStackTrace();
        }

        /*
            카카오 세션을 가져와서 인증서를 만듭니다.
         */
        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            onNotSignedUp();
        }

        @Override
        public void onNotSignedUp() {
            control.switchFragment(new KakaoLoginFragment());
        }

        @Override
        public void onSuccess(UserProfile result) {
            // 여기서 가입 인증서를 생성합니다.

        }
    }

    class OnDialogDismissHandler implements OnYesClickListener {
        @Override
        public void onClick(DialogInterface dialog) {
            dialog.dismiss();
        }
    }
}
