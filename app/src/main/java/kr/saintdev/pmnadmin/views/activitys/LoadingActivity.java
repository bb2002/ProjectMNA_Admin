package kr.saintdev.pmnadmin.views.activitys;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.views.fragments.SuperFragment;

public class LoadingActivity extends AppCompatActivity {
    SuperFragment nowView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.nowView.onActivityResult(requestCode, resultCode, data);
    }

    public void switchFragment(SuperFragment view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.loading_container, view);
        ft.commit();

        this.nowView = view;
    }

    public void setActionBarTitle(@Nullable String title) {
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            if(title == null) {
                bar.hide();
            } else {
                bar.show();
                bar.setTitle(title);
            }
        }
    }
}
