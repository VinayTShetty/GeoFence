package com.succorfish.geofence.customObjects;

import com.clj.fastble.data.BleDevice;

import java.io.Serializable;

public class MainActivityConnectedDevices implements Serializable {
    private BleDevice bleDevice;
    private boolean is_DisconnectedFromFirmware;

    public MainActivityConnectedDevices (BleDevice loc_bleDevice,boolean loc_is_DisconnectedFromFirmware){
        this.bleDevice=loc_bleDevice;
        this.is_DisconnectedFromFirmware=loc_is_DisconnectedFromFirmware;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public boolean isIs_DisconnectedFromFirmware() {
        return is_DisconnectedFromFirmware;
    }

    public void setIs_DisconnectedFromFirmware(boolean is_DisconnectedFromFirmware) {
        this.is_DisconnectedFromFirmware = is_DisconnectedFromFirmware;
    }
}
