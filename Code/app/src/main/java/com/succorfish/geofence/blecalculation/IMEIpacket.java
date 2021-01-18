package com.succorfish.geofence.blecalculation;

import java.util.ArrayList;

public class IMEIpacket {
    public static ArrayList<byte[]> askIMEI_number(){
        ArrayList<byte[]> packet=new ArrayList<byte[]>();
        byte [] imei=new byte[16];
        imei[0]=(byte)0xe1;
        packet.add(imei);
        return  packet;
    }
}
