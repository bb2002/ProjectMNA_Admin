package kr.saintdev.pmnadmin.views.fragments.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.models.datas.profile.MeProfile;
import kr.saintdev.pmnadmin.models.datas.profile.MeProfileManager;
import kr.saintdev.pmnadmin.views.activitys.LoadingActivity;
import kr.saintdev.pmnadmin.views.fragments.SuperFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-15
 */

public class LoadingFragment extends SuperFragment {
    LoadingActivity control = null;
    MeProfileManager profileManager = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_auth_loading, container, false);
        this.control = (LoadingActivity) getActivity();
        this.control.setActionBarTitle(null);

        this.profileManager = MeProfileManager.getInstance(control);

        // 카카오 로그인을 시도합니다.
        UserManagement.getInstance().requestMe(new OnKakaoLoginHandler());
        return v;
    }



    class OnKakaoLoginHandler extends MeResponseCallback {
        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            control.switchFragment(new KakaoLoginFragment());
        }

        @Override
        public void onNotSignedUp() {
            control.switchFragment(new KakaoLoginFragment());
        }

        @Override
        public void onSuccess(UserProfile result) {
            MeProfile profile = profileManager.getProfile();

            if(profile == null) {
                // 회원가입 처리를 한다.
            } else {
                // 자동 로그인 처리를 한다.
            }
        }
    }
}
