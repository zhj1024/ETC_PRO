package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Debug;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

import javax.net.ssl.SSLSocket;

public class StartActivity extends AppCompatActivity {

    /*private Handler handler = new Handler();*/
    private ImageView imageView = null;
    private TextView textView;
    private TextView textView1;
    private int clo = 0;
    private SSLSocket Client_sslSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        if (0!=(getApplicationInfo().flags&= ApplicationInfo.FLAG_DEBUGGABLE)){
//            Toast.makeText(StartActivity.this,getResources().getString(R.string.Prompt_informationss),Toast.LENGTH_SHORT).show();
//            finish();
//        }
       /* EasyPermission.with(this).code(REQUEST_CODE).permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION).request();*/
         /*  if (BuildConfig.DEBUG){
            Toast.makeText(StartActivity.this,"有人企图调试此Apk,立即退出！！！", Toast.LENGTH_SHORT).show();
            finish();
        }*/

//        t();
        detectedDynamicDebug();
       // st();
       /* //通过postDelayed来实现延时三秒
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoLogin();
            }
        },3000);*/
       /* spark();*/

        imageView = (ImageView) findViewById(R.id.image);
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.mipmap.logo);
        Bitmap bitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
        //绘制图形
        Canvas canvas = new Canvas(bitmap);
        Matrix martix = new Matrix();

        martix.setScale(0.6f,0.6f);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bm,martix,paint);

        imageView.setImageBitmap(bitmap);

       /* t();*/
       // getSingInfo();
        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textViews);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.3f);
        AlphaAnimation alphaAnimations = new AlphaAnimation(1.0f,0.2f);
        alphaAnimation.setDuration(3000);
        imageView.startAnimation(alphaAnimation);
        textView.startAnimation(alphaAnimations);
        textView1.startAnimation(alphaAnimations);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
              /*  imageView.setBackgroundResource(R.drawable.common_full_open_on_phone);
                textView.setBackgroundResource(R.drawable.common_full_open_on_phone);*/
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gotoLogin();  //动画结束时的跳转界面
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void detectedDynamicDebug(){
        if (!BuildConfig.DEBUG){
            if (Debug.isDebuggerConnected()){
                //进程自杀
                int myPid = android.os.Process.myPid();
                android.os.Process.killProcess(myPid);
                //异常退出虚拟机
                System.exit(1);
            }
        }
    }

//    public void t(){
//        try {
//            if (DeviceUtil.isSimulator(com.zhj.myapp2.StartActivity.this)){
//                Toast.makeText(com.zhj.myapp2.StartActivity.this,getResources().getString(R.string.Installationequipments), Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(com.zhj.myapp2.StartActivity.this,getResources().getString(R.string.Installationequipmentss), Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * 跳转至登录界面
     */
    private void gotoLogin(){
        Intent intent = new Intent(StartActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
        //取消界面时跳转的动画
        overridePendingTransition(0,0);
    }

    /*//屏蔽物理返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isUnderTraced() {
        String processStatusFilePath = String.format(Locale.US,  "/proc/%d/status", android.os.Process.myPid());
        File procInfoFile = new File(processStatusFilePath);
        try {
            BufferedReader b = new BufferedReader(new FileReader(procInfoFile));
            String readLine;
            while ((readLine = b.readLine()) != null) {
                if(readLine.contains("TracerPid")) {
                    String[] arrays = readLine.split(":");
                    if(arrays.length == 2) {
                        int tracerPid = Integer.parseInt(arrays[1].trim());
                        if(tracerPid != 0) {
                            return true;
                        }
                    }
                }
            }

            b.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
