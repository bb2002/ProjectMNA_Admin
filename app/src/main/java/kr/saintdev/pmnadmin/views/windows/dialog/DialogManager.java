package kr.saintdev.pmnadmin.views.windows.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnNoClickListener;
import kr.saintdev.pmnadmin.views.windows.dialog.clicklistener.OnYesClickListener;


/**
 * Created by 5252b on 2017-08-01.
 */

public class DialogManager {
    private AlertDialog.Builder builder = null;
    private Context context = null;
    private OnYesClickListener yesListener = null;
    private OnNoClickListener noListener = null;

    public DialogManager(Context context) {
        this.builder = new AlertDialog.Builder(context);
        this.context = context;
    }

    public void setTitle(String title) {
        this.builder.setTitle(title);
    }

    public void setDescription(String desc) {
        this.builder.setMessage(desc);
    }

    public void setOnYesButtonClickListener(OnYesClickListener listener, String text) {
        this.yesListener = listener;

        this.builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesListener.onClick(dialog);
            }
        });
    }

    public void setOnNoButtonClickListener(OnNoClickListener listener, String text) {
        this.noListener = listener;

        this.builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noListener.onNoClick(dialog);
            }
        });
    }

    public void show() {
        this.builder.create().show();
    }
}
