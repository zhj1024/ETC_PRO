package com.example.myapplication.ui.loading;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

public class LoadingDialog extends Dialog {
    private TextView tv;


    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView) this.findViewById(R.id.tv);
        tv.setText("正在登录...");
        LinearLayout linearLayout = (LinearLayout) this
                .findViewById(R.id.LinearLayout);
        linearLayout.getBackground().setAlpha(210);
    }
}
