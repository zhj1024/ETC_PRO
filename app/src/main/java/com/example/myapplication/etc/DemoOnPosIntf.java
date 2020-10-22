package com.example.myapplication.etc;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jxhc.rsudriver.RsuDriverFactory;
import com.xfkj.etcpos.core.ETCPOS;
import com.xfkj.etcpos.core.IEtcPosConfig;
import com.xfkj.etcpos.core.IRSU;
import com.xfkj.etcpos.core.biz.BizHelperContainer;
import com.xfkj.etcpos.core.biz.LocalPsamCryptographyEngine;
import com.xfkj.etcpos.core.exceps.CryptographyEngineException;
import com.xfkj.etcpos.core.exceps.NoOBUFoundException;
import com.xfkj.etcpos.core.exceps.OBUInvalidException;
import com.xfkj.etcpos.core.exceps.OnlineOverstockException;
import com.xfkj.etcpos.core.exceps.RSUCommunicationException;
import com.xfkj.etcpos.core.model.EtcVehicleInfo;
import com.xfkj.etcpos.core.model.InitialDeviceRequest;
import com.xfkj.etcpos.core.model.TradeRequest;
import com.xfkj.etcpos.core.model.TradeResult;

/**
 * 集成POS接口实例
 */
public class DemoOnPosIntf {

    private String rsuIp;
    private IRSU rsu;

    private TextView resultTextView;


    public DemoOnPosIntf(TextView resultTextView){
        this.resultTextView = resultTextView;
    }

    public void initEtcPos(){
        //初始化ETC-POS
        RsuDriverFactory.registerCommunicateDriver(new RSUSerialDriver());
        ETCPOS.config()
                .setGlobalProperty(IEtcPosConfig.CRYPTO_TYPE, IEtcPosConfig.LOCAL_PSAM) // 使用本地PSAM加密
                .setGlobalProperty(IEtcPosConfig.LOG_TYPE, IEtcPosConfig.CONSOLE) // 日志采用控制台打印
                .setGlobalProperty(IEtcPosConfig.ROAD_NET_ID, "1201") // 路网编号
                .setGlobalProperty(IEtcPosConfig.STATION_ID, "3333") // 站编号
                .setGlobalProperty(IEtcPosConfig.PSAM_EXIST ,false)    //未插入PSAM
                .setGlobalProperty(IEtcPosConfig.GET_SECURE_LENGTH,16)  //GetSecure长度
                .setGlobalProperty(IEtcPosConfig.DECRYPT_VEHICLE_FILE,false);    //不解密车辆信息


        //本地PSAM加密方式
        BizHelperContainer.getInstance().registerCryptographyEngine(IEtcPosConfig.LOCAL_PSAM,new LocalPsamCryptographyEngine(new LCPsamDriver()));
        resultTextView.append("初始化ETC-POS模式\n完成");
    }

    public String readObuInfo(View view) {
        EtcVehicleInfo etcVehicleInfo = new EtcVehicleInfo();
        try {
             etcVehicleInfo = rsu.fetchOBUInfo(true, 2000);   //读取完整的OBU信息
            rsu.cancelTrading(etcVehicleInfo, true); //这里让OBU响一声
            System.out.println(etcVehicleInfo.toString());
            resultTextView.append("读取OBU结果:" + etcVehicleInfo.toString() +"\n");

        }catch (OBUInvalidException e){
            e.printStackTrace();
            resultTextView.append("读取OBU结果:" + e+","+e.getEtcInfo() +"\n");
        }catch (CryptographyEngineException | NoOBUFoundException | RSUCommunicationException e) {
            e.printStackTrace();
            resultTextView.append("读取OBU结果:" + e +"\n");
        }
        return etcVehicleInfo.toString();
    }

    public void initPosRsu(String rsuIp, final int power) {
        this.rsuIp = rsuIp;
        //初始化POS
        rsu = ETCPOS.etcPos().initRsu(rsuIp, 0, new InitialDeviceRequest() {{
            this.setLaneMode((short) 3);
            this.setPllChannelId((short) 0);
            this.setTxPower((short) power);
            this.setWaitTime((short) 2000);
            this.setTransClass((short) 0);

            this.setPsamSlotNo((short) 1);
            this.setChannelId((short) 1);
            this.setRsuId("1501");
            this.setRsuVersion("01");
        }});
        if(rsu == null){
            resultTextView.append("初始化5.8G模块失败\n");
        }else {
            resultTextView.append("初始化5.8G模块成功\n");
        }
    }

    public void psamTrade(View view) {
        try {
            resultTextView.append("开始0元交易");
            EtcVehicleInfo etcVehicleInfo = rsu.fetchOBUInfo(false, 2000);    //开始读取OBU信息
            resultTextView.append("读取OBU信息:"+etcVehicleInfo  +"\n");
            TradeRequest tr = new TradeRequest();
            tr.setConsumeMoney(0);
            tr.setObuid(etcVehicleInfo.getObuid());
            TradeResult tradeResult = rsu.entryTrade(etcVehicleInfo, tr, 2000);    //进行扣费
            resultTextView.append("交易结果:"+tradeResult  +"\n");
        } catch (RSUCommunicationException | NoOBUFoundException |OBUInvalidException |CryptographyEngineException|OnlineOverstockException e ) {
            e.printStackTrace();
            resultTextView.append("交易失败:"+e);
        }

    }

    public void closePos(View view) {
        if(rsu != null)ETCPOS.etcPos().closeRsu(rsuIp);
    }
}
