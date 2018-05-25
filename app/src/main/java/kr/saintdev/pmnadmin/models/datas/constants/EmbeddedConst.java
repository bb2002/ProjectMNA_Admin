package kr.saintdev.pmnadmin.models.datas.constants;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-25
 */

public interface EmbeddedConst {
    String RESPI_HOST = "http://funniest.iptime.org:5001/";

    // 현재 사무실 상태를 불러올 수 있는 요청 주소
    String OFFICE_STATUS = RESPI_HOST + "status";

    // 각 부분에 대한 컨트롤
    String OFFICE_CONTROL_DOOR = RESPI_HOST + "door_lock";
    String OFFICE_CONTROL_HALL_LIGHT = RESPI_HOST + "hall/light";
    String OFFICE_CONTROL_KITCHEN_LIGHT = RESPI_HOST + "kitchen/light";
    String OFFICE_CONTROL_TERRACE_LIGHT = RESPI_HOST + "terrace/light";
}
