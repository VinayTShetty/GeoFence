package com.succorfish.geofence.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.succorfish.geofence.BaseFragment.BaseFragment;
import com.succorfish.geofence.CustomObjectsAPI.AssetDeatils;
import com.succorfish.geofence.MainActivity;
import com.succorfish.geofence.R;

import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.succorfish.geofence.utility.RetrofitHelperClass.haveInternet;

public class FragmentRemoteTrackingMap extends BaseFragment {
    View fragmentRemoteTrackingView;
    private Unbinder unbinder;
    MainActivity mainActivity;
    MapView mMapView;
    String connected_bleAddress="";
    GoogleMap fgragmentRemoteTrackinggoogleMap;
    String deviceIdToFetchForLatLong=null;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        /**
         * Fetch Bundle Data.
         */
        getBundleData();
    }

    private void getBundleData() {
     Bundle bundle=   this.getArguments();
     if(bundle!=null){
         deviceIdToFetchForLatLong=  bundle.getString(FragmentRemoteTrackingList.class.getName());
     }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRemoteTrackingView = inflater.inflate(R.layout.fragment_remotetracking_map, container, false);
        unbinder = ButterKnife.bind(this, fragmentRemoteTrackingView);
        bottomLayoutVisibility(false);
        setUpMap(fragmentRemoteTrackingView, savedInstanceState);
        call_Lat_Long_API_ForDeviceId();
        return fragmentRemoteTrackingView;
    }

    private void call_Lat_Long_API_ForDeviceId() {
        if(deviceIdToFetchForLatLong!=null&&deviceIdToFetchForLatLong.length()>0){
            latLongAPI();
        }
    }

    private void latLongAPI() {
        if(haveInternet(getActivity())){
            Call<String> detailsOfAssetList=   mainActivity.mApiService.getLatLongOfAsset(deviceIdToFetchForLatLong);
            detailsOfAssetList.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    if (response.code() == 200 || response.isSuccessful()) {
                        System.out.println("response mAddInstData---------" + response.body().toString());
                        if (response.body() != null && !response.body().equalsIgnoreCase("") && !response.body().equalsIgnoreCase("null")) {
                            AssetDeatils assetDeatil= gson.fromJson(response.body(), AssetDeatils.class);
                            if(assetDeatil!=null){
                                if((assetDeatil.getLat()!=null)&&(!assetDeatil.getLat().equalsIgnoreCase("")&&(assetDeatil.getLng()!=null)&&(!assetDeatil.getLng().equalsIgnoreCase("")))){
                                    long latitude=Long.parseLong(assetDeatil.getLat());
                                    long longitude=Long.parseLong(assetDeatil.getLng());
                                    displayLocationInMap(latitude,longitude);
                                }
                            }
                        } else {

                        }

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }
    }

    private void bottomLayoutVisibility(boolean hide_true_unhide_false){
        mainActivity.hideBottomLayout(hide_true_unhide_false);
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
            }
        });

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
        unbinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public String toString() {
        return FragmentRemoteTrackingMap.class.getSimpleName();
    }
}
