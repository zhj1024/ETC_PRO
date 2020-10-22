package com.example.myapplication.etc;

import com.jxhc.rsudriver.AbstractCommunicationDriver;
import com.jxhc.rsudriver.ETC_CONST;
import com.jxhc.rsudriver.biz.ByteHex;
import com.jxhc.rsudriver.exceptions.CommunicationException;
import com.jxhc.rsudriver.exceptions.FrameReceiveException;
import com.jxhc.rsudriver.exceptions.FrameSendException;
import com.xfkj.etcpos.core.ETC_LOG;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RSUSerialDriver extends AbstractCommunicationDriver {

    private static final String TAG="ObuSerialDriver";
    private SerialPort mSerialPort;
    private InputStream inputStream;
    private OutputStream outputStream;

    @Override
    public void openCommunication(int mode, String dev, int port)throws CommunicationException {
        try {
            mSerialPort=new SerialPort (new File(dev), 115200, 0);
            inputStream=mSerialPort.getInputStream ();
            outputStream=mSerialPort.getOutputStream ();
        } catch (SecurityException e) {
            e.printStackTrace ();
            throw new CommunicationException(ETC_CONST.RTN_OTHER_ERROR);
        } catch (IOException e) {
            e.printStackTrace ();
            throw new CommunicationException(ETC_CONST.RTN_OTHER_ERROR);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    @Override
    public void send(String dev,byte[] data,int dataLen)throws FrameSendException {
        try {
            outputStream.write (data, 0, dataLen);
            outputStream.flush ();
           // Thread.sleep (50);
        } catch (IOException e) {
            e.printStackTrace ();
            throw new FrameSendException(ETC_CONST.RTN_DEVICE_NOT_OPEN);
      }
//        catch (InterruptedException e) {
//            e.printStackTrace ();
//        }
    }
    @Override
    public byte[] receive(String dev,int timeout)throws FrameReceiveException {
        byte[] buffer=new byte[1024];
        int bl=0;
        int targetLen=-1;
        int timeoutAdder=0;
        try {
            while (++timeoutAdder < timeout) {
                int av=inputStream.available ();
                if (av != 0) {
                    int readLen=inputStream.read (buffer, bl, av);
                    bl+=readLen;
                    String frameHex=new ByteHex(buffer, bl).toString ();
                    frameHex=frameHex.substring (14, frameHex.length ());
                    // Log.e (TAG, "frameLength1: "+readLen);
                    // Log.e (TAG, "frameLength: "+frameHex.length () / 2);
                    if (frameHex.length () / 2 == ByteHex.byteToInt (buffer[4])) {    // 帧长度已获得
                        //   Log.e (TAG, "frameLength: success");
                        break;
                    } else {
                        Thread.sleep (5);
                        continue;
                    }
                } else {
                    Thread.sleep (5);
                    continue;
                }
            }
            if (buffer[0] != 0x55 || buffer[1] != 0xAA) {
                int s;
                do {
                    s=inputStream.available ();    // 初步判定帧结构是否正确，如果不正确，则尝试清空InputStream
                    inputStream.skip (s);
                } while (s != 0);
            }
        } catch (IOException e) {
            e.printStackTrace ();
            throw new FrameReceiveException(ETC_CONST.RTN_DEVICE_NOT_OPEN);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
//        try {
//            Thread.sleep (50);
//        } catch (InterruptedException e) {
//            e.printStackTrace ();
//        }
        if (bl == 0) throw new FrameReceiveException(ETC_CONST.RTN_CHANNEL_DEVICE_NO_RESPONSE);
        byte[] rec=new byte[bl];
        System.arraycopy (buffer, 0, rec, 0, bl);
        ETC_LOG.error ("read " + ByteHex.bytesToHexString (rec));
        return rec;
    }

    @Override
    public void closeCommunication(String dev){
        try {
            inputStream.close ();
            outputStream.close ();
            mSerialPort.close ();
            mSerialPort=null;
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


}
