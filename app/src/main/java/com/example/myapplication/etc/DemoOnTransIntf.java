package com.example.myapplication.etc;

import android.view.View;
import android.widget.TextView;

import com.jxhc.rsudriver.ETC_CONST;
import com.jxhc.rsudriver.ICustomRsuDriver;
import com.jxhc.rsudriver.IRsuDriver;
import com.jxhc.rsudriver.RsuDriverFactory;
import com.jxhc.rsudriver.model.GetSecure_res;
import com.jxhc.rsudriver.model.INITIALISATION_res;
import com.jxhc.rsudriver.model.RSU_INIT_res;
import com.jxhc.rsudriver.model.TransferChannel_res;

/**
 * 透传接口实例
 */
public class DemoOnTransIntf {

    private IRsuDriver standardDriverInstance;
    private ICustomRsuDriver customDriverInstance;

    private long fd;

    private TextView resultTextView;

    public DemoOnTransIntf(TextView resultTextView) {
        this.resultTextView = resultTextView;
    }

    public void getSecure(View view) {
        String time = String.format("%08X", System.currentTimeMillis() / 1000);
        String beaconId = time;
        INITIALISATION_res vst = standardDriverInstance .INITIALISATION(fd, beaconId, time, 0, 1, "418729a01a0004002b", 0, 2000);    //这个指令直接携带余额
        resultTextView.append("BST:"+ vst +"\n");
        if (vst.rtnValue == ETC_CONST.RTN_DEVICE_NOT_OPEN || vst.rtnValue == ETC_CONST.RTN_DEVICE_NO_RESPONSE ){
            resultTextView.append("模块通信错误\n");
        } else if(vst.rtnValue != 0 || vst.returnStatus != 0){
            resultTextView.append("未读到OBU\n");
        }
        int length = 16;
        GetSecure_res gs = standardDriverInstance.GetSecure(fd, 0, 1, 1, "0000000000000000", 1, 1, 0, length, "ed506a24c9ad1d92", 0, 0, 2000);
        resultTextView.append("读取车辆信息密文:"+gs+"\n");
        if (gs.rtnValue == ETC_CONST.RTN_DEVICE_NO_RESPONSE || gs.file == null){
            resultTextView.append("模块通信错误\n");
        }

        //释放链路，响一声
        standardDriverInstance.SetMMI(fd, 1, 1, 0, 1000);
        standardDriverInstance.EVENT_REPORT(fd, 1, 1, 1, 1000);
    }

    public void readIcc(View view) {
        String time = String.format("%08X", System.currentTimeMillis() / 1000);
        String beaconId = time;
        INITIALISATION_res vst = standardDriverInstance .INITIALISATION(fd, beaconId, time, 0, 1, "418729a01a0004002b", 0, 2000);    //这个指令直接携带余额
        resultTextView.append("BST:"+ vst +"\n");

        //选择IC卡DF01文件
        TransferChannel_res res = standardDriverInstance.TransferChannel(fd, 1, 1, 1, 1, "0700A40000021001", 2000);
        resultTextView.append("选择IC卡DF01文件:"+res+"\n");

        //读取IC卡余额
        res = standardDriverInstance.TransferChannel(fd, 1, 1, 1, 1, "05805C000204", 2000);
        resultTextView.append("读取IC卡余额:"+res+"\n");

        //读过站信息
        res = standardDriverInstance.TransferChannel(fd, 1, 1, 1, 1, "0500B201cc2b", 2000);
        resultTextView.append("读取过站信息文件:"+res+"\n");

        //释放链路，响一声
        standardDriverInstance.SetMMI(fd, 1, 1, 0, 1000);
        standardDriverInstance.EVENT_REPORT(fd, 1, 1, 1, 1000);
    }

    public void initRsuTrans(String rsuIp, int power) {
        RsuDriverFactory.registerCommunicateDriver(new RSUSerialDriver());
        standardDriverInstance = RsuDriverFactory.getStandardDriverInstance();
        customDriverInstance = RsuDriverFactory.getCustomDriverInstance();
        fd = standardDriverInstance.RSU_Open(0, rsuIp, 0);
        resultTextView.append("打开串口,句柄:"+fd+"\n");

        RSU_INIT_res rsu_INIT = standardDriverInstance.RSU_INIT(fd, String.format("%08X", System.currentTimeMillis() / 1000),
                10, 0x30, power, 0, 1000);
        resultTextView.append("初始化模块:"+rsu_INIT +"\n");
        customDriverInstance.setPowAfterCal(fd,(short)power,2000);
    }

    public void closeRsu(View view) {
        standardDriverInstance.RSU_Close(fd);
        fd = -1;
    }
}
