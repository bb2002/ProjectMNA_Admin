package kr.saintdev.pmnadmin.views.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.views.adapters.MainViewPageAdapter;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-17
 */

public class MainActivity extends AppCompatActivity {
    ViewPager contentView = null;
    ImageButton[] options = null;

    MainViewPageAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_main);

        this.contentView = findViewById(R.id.main_container);
        this.options = new ImageButton[]{
                findViewById(R.id.main_navbutton_staff),
                findViewById(R.id.main_navbutton_store),
                findViewById(R.id.main_navbutton_alarm),
                findViewById(R.id.main_navbutton_settings)
        };

        OnButtonClickHandler handler = new OnButtonClickHandler();
        for(ImageButton b : this.options) {
            b.setOnClickListener(handler);
        }

        this.adapter = new MainViewPageAdapter(getSupportFragmentManager());
        this.contentView.setAdapter(this.adapter);
    }

    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int item = 0;

            switch(v.getId()) {
                case R.id.main_navbutton_staff: item = 0; break;
                case R.id.main_navbutton_store: item = 1; break;
                case R.id.main_navbutton_alarm: item = 2; break;
                case R.id.main_navbutton_settings: item = 3; break;
            }

            contentView.setCurrentItem(item);
        }
    }
}
