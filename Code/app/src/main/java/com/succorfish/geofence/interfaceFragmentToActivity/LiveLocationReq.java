package com.succorfish.geofence.interfaceFragmentToActivity;

import java.util.ArrayList;

public interface LiveLocationReq {
   public void requestLiveLocationFromFirmware( String bleAddress, ArrayList<byte[]> packet_liveLocation);
}
