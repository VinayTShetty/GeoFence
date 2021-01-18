package com.succorfish.geofence.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.clj.fastble.data.BleDevice;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.succorfish.geofence.BaseFragment.BaseFragment;
import com.succorfish.geofence.MainActivity;
import com.succorfish.geofence.R;
import com.succorfish.geofence.interfaceActivityToFragment.LiveRequestDataPassToFragment;
import com.succorfish.geofence.interfaceFragmentToActivity.LiveLocationReq;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.succorfish.geofence.MainActivity.CONNECTED_BLE_ADDRESS;
import static com.succorfish.geofence.blecalculation.LiveLocationPacketManufacturer.Start_Stop_LIVE_LOCATION;

public class FragmentLiveTracking extends BaseFragment {
    View fragmentLiveTrackingView;
    private Unbinder unbinder;
    MainActivity mainActivity;
    MapView mMapView;
    LiveLocationReq liveLocationReq;
    String connected_bleAddress="";
    GoogleMap fgragmentLiveTrackinggoogleMap;
    final Handler handler = new Handler();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLiveTrackingView = inflater.inflate(R.layout.fragment_livetracking, container, false);
        unbinder = ButterKnife.bind(this, fragmentLiveTrackingView);
        getConnectedBleAddress();
        interfaceIntialization();
        setUpMap(fragmentLiveTrackingView, savedInstanceState);
        interfaceImplementation_CallBack();
        return fragmentLiveTrackingView;
    }

    int counter=0;
    private void interfaceImplementation_CallBack() {
        mainActivity.setUpLiveRequest(new LiveRequestDataPassToFragment() {
            @Override
            public void liveRequestDataFromFirmware(Double latitudeValue, Double longValue, String bleAddress, BleDevice bleDevice) {
                counter++;
                if(counter%2==0){
                    showUpdatedMarkerInMap(latitudeValue+counter,longValue+counter);
                }else {
                    showUpdatedMarkerInMap(latitudeValue-counter,longValue-counter);
                }

            }
        });
    }

    private void getConnectedBleAddress(){
        connected_bleAddress=CONNECTED_BLE_ADDRESS;
    }

    private void interfaceIntialization() {
        liveLocationReq =(LiveLocationReq)getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(liveLocationReq!=null){
            liveLocationReq.requestLiveLocationFromFirmware(connected_bleAddress,Start_Stop_LIVE_LOCATION(false));
        }
    }

    public String toString() {
        return FragmentLiveTracking.class.getSimpleName();
    }

    private void setUpMap(View fragmentMapView, Bundle savedInstanceState) {
        mMapView = (MapView) fragmentMapView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                /**
                 * ask for Updates on Live Location Tracking.
                 */
                fgragmentLiveTrackinggoogleMap=mMap;
                if(liveLocationReq!=null){
                    liveLocationReq.requestLiveLocationFromFirmware(connected_bleAddress,Start_Stop_LIVE_LOCATION(true));

                }



            }
        });

    }


    @OnClick(R.id.fragment_live_tracking)
    public void backimageClick(){
        mainActivity.onBackPressed();
    }



    private void showUpdatedMarkerInMap(Double latitude,Double longitude){
        if(fgragmentLiveTrackinggoogleMap!=null){
            fgragmentLiveTrackinggoogleMap.clear();
            LatLng location = new LatLng(latitude,latitude);
            Circle circle = fgragmentLiveTrackinggoogleMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude,longitude))
                    .radius(10)
                    .strokeColor(Color.BLACK).strokeWidth(3)
                    .fillColor(R.color.colorAccent));
            MarkerOptions markerOptions = new MarkerOptions().position(location);
            fgragmentLiveTrackinggoogleMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.circular_fence)));
            LatLng breach_latitude_longitude = new LatLng(latitude,latitude);
            MarkerOptions circular_lat_long = new MarkerOptions();
            circular_lat_long.position(breach_latitude_longitude);
            circular_lat_long.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.map_custom_marker));
            Marker marker =    fgragmentLiveTrackinggoogleMap.addMarker(circular_lat_long);
            fgragmentLiveTrackinggoogleMap.getUiSettings().setRotateGesturesEnabled(true);
            fgragmentLiveTrackinggoogleMap.getUiSettings().setZoomControlsEnabled(true);
            fgragmentLiveTrackinggoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(breach_latitude_longitude, 10));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isVisible()){
                        fgragmentLiveTrackinggoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View markerinfoView=getLayoutInflater().inflate(R.layout.fence_info_window, null);
                                TextView name=(TextView)markerinfoView.findViewById(R.id.name);
                                TextView details=(TextView)markerinfoView.findViewById(R.id.details);
                                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                name.setText("Updated At \n"+mydate);
                                return  markerinfoView;
                            }
                        });
                        marker.showInfoWindow();
                    }
                }
            },2000);
            fgragmentLiveTrackinggoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    fgragmentLiveTrackinggoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View markerinfoView=getLayoutInflater().inflate(R.layout.fence_info_window, null);
                            TextView name=(TextView)markerinfoView.findViewById(R.id.name);
                            TextView details=(TextView)markerinfoView.findViewById(R.id.details);
                            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                            name.setText("Updated At \n"+mydate);
                            return  markerinfoView;
                        }
                    });
                    marker.showInfoWindow();
                }
            });
        }

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int VectorResId) {
        Drawable VectorDrawable = ContextCompat.getDrawable(context, VectorResId);
        VectorDrawable.setBounds(0, 0, VectorDrawable.getIntrinsicWidth(), VectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(VectorDrawable.getIntrinsicWidth(), VectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canves = new Canvas(bitmap);
        VectorDrawable.draw(canves);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
