package com.succorfish.geofence.interfaceActivityToFragment;

import com.clj.fastble.data.BleDevice;

public interface LiveRequestDataPassToFragment {
    public void liveRequestDataFromFirmware(Double latitudeValue, Double longValue, String bleAddress, BleDevice bleDevice);
}
