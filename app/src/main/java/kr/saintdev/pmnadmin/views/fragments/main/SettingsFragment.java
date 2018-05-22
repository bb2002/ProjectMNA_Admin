package kr.saintdev.pmnadmin.views.fragments.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.saintdev.pmnadmin.models.datas.profile.MeProfile;
import kr.saintdev.pmnadmin.models.datas.profile.MeProfileManager;
import kr.saintdev.pmnadmin.models.tasks.BackgroundWork;
import kr.saintdev.pmnadmin.models.tasks.OnBackgroundWorkListener;
import kr.saintdev.pmnadmin.models.tasks.downloader.ImageDownloader;
import kr.saintdev.pmnadmin.views.activitys.MainActivity;
import kr.saintdev.pmnadmin.views.fragments.SuperFragment;
import kr.saintdev.pmnadmin.R;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-17
 */

public class SettingsFragment extends SuperFragment {
    MainActivity control = null;

    MeProfileManager profileManager = null;
    CircleImageView profileView = null;

    private static final int REQUEST_PROFILE_IMAGE = 0x0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_settings, container, false);
        this.control = (MainActivity) getActivity();

        this.profileManager = MeProfileManager.getInstance(control);
        this.profileView = v.findViewById(R.id.main_settings_profile_icon);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        MeProfile profile = this.profileManager.getProfile();
        ImageDownloader profileDownloder =
                new ImageDownloader(profile.getKakaoProfileIcon(), REQUEST_PROFILE_IMAGE, new OnBackgroundWorker());
        profileDownloder.execute();     // 프사 다운로드를 시작합니다.
    }

    class OnBackgroundWorker implements OnBackgroundWorkListener {
        @Override
        public void onSuccess(int requestCode, BackgroundWork worker) {
            if(requestCode == REQUEST_PROFILE_IMAGE) {
                // 프로필 사진을 다운로드 했습니다.
                Bitmap profile = (Bitmap) worker.getResult();
                profileView.setImageBitmap(profile);
            }
        }

        @Override
        public void onFailed(int requestCode, Exception ex) {
            if(requestCode == REQUEST_PROFILE_IMAGE) {
                Toast.makeText(control, "프로필 사진을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
