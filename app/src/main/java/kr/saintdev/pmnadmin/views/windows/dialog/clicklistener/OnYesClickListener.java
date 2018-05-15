package kr.saintdev.pmnadmin.views.windows.dialog.clicklistener;

import android.content.DialogInterface;

/**
 * Created by 5252b on 2017-08-01.
 * 이 인터페이스를 재정의 하여 객체로 만든 후, DialogManager 의 YesClickListener 에 인자로 씁니다.
 */

public interface OnYesClickListener {
    void onClick(DialogInterface dialog);
}
