package kr.saintdev.pmnadmin.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import kr.saintdev.pmnadmin.R;

public class StaffInfoAdapter extends BaseAdapter {
    private ArrayList<JSONObject> staffDatas = new ArrayList<>();

    public void addItem(JSONObject obj) {
        staffDatas.add(obj);
    }

    public void clear() {
        this.staffDatas.clear();
    }

    @Override
    public int getCount() {
        return staffDatas.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return staffDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_staff_status, parent, false);
        }

        TextView nameView = convertView.findViewById(R.id.staff_status_name);
        TextView timeView = convertView.findViewById(R.id.staff_status_signtime);
        TextView statusView = convertView.findViewById(R.id.staff_status_status);

        JSONObject item = staffDatas.get(position);
        try {
            nameView.setText(item.getString("staff-name"));
            timeView.setText(item.getString("staff-sign-time"));

            if(item.getString("staff-status").equals("working")) {
                // 직원은 근무중 입니다.
                Date startDate = new Date(item.getInt("staff-start-time") * 1000);
                statusView.setText(startDate.getHours() + ":" + startDate.getMinutes() + " ~ 근무중");
            } else {
                // 직원은 퇴근했습니다.
                statusView.setText("미출근");
            }
        } catch(JSONException jex) {
            nameView.setText("ERROR!");
            timeView.setText("ERROR!");
            statusView.setText("ERROR!");
            jex.printStackTrace();
        }

        return convertView;
    }
}
