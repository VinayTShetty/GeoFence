package com.succorfish.geofence;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RelativeLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.succorfish.geofence.Fragment.FragmentBandConfiguration;
import com.succorfish.geofence.Fragment.FragmentChatting;
import com.succorfish.geofence.Fragment.FragmentDeviceConfiguration;
import com.succorfish.geofence.Fragment.FragmentHistory;
import com.succorfish.geofence.Fragment.FragmentIndustrySpecificConfig;
import com.succorfish.geofence.Fragment.FragmentLiveTracking;
import com.succorfish.geofence.Fragment.FragmentMap;
import com.succorfish.geofence.Fragment.FragmentScan;
import com.succorfish.geofence.Fragment.FragmentServerConfiguration;
import com.succorfish.geofence.Fragment.FragmentSetting;
import com.succorfish.geofence.Fragment.FragmentSimConfiguration;
import com.succorfish.geofence.Fragment.FragmentUARTConfiguration;
import com.succorfish.geofence.Fragment.FragmentWifiConfiguration;
import com.succorfish.geofence.MyServices.BluetoothLeService;
import com.succorfish.geofence.RoomDataBaseEntity.ChatInfo;
import com.succorfish.geofence.RoomDataBaseEntity.Geofence;
import com.succorfish.geofence.RoomDataBaseEntity.GeofenceAlert;
import com.succorfish.geofence.RoomDataBaseEntity.PolygonEnt;
import com.succorfish.geofence.RoomDataBaseEntity.Rules;
import com.succorfish.geofence.RoomDataBaseHelper.RoomDBHelper;
import com.succorfish.geofence.customA2_object.GeoFenceObjectData;
import com.succorfish.geofence.customA2_object.LatLong;
import com.succorfish.geofence.customA2_object.RuleId_Value_ActionBitMask;
import com.succorfish.geofence.customObjects.ChattingObject;
import com.succorfish.geofence.customObjects.CustBluetootDevices;
import com.succorfish.geofence.customObjects.HistroyList;
import com.succorfish.geofence.customObjects.IncommingMessagePacket;
import com.succorfish.geofence.dialog.DialogProvider;
import com.succorfish.geofence.helper.PreferenceHelper;
import com.succorfish.geofence.interfaceActivityToFragment.ChatDeliveryACK;
import com.succorfish.geofence.interfaceActivityToFragment.ConnectionProgressDialogShow;
import com.succorfish.geofence.interfaceActivityToFragment.ConnectionStatus;
import com.succorfish.geofence.interfaceActivityToFragment.GeoFenceDialogAlertShow;
import com.succorfish.geofence.interfaceActivityToFragment.LiveRequestDataPassToFragment;
import com.succorfish.geofence.interfaceActivityToFragment.OpenDialogToCheckDeviceName;
import com.succorfish.geofence.interfaceActivityToFragment.PassChatObjectToFragment;
import com.succorfish.geofence.interfaceActivityToFragment.PassScanDeviceToActivity_interface;
import com.succorfish.geofence.interfaceFragmentToActivity.DeviceConfigurationPackets;
import com.succorfish.geofence.interfaceFragmentToActivity.DeviceConnectDisconnect;
import com.succorfish.geofence.interfaceFragmentToActivity.IndustrySpeificConfigurationPackets;
import com.succorfish.geofence.interfaceFragmentToActivity.LiveLocationReq;
import com.succorfish.geofence.interfaceFragmentToActivity.MessageChatPacket;
import com.succorfish.geofence.interfaceFragmentToActivity.PassBuzzerVolumeToDevice;
import com.succorfish.geofence.interfaceFragmentToActivity.PassClickedDeviceForConnection;
import com.succorfish.geofence.interfaceFragmentToActivity.ResetDeviceInterface;
import com.succorfish.geofence.interfaceFragmentToActivity.ServerConfigurationDataPass;
import com.succorfish.geofence.interfaceFragmentToActivity.SimConfigurationPackets;
import com.succorfish.geofence.interfaceFragmentToActivity.WifiConfigurationPackets;
import com.succorfish.geofence.interfaces.API;
import com.succorfish.geofence.interfaces.onAlertDialogCallBack;
import com.succorfish.geofence.utility.URL_helper;
import com.succorfish.geofence.utility.Utility;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import static com.succorfish.geofence.RoomDataBaseHelper.RoomDBHelper.getRoomDBInstance;
import static com.succorfish.geofence.blecalculation.Blecalculation.WriteValue01;
import static com.succorfish.geofence.blecalculation.Blecalculation.askForGeoFenceId_timeStamp;
import static com.succorfish.geofence.blecalculation.Blecalculation.calculateAlgorithmValue;
import static com.succorfish.geofence.blecalculation.Blecalculation.checkType;
import static com.succorfish.geofence.blecalculation.Blecalculation.convert4bytes;
import static com.succorfish.geofence.blecalculation.Blecalculation.convertHexToLong;
import static com.succorfish.geofence.blecalculation.Blecalculation.geoFenceId;
import static com.succorfish.geofence.blecalculation.Blecalculation.getConnectionMainCode;
import static com.succorfish.geofence.blecalculation.Blecalculation.getFloatingPointValueFromHex;
import static com.succorfish.geofence.blecalculation.Blecalculation.hexToint;
import static com.succorfish.geofence.blecalculation.Blecalculation.sendAckReadyForNextPacket;
import static com.succorfish.geofence.blecalculation.Blecalculation.send_Geo_fenceID_fetched_finished_Acknoledgement;
import static com.succorfish.geofence.blecalculation.Blecalculation.set_firmwareTimeStamp;
import static com.succorfish.geofence.blecalculation.ByteConversion.convert7bytesToLong;
import static com.succorfish.geofence.blecalculation.ByteConversion.convertHexStringToString;
import static com.succorfish.geofence.blecalculation.DeviceTokenPacket.deviceTokenpacketArray;
import static com.succorfish.geofence.blecalculation.IMEIpacket.askIMEI_number;
import static com.succorfish.geofence.blecalculation.LiveLocationPacketManufacturer.Start_Stop_LIVE_LOCATION;
import static com.succorfish.geofence.blecalculation.MessageCalculation.incommingMessageACK;
import static com.succorfish.geofence.encryption.Encryption.decryptData;
import static com.succorfish.geofence.encryption.Encryption.encryptData;
import static com.succorfish.geofence.helper.UUID.TRACKER_CHARCTERSTICS_UUID;
import static com.succorfish.geofence.helper.UUID.TRACKER_SERVICE_UUID;
import static com.succorfish.geofence.utility.RetrofitHelperClass.getClientWithAutho;
import static com.succorfish.geofence.utility.RetrofitHelperClass.haveInternet;
import static com.succorfish.geofence.utility.Utility.ble_on_off;
import static com.succorfish.geofence.utility.Utility.getBluetoothAdapter;
import static com.succorfish.geofence.utility.Utility.getCurrenTimeStamp;
import static com.succorfish.geofence.utility.Utility.getDateTime;
import static com.succorfish.geofence.utility.Utility.getDateWithtime;
import static com.succorfish.geofence.utility.Utility.getHexArrayList;
import static com.succorfish.geofence.utility.Utility.getID_From_ArrayList;
import static com.succorfish.geofence.utility.Utility.getTimeStampMilliSecondd;
import static com.succorfish.geofence.utility.Utility.get_TimeStamp_ArrayList;
import static com.succorfish.geofence.utility.Utility.removePreviousZero;
import static com.succorfish.geofence.utility.Utility.splitString;

public class MainActivity extends AppCompatActivity implements
        PassClickedDeviceForConnection,
        PassBuzzerVolumeToDevice,
        MessageChatPacket,
        DeviceConfigurationPackets,
        ServerConfigurationDataPass,
        IndustrySpeificConfigurationPackets,
        WifiConfigurationPackets,
        SimConfigurationPackets,
        ResetDeviceInterface,
        LiveLocationReq,
        DeviceConnectDisconnect {
    private Unbinder unbinder;
    PassScanDevicesRxBle passScanDevicesRxBle;
    PassScanDeviceToActivity_interface  passScanDeviceToActivity_interface;

    /**
     * interface from Activity to Fragment
     */
    GeoFenceDialogAlertShow geoFenceDialogAlertShow;
    ConnectionStatus connectionStatus;
    ConnectionProgressDialogShow connectionProgressDialogShow;
    OpenDialogToCheckDeviceName openDialogToCheckDeviceName;
    ChatDeliveryACK chatDeliveryACK;
    LiveRequestDataPassToFragment liveRequestDataPassToFragment;
    PassChatObjectToFragment passChatObjectToFragment;
    /**
     * interface from Activity to Fragment
     */
    @BindView(R.id.bottom_navigation_layout)
    public RelativeLayout bottomRelativelayout;
    FragmentTransaction fragmentTransction;
    /**
     * Exit window.
     */
    private boolean exit = false;
    private Handler scanHandler;
    // Stops scanning after 10 seconds.

    ArrayList<String> from_firmware_ID_TimeStamp;
    ArrayList<String> from_DataBase_ID_TimeStamp;
    ArrayList<String> from_firmware_ID_TimeStamp_A6_Packet = new ArrayList<String>();
    ArrayList<String> from_firmware_ID_TimeStamp_A8_Packet = new ArrayList<String>();
    public PreferenceHelper preferenceHelper;
    private boolean application_Visible_ToUser = false;
    private KProgressHUD hud;
    DialogProvider dialogProvider;
    String deviceToken_fromFirmware = "";
    private static String imeiNumberFomFirmware="";
    public static RoomDBHelper roomDBHelperInstance;
    /**
     * Device Configuration Arraylist.
     */
    ArrayList<String> hexConverted_DeviceConfigurationList = new ArrayList<String>();
    /**
     * Chat Message ArrayList
     */
    ArrayList<String> hexConverted_ChatMessageList = new ArrayList<String>();
    /**
     * Server Configuration ArrayList
     */
    ArrayList<String> hexConverted_ServerConfigurationList = new ArrayList<String>();
    /**
     * Industry specific configurarion
     */
    ArrayList<String> hexConverted_IndustrySpecificList = new ArrayList<String>();
    /**
     * Wifi Configuration
     */
    ArrayList<String> hexConverted_WifiConfigurationList = new ArrayList<String>();
    /**
     * Sim Configuration
     */
    ArrayList<String> hexConverted_SimConfigurationList = new ArrayList<String>();
    /**
     * IMEI number asking
     */
    ArrayList<String> hexConverted_IMEIList = new ArrayList<String>();
    /**
     * Device Token ArrayList
     */
    ArrayList<String> hexConverted_deviceToken = new ArrayList<String>();
    /**
     * Live location Request
     */
    ArrayList<String> hexConverted_liveLocation = new ArrayList<String>();
    /**
     * Rest Devie Request
     */
    ArrayList<String> HexConverted_DevicePacektList=new ArrayList<String>();
    /**
     * Used to Send the Connected BLE Address to different fragment.
     */
    ArrayList<String> HexConverted_IncommingMessagePacket=new ArrayList<>();
    private static String incommingMessageForConcatenation="";
    public static String CONNECTED_BLE_ADDRESS = "";
    /**
     * Retrofit Implementation
     */
    public Retrofit mRetrofit_instance;
    public API mApiService;

    /**
     *  Incomming message packet.
     */
  private   IncommingMessagePacket incommingMessagePacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(MainActivity.this);
        intializeView();
        bindBleServiceToMainActivity();
        intializeRoomDataBaseInstance();
        removeallFragments();
        createNotificationChannel_codeTutor();
        interfaceImpleMainActivity();
        fastBleImplementation();
        intializeScanHandler();
        intializePreferenceInstance();
        intializeRetrofitInstance();
        intializeDialog();
        replaceFragment(new FragmentScan(), null, null, false);
     //   replaceFragment(new FragmentChatting(), null, null, false);
    }

    private void intializeRoomDataBaseInstance() {
        roomDBHelperInstance = getRoomDBInstance(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        application_Visible_ToUser = false;
        registerReceiver(bluetootServiceRecieverData, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        application_Visible_ToUser = true;
        unregisterReceiver(bluetootServiceRecieverData);
    }

    @Override
    protected void onStop() {
        super.onStop();
        application_Visible_ToUser = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        stopScanningWhenActivityClosed();
        unbindService(serviceConnection);
        mBluetoothLeService = null;
    }

    private void stopScanningWhenActivityClosed() {
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
               stopScanning();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivity_container);
        if (fragment.toString().equalsIgnoreCase(new FragmentScan().toString())) {
            /**
             * make logic here for the Exit window.
             */
            if (exit) {
                DialogProvider.ExitDialog(MainActivity.this, getString(R.string.str_exit_confirmation));
            } else {
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 2000);
            }
        } else if (fragment.toString().equalsIgnoreCase(new FragmentChatting().toString())) {
            replaceFragment(new FragmentScan(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentSetting().toString())) {
            replaceFragment(new FragmentScan(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentHistory().toString())) {
            replaceFragment(new FragmentScan(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentMap().toString())) {
            replaceFragment(new FragmentHistory(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentDeviceConfiguration().toString())) {
            replaceFragment(new FragmentSetting(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentSimConfiguration().toString())) {
            replaceFragment(new FragmentSetting(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentServerConfiguration().toString())) {
            replaceFragment(new FragmentSetting(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentIndustrySpecificConfig().toString())) {
            replaceFragment(new FragmentSetting(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentWifiConfiguration().toString())) {
            replaceFragment(new FragmentSetting(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentUARTConfiguration().toString())) {
            replaceFragment(new FragmentSimConfiguration(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentBandConfiguration().toString())) {
            replaceFragment(new FragmentSimConfiguration(), null, null, false);
        } else if (fragment.toString().equalsIgnoreCase(new FragmentLiveTracking().toString())) {
            replaceFragment(new FragmentScan(), null, null, false);
        }
    }

    private void intializeView() {
        hud = KProgressHUD.create(this);
    }

    private void showProgressDialog(String labelToShow) {
        hud
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(labelToShow)
                .setCancellable(false);
        hud.show();
    }

    private void cancelProgressDialog() {
        if (hud != null) {
            if (hud.isShowing()) {
                hud.dismiss();
            }
        }
    }

    private void intializeDialog() {
        dialogProvider = new DialogProvider(this);
    }


    private void intializeScanHandler() {
        scanHandler = new Handler();
    }

    private void intializeRetrofitInstance() {
        String userName = "";
        String password = "";
        if (preferenceHelper.get_Remember_me_Checked()) {
            userName = preferenceHelper.get_PREF_remember_me_userName();
            password = preferenceHelper.get_PREF_remember_password();
        } else if (!preferenceHelper.get_Remember_me_Checked()) {
            userName = preferenceHelper.get_userName();
            password = preferenceHelper.get_password();
        }

        if (!userName.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
            mRetrofit_instance = new Retrofit.Builder()
                    .baseUrl(URL_helper.SERVER_URL_SUCCORFISH)
                    .client(getClientWithAutho(userName, password))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            intialize_API_instance();
        }
    }

    private void intialize_API_instance() {
        mApiService = mRetrofit_instance.create(API.class);
    }

    private void getDeviceTokenAPI(String imeiNumber, String bleAddress) {
        if (haveInternet(this)) {
            Call<String> deviceToken = mApiService.getDeviceToken(imeiNumber);
            deviceToken.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    deviceToken_fromFirmware = "";
                    if (response.code() == 200 || response.isSuccessful()) {
                        deviceToken_fromFirmware = response.body().toString();
                        deviceToken_fromFirmware = deviceToken_fromFirmware.substring(1, deviceToken_fromFirmware.length() - 1);
                        processDeviceTokenAndSend(deviceToken_fromFirmware, bleAddress);
                    } else if (response.code() == 400) {
                        deviceToken_fromFirmware = "";
                        dialogProvider.errorDialog("Server Error");
                    } else {
                        dialogProvider.errorDialog("Server Error");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    deviceToken_fromFirmware = "";
                }
            });
        } else {
            dialogProvider.errorDialog("No Internet\nCannot fetch Device Token");
        }
    }

    ArrayList<byte[]> deviceToken__byteArray;
    private void processDeviceTokenAndSend(String deviceToken_fromFirmware, String bleAddress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog("Please wait");
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
               deviceToken__byteArray = new ArrayList<byte[]>();
                List<String> listOfString_startPacket = splitString(deviceToken_fromFirmware, 13);
                int indexPosition = listOfString_startPacket.size();
                for (String individualString : listOfString_startPacket) {
                    deviceToken__byteArray.add(deviceTokenpacketArray(indexPosition, individualString));
                    indexPosition--;
                }

            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getBluetoothAdapter() != null) {
                    BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
                    if (bluetoothAdapter.isEnabled()) {
                        final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                        BleDevice bleDevice = new BleDevice(getBleDevice);
                        if (BleManager.getInstance().isConnected(bleDevice)) {
                            hexConverted_deviceToken = new ArrayList<String>();
                            hexConverted_deviceToken = getHexArrayList(deviceToken__byteArray);
                            writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_deviceToken.get(0).toCharArray()), "SERVER DEVICE CONFIGURATION", hexConverted_deviceToken);
                        }
                    }
                }
            }
        });
    }

    private void intializePreferenceInstance() {
        preferenceHelper = new PreferenceHelper(MainActivity.this);
    }

    private void removeallFragments() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void replaceFragment(Fragment fragment, String data_TobePassed, String String_Tag, boolean addtoBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransction = fragmentManager.beginTransaction();
        Bundle sendBundle = new Bundle();
        fragmentTransction.replace(R.id.mainActivity_container, fragment, fragment.toString());
        if (addtoBackStack) {
            fragmentTransction.addToBackStack(fragment.toString());
        }
        if ((data_TobePassed != null) && (String_Tag.length() > 0) && (!String_Tag.equalsIgnoreCase(""))) {
            sendBundle.putString(String_Tag, data_TobePassed);
            fragment.setArguments(sendBundle);
        }
        fragmentTransction.commit();
    }

    public void 100start_stop_SCAN(Context context) {
        if (new Utility().ble_on_off()) {
            /**
             * Rx Ble Start Scanning
             */
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                if (SCAN_TAG.equalsIgnoreCase("SCAN_NOT_STARTED")) {
                    RxBleStartScanning(context);
                    scanHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopScanning();
                        }
                    }, SCAN_PERIOD);
                } else if (SCAN_TAG.equalsIgnoreCase("SCAN_STARTED")) {
                } else if (SCAN_TAG.equalsIgnoreCase("SCAN_COMPLETED")) {
                    RxBleStartScanning(context);
                    scanHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopScanning();
                        }
                    }, SCAN_PERIOD);
                }
            }
        }
    }

    public void stopScanning() {
        if (new Utility().ble_on_off()) {
            mainActivityScanningInstance.dispose();
            SCAN_TAG = "SCAN_COMPLETED";
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivity_container);
            if (fragment != null) {
                if (fragment.toString().equalsIgnoreCase(new FragmentScan().toString())) {
                    new Utility().showTaost(this, "Scan Time Out ", getResources().getDrawable(R.drawable.scannertimeout));
                }
            }
            System.out.println("TESTDEMO SCAN_STOPPPED");
            if (goForConnect) {
                for (int i = 0; i < customBluetoothsNewScannning.size(); i++) {
                    if (customBluetoothsNewScannning.get(i).getBleaddress().equalsIgnoreCase("D4:22:50:31:E4:95")) {
                        System.out.println("TESTDEMO DEVICE_AVALIABLE");
                      //  connectRxBleDevice(customBluetoothsNewScannning.get(i).getCustom_RxBleDevice(), -1);
                        System.out.println("TESTDEMO GOING FOR CONECETION");
                        break;
                    }
                }
            } else {
            }
        }
    }

    ArrayList<CustomBluetooth> customBluetoothsNewScannning = new ArrayList<CustomBluetooth>();

    private void RxBleStartScanning(Context context) {
        RxBleClient rxBleClient = RxBleClient.create(context);
        try {
            SCAN_TAG = "SCAN_STARTED";
            Disposable scanDisposable = rxBleClient.scanBleDevices(
                    new com.polidea.rxandroidble2.scan.ScanSettings.Builder()
                            .setScanMode(com.polidea.rxandroidble2.scan.ScanSettings.SCAN_MODE_BALANCED)
                            .build(),
                    new com.polidea.rxandroidble2.scan.ScanFilter.Builder()
                            .setServiceUuid(null)
                            .build() // Optional, more than one ScanFilter may be passed as varargs.
            )
                    .subscribe(scanResult -> {
                        if (scanResult != null) {
                            if (scanResult != null) {
                                if (scanResult.getBleDevice() != null) {
                                    if (scanResult.getBleDevice().getName() != null) {
                                        final RxBleDevice bluetoothDevice = scanResult.getBleDevice();
                                        if ((passScanDevicesRxBle != null)) {
                                            if ((bluetoothDevice != null) && (bluetoothDevice.getBluetoothDevice().getName().startsWith(getResources().getString(R.string.device_name_filter)))) {
                                                CustomBluetooth customBluetooth = new CustomBluetooth(bluetoothDevice, getResources().getString(R.string.device_name_alias), bluetoothDevice.getMacAddress());
                                                passScanDevicesRxBle.scannedRxBleDevices(customBluetooth);
                                                customBleDeviceAfterDisconnection.add(customBluetooth);
                                                if (goForConnect) {
                                                    customBluetoothsNewScannning.add(customBluetooth);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }, throwable -> {
                    });
            mainActivityScanningInstance = scanDisposable;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUpInterfaceInMainActivity(PassScanDevicesRxBle loc_passScanDevicesRxBle) {
        passScanDevicesRxBle = loc_passScanDevicesRxBle;
    }

    public void setUpGeoFenceAlertDialogInterface(GeoFenceDialogAlertShow loc_geoFenceAlertDialogInterface) {
        geoFenceDialogAlertShow = loc_geoFenceAlertDialogInterface;
    }

    public void setUpConnectionStatus(ConnectionStatus loc_connectionStatus) {
        connectionStatus = loc_connectionStatus;
    }

    public void setUpconnectionProgressDialogShow(ConnectionProgressDialogShow loc_connectionProgressDialogShow) {
        connectionProgressDialogShow = loc_connectionProgressDialogShow;
    }

    public void setUpOpenDialogToCheckDeviceName(OpenDialogToCheckDeviceName loc_openDialogToCheckDeviceName) {
        openDialogToCheckDeviceName = loc_openDialogToCheckDeviceName;
    }

    public void setUpchatDeliveryACK(ChatDeliveryACK loc_chatDeliveryACK) {
        chatDeliveryACK = loc_chatDeliveryACK;
    }
    public void setUpLiveRequest(LiveRequestDataPassToFragment liveRequestDataPassToFragment_loc){
        liveRequestDataPassToFragment=liveRequestDataPassToFragment_loc;
    }
    public void setUpPassChatObjectToFragment(PassChatObjectToFragment passChatObjectToFragment_loc){
        passChatObjectToFragment=passChatObjectToFragment_loc;
    }

    public void interfaceImpleMainActivity() {
        setUpInterfaceInMainActivity(new PassScanDevicesRxBle() {
            @Override
            public void scannedRxBleDevices(CustomBluetooth customBluetooth) {
            }
        });
        setUpGeoFenceAlertDialogInterface(new GeoFenceDialogAlertShow() {
            @Override
            public void showDialogInterface(String ruleVioation, String bleAddress, String message_one, String messageTwo, String time_stamp) {

            }
        });
        setUpConnectionStatus(new ConnectionStatus() {
            @Override
            public void connectedDevicePostion(BluetoothDevice bluetoothDevice, boolean status) {

            }
        });
        setUpconnectionProgressDialogShow(new ConnectionProgressDialogShow() {
            @Override
            public void connectProgress(boolean connectStatus) {

            }
        });
        setUpOpenDialogToCheckDeviceName(new OpenDialogToCheckDeviceName() {
            @Override
            public void showDialogNameNotAvaliable(String bleAddressForDeviceAfterConfermation, String deviToken,String imeiNumberFromFirmware) {

            }
        });
        setUpchatDeliveryACK(new ChatDeliveryACK() {
            @Override
            public void chatDeliveryStatus(String bleAddress, String sequenceNumber, String messageStatus) {

            }
        });
        setUpLiveRequest(new LiveRequestDataPassToFragment() {
            @Override
            public void liveRequestDataFromFirmware(Double latitudeValue, Double longValue, String bleAddress, BleDevice bleDevice) {

            }
        });

        setUpPassChatObjectToFragment(new PassChatObjectToFragment() {
            @Override
            public void ChatObjetShare(ChattingObject chattingObject) {

            }
        });

        setupPassScanDeviceToActivity_interface(new PassScanDeviceToActivity_interface() {
            @Override
            public void sendCustomBleDevice(CustBluetootDevices custBluetootDevices) {

            }
        });
    }

    private void fastBleImplementation() {
        /**
         * Ble Connection library.
         */
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    @Override
    public void clickedDevice(CustBluetootDevices custombleDevice, int postion) {
        Utility utility = new Utility();
        if (!ble_on_off()) {
            utility.showTaost(MainActivity.this, "Turn On Bluetooth", getResources().getDrawable(R.drawable.ic_bluetoth_not_enabled));
            return;
        }

        if(!custombleDevice.isConnected()){

        }else if(custombleDevice.isConnected()){

        }



        if (!BleManager.getInstance().isConnected(custombleDevice.getBleaddress())) {
            connectRxBleDevice(custombleDevice.getCustom_RxBleDevice(), postion);
        } else {
            BleManager.getInstance().disconnect(new BleDevice(custombleDevice.getCustom_RxBleDevice().getBluetoothDevice()));
            if (main_Activity_connectedDevicesAliasList.size() > 0) {
                for (MainActivityConnectedDevices mainActivityConnecteDevices : main_Activity_connectedDevicesAliasList) {
                    if (mainActivityConnecteDevices.getBleDevice().getMac().equalsIgnoreCase(custombleDevice.getBleaddress())) {
                        mainActivityConnecteDevices.setIs_DisconnectedFromFirmware(true);
                        main_Activity_connectedDevicesAliasList.remove(mainActivityConnecteDevices);
                    }
                }
            }
        }
    }

    private void connectRxBleDevice(RxBleDevice bleDevice, int itemSelectedPositon) {
        BleManager.getInstance().connect(new BleDevice(bleDevice.getBluetoothDevice()), new BleGattCallback() {
            @Override
            public void onStartConnect() {
                System.out.println("onStartConnect ");
                if (connectionProgressDialogShow != null) {
                    connectionProgressDialogShow.connectProgress(true);
                }
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                System.out.println("onConnectFail " + bleDevice.getMac());
                if (connectionProgressDialogShow != null) {
                    connectionProgressDialogShow.connectProgress(false);
                }

                if (connectionStatus != null) {
                    connectionStatus.connectedDevicePostion(bleDevice.getDevice(), false);
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if (connectionProgressDialogShow != null) {
                    connectionProgressDialogShow.connectProgress(false);
                }

                System.out.println("TagCheck onConnectSuccess " + bleDevice.getMac());
                if (connectionStatus != null) {
                    connectionStatus.connectedDevicePostion(bleDevice.getDevice(), true);
                }
                notifyDataCharctersticChanged(bleDevice, itemSelectedPositon);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                System.out.println("TagCheck onDisConnected " + device.getMac() + " isActiveDisconnected= " + isActiveDisConnected);

                if (connectionProgressDialogShow != null) {
                    connectionProgressDialogShow.connectProgress(false);
                }

                if (connectionStatus != null) {
                    connectionStatus.connectedDevicePostion(device.getDevice(), false);
                }
                Utility utility = new Utility();
                utility.showTaost(MainActivity.this, "Disconnected", getResources().getDrawable(R.drawable.ic_location_not_enabled));
                checkisDisconnectedFromFirmware(bleDevice);
            }
        });
    }

    GeoFenceObjectData geoFenceObjectData;
    private List<LatLong> latLong;
    List<RuleId_Value_ActionBitMask> ruleId_value_actionBitMasks;


    /**
     * Used for Checking the log issues issues with setWriteType.
     */

    private void notifyDataCharctersticChanged(BleDevice bleDevice, int ItemPositionSelected) {
        /**
         * Making the writeType default
         */
        BleManager.getInstance().notify(
                bleDevice,
                TRACKER_SERVICE_UUID,
                TRACKER_CHARCTERSTICS_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                writeAuthenicationToDevice(bleDevice);
                            }
                        });
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                    }

                    @Override
                    public void onCharacteristicChanged(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    String blehexObtainedFrom_Firmware = "";
                                    blehexObtainedFrom_Firmware = HexUtil.encodeHexStr(data);
                                    /**
                                     * Differentiate here for connection maintainence code and encryption data mainpulation.
                                     */
                                    if ((blehexObtainedFrom_Firmware.length() == 6) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("0101"))) {
                                        System.out.println(" For Connection Maintainence Code  " + blehexObtainedFrom_Firmware);
                                        int m_auth_key = hexToint(blehexObtainedFrom_Firmware.substring(4, 6));
                                        int calculatedMagicNumber = calculateAlgorithmValue(m_auth_key);
                                        byte[] ConnectionArray = getConnectionMainCode(calculatedMagicNumber);
                                        writeConnectionMaintainceCode(bleDevice, ConnectionArray, ItemPositionSelected);
                                    }
                                    else {
                                        /**
                                         * Go for the Encryption Code Manipulation here.
                                         * 1)Take the encrypted byte array.
                                         * 2)Decrypt the byte array.
                                         * 3)Convert the Decrypt array to HexString.
                                         */
                                        try {
                                            byte[] decrypted_byteArray_FromFirmware = decryptData(data);
                                            String hex_converted_decrypted_byte_array = HexUtil.encodeHexStr(decrypted_byteArray_FromFirmware);
                                            blehexObtainedFrom_Firmware = hex_converted_decrypted_byte_array;
                                            System.out.println("Decrypted Firmware HEX Value= " + blehexObtainedFrom_Firmware);
                                        } catch (IllegalBlockSizeException e) {
                                            e.printStackTrace();
                                        } catch (InvalidKeyException e) {
                                            e.printStackTrace();
                                        } catch (BadPaddingException e) {
                                            e.printStackTrace();
                                        } catch (NoSuchAlgorithmException e) {
                                            e.printStackTrace();
                                        } catch (NoSuchPaddingException e) {
                                            e.printStackTrace();
                                        }
                                        /**
                                         * GeoFence Data Manipulation Start.
                                         */
                                        if ((blehexObtainedFrom_Firmware.toString().startsWith("a6"))) {
                                            /**
                                             * add all the GeoFence Id to the Map.
                                             */
                                            if ((blehexObtainedFrom_Firmware.toString().startsWith("a6")) && (blehexObtainedFrom_Firmware.length() == 32) && (!blehexObtainedFrom_Firmware.substring(2, 4).equalsIgnoreCase("00"))) {
                                                /**
                                                 *  3 Packets Validation
                                                 */
                                                /**
                                                 * Key:- ID ,              Value= TimeStamp
                                                 */
                                                String id_timeStamp_1 = hexToint(blehexObtainedFrom_Firmware.substring(4, 8)) + ":" + convertHexToLong(blehexObtainedFrom_Firmware.substring(8, 16));
                                                String id_timeStamp_2 = hexToint(blehexObtainedFrom_Firmware.substring(16, 20)) + ":" + convertHexToLong(blehexObtainedFrom_Firmware.substring(20, 28));

                                                if (!id_timeStamp_1.equalsIgnoreCase("0:0")) {
                                                    from_firmware_ID_TimeStamp.add(id_timeStamp_1);
                                                }

                                                if (!id_timeStamp_2.equalsIgnoreCase("0:0")) {
                                                    from_firmware_ID_TimeStamp.add(id_timeStamp_2);
                                                }


                                            }
                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.equalsIgnoreCase("A4000000000000000000000000000000"))) {

                                            /**
                                             * Check if the Firmware dosent contains any ID_TIME stamp send ACK to the firmware..
                                             */
                                            showProgressDialog("Syncing");
                                            process_TimeStamp_id_One_After_Other_A6_packet(bleDevice);

                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.equalsIgnoreCase("a2010000000000000000000000000000"))) {
                                            /**
                                             * Error Packet of the GeoFence.
                                             */
                                            System.out.println("Error Packet From Firmware");
                                            cancelProgressDialog();

                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("a201"))) {

                                            String locaVariableForBlock = blehexObtainedFrom_Firmware;
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {

                                                    /**
                                                     * First Packet Started.
                                                     */
                                                    System.gc();
                                                    geoFenceObjectData = new GeoFenceObjectData();
                                                    latLong = geoFenceObjectData.getLatLongInstance();
                                                    ruleId_value_actionBitMasks = geoFenceObjectData.getInstanceOfruleId_value_actionBitMasks();
                                                    /**
                                                     * 1st packet Parameters
                                                     * GeoFence ID
                                                     *Geo Fence Size
                                                     *Geo Fence Type
                                                     *Geo Fence Radius_Vertices.
                                                     */
                                                    geoFenceObjectData.setGeoId(hexToint(locaVariableForBlock.substring(6, 10)));
                                                    geoFenceObjectData.setGeosize(hexToint(locaVariableForBlock.substring(10, 14)));
                                                    geoFenceObjectData.setGeoFenceType(checkType(locaVariableForBlock.substring(14, 16)));
                                                    geoFenceObjectData.setRadius_vertices(Double.parseDouble("" + convert4bytes(locaVariableForBlock.substring(16, 24))));
                                                    geoFenceObjectData.setFirmwareTimeStamp("" + convertHexToLong(locaVariableForBlock.substring(24, 32)));
                                                    AsyncTask.execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            /**
                                                             * Removed the GeoFence ID details in the Table GeoFence,Rules_info_Table.
                                                             */
                                                            roomDBHelperInstance.get_GeoFence_info_dao().deleteRecordFromGeoFenceId(""+geoFenceObjectData.getGeoId());
                                                            roomDBHelperInstance.get_RulesTable_dao().deleteRecordFromGeoFenceId(""+geoFenceObjectData.getGeoId());

                                                        }
                                                    });
                                                }
                                            });

                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("a202"))) {

                                            String locaVariableForBlock = blehexObtainedFrom_Firmware;

                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    /**
                                                     * 2 nd Packet Parameter Packet format
                                                     * Latitude
                                                     * Longitude
                                                     */

                                                    String lat_from_firmware_1 = locaVariableForBlock.substring(6, 14);
                                                    String long_from_firmware_1 = locaVariableForBlock.substring(14, 22);

                                                    if (!(lat_from_firmware_1.equalsIgnoreCase("00000000")) && !(long_from_firmware_1.equalsIgnoreCase("00000000"))) {
                                                        latLong.add(new LatLong(
                                                                Double.parseDouble(getFloatingPointValueFromHex(lat_from_firmware_1)),
                                                                Double.parseDouble(getFloatingPointValueFromHex(long_from_firmware_1))
                                                        ));
                                                    }


                                                }
                                            });
                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("a203"))) {

                                            String locaVariableForBlock = blehexObtainedFrom_Firmware;
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    /**
                                                     *  3rd  Packet Parameter
                                                     *  Number of Rules
                                                     */
                                                    geoFenceObjectData.setGsm_ReportingTime("" + convert4bytes(locaVariableForBlock.substring(6, 14)));
                                                    geoFenceObjectData.setIridium_reportingTime("" + convert4bytes(locaVariableForBlock.substring(14, 22)));
                                                    geoFenceObjectData.setNumberOfRules(hexToint(locaVariableForBlock.substring(22, 24)));


                                                }
                                            });

                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("a204"))) {

                                            String locaVariableForBlock = blehexObtainedFrom_Firmware;
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    /**
                                                     * 4 th  Packet Parameter
                                                     * RuleId
                                                     * Value
                                                     * ActionBitMask
                                                     */
                                                    ruleId_value_actionBitMasks.add(new RuleId_Value_ActionBitMask(
                                                            hexToint(locaVariableForBlock.substring(6, 8)),
                                                            "" + convert4bytes(locaVariableForBlock.substring(8, 16)),
                                                            "" + convert4bytes(locaVariableForBlock.substring(16, 24))
                                                    ));
                                                }
                                            });

                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 8).equalsIgnoreCase("a2050101"))) {

                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (geoFenceObjectData.getGeoFenceType().equalsIgnoreCase("Circular")) {
                                                        insertIntoGeoFenceTable(geoFenceObjectData);
                                                        for (int i = 0; i < ruleId_value_actionBitMasks.size(); i++) {
                                                            roomDBHelperInstance.get_RulesTable_dao().insert_RulesTable(new Rules("NA", "" + geoFenceObjectData.getGeoId(), "" + ruleId_value_actionBitMasks.get(i).getRuleId(), ruleId_value_actionBitMasks.get(i).getValue()));
                                                        }
                                                    } else if (geoFenceObjectData.getGeoFenceType().equalsIgnoreCase("Polygon")) {
                                                        insertIntoGeoFenceTable(geoFenceObjectData);
                                                        for (int i = 0; i < latLong.size(); i++) {
                                                            roomDBHelperInstance.get_Polygon_info_dao().insert_Polygon(new PolygonEnt("" + geoFenceObjectData.getGeoId(), "" + latLong.get(i).getLatitude(), "" + latLong.get(i).getLongitude(), "" + geoFenceObjectData.getFirmwareTimeStamp()));
                                                        }
                                                        for (int i = 0; i < ruleId_value_actionBitMasks.size(); i++) {
                                                            roomDBHelperInstance.get_RulesTable_dao().insert_RulesTable(new Rules("NA", "" + geoFenceObjectData.getGeoId(), "" + ruleId_value_actionBitMasks.get(i).getRuleId(), "" + ruleId_value_actionBitMasks.get(i).getValue()));
                                                        }
                                                    }

                                                }
                                            });
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    System.out.println("**************1st Packet *********** ");
                                                    System.out.println("Details_Tag Id = " + geoFenceObjectData.getGeoId());
                                                    System.out.println("Details_Tag Type = " + geoFenceObjectData.getGeoFenceType());
                                                    System.out.println("Details_Tag Geo Size = " + geoFenceObjectData.getGeosize());
                                                    System.out.println("Details_Tag Geo Radius_Vertices = " + geoFenceObjectData.getRadius_vertices());
                                                    System.out.println("-------------------------------------------------------------- ");
                                                    System.out.println("**************2 nd Packet*********** ");
                                                    System.out.println("Details_Tag number of LatLong Obtaine= " + geoFenceObjectData.getLatLong().size());
                                                    for (int i = 0; i < geoFenceObjectData.getLatLong().size(); i++) {
                                                        System.out.println("Details_Tag  latitude= " + geoFenceObjectData.getLatLong().get(i).getLatitude() + " Longitude " + geoFenceObjectData.getLatLong().get(i).getLongitude());
                                                    }
                                                    System.out.println("-------------------------------------------------------------- ");
                                                    System.out.println("Details_Tag Geo Number of Rules = " + geoFenceObjectData.getNumberOfRules());
                                                    System.out.println("--------------------------------------------------------------");
                                                    System.out.println("************** 4th Packet*********** ");
                                                    for (int i = 0; i < ruleId_value_actionBitMasks.size(); i++) {
                                                        System.out.println("Details_Tag RuleId_Value_NumberOfAction Rule ID = " + ruleId_value_actionBitMasks.get(i).getRuleId() + " Value = " + ruleId_value_actionBitMasks.get(i).getValue() + " Number of BitMask = " + ruleId_value_actionBitMasks.get(i).getActionBitMask());
                                                    }
                                                    System.out.println("--------------------------------------------------------------");
                                                    /**
                                                     * Used to remove the TimeStamp and ID from A6 packets.
                                                     */
                                                    removeId_time_Stamp_from_firmware_arraylist(bleDevice, geoFenceObjectData.getGeoId() + ":" + geoFenceObjectData.getFirmwareTimeStamp());
                                                    System.out.println("Removed ID and TimeStamp from the arrayList ID= " + geoFenceObjectData.getGeoId() + " TimeStamp= " + geoFenceObjectData.getFirmwareTimeStamp());
                                                    process_TimeStamp_id_One_After_Other_A6_packet(bleDevice);
                                                    /**
                                                     * Used to remove the TimeStamp and ID from A8 packets.
                                                     */
                                                    remove_id_time_stamp_from_A8_packet_SendAck_if_packet_process_Finished(geoFenceObjectData.getGeoId() + ":" + geoFenceObjectData.getFirmwareTimeStamp(), bleDevice);
                                                }
                                            });


                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 2).equalsIgnoreCase("a6"))) {

                                            String id_1 = blehexObtainedFrom_Firmware.substring(4, 8);
                                            String timeStamp_1 = blehexObtainedFrom_Firmware.substring(8, 16);

                                            String id_2 = blehexObtainedFrom_Firmware.substring(16, 20);
                                            String timeStamp_2 = blehexObtainedFrom_Firmware.substring(20, 28);

                                            if ((!id_1.equalsIgnoreCase("0000")) && (!timeStamp_1.equalsIgnoreCase("00000000"))) {
                                                from_firmware_ID_TimeStamp_A6_Packet.add(hexToint(id_1) + ":" + convertHexToLong(timeStamp_1));
                                            }
                                            if ((!id_2.equalsIgnoreCase("0000")) && (!timeStamp_2.equalsIgnoreCase("00000000"))) {
                                                from_firmware_ID_TimeStamp_A6_Packet.add(hexToint(id_2) + ":" + convertHexToLong(timeStamp_2));
                                            }

                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 2).equalsIgnoreCase("a5"))) {
                                            String locaVariableForBlock = blehexObtainedFrom_Firmware;
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    /**
                                                     * Hex Values
                                                     */
                                                    String geoFenceId_hex = locaVariableForBlock.substring(2, 6);
                                                    String breachLatitude_hex = locaVariableForBlock.substring(6, 14);
                                                    String breachLongitude_hex = locaVariableForBlock.substring(14, 22);
                                                    String rule_Id_hex = locaVariableForBlock.substring(22, 24);
                                                    String rule_Value_hex = locaVariableForBlock.substring(24, 32);
                                                    /**
                                                     * Hex Converted Values
                                                     */
                                                    String geoFence_id = removePreviousZero("" + hexToint(geoFenceId_hex));
                                                    String breach_latitude = getFloatingPointValueFromHex(breachLatitude_hex);
                                                    String breach_longitude = getFloatingPointValueFromHex(breachLongitude_hex);
                                                    String rule_id = "" + hexToint(rule_Id_hex);
                                                    String rule_value = removePreviousZero("" + hexToint(rule_Value_hex));
                                                    String bleAddressWithColon = bleDevice.getMac();
                                                    String bleAddress = bleDevice.getMac().replace(":", "").toLowerCase();
                                                    boolean isGeoFenceId_Avaliable = roomDBHelperInstance.get_GeoFence_info_dao().isGeoFenceId_Avaliable(geoFence_id);
                                                    if (isGeoFenceId_Avaliable) {
                                                        String geofenceType =roomDBHelperInstance.get_GeoFence_info_dao().getGeoFenceTypeFromGeoFenceId(geoFence_id);
                                                        String originalValue = "NA";
                                                        String bleDeviceNameSaved = roomDBHelperInstance.get_TableDevice_dao().getDeviceNameSavedFromBleAddress(bleAddress);
                                                        originalValue =  removePreviousZero(roomDBHelperInstance.get_RulesTable_dao().getRuleValueFromGeoFenceId_ruleId(geoFence_id,rule_id));
                                                        if (originalValue.equalsIgnoreCase("")) {
                                                            originalValue = "NA";
                                                        }
                                                        String timeStamp = getCurrenTimeStamp();
                                                        GeofenceAlert geofenceAlert = new GeofenceAlert();
                                                        geofenceAlert.setGeofence_ID(geoFence_id);
                                                        geofenceAlert.setGeo_name("NA");
                                                        geofenceAlert.setGeo_Type(geofenceType);

                                                        /**
                                                         * BreachType Check
                                                         */

                                                        if (rule_id.equalsIgnoreCase("07")) {
                                                            if (rule_Value_hex.equalsIgnoreCase("00000000")) {
                                                                geofenceAlert.setBreach_Type("OUT");

                                                            } else if (rule_Value_hex.equalsIgnoreCase("00000001")) {
                                                            }
                                                            geofenceAlert.setBreach_Type("IN");
                                                        }
                                                        geofenceAlert.setBreach_Lat(breach_latitude);
                                                        geofenceAlert.setBreach_Long(breach_longitude);
                                                        geofenceAlert.setDate_Time(getDateTime());
                                                        geofenceAlert.setTimeStamp(timeStamp);
                                                        geofenceAlert.setBleAddress("SC2 Device : " + bleAddressWithColon);
                                                        geofenceAlert.setIs_Read("0");
                                                        geofenceAlert.setBreachRule_ID(rule_id);
                                                        geofenceAlert.setAlias_name_alert(bleDeviceNameSaved);

                                                        /**
                                                         * Rule Name
                                                         */
                                                        if (rule_Id_hex.equalsIgnoreCase("03")) {
                                                            geofenceAlert.setRule_Name("Breach Minimum Dwell Time");

                                                        } else if (rule_Id_hex.equalsIgnoreCase("04")) {

                                                            geofenceAlert.setRule_Name("Breach Maximum Dwell Time");

                                                        } else if (rule_Id_hex.equalsIgnoreCase("05")) {
                                                            geofenceAlert.setRule_Name("Breach Minimum speed limit");

                                                        } else if (rule_Id_hex.equalsIgnoreCase("06")) {
                                                            geofenceAlert.setRule_Name("Breach Maximum speed limit");

                                                        } else if (rule_Id_hex.equalsIgnoreCase("07")) {
                                                            geofenceAlert.setRule_Name("Boundary Cross Violation");

                                                        } else if (rule_Id_hex.equalsIgnoreCase("01")) {
                                                            geofenceAlert.setRule_Name("Start Date Geo Fence Enabled/Disabled");

                                                        } else if (rule_Id_hex.equalsIgnoreCase("02")) {
                                                            geofenceAlert.setRule_Name("End Date Geo Fence Enabled/Disabled");

                                                        }
                                                        geofenceAlert.setBreachRuleValue(rule_value);
                                                        if (rule_Id_hex.equalsIgnoreCase("01")) {
                                                            /**
                                                             * Not used by the firmware
                                                             */
                                                            geofenceAlert.setMessage_one("GeoFence Should be Disabled/Enabled");
                                                            geofenceAlert.setMessage_two("Date: ");
                                                        } else if (rule_Id_hex.equalsIgnoreCase("02")) {
                                                            /**
                                                             * Not used by the Firmware
                                                             */
                                                            geofenceAlert.setMessage_one("GeoFence Should be Disabled/Enabled");
                                                            geofenceAlert.setMessage_two("Date: ");
                                                        } else if (rule_Id_hex.equalsIgnoreCase("03")) {

                                                            geofenceAlert.setMessage_one("Minimum Dwell Time Permitted :  " + originalValue + " Min");
                                                            String messageTwo = rule_value;
                                                            if (messageTwo.equalsIgnoreCase("")) {
                                                                messageTwo = "0";
                                                            }
                                                            geofenceAlert.setMessage_two("Current Time :" + messageTwo + " Min");
                                                        } else if (rule_Id_hex.equalsIgnoreCase("04")) {
                                                            geofenceAlert.setMessage_one("Maximum Dwell Time Permitted :  " + originalValue + " Min");
                                                            String messageTwo = rule_value;
                                                            if (messageTwo.equalsIgnoreCase("")) {
                                                                messageTwo = "0";
                                                            }
                                                            geofenceAlert.setMessage_two("Current Time :" + messageTwo + " Min");
                                                        } else if (rule_Id_hex.equalsIgnoreCase("05")) {
                                                            geofenceAlert.setMessage_one("Minimum Speed Limit  :  " + originalValue + " km/hr");
                                                            String messageTwo = rule_value;
                                                            if (messageTwo.equalsIgnoreCase("")) {
                                                                messageTwo = "0";
                                                            }
                                                            geofenceAlert.setMessage_two("Current Speed :" + messageTwo + " km/hr");
                                                        } else if (rule_Id_hex.equalsIgnoreCase("06")) {
                                                            geofenceAlert.setMessage_one("Maximum Speed Limit  :  " + originalValue + " km/hr");
                                                            String messageTwo = rule_value;
                                                            if (messageTwo.equalsIgnoreCase("")) {
                                                                messageTwo = "0";
                                                            }
                                                            geofenceAlert.setMessage_two("Current Speed :" + messageTwo + " km/hr");

                                                        } else if (rule_Id_hex.equalsIgnoreCase("07")) {

                                                            if (rule_Value_hex.equalsIgnoreCase("00000001")) {
                                                                geofenceAlert.setMessage_one("came in GeoFence" + "ID= " + geoFence_id);
                                                                geofenceAlert.setMessage_two("");
                                                            } else if (rule_Value_hex.equalsIgnoreCase("00000000")) {
                                                                geofenceAlert.setMessage_one("Went out of Geo Fence" + "ID= " + geoFence_id);
                                                                geofenceAlert.setMessage_two("");
                                                            }
                                                        }
                                                        geofenceAlert.setOriginalRuleValue(originalValue);
                                                        String geoFence_timestamp = roomDBHelperInstance.get_GeoFence_info_dao().getFirmwareTimeStampFromGeoFenceId(geoFence_id);
                                                        String geoFence_lat = roomDBHelperInstance.get_GeoFence_info_dao().getLatitudeFromGeoFenceId(geoFence_id);
                                                        String geoFence_long = roomDBHelperInstance.get_GeoFence_info_dao().getLongitudeFromGeoFenceId(geoFence_id);
                                                        String geoFence_long_vertices_radius = roomDBHelperInstance.get_GeoFence_info_dao().getRadiusVerticesFromGeoFenceId(geoFence_id);
                                                        geofenceAlert.setGeoFence_timestamp(geoFence_timestamp);
                                                        geofenceAlert.setGeoFence_lat(geoFence_lat);
                                                        geofenceAlert.setGeoFence_long(geoFence_long);
                                                        geofenceAlert.setGeoFence_radius_vertices(geoFence_long_vertices_radius);
                                                        roomDBHelperInstance.get_GeoFenceAlert_info_dao().insert_GeoFence_Alert(geofenceAlert);

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (geoFenceDialogAlertShow != null) {
                                                                    HistroyList histroyList = new HistroyList();


                                                                    histroyList.setGeoFenceId(geofenceAlert.getId());
                                                                    histroyList.setGeoFenceType(geofenceAlert.getGeo_Type());
                                                                    histroyList.setBreachlatitude(Double.parseDouble(geofenceAlert.getBreach_Lat()));
                                                                    histroyList.setBreachLongitude(Double.parseDouble(geofenceAlert.getBreach_Long()));
                                                                    histroyList.setBrachMessage(geofenceAlert.getRule_Name());
                                                                    histroyList.setMessage_one(geofenceAlert.getMessage_one());
                                                                    histroyList.setMessage_two(geofenceAlert.getMessage_two());
                                                                    histroyList.setTimeStamp(timeStamp);
                                                                    histroyList.setGeoFenceTimStamp(geoFence_timestamp);

                                                                    /**
                                                                     * Increment the alertNotification for Evey alert
                                                                     */
                                                                    String header_ruleViolation = geofenceAlert.getRule_Name();

                                                                    String ble_deviceName = bleDeviceNameSaved;
                                                                    String frist_message = geofenceAlert.getMessage_one();
                                                                    String second_message = geofenceAlert.getMessage_two();
                                                                    geoFenceDialogAlertShow.showDialogInterface(header_ruleViolation, ble_deviceName, frist_message, second_message, timeStamp);
                                                                    if (application_Visible_ToUser) {
                                                                        /**
                                                                         * show notification to the user  when the application is in backGround
                                                                         */
                                                                        createNotification(ble_deviceName, header_ruleViolation, frist_message, second_message);
                                                                    } else if (checkPhoneScreenLocked() && (!application_Visible_ToUser)) {
                                                                        /**
                                                                         * Show notification to the user when the application is in forground and Device is locked.
                                                                         * This Screen lock method will work only when the user has selcted any one of the security patters for locking the screen other than none.
                                                                         */
                                                                        createNotification(ble_deviceName, header_ruleViolation, frist_message, second_message);
                                                                    }
                                                                    try {
                                                                        Thread.sleep(10);
                                                                        writeDataToFirmware(bleDevice, sendAckReadyForNextPacket(), "A5 ALERTS RECIEVED,SENDING ACK READY FOR NEXT ALERT");
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    playAlertMusic();
                                                                    System.gc();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("a801"))) {
                                            from_firmware_ID_TimeStamp_A8_Packet = new ArrayList<String>();
                                            from_firmware_ID_TimeStamp_A8_Packet.clear();
                                            System.gc();
                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("a802"))) {
                                            String locaVariableForBlock = blehexObtainedFrom_Firmware;
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {

                                                    String id_1 = locaVariableForBlock.substring(4, 8);
                                                    String timeStamp_1 = locaVariableForBlock.substring(8, 16);

                                                    String id_2 = locaVariableForBlock.substring(16, 20);
                                                    String timeStamp_2 = locaVariableForBlock.substring(20, 28);

                                                    if ((!id_1.equalsIgnoreCase("0000")) && (!timeStamp_1.equalsIgnoreCase("00000000"))) {
                                                        from_firmware_ID_TimeStamp_A8_Packet.add(hexToint(id_1) + ":" + convertHexToLong(timeStamp_1));
                                                    }
                                                    if ((!id_2.equalsIgnoreCase("0000")) && (!timeStamp_2.equalsIgnoreCase("00000000"))) {
                                                        from_firmware_ID_TimeStamp_A8_Packet.add(hexToint(id_2) + ":" + convertHexToLong(timeStamp_2));
                                                    }
                                                }
                                            });
                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 4).equalsIgnoreCase("a803"))) {
                                            process_TimeStamp_id_One_After_Other_A8_packet(bleDevice);
                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && (blehexObtainedFrom_Firmware.substring(0, 2).equalsIgnoreCase("b1"))) {
                                            /**
                                             * ACK packet for the message sent.
                                             */
                                            String locaVariableForBlock = blehexObtainedFrom_Firmware;
                                            String sequenceNumber=""+convert4bytes(locaVariableForBlock.substring(4,12));
                                            String messageACK=locaVariableForBlock.substring(12,14);
                                            String messageStatusProcessed = "";

                                            if (messageACK.equalsIgnoreCase("00")) {
                                                messageStatusProcessed = getString(R.string.fragment_chat_message_mesaage_invalid_channel_id);
                                                changeMessageStatusInDb(bleDevice.getMac().replace(":", "").toLowerCase(), sequenceNumber, messageStatusProcessed);
                                            } else if (messageACK.equalsIgnoreCase("01")) {
                                                messageStatusProcessed = getString(R.string.fragment_chat_message_mesaage_full_message_recieved_by_device);
                                                changeMessageStatusInDb(bleDevice.getMac().replace(":", "").toLowerCase(), sequenceNumber, messageStatusProcessed);
                                            } else if (messageACK.equalsIgnoreCase("02")) {
                                                messageStatusProcessed = getString(R.string.fragment_chat_message_mesaage_message_sent_gsm);
                                                changeMessageStatusInDb(bleDevice.getMac().replace(":", "").toLowerCase(), sequenceNumber, messageStatusProcessed);
                                            } else if (messageACK.equalsIgnoreCase("03")) {
                                                messageStatusProcessed = getString(R.string.fragment_chat_message_mesaage_failed_message_gsm);
                                                changeMessageStatusInDb(bleDevice.getMac().replace(":", "").toLowerCase(), sequenceNumber, messageStatusProcessed);
                                            } else if (messageACK.equalsIgnoreCase("04")) {
                                                messageStatusProcessed = getString(R.string.fragment_chat_message_mesaage_send_to_iridium);
                                                changeMessageStatusInDb(bleDevice.getMac().replace(":", "").toLowerCase(), sequenceNumber, messageStatusProcessed);
                                            } else if (messageACK.equalsIgnoreCase("05")) {
                                                messageStatusProcessed = getString(R.string.fragment_chat_message_mesaage_server_sending_failed);
                                                changeMessageStatusInDb(bleDevice.getMac().replace(":", "").toLowerCase(), sequenceNumber, messageStatusProcessed);
                                            } else {
                                                messageStatusProcessed = getString(R.string.fragment_chat_message_mesaage_failed_app);
                                                changeMessageStatusInDb(bleDevice.getMac().replace(":", "").toLowerCase(), sequenceNumber, messageStatusProcessed);
                                            }
                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && ((blehexObtainedFrom_Firmware.substring(0, 2).equalsIgnoreCase("e1")))) {
                                            String localVariableForBlock = blehexObtainedFrom_Firmware;
                                            localVariableForBlock = localVariableForBlock.substring(16, 18) + localVariableForBlock.substring(14, 16) + localVariableForBlock.substring(12, 14) + localVariableForBlock.substring(10, 12) + localVariableForBlock.substring(8, 10) + localVariableForBlock.substring(6, 8) + localVariableForBlock.substring(4, 6);
                                            /**
                                             * After getting the IMEI.
                                             * Ask for the token from firmware(If token avaliable in DB)
                                             * API(Not avaliable in DB,Get from API)
                                             */
                                            imeiNumberFomFirmware="";
                                            imeiNumberFomFirmware=convert7bytesToLong(localVariableForBlock);
                                            checkTokenAvaliableForImeiNumber(bleDevice.getDevice().getAddress(),imeiNumberFomFirmware);
                                        }
                                        else if ((blehexObtainedFrom_Firmware.length() == 32) && ((blehexObtainedFrom_Firmware.substring(0, 2).equalsIgnoreCase("e2")))) {
                                            String localVariableForBlock = blehexObtainedFrom_Firmware;
                                            if (localVariableForBlock.equalsIgnoreCase("e2010100000000000000000000000000")) {
                                                /**
                                                 * Ask for geoFence ID and Add Devices for Re-Connection.
                                                 * As its valid Token..
                                                 */

                                                if (main_Activity_connectedDevicesAliasList.size() > 0) {
                                                    Iterator<MainActivityConnectedDevices> iter = main_Activity_connectedDevicesAliasList.iterator();
                                                    while (iter.hasNext()) {
                                                        MainActivityConnectedDevices mainActivityConnectedDevices = iter.next();
                                                        if (mainActivityConnectedDevices.isIs_DisconnectedFromFirmware()) {
                                                            iter.remove();
                                                        }
                                                    }
                                                }
                                                main_Activity_connectedDevicesAliasList.add(new MainActivityConnectedDevices(bleDevice, false));
                                                cancelProgressDialog();
                                                AsyncTask.execute(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        boolean isRecordAvaliableForBleAddress = roomDBHelperInstance.get_TableDevice_dao().isRecordAvaliableForBleAddress(bleDevice.getMac().toLowerCase().replace(":", ""));
                                                        if (isRecordAvaliableForBleAddress) {
                                                            loadGeoFenceId_TimeStamp(bleDevice);
                                                        } else {
                                                            if ((openDialogToCheckDeviceName != null) && (ItemPositionSelected != -1)) {
                                                                openDialogToCheckDeviceName.showDialogNameNotAvaliable(bleAddressForDeviceAfterConfermation, deviceToken_fromFirmware,imeiNumberFomFirmware);
                                                                imeiNumberFomFirmware="";
                                                            }
                                                            loadGeoFenceId_TimeStamp(bleDevice);
                                                        }
                                                    }
                                                });

                                            } else if (localVariableForBlock.equalsIgnoreCase("e2010000000000000000000000000000")) {
                                                /**
                                                 * Disconnect the device Give pop up.
                                                 * Token is Invalid.
                                                 */

                                                deviceToken_fromFirmware = "";
                                                cancelProgressDialog();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialogProvider.errorDialog("Invalid Token");
                                                    }
                                                });
                                                BleManager.getInstance().disconnect(bleDevice);
                                            } else if (localVariableForBlock.equalsIgnoreCase("e2010200000000000000000000000000")) {
                                                /**
                                                 * Device Token not found.So Disconnect
                                                 */
                                                deviceToken_fromFirmware = "";
                                                cancelProgressDialog();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialogProvider.errorDialog("Try After Some Time");
                                                    }
                                                });
                                                BleManager.getInstance().disconnect(bleDevice);
                                            }

                                        }
                                        else if((blehexObtainedFrom_Firmware.length() == 32) && ((blehexObtainedFrom_Firmware.substring(0, 2).equalsIgnoreCase("e3")))){
                                            /**
                                             * live tracking data obtained from the firmware.
                                             */
                                            String localVariableForBlock = blehexObtainedFrom_Firmware;
                                            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivity_container);
                                            if (fragment.toString().equalsIgnoreCase(new FragmentLiveTracking().toString())) {
                                                if(liveRequestDataPassToFragment!=null){
                                                    liveRequestDataPassToFragment.liveRequestDataFromFirmware(Double.parseDouble(getFloatingPointValueFromHex(localVariableForBlock.substring(4,12))), Double.parseDouble(getFloatingPointValueFromHex(localVariableForBlock.substring(12,20))),
                                                            bleDevice.getMac(),
                                                            bleDevice
                                                    );

                                                }
                                            }else {
                                                hexConverted_liveLocation = new ArrayList<String>();
                                                if (getBluetoothAdapter() != null) {
                                                    BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
                                                    if (bluetoothAdapter.isEnabled()) {
                                                        if (BleManager.getInstance().isConnected(bleDevice)) {
                                                            hexConverted_liveLocation = getHexArrayList(Start_Stop_LIVE_LOCATION(false));
                                                            writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_liveLocation.get(0).toCharArray()), "Live Location Request "+false, hexConverted_liveLocation);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c1010100000000000000000000000000"))){
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Setting Saved", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                 onBackPressed();
                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                        else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c3010100000000000000000000000000"))){
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Sim Details\nsaved", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                    onBackPressed();
                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }   else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c4010100000000000000000000000000"))){
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Server Configuration\nSaved", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                    onBackPressed();
                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                        else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c5010100000000000000000000000000"))){
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Device Reset Sucessfull", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }  else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c2010100000000000000000000000000"))){
                                            /**
                                             * Industry Specifc Configuration Sucess.
                                             */
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Industry Specific Configuration Saved", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                    onBackPressed();

                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        } else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c2010000000000000000000000000000"))){
                                            /**
                                             * Industry Specifc Configuration failure.
                                             */
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Something Went Wrong\nPlease try again.", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                    onBackPressed();

                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c6010100000000000000000000000000"))){
                                            /**
                                             * Wifi Configuration Sucess.
                                             */
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Wifi Configuration Saved", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                    onBackPressed();

                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.equalsIgnoreCase("c6010000000000000000000000000000"))){
                                            /**
                                             * Wifi Configuration Sucess.
                                             */
                                            cancelProgressDialog();
                                            dialogProvider.errorDialogWithCallBack("SC2 Companion App","Something Went Wrong\nPlease try again.", 0, false, new onAlertDialogCallBack() {
                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                    onBackPressed();

                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }else if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.substring(0,2).equalsIgnoreCase("b2"))){
                                            /**
                                             * Process the incomming message s here.
                                             */
                                                    if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.substring(0,2).equalsIgnoreCase("b2"))&&(blehexObtainedFrom_Firmware.substring(4,6).equalsIgnoreCase("01"))){
                                                        /**
                                                         * message Start packet with upcode 01
                                                         */
                                                        String localVariableForBlock= blehexObtainedFrom_Firmware;
                                                        /**
                                                         * Message Starting packet.
                                                         */
                                                        incommingMessagePacket=new IncommingMessagePacket();
                                                        incommingMessagePacket.setTotalLengthOfTextmessage(""+Integer.parseInt(localVariableForBlock.substring(8,10),16));
                                                        String  timeStampInHexOppsite=localVariableForBlock.substring(16,18)+""+localVariableForBlock.substring(14,16)+""+localVariableForBlock.substring(12,14)+""+localVariableForBlock.substring(10,12);
                                                        incommingMessagePacket.setTimeStamp(""+Integer.parseInt(timeStampInHexOppsite,16));
                                                        incommingMessagePacket.setSequenceNumber(""+Integer.parseInt(localVariableForBlock.substring(18,26),16));
                                                    }
                                            if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.substring(0,2).equalsIgnoreCase("b2"))&&(blehexObtainedFrom_Firmware.substring(4,6).equalsIgnoreCase("02"))){
                                                /**
                                                 * message Start packet with upcode 02
                                                 */
                                                String localVariableForBlock= blehexObtainedFrom_Firmware;
                                                /**
                                                 * Message Starting packet.
                                                 */
                                                incommingMessagePacket.setMessagePacketDataLength(""+Integer.parseInt(localVariableForBlock.substring(2,4),16));
                                                int messageToExtractFrom=Integer.parseInt(incommingMessagePacket.getMessagePacketDataLength());
                                                messageToExtractFrom=messageToExtractFrom-2;
                                                messageToExtractFrom=messageToExtractFrom*2;
                                                String hexString=localVariableForBlock.substring(8,messageToExtractFrom+8);
                                                String getlast2charcters=hexString.length() > 2 ? hexString.substring(hexString.length() - 2) : hexString;
                                                if(getlast2charcters.equalsIgnoreCase("00")){
                                                    hexString=hexString.substring(0,hexString.length()-2);
                                                    incommingMessageForConcatenation=incommingMessageForConcatenation+convertHexStringToString(hexString);
                                                }else {
                                                    incommingMessageForConcatenation=incommingMessageForConcatenation+convertHexStringToString(hexString);
                                                }
                                            }
                                            if((blehexObtainedFrom_Firmware.length()==32)&&(blehexObtainedFrom_Firmware.substring(0,2).equalsIgnoreCase("b2"))&&(blehexObtainedFrom_Firmware.substring(4,6).equalsIgnoreCase("03"))){
                                                /**
                                                 * message Start packet with upcode 03 ending packet incomming message.
                                                 */
                                                incommingMessagePacket.setMessageData(incommingMessageForConcatenation);
                                                incommingMessageForConcatenation="";
                                                String localVariableForBlock= blehexObtainedFrom_Firmware;
                                                if(localVariableForBlock.substring(6,8).equalsIgnoreCase("01")){
                                                    incommingMessagePacket.setEndpacketChannelID(getResources().getString(R.string.GSM));
                                                }else if(localVariableForBlock.substring(6,8).equalsIgnoreCase("02")){
                                                    incommingMessagePacket.setEndpacketChannelID(getResources().getString(R.string.IRIDIUM));
                                                }

                                                byte channelId=0;
                                                if(incommingMessagePacket.getEndpacketChannelID().equalsIgnoreCase(getResources().getString(R.string.IRIDIUM))){
                                                    channelId=2;
                                                }else   if(incommingMessagePacket.getEndpacketChannelID().equalsIgnoreCase(getResources().getString(R.string.GSM))){
                                                    channelId=1;
                                                }
                                                if(Integer.parseInt(incommingMessagePacket.getTotalLengthOfTextmessage())==Integer.parseInt(""+incommingMessagePacket.getMessageData().length())){
                                                    /**
                                                     * Recieveed incomming message sucess
                                                     */
                                                    sendAckIncommigMessageRecievedFromDevice(bleDevice.getMac(),incommingMessageACK(incommingMessagePacket.getSequenceNumber(),channelId, (byte) 1));
                                                    /**
                                                     * insert chat to table and send UI updte for recycleView.
                                                     */
                                                    insertChatInfoToTable(incommingMessagePacket.getMessageData(),incommingMessagePacket.getSequenceNumber(),bleDevice.getMac().toLowerCase().replace(":",""), incommingMessagePacket.getEndpacketChannelID());

                                                }else {
                                                    /**
                                                     * incomming message recieved failure..
                                                     */
                                                    sendAckIncommigMessageRecievedFromDevice(bleDevice.getMac(),incommingMessageACK(incommingMessagePacket.getSequenceNumber(),channelId, (byte) 0));
                                                }

                                            }

                                        }
                                    }
                                }
                            }
                        });
                    }


                });
    }

    private void insertChatInfoToTable(String messageRecieved,String sequenceNumber,String bleAddress,String GSM_IRIDIUM) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String timeStampDateBase =getTimeStampMilliSecondd();
           //     String timeStampDateBase = DateUtilsMyHelper.getCurrentDate(DateUtilsMyHelper.dateFormatStandard);
                String date_time = getDateWithtime();
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setFrom_name(getString(R.string.fragment_chat_server_name));
                chatInfo.setTo_name(getString(R.string.fragment_chat_owner_name));
                chatInfo.setMsg_txt(messageRecieved);
                chatInfo.setTime(date_time);
                chatInfo.setStatus(getResources().getString(R.string.fragment_chat_message_mesaage_recieved_from_ble));
                chatInfo.setSequence("" + sequenceNumber);
                chatInfo.setIdentifier(bleAddress);
                chatInfo.setTimeStamp(timeStampDateBase);
                if (GSM_IRIDIUM.equalsIgnoreCase(getResources().getString(R.string.GSM))) {
                    chatInfo.setIsGSM("1");
                } else if (GSM_IRIDIUM.equalsIgnoreCase(getResources().getString(R.string.IRIDIUM))) {
                    chatInfo.setIsGSM("0");
                }
                roomDBHelperInstance.get_Chat_info_dao().insert_ChatInfo(chatInfo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivity_container);
                        if (fragment.toString().equalsIgnoreCase(new FragmentChatting().toString())) {
                            ChattingObject chattingObject = new ChattingObject();
                            chattingObject.setMode(getResources().getString(R.string.fragment_chatting_incomming_message));
                            chattingObject.setMessage(messageRecieved);
                            chattingObject.setDate(date_time);
                            chattingObject.setTimeStamp(timeStampDateBase);
                            chattingObject.setTime_chat(date_time.substring(11,16));
                            chattingObject.setBleAddress(bleAddress);
                            chattingObject.setSequenceNumber(sequenceNumber);
                            if(passChatObjectToFragment!=null){
                                passChatObjectToFragment.ChatObjetShare(chattingObject);
                            }
                        }

                    }
                });
            }
        });

    }


    private void checkTokenAvaliableForImeiNumber(String bleaddress, String imeiNumber) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean isImeiNumberAValiable = roomDBHelperInstance.get_TableDevice_dao().isImeiNumberExists(imeiNumber);
                if (isImeiNumberAValiable) {
                    String deviceToken = roomDBHelperInstance.get_TableDevice_dao().getDeviceToken(imeiNumber);
                    if ((!deviceToken.equalsIgnoreCase("")) && (!deviceToken.equalsIgnoreCase("NA")) && (deviceToken != null) && (deviceToken.length() > 1)) {
                        deviceToken_fromFirmware = deviceToken;
                        processDeviceTokenAndSend(deviceToken_fromFirmware, bleaddress);
                    }
                } else {
                    /**
                     * call the API and Get the device Token and Send it to the Firmware..
                     */
                    getDeviceTokenAPI(imeiNumber, bleaddress);
                }
            }
        });
    }



    private void writeAuthenicationToDevice(BleDevice bleDevice) {
        BleManager.getInstance().write(
                bleDevice,
                TRACKER_SERVICE_UUID,
                TRACKER_CHARCTERSTICS_UUID,
                WriteValue01(),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("DATA Authnenication to Device written");
                            }
                        });
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                    }
                });

    }

    public void writeConnectionMaintainceCode(BleDevice bleDevice, byte[] datasent, int itemSelectedForConnection) {
        BleManager.getInstance().write(
                bleDevice,
                TRACKER_SERVICE_UUID,
                TRACKER_CHARCTERSTICS_UUID,
                datasent,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] Write) {
                        System.out.println("DATA writeConnectionMaintainceCode");

                        from_DataBase_ID_TimeStamp = new ArrayList<String>();
                        from_firmware_ID_TimeStamp = new ArrayList<String>();
                        from_firmware_ID_TimeStamp_A8_Packet = new ArrayList<String>();
                        System.gc();

                        /**
                         * Logic to checkDeviceName exits in the Table
                         */
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                  /*  if (!(CheckRecordAvaliableinDB(device_name_table, tbl_device_bleAddress, bleDevice.getMac().replace(":", "").toLowerCase()))) {
                                        if ((openDialogToCheckDeviceName != null) && (itemSelectedForConnection != -1)) {
                                            openDialogToCheckDeviceName.showDialogNameNotAvaliable(itemSelectedForConnection);
                                        }
                                    }

                                    *//**
                                 * Take the GeoFence Id and TimeStamp from the Table and Fill in the list.
                                 * Later compare it with Firmware Obtained Ids.
                                 *//*
                                    loadGeoFenceId_TimeStamp();*/
                            }
                        });

                        /**
                         * Ask IMEI numner from the Firmware...
                         */

                        hexConverted_IMEIList = new ArrayList<String>();
                        hexConverted_IMEIList = getHexArrayList(askIMEI_number());
                        writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_IMEIList.get(0).toCharArray()), "ASKING IMEI NUMBER", hexConverted_IMEIList);
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                    }
                });

    }

    public static void writeDataToFirmware(BleDevice bleDevice, byte[] datasent, String dataWritten_reason) {
        try {
            BleManager.getInstance().write(
                    bleDevice,
                    TRACKER_SERVICE_UUID,
                    TRACKER_CHARCTERSTICS_UUID,
                    encryptData(datasent),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {

                            byte[] decrypted_byteArray_FromFirmware = new byte[0];
                            try {
                                decrypted_byteArray_FromFirmware = decryptData(encryptData(datasent));
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            String hex_converted_decrypted_byte_array = HexUtil.encodeHexStr(decrypted_byteArray_FromFirmware);
                            System.out.println("Data Written= " + dataWritten_reason + " HexValue Written to Firmware " + hex_converted_decrypted_byte_array);
                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            System.out.println("Data Written Failed " + dataWritten_reason + " Error= " + exception.getDescription());
                        }
                    });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private void insertIntoGeoFenceTable(GeoFenceObjectData geoFenceObjectData) {
        Geofence geofence=new Geofence();
        geofence.setName("NA");
        geofence.setGeofence_ID(""+geoFenceObjectData.getGeoId());
        geofence.setType(geoFenceObjectData.getGeoFenceType());
        if (geoFenceObjectData.getGeoFenceType().toString().equalsIgnoreCase("Circular")) {
            geofence.setLat(""+geoFenceObjectData.getLatLong().get(0).getLatitude());
            geofence.setLongValue(""+geoFenceObjectData.getLatLong().get(0).getLongitude());
        }else if(geoFenceObjectData.getGeoFenceType().toString().equalsIgnoreCase("Polygon")){
            geofence.setLat("NA");
            geofence.setLongValue("NA");
        }
        geofence.setRadiusOrvertices(""+geoFenceObjectData.getRadius_vertices());
        geofence.setNumber_of_rules(""+geoFenceObjectData.getNumberOfRules());
        geofence.setIs_active("NA");
        geofence.setGsm_reporting(""+geoFenceObjectData.getIridium_reportingTime());
        geofence.setIridium_reporting(""+geoFenceObjectData.getGsm_ReportingTime());
        geofence.setFirmware_timestamp(""+geoFenceObjectData.getFirmwareTimeStamp());
        roomDBHelperInstance.get_GeoFence_info_dao().insert_GeoFence(geofence);
    }

    private void loadGeoFenceId_TimeStamp(BleDevice bleDevice) {
        if (from_DataBase_ID_TimeStamp != null) {
            from_DataBase_ID_TimeStamp.clear();
        }
        List<Geofence> geofenceList = roomDBHelperInstance.get_GeoFence_info_dao().getAll_GeoFence();
        for (Geofence geofence : geofenceList) {
            from_DataBase_ID_TimeStamp.add(geofence.getGeofence_ID() + ":" + geofence.getFirmware_timestamp());
        }
        deviceToken_fromFirmware = "";
        System.gc();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                writeDataToFirmware(bleDevice, askForGeoFenceId_timeStamp(), "Asking GeoFence ID TimeStamp after connection ");
            }
        });

    }

    @Override
    public void timeIntervalGiven(String bleAddress, String timeInterval) {
        System.out.println("Time_Interval = " + timeInterval);
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    writeDataToFirmware(bleDevice, set_firmwareTimeStamp(Integer.parseInt(timeInterval)), "Firmware Time Stamp ");
                }
            }
        }
    }

    private void playAlertMusic() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.fence_alert);
        mediaPlayer.start();
    }


    ArrayList<CustomBluetooth> customBleDeviceAfterDisconnection = new ArrayList<CustomBluetooth>();
    private static boolean goForConnect = false;

    private void checkisDisconnectedFromFirmware(RxBleDevice bleDevice) {
        customBleDeviceAfterDisconnection = new ArrayList<CustomBluetooth>();
       /* if (main_Activity_connectedDevicesAliasList.size() > 0) {
            for (MainActivityConnectedDevices mainActivityConnectedDevices : main_Activity_connectedDevicesAliasList) {
                if (mainActivityConnectedDevices.getBleDevice().getMac().equalsIgnoreCase(bleDevice.getMacAddress()) && (!mainActivityConnectedDevices.isIs_DisconnectedFromFirmware())) {

                    *//**
         * Here passing -1 is just dummy data to the device.
         * Issue Resolving is.
         * 1)Start scanning.
         * 2)If avaliable in Scanning,then connect.
         * 3)Stop scan.
         *//*
                    goForConnect = true;
                    CustomBluetooth customBluetooth = new CustomBluetooth(bleDevice, getResources().getString(R.string.device_name_alias), bleDevice.getMacAddress());
                    customBleDeviceAfterDisconnection.add(customBluetooth);
                    start_stop_SCAN(this);
                    // connectRxBleDevice(bleDevice, -1);
                }
            }
        }*/

        goForConnect = true;
        CustomBluetooth customBluetooth = new CustomBluetooth(bleDevice, getResources().getString(R.string.device_name_alias), bleDevice.getMacAddress());
        customBleDeviceAfterDisconnection.add(customBluetooth);
        //  RxBleStartScanning(this);
        start_stop_SCAN(this);
        // connectRxBleDevice(bleDevice, -1);
    }

    private void process_TimeStamp_id_One_After_Other_A6_packet(BleDevice bleDevice) {
        if (from_firmware_ID_TimeStamp.size() > 0) {
            if (check_timeStamp_id_Avaliable_In_dataBase_Arraylist(from_firmware_ID_TimeStamp.get(0))) {
                removeId_time_Stamp_from_firmware_arraylist(bleDevice, null);
                process_TimeStamp_id_One_After_Other_A6_packet(bleDevice);
            } else {
                /**
                 * Request ID_TimeStamp from firmware.
                 */
                String firmwareDetailsArray = from_firmware_ID_TimeStamp.get(0);
                String geofenceId = getID_From_ArrayList(firmwareDetailsArray);
                String geofenceTimeStamp = get_TimeStamp_ArrayList(firmwareDetailsArray);
                writeDataToFirmware(bleDevice, geoFenceId(Integer.parseInt(geofenceId)), "Asking For GeoFenceID from Time Stamp=  ID= " + geofenceId);
            }
        } else {
            /**
             * Send Ack that no geoFence id there..so ready to recieve geoFence alert.
             */
            cancelProgressDialog();
            writeDataToFirmware(bleDevice, send_Geo_fenceID_fetched_finished_Acknoledgement((byte) 0xa4), "ACK to Firmware i.e Ready to Recieve alerts A4FF");
        }
    }

    private void removeId_time_Stamp_from_firmware_arraylist(BleDevice bleDevice, String index_items) {
        if (index_items == null) {
            from_firmware_ID_TimeStamp.remove(0);
        } else if (index_items.length() > 2) {
            from_firmware_ID_TimeStamp.remove(index_items);
        }
    }

    private boolean check_timeStamp_id_Avaliable_In_dataBase_Arraylist(String id_timeStamp_from_firmware) {
        boolean result = false;
        if (from_DataBase_ID_TimeStamp.contains(id_timeStamp_from_firmware)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    private void process_TimeStamp_id_One_After_Other_A8_packet(BleDevice bleDevice) {
        if (from_firmware_ID_TimeStamp_A8_Packet.size() > 0) {
            String firmwareDetailsArray = from_firmware_ID_TimeStamp_A8_Packet.get(0);
            String geofenceId = getID_From_ArrayList(firmwareDetailsArray);
            writeDataToFirmware(bleDevice, geoFenceId(Integer.parseInt(geofenceId)), "Asking For GeoFenceID from A8 PACKET " + geofenceId);
        }
    }

    private void remove_id_time_stamp_from_A8_packet_SendAck_if_packet_process_Finished(String id_stamp_from_firmware, BleDevice bleDevice) {
        if (from_firmware_ID_TimeStamp_A8_Packet.size() > 0) {
            if (from_firmware_ID_TimeStamp_A8_Packet.contains(id_stamp_from_firmware)) {
                from_firmware_ID_TimeStamp_A8_Packet.remove(id_stamp_from_firmware);
                if (from_firmware_ID_TimeStamp_A8_Packet.isEmpty()) {
                    writeDataToFirmware(bleDevice, send_Geo_fenceID_fetched_finished_Acknoledgement((byte) 0xa8), "ACK to Firmware i.e Ready to Recieve alerts A8FF");
                } else {
                    process_TimeStamp_id_One_After_Other_A8_packet(bleDevice);
                }
            }
        } else {
            System.out.println("NOT_SENDING A8 ACK TO FIRMWARE ");
        }
    }

    private void createNotificationChannel_codeTutor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.GEO_FENCE_ID), getString(R.string.GEO_FENCE_ALERTS), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(getString(R.string.GEO_FENCE_DESCRIPTION));
            notificationChannel.setShowBadge(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private boolean checkPhoneScreenLocked() {
        KeyguardManager myKM = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.isKeyguardLocked()) {
            return true;
        } else {
            return false;
        }
    }

    public void createNotification(String bleDeviceAliasName, String headerName, String message_one, String message_two) {
        String endResult = bleDeviceAliasName + "\n" + headerName + "\n" + message_one + "\n" + message_two;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.GEO_FENCE_ID))
                .setSmallIcon(R.drawable.geo_fence_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification_icon))
                .setContentTitle("GeoFence Alert")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId(getString(R.string.GEO_FENCE_ID))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(endResult));
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), builder.build());
    }

    @Override
    public void messagePacketArray(String bleAddress, ArrayList<byte[]> messageArraylist) {
        /**
         * call back from the interface after typing the message.
         */
        hexConverted_ChatMessageList = new ArrayList<String>();
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    hexConverted_ChatMessageList = getHexArrayList(messageArraylist);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_ChatMessageList.get(0).toCharArray()), "CHAT MESSAGE INTIAL PACKET", hexConverted_ChatMessageList);
                }
            }
        }
    }

    private void changeMessageStatusInDb(String bleAddress, String sequenceNumber, String messageStatus) {
        if (chatDeliveryACK != null) {
            chatDeliveryACK.chatDeliveryStatus(bleAddress, sequenceNumber, messageStatus);
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                roomDBHelperInstance.get_Chat_info_dao().updateMessageStatusInDb(bleAddress,sequenceNumber,messageStatus);
            }
        });
    }

    @Override
    public void deviceConfigurationDetails(String bleAddress, ArrayList<byte[]> configurationList) {
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    hexConverted_DeviceConfigurationList = new ArrayList<String>();
                    hexConverted_DeviceConfigurationList = getHexArrayList(configurationList);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_DeviceConfigurationList.get(0).toCharArray()), "DEVICE CONFIGURATION INTIAL PACKET", hexConverted_DeviceConfigurationList);
                    showProgressDialog("Saving Setting");
                }
            }
        }
    }

    private void writeDataToFirmwareAfterConfermation(BleDevice bleDevice, byte[] datasent, String dataWritten_reason, ArrayList<String> arrayListInHex) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            BleManager.getInstance().write(
                    bleDevice,
                    TRACKER_SERVICE_UUID,
                    TRACKER_CHARCTERSTICS_UUID,
                    encryptData(datasent),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] byteArrayOnWriteSucess) {
                            byte[] decrypted_byteArray_FromFirmware = new byte[0];
                            try {
                                decrypted_byteArray_FromFirmware = decryptData(byteArrayOnWriteSucess);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            String hex_converted_decrypted_byte_array = HexUtil.encodeHexStr(decrypted_byteArray_FromFirmware);
                            System.out.println("DATA WRITTEN " + hex_converted_decrypted_byte_array);
                            if (!arrayListInHex.isEmpty() && (arrayListInHex.contains(hex_converted_decrypted_byte_array))) {
                                arrayListInHex.remove(hex_converted_decrypted_byte_array);
                                System.out.println("DATA REMOVED AFTER WRITING " + hex_converted_decrypted_byte_array);
                                if (arrayListInHex.size() > 0) {
                                    byte[] bytes = HexUtil.decodeHex(arrayListInHex.get(0).toCharArray());
                                    writeDataToFirmwareAfterConfermation(bleDevice, bytes, "DATA WRITING AFTER REMOVING = " + arrayListInHex.get(0).toString().toUpperCase(), arrayListInHex);
                                }
                            }
                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            System.out.println("DATA FAILED " + dataWritten_reason + " Error= " + exception.getDescription() + " Error Code " + exception.getCode());
                        }
                    });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void ServerConfigurationPacketArray(String bleAddress, ArrayList<byte[]> entitemessageList) {
        hexConverted_ServerConfigurationList = new ArrayList<String>();
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    hexConverted_ServerConfigurationList = getHexArrayList(entitemessageList);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_ServerConfigurationList.get(0).toCharArray()), "SERVER DEVICE CONFIGURATION", hexConverted_ServerConfigurationList);
                    showProgressDialog("Saving Setting");
                }
            }
        }
    }

    @Override
    public void industrySpcificConfigurationDetails(String bleAddress, ArrayList<byte[]> entitemessageList) {
        hexConverted_IndustrySpecificList = new ArrayList<String>();
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    hexConverted_IndustrySpecificList = getHexArrayList(entitemessageList);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_IndustrySpecificList.get(0).toCharArray()), "Industry Specific Configuration", hexConverted_IndustrySpecificList);
                    showProgressDialog("Saving Setting");
                }
            }
        }
    }

    @Override
    public void wifiConfigurationDetails(String bleAddress, ArrayList<byte[]> configArrayList) {
        hexConverted_WifiConfigurationList = new ArrayList<String>();
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    hexConverted_WifiConfigurationList = getHexArrayList(configArrayList);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_WifiConfigurationList.get(0).toCharArray()), "SERVER DEVICE CONFIGURATION", hexConverted_WifiConfigurationList);
                    showProgressDialog("Saving Setting");
                }
            }
        }
    }


    @Override
    public void SimConfigurationDetails(String bleAddress, ArrayList<byte[]> simconfigurationList) {
        hexConverted_SimConfigurationList = new ArrayList<String>();
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    hexConverted_SimConfigurationList = getHexArrayList(simconfigurationList);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_SimConfigurationList.get(0).toCharArray()), "SIM CONFIGURATION DATA PARSING", hexConverted_SimConfigurationList);
                    showProgressDialog("Saving Setting");
                }
            }
        }
    }

    @Override
    public void resetDevicePacketSend(String bleaddress, ArrayList<byte[]> resetFirmware) {
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleaddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    HexConverted_DevicePacektList=new ArrayList<String>();
                    HexConverted_DevicePacektList = getHexArrayList(resetFirmware);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(HexConverted_DevicePacektList.get(0).toCharArray()), "Reset Device", HexConverted_DevicePacektList);
                    showProgressDialog("Reseting Device");
                }
            }
        }
    }

    @Override
    public void requestLiveLocationFromFirmware(String bleAddress,ArrayList<byte[]> liveLocationRequestPakets) {
            hexConverted_liveLocation = new ArrayList<String>();
            if (getBluetoothAdapter() != null) {
                BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
                if (bluetoothAdapter.isEnabled()) {
                    final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
                    BleDevice bleDevice = new BleDevice(getBleDevice);
                    if (BleManager.getInstance().isConnected(bleDevice)) {
                        hexConverted_liveLocation = getHexArrayList(liveLocationRequestPakets);
                        writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(hexConverted_liveLocation.get(0).toCharArray()), "Live Location Request", hexConverted_liveLocation);
                    }
                }
            }
    }

    private void sendAckIncommigMessageRecievedFromDevice(String blAddress,ArrayList<byte[]> incomingMessagePacektRecieved){
        if (getBluetoothAdapter() != null) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter.isEnabled()) {
                final BluetoothDevice getBleDevice = bluetoothAdapter.getRemoteDevice(blAddress);
                BleDevice bleDevice = new BleDevice(getBleDevice);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    HexConverted_IncommingMessagePacket=new ArrayList<String>();
                    HexConverted_IncommingMessagePacket = getHexArrayList(incomingMessagePacektRecieved);
                    writeDataToFirmwareAfterConfermation(bleDevice, HexUtil.decodeHex(HexConverted_IncommingMessagePacket.get(0).toCharArray()), "INCOMMING MESSGE REQUEST ACK", HexConverted_IncommingMessagePacket);
                }
            }
        }
    }


    /**
     * Google BLE libraray implementation.
     */
    public BluetoothLeService mBluetoothLeService;
    private BluetoothLeScanner bluetoothLeScanner =
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 30000;
    public static String SCAN_TAG = "";
    private void bindBleServiceToMainActivity() {
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };
    /**
     * BroadCast Reciever Data Trigger.
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_TIMER_ACTION));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE));
        return intentFilter;
    }
    private final BroadcastReceiver bluetootServiceRecieverData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS)))) {
                /**
                 * Connection/Disconnection of the Device.
                 */
                String bleAddress = intent.getStringExtra((getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_BLE_ADDRESS)));
                boolean connectionStatus = intent.getBooleanExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_CONNECTED_DISCONNECTED), false);
                passConnectionSucesstoFragmentScanForUIChange(bleAddress, connectionStatus);
            } else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION)))) {
                /**
                 * Data Written to the firmware getting loop back after write confermation.
                 */
                String bleAddress = intent.getStringExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_ADDRESS));
                byte[] dataWritten = intent.getByteArrayExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN));
                int dataWrittenType = intent.getIntExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN_TYPE), -1);
                System.out.println("what data written to the Firmware= "+convertHexToBigIntegert(bytesToHex(dataWritten)));
                System.out.println("what data written to the Firmware bleAddres = "+bleAddress);
                System.out.println("what data written to the Firmware type = "+dataWrittenType);
            }else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED)))) {
                /**
                 * Data Obtained from the firmware.
                 */
                String bleAddress = intent.getStringExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_BLE_ADDRESS));
                byte[] obtainedFromFirmware = intent.getByteArrayExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_DATA_RECIEVED));
                if(showDataForItemInRecycleView!=null){
                    showDataForItemInRecycleView.recievedDataFromFirmware(bleAddress,obtainedFromFirmware);
                }
                System.out.println("DATA_FIRMWARE_OBTAINED= "+""+convertHexToBigIntegert(bytesToHex(obtainedFromFirmware)));

            }else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_TIMER_ACTION)))) {
                /**
                 * Logic to cacen the progress dialog and hide it.
                 * 1)show something went wrong try again later after some time...
                 * 2)Clear scan device and Scan again.
                 */
                boolean timerCancelled=intent.getBooleanExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_TIMER_FINISH_KEY),false);
                passTimerOutConnectionTag(timerCancelled);
            }else  if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE)))) {
                /**
                 * Send Data to BLE Device.
                 */
                boolean notificationEnabled=intent.getBooleanExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE_DATA),false);
                System.out.println("ENABLE_NOTIFICATION_TRUE MainActivity "+notificationEnabled);
                if(notificationEnabled){
                    String bleAddress = intent.getStringExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_NOTIFICATION_ENABLE_BLE_AADRESS));
                    System.out.println("bleAddress= "+bleAddress);
                    mBluetoothLeService.sendDataToBleDevice(bleAddress,WriteValue01());
                }
            }
        }

        private void passTimerOutConnectionTag(boolean result) {
            if(deviceConnectionTimeOut!=null){
                deviceConnectionTimeOut.connectionTimeOutTimer(result);
            }
        }

        private void passConnectionSucesstoFragmentScanForUIChange(String connectedDeviceAddress, boolean connect_disconnect) {
            if (passConnectionStatusToFragment != null) {
                passConnectionStatusToFragment.connectDisconnect(connectedDeviceAddress, connect_disconnect);
            }
        }
    };

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (passScanDeviceToActivity_interface != null) {
                        if (result != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if ((result.getDevice().getName() != null) && (result.getDevice().getName().length() > 0)&&(result.getDevice().getName().startsWith("Succorfish"))) {
                                        passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(), result.getDevice().getName(), result.getDevice(), false));
                                    }
                                }
                            });


                        }
                    }
                }
            };


    private void startScan() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SCAN_TAG = getResources().getString(R.string.SCAN_STARTED);
                bluetoothLeScanner.startScan(leScanCallback);
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_PERIOD);
    }

    private void stopScan() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                    SCAN_TAG = getResources().getString(R.string.SCAN_STOPED);
                    bluetoothLeScanner.stopScan(leScanCallback);
                }

            }
        });
    }

    public void start_stop_scan() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(ble_on_off()){
                if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STOPED)) || (SCAN_TAG.equalsIgnoreCase(""))) {
                    startScan();
                }else if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                    /**
                     * Scan already started.
                     */
                }
            }
        }

    }

    public void setupPassScanDeviceToActivity_interface(PassScanDeviceToActivity_interface loc_passScanDeviceToActivity_interface) {
        this.passScanDeviceToActivity_interface = loc_passScanDeviceToActivity_interface;
    }

    @Override
    public void makeDevieConnecteDisconnect(CustBluetootDevices custBluetootDevices, boolean connect_disconnect) {
        if (connect_disconnect) {
            boolean connectissue = mBluetoothLeService.connect(custBluetootDevices.getBleAddress());
            if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                SCAN_TAG = getResources().getString(R.string.SCAN_STOPED);
                stopScan();
            }
        } else {
            mBluetoothLeService.disconnect(custBluetootDevices.getBleAddress());
            if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                SCAN_TAG = getResources().getString(R.string.SCAN_STOPED);
                stopScan();
            }
        }
    }
}


