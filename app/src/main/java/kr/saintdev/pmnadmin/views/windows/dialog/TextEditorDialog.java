package kr.saintdev.pmnadmin.views.windows.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import kr.saintdev.pmnadmin.R;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-21
 */

public class TextEditorDialog extends Dialog {
    String defaultValue = "";

    EditText editor = null;
    Button okButton = null;
    String data = null;

    public TextEditorDialog(@NonNull Context context, String defaultValue) {
        super(context);

        if(defaultValue != null) this.defaultValue = defaultValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_texteditor);

        this.editor = findViewById(R.id.texteditor_editor);
        this.okButton = findViewById(R.id.texteditor_commit);
        this.okButton.setOnClickListener(new OnButtonClickHandler());
    }

    public String getData() {
        return this.data;
    }

    class OnButtonClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String s = editor.getText().toString();

            if(s.length() == 0) {
                data = null;
            } else {
                data = s;
            }

            dismiss();
        }
    }
}
