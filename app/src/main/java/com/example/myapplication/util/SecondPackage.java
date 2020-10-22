package com.example.myapplication.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SecondPackage {
    private final static String TAG = "SecondPackage";

    public SecondPackage(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    private Context context;

    private void byte2hex(byte b, StringBuffer buf) {

        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',

                '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        int high = ((b & 0xf0) >> 4);

        int low = (b & 0x0f);

        buf.append(hexChars[high]);

        buf.append(hexChars[low]);

    }

    public String toHexString(byte[] block) {

        StringBuffer buf = new StringBuffer();


        int len = block.length;


        for (int i = 0; i < len; i++) {

            byte2hex(block[i], buf);

            if (i < len - 1) {

                buf.append(":");

            }

        }

        return buf.toString();

    }

    public boolean getSignInfo() {
        boolean checkright = false;
        try {
            PackageInfo packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(
                    "com.ctcf.originsign", PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];

            String code = String.valueOf(sign.hashCode());
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sign.toByteArray());
            byte[] digest = md.digest();

            String res = toHexString(digest);
            //if (code == xxxxxxxxx) {
            //对比MD5值和hashcode是否和自己原来的MD5相同
//            if (res.equals("dashcode")
//                    && code == MD5值) {
            if (code.equals("DB:DF:F8:8C:CC:3F:BA:B5:59:15:D6:A5:41:F2:D6:13")) {
                checkright = true;
            } else {
                checkright = false;
            }


            //parseSignature(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkright;
    }

    void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature));
            byte[] buffer = cert.getEncoded();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }
}
