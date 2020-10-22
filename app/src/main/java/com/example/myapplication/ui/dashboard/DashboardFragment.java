package com.example.myapplication.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.etc.DemoOnPosIntf;
import com.example.myapplication.etc.DemoOnTransIntf;
import com.example.myapplication.etc.PSAMDemo;
import com.example.myapplication.pojo.LoginVo;
import com.example.myapplication.pojo.MySession;
import com.example.myapplication.util.ConstantClass;
import com.example.myapplication.util.TrustAllCerts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String returnMessage = (String) msg.obj;
                Log.d("222222222222222222222===",  ""+msg.obj);
                if (returnMessage != null){



//                    SQLiteDatabase db = dbHelper.getWritableDatabase();
//                    dbHelper = new MyDatabaseHelper(context,"AppStore.db",null,1);
//                    dbHelper.getWritableDatabase();
//                    edit.commit();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                }
            }

        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
       // root.setBackgroundColor(getResources().getColor());
        ImageView imageView = root.findViewById(R.id.h_back);
        Button button = root.findViewById(R.id.btn_quit);
        Bundle bundle =this.getArguments();//得到从Activity传来的数据

        if(bundle!=null){
            ConstantClass.mess = bundle.getString("data");
        }
        String url = getResources().getString(R.string.loginOUT);
        Glide.with(getContext()).load(R.mipmap.logo).bitmapTransform(new BlurTransformation(getContext(), 5)).into(imageView);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        testGet(url,ConstantClass.mess);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        return root;
    }


    public void testGet(String url,String mess){
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
                .addHeader("session_token",mess)
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