package com.succorfish.geofence.blecalculation;

import java.util.ArrayList;

public class LiveLocationPacketManufacturer {
    public static final  byte LIVE_TRACKING_UP_CODE= (byte) 0XE3;
    public static final  byte LIVE_TRACKING_START= (byte) 0X01;
    public static final  byte LIVE_TRACKING_STOP= (byte) 0X00;

    public static ArrayList<byte[]> Start_Stop_LIVE_LOCATION(boolean start_stop){
        ArrayList<byte[]> liveLocation_Arraylist=new ArrayList<byte[]>();
        byte [] startLiveLocationPacket=new byte[16];
        startLiveLocationPacket[0]=LIVE_TRACKING_UP_CODE;
        /**
         * Data length
         */
        startLiveLocationPacket[1]=0X01;
        if(start_stop){
            startLiveLocationPacket[2]=0X01;
        }else {
            startLiveLocationPacket[2]=0X00;
        }
        liveLocation_Arraylist.add(startLiveLocationPacket);
        return liveLocation_Arraylist;
    }
}
