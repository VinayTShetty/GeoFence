package com.succorfish.geofence.customObjects;
import androidx.annotation.Nullable;
import com.polidea.rxandroidble2.RxBleDevice;
import java.io.Serializable;
public class CustomBluetooth implements Serializable {
    private String bleaddress;
    private String deviceName;
    private RxBleDevice custom_RxBleDevice;

    public CustomBluetooth(RxBleDevice loc_rxbleDevice,String loc_deviceName,String loc_bleaddress){
        this.custom_RxBleDevice=loc_rxbleDevice;
        this.deviceName=loc_deviceName;
        this.bleaddress=loc_bleaddress;
    }



    public String getBleaddress() {
        return bleaddress;
    }
    public void setBleaddress(String bleaddress) {
        this.bleaddress = bleaddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public RxBleDevice getCustom_RxBleDevice() {
        return custom_RxBleDevice;
    }
    public void setCustom_RxBleDevice(RxBleDevice custom_RxBleDevice) {
        this.custom_RxBleDevice = custom_RxBleDevice;
    }

    /**
     *
     * equals method is used to make the Unique when the Device is added to the Arraylist.
     * By overriding the equals method.
     *
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof CustomBluetooth && (this.bleaddress.equalsIgnoreCase(((CustomBluetooth) obj).bleaddress));
    }
}
