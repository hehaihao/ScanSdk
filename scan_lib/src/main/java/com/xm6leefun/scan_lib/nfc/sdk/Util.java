package com.xm6leefun.scan_lib.nfc.sdk;

public class Util {
    public static String bytesToHex(byte[] data){
        String s = "";
        for (byte a: data) {
            s += String.format("%02X",a);
        }
        return s;
    }
}
