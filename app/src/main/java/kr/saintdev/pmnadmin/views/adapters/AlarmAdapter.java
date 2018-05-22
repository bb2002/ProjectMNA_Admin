package kr.saintdev.pmnadmin.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.saintdev.pmnadmin.R;
import kr.saintdev.pmnadmin.models.datas.objects.AlarmObject;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-22
 */

public class AlarmAdapter extends BaseAdapter {
    private ArrayList<AlarmObject> alarms = new ArrayList<>();
    private View.OnClickListener listener = null;

    public void addAlarmItem(AlarmObject obj) {
        this.alarms.add(obj);
    }

    public void setDeleteClickHandler(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void clear() {
        this.alarms.clear();
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_alarm, parent, false);
        }

        AlarmObject alarmObj = alarms.get(position);

        TextView title = convertView.findViewById(R.id.alarm_title);
        TextView content = convertView.findViewById(R.id.alarm_content);
        if(this.listener != null) {
            ImageButton deleteButton = convertView.findViewById(R.id.alarm_remove);
            deleteButton.setTag(alarmObj);      // DeleteButton 에 AlarmObject 를 포인팅합니다.
            deleteButton.setOnClickListener(listener);
        }
        ImageView iconView = convertView.findViewById(R.id.alarm_icon);

        title.setText(alarmObj.getAlarmTitle());
        content.setText(alarmObj.getAlarmContent());

        return convertView;
    }
}
