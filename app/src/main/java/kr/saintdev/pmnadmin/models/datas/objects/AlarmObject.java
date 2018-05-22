package kr.saintdev.pmnadmin.models.datas.objects;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-22
 */

public class AlarmObject {
    private int alarmId = 0;
    private String alarmTitle = null;
    private String alarmContent = null;
    private String alarmTarget = null;
    private String alarmSender = null;
    private String alarmType = null;
    private String alarmCreated = null;

    public AlarmObject(int alarmId, String alarmTitle, String alarmContent, String alarmTarget, String alarmSender, String alarmType, String alarmCreated) {
        this.alarmId = alarmId;
        this.alarmTitle = alarmTitle;
        this.alarmContent = alarmContent;
        this.alarmTarget = alarmTarget;
        this.alarmSender = alarmSender;
        this.alarmType = alarmType;
        this.alarmCreated = alarmCreated;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public String getAlarmTitle() {
        return alarmTitle;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public String getAlarmTarget() {
        return alarmTarget;
    }

    public String getAlarmSender() {
        return alarmSender;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public String getAlarmCreated() {
        return alarmCreated;
    }
}
