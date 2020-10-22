package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.pojo.LoginVo;
import com.example.myapplication.pojo.MySession;
import com.example.myapplication.ui.loading.LoadingDialog;
import com.example.myapplication.util.MD5;
import com.example.myapplication.util.TrustAllCerts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText userName;
    private EditText passWord;
    LoadingDialog dialog;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String returnMessage = (String) msg.obj;
                Log.d("222222222222222222222===",  ""+msg.obj);
                if (returnMessage != null){
                    LoginVo logiVo = null;
                    try {
                        logiVo = JSONObject.parseObject(returnMessage, LoginVo.class);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("数据解析错误",e+"======"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    }
                    final Integer code = logiVo.getCode();
                    System.out.println(code);
                    System.out.println(logiVo.getMsg());
                    System.out.println(logiVo.getT());
                    if (code == 0) {
                        final MySession app_Handler = (MySession)getApplication();

                        SharedPreferences sp = getSharedPreferences("LoginToken", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit=sp.edit();
                        edit.putString("session_token",logiVo.getData().get("session_token"));
//                    SQLiteDatabase db = dbHelper.getWritableDatabase();
//                    dbHelper = new MyDatabaseHelper(context,"AppStore.db",null,1);
//                    dbHelper.getWritableDatabase();
//                    edit.commit();
                        app_Handler.setSession_token(logiVo.getData().get("session_token"));
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else if(code == 402){
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.errors), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.errors), Toast.LENGTH_SHORT).show();
                    }
                }else if (msg.what == 2){
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.overtime), Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        final MySession mySession = (MySession) getApplication();
        Button button = findViewById(R.id.login_btn);    //登录按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mySession.getSession_token() == null){
                    Log.d("LoginActivity.this","!!!!");
                    userName = findViewById(R.id.login_edit_userName);    //用户名
                    passWord = findViewById(R.id.login_edit_pwd);    //密码
                    MD5 md5 = new MD5();
                    String url = getResources().getString(R.string.loginURL) + "?userName="+userName.getText().toString()+"&password="+md5.MD5Encode( passWord.getText().toString());   //请求路径
                    dialog = new LoadingDialog(LoginActivity.this);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    testGet(url);
                    // testOkhttpGet("LoginActivity.this",url);
                }else{
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

//    //Get请求
//    private void testOkhttpGet(final String TAG, String url) {
//        try {
//            okhttp3.Request request = new okhttp3.Request.Builder().url(url)
//                    .addHeader("from","handheld").get().build();
//            OkHttpClient okHttpClient = new OkHttpClient();
//            final Call call = okHttpClient.newCall(request);
//            Log.d("请求头=========", String.valueOf(request));
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
////                Message message = Message.obtain();
////                message.what = 0;
////                message.obj = e.getMessage();
////                mHandler.sendMessage(message);
//                    //   Toast.makeText(MainActivity.this, getResources().getString(R.string.overtime), Toast.LENGTH_SHORT).show();
//                    Message message =  mHandler.obtainMessage(2,getResources().getString(R.string.overtime));
//                    mHandler.sendMessage(message);
//                    Log.d(TAG, "onFailure: 请求失败 === " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                }
//
//                @Override
//                public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                    //    System.out.println("!1111111111111111==="+response.body().string());
//
//                    Message message =  mHandler.obtainMessage(1,response.body().string());
//                    mHandler.sendMessage(message);
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.i("MainActivity 请求登录异常 ===",e + "===="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        }
//
//
//    }


    public void testGet(String url){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory(), new TrustAllCerts());
        builder.hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier());
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request mRequest = new Request.Builder()
                .url(url)
                .addHeader("from","handheld")
                .get()
                .build();

        mOkHttpClient.newCall(mRequest).enqueue(new Callback(){

            @Override
            public void onFailure(Call p1, IOException p2)
            {
                //请求失败
                Log.d("data", "请求失败");
                Message message =  mHandler.obtainMessage(2,getResources().getString(R.string.overtime));
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功
                //Log.d("data", response.body().string());
                Message message =  mHandler.obtainMessage(1,response.body().string());
                mHandler.sendMessage(message);
            }

        });
    }

}