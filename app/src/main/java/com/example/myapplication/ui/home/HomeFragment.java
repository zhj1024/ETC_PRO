package com.example.myapplication.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.etc.DemoOnPosIntf;
import com.example.myapplication.etc.DemoOnTransIntf;
import com.example.myapplication.etc.PSAMDemo;
import com.example.myapplication.pojo.MySession;
import com.example.myapplication.util.ConstantClass;
import com.example.myapplication.util.TrustAllCerts;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private HomeFragment homeFragment;

    private TextView resultTextView;
    private EditText serialPortText;
    private EditText powerText;
    private EditText psamText;
    private String rsuIp;

    private DemoOnPosIntf posDemo;
    private DemoOnTransIntf transDemo;
    private PSAMDemo psamDemo;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String ReturnMessage = (String) msg.obj;
                Toast.makeText(getActivity(), getResources().getString(R.string.button), Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        root.setBackgroundColor(getResources().getColor(R.color.blues));
        //这个文本框用于显示执行结果(setMovementMethod支持文字复制，搜索)
        resultTextView = (TextView)root.findViewById(R.id.result_text_view);
        resultTextView .setMovementMethod(ScrollingMovementMethod.getInstance());

        //串口号
        serialPortText = (EditText)root.findViewById(R.id.text_serial_port);
        //功率
        powerText = (EditText) root.findViewById(R.id.text_power);
        //PSAM指令
    //    psamText = (EditText)root.findViewById(R.id.text_psam_cmd);

        posDemo = new DemoOnPosIntf(resultTextView);
        transDemo = new DemoOnTransIntf(resultTextView);
        psamDemo = new PSAMDemo(psamText,resultTextView);

        //清空直接结果文本框
        root.findViewById(R.id.btn_clear_result).setOnClickListener(v->{
            resultTextView.setText("");
        });

        //PSAM卡DEMO 适用于蓝畅的手持机，其他品牌应自行开发
        //执行PSAM指令
      //  root.findViewById(R.id.btn_exec_psam).setOnClickListener(psamDemo::execPsam);

        //POS方式DEMO
        //打开模块
        root.findViewById(R.id.btn_open_rsu).setOnClickListener(v->{
           try {
               rsuIp = serialPortText.getText().toString();
               int power = Integer.parseInt(powerText.getText().toString());
               posDemo.initEtcPos();     // 初始化ETC-POS，进行本地加密
               posDemo.initPosRsu(rsuIp,power);    //通过POS方式，初始化连接ETC里的网络模块
               Log.d("Home","2222222222222222222222");
               String result = posDemo.readObuInfo(root);
               Bundle bundle =this.getArguments();//得到从Activity传来的数据
               if(bundle!=null){
                   ConstantClass.mess = bundle.getString("data");
               }
               postTest(getResources().getString(R.string.obu),result,ConstantClass.mess);
           }catch (Exception e){
               e.printStackTrace();
           }
        });
        //读取OBU信息
       // root.findViewById(R.id.btn_fetch_obu_info).setOnClickListener(posDemo::readObuInfo);    //读取OBU信息，读到了后，响一声表示读到了
        //本地PSAM交易
     //   root.findViewById(R.id.btn_psam_trade).setOnClickListener(posDemo::psamTrade);       //读到了后，就开始进行扣费
        //关闭模块
     //   root.findViewById(R.id.btn_close_pos).setOnClickListener(posDemo::closePos);        //关闭网络连接模块


        //透传方式DEMO
        //初始化模块
//        root.findViewById(R.id.btn_open_rsu_trans).setOnClickListener(v->{
//            rsuIp = serialPortText.getText().toString();
//            int power = Integer.parseInt(powerText.getText().toString());
//            transDemo.initRsuTrans(rsuIp,power);
//        });
        //读取车辆信息密文
    //    root.findViewById(R.id.btn_get_secure).setOnClickListener(transDemo::getSecure);
        //读取IC卡信息
      //  root.findViewById(R.id.btn_read_icc).setOnClickListener(transDemo::readIcc);
        //关闭模块
     //  root.findViewById(R.id.btn_close_trans).setOnClickListener(transDemo::closeRsu);

        //模块上电
        power(1);

        //final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//
//                //textView.setText(s);
//            }
//        });


       // rsBlur(getContext(),R.mipmap.ic_sp,20);
        return root;
    }


    public void postTest(String url,String result,String session_token) throws JSONException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory(), new TrustAllCerts());
        builder.hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier());
        OkHttpClient okHttpClient = builder.build();

        //final MySession app = (MySession) getApplication();

        Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        String json = gson.toJson(result);

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);

        Request request = new Request.Builder()
                .addHeader("session_token",session_token)
                .url(url)//请求的url
                .post(requestBody)
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    line = br.readLine();
                    //line=response.body().string();
                    if (line != null) {
                        Gson gson = new Gson();

                    }
                 /*   for (int i=0;i<psDetailsModels.size();i++){

                    }*/
                    Message message = mHandler.obtainMessage(1, response.body().string());
                    mHandler.sendMessage(message);
                } else {

                }
            }
        });
    }


    public static boolean power(int state) {
        // TODO 模块上电
        return true;

    }

    private static Bitmap rsBlur(Context context,Bitmap source,int radius){

        Bitmap inputBmp = source;
        //(1)
        RenderScript renderScript =  RenderScript.create(context);

        Log.i("TAG","scale size:"+inputBmp.getWidth()+"*"+inputBmp.getHeight());

        // Allocate memory for Renderscript to work with
        //(2)
        final Allocation input = Allocation.createFromBitmap(renderScript,inputBmp);
        final Allocation output = Allocation.createTyped(renderScript,input.getType());
        //(3)
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //(4)
        scriptIntrinsicBlur.setInput(input);
        //(5)
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);
        //(6)
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        //(7)
        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        //(8)
        renderScript.destroy();

        return inputBmp;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }
}