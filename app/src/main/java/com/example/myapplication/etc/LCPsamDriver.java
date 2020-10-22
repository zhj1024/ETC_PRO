package com.example.myapplication.etc;

import android.util.Log;

import com.jxhc.rsudriver.model.PSAM_CHANNEL_res;
import com.xfkj.etcpos.core.driver.ILocalPsamDriver;
import com.xfkj.etcpos.core.exceps.CryptographyEngineException;

public class LCPsamDriver implements ILocalPsamDriver {

    private static int PSAM_SLOT = 2;

    @Override
    public PSAM_CHANNEL_res psamChannel(int apduList, String apduStr) throws CryptographyEngineException {
        Log.i("PSAM","->"+apduStr);
        PSAM_CHANNEL_res ps = new PSAM_CHANNEL_res(0);
        ps.apduList = apduList;
        ps.data = "TODO 返回PSAM指令执行结果";
        Log.i("PSAM","<-"+ps.data);
        return ps;
    }

    @Override
    public void psamReset() {
        System.out.println("TODO 复位PSAM");
    }

}
