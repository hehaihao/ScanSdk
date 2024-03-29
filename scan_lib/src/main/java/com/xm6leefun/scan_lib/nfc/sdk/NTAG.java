package com.xm6leefun.scan_lib.nfc.sdk;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;

import java.io.IOException;

/**
 * Created by xuhua on 16/03/2018.
 */

public class NTAG {

    protected String uuid;

    protected NTAGTech tech;

    NTAG(String id,NTAGTech v) throws NFCException {
        if(v == null){
            throw new NFCException("tech null");
        }
        uuid = id;
        tech = v;
    }

    public Tag getTag(){
        return tech.mul.getTag();
    }

    public  String getUUID() {
        return uuid;
    }
    //读数据
    //@addr,page addr or block addr
    public byte[] read(int addr) throws IOException {
        if(tech.mul != null){
            if(!tech.mul.isConnected()){
                throw new IOException("not connected");
            }
            return tech.mul.readPages(addr);
        }else if(tech.mcv != null){
            if(!tech.mcv.isConnected()){
                throw new IOException("not connected");
            }
            return tech.mcv.readBlock(addr);
        }else {
            throw new IOException("tech not imp");
        }
    }

    //读取ndef消息
    public NdefMessage readNdef() throws Exception {
        byte data[] = read(0x4);
        if(data[0] != 0x03){
            throw new FormatException("error message format 1");
        }
        int l = data[1] - 2;
        if(l <= 0){
            throw new FormatException("error message format 2");
        }
        int n = (l % 4 == 0)?(l/4):(l/4 + 1);
        byte[] md = new byte[n * 4 + 2];
        md[0] = data[2];
        md[1] = data[3];
        int pos = 2;
        for(int i=0; i < n; i++){
            byte tmp[] = read(0x5 + i);
            System.arraycopy(tmp,0, md, pos, 4);
            pos += 4;
        }
        byte [] ret = new byte[data[1]];
        System.arraycopy(md,0, ret,0, data[1]);
        return new NdefMessage(ret);
    }

    //写NDEF格式信息
    public  void writeNdef(NdefMessage msg) throws IOException{
        byte [] b = msg.toByteArray();
        byte wb[] = new byte[b.length + 2];
        wb[0] = 0x03;
        wb[1] = (byte)b.length;
        System.arraycopy(b,0, wb,2, b.length);
        writePages(0x4,wb);
    }

    //写多页数据
    //@addr,page addr or block addr
    public void writePages(int addr,byte[] data) throws IOException{
        int l = (data.length%4==0)?data.length/4:data.length/4+1;
        byte[] tmp = new byte[l*4];
        System.arraycopy(data,0,tmp,0,data.length);
        for(int i=0;i < l ;i++){
            byte [] wb = new byte[4];
            wb[0] = tmp[i*4 + 0];
            wb[1] = tmp[i*4 + 1];
            wb[2] = tmp[i*4 + 2];
            wb[3] = tmp[i*4 + 3];
            write(addr+i,wb);
        }
    }
    //写数据
    //@addr,page addr or block addr
    public void write(int addr,byte[] data) throws IOException {
        if(tech.mul != null){
            if(!tech.mul.isConnected()){
                throw new IOException("not connected");
            }
            tech.mul.writePage(addr,data);
        }else if(tech.mcv != null){
            if(!tech.mcv.isConnected()){
                throw new IOException("not connected");
            }
            tech.mcv.writeBlock(addr,data);
        }else {
            throw new IOException("tech not imp");
        }
    }

    //写入原始命令并返回数据
    public byte[] post(byte[] data) throws IOException {
        if(tech.mul != null){
            if(!tech.mul.isConnected()){
                throw new IOException("not connected");
            }
            return tech.mul.transceive(data);
        }else if(tech.mcv != null){
            if(!tech.mcv.isConnected()){
                throw new IOException("not connected");
            }
            return tech.mcv.transceive(data);
        }else {
            throw new IOException("tech not imp");
        }
    }
}
