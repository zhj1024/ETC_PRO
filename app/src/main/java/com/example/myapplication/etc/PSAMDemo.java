package com.example.myapplication.etc;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PSAMDemo {

    private EditText psamText;
    private TextView resultTextView;
    private static int PSAM_SLOT = 2;

    public PSAMDemo(EditText psamText, TextView resultTextView) {
        this.resultTextView = resultTextView;
        this.psamText = psamText;
    }

    //PSAM卡槽初始化
    public void initPsamSlot(Activity activity){
        System.out.println("TODO 这里初始化卡槽");
    }

    public void execPsam(View view) {
        resultTextView.setText("TODO PSAM指令返回:xxxxxxxxx"  );
    }

}
