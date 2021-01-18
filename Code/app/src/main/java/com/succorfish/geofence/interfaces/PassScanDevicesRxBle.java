package com.succorfish.geofence.interfaces;

import com.polidea.rxandroidble2.RxBleDevice;
import com.succorfish.geofence.customObjects.CustomBluetooth;

public interface PassScanDevicesRxBle {
    public void scannedRxBleDevices(CustomBluetooth customBluetooth);
}




